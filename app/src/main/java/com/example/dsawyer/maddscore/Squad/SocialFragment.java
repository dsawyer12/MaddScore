package com.example.dsawyer.maddscore.Squad;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.PostUserMap;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Post;
import com.example.dsawyer.maddscore.Social.ConfirmDeletePostDialog;
import com.example.dsawyer.maddscore.Social.PostCommentDialog;
import com.example.dsawyer.maddscore.Social.PostFragment;
import com.example.dsawyer.maddscore.Utils.LoadingDialog;
import com.example.dsawyer.maddscore.Utils.SocialPostRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SocialFragment extends Fragment implements ConfirmDeletePostDialog.OnPostDeletedListener {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private FirebaseUser currentUser;
    private User mUser;
    private Squad mSquad;

    private LinearLayoutManager layoutManager;
    private SocialPostRecyclerViewAdapter adapter;
    private ArrayList<PostUserMap> posts;

    final int loadCount = 20;
    int totalItems = 0, finished = 0, firstVisibleItem, lastCompleteVisibleItem;
    boolean isLoading = false, isLastItemReached = false;
    String lastKey, lastNode;

    private SocialPostRecyclerViewAdapter.OnItemClickListener listener;

    private RecyclerView recyclerView;
    TextView no_activity, squad_name, squad_id;
    LoadingDialog loadingDialog;


    @Override
    public void onDelete(PostUserMap postMap) {
        if (adapter != null) {
            adapter.getPosts().remove(postMap);
            adapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: social Fragment");
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        no_activity = view.findViewById(R.id.no_activity);
        squad_name = view.findViewById(R.id.squad_name);
        squad_id = view.findViewById(R.id.squad_id);

        loadingDialog = new LoadingDialog(getActivity());

        FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> {
            PostFragment postFragment = new PostFragment();
            Bundle args = new Bundle();
            args.putParcelable("mUser", mUser);
            postFragment.setArguments(args);
            if (getActivity() != null)
                ((SquadActivity) getActivity()).setFragment(postFragment, R.id.main_frame, "postFragment");
        });

        if (this.getArguments() != null) {
            mUser = this.getArguments().getParcelable("mUser");
            mSquad = this.getArguments().getParcelable("squad");
            if (mSquad != null) {
                squad_name.setText(mSquad.getSquadName());
                squad_id.setText("#" + mSquad.getPublicID());
            }
        }

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            setAdapterListeners();
            getLastPostKey();

            layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new SocialPostRecyclerViewAdapter(getActivity(), listener, currentUser.getUid());
            recyclerView.setAdapter(adapter);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) fab.hide();
                    else fab.show();

                    totalItems = layoutManager.getItemCount();
                    firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    lastCompleteVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
//
                    if (!isLoading && (lastCompleteVisibleItem <= totalItems) && totalItems >= (firstVisibleItem + loadCount)) {
                        getSocialPosts();
                        isLoading = true;
                    }
                }
            });

        } else {
            if (getActivity() != null)
                Toast.makeText(getActivity(), "Something went wrong while trying to retrieve your account data.", Toast.LENGTH_LONG).show();
        }
    }

    public void setAdapterListeners() {
        listener = (v, position) -> {
            switch (v.getId()) {
                case (R.id.post_like):
                    if (adapter.getPost(position).getUserLikesList() != null) {
                        if (adapter.getPost(position).getUserLikesList().containsKey(currentUser.getUid())) {
                            adapter.getPost(position).getUserLikesList().remove(currentUser.getUid());
                            ref.child("socialPosts").child(mUser.getSquad()).child(adapter.getPost(position).getPostID()).child("userLikesList").child(currentUser.getUid()).removeValue();
                        } else {
                            adapter.getPost(position).getUserLikesList().put(currentUser.getUid(), true);
                            ref.child("socialPosts").child(mUser.getSquad()).child(adapter.getPost(position).getPostID()).child("userLikesList").child(currentUser.getUid()).setValue(true);
                        }
                    } else {
                        HashMap<String, Boolean> userList = new HashMap<>();
                        userList.put(currentUser.getUid(), true);
                        adapter.getPost(position).setUserLikesList(userList);
                        ref.child("socialPosts").child(mUser.getSquad()).child(adapter.getPost(position).getPostID()).child("userLikesList").child(currentUser.getUid()).setValue(true);
                    }
                    break;

                case (R.id.post_comment):
                    PostCommentDialog pc = new PostCommentDialog();
                    String postID = adapter.getPost(position).getPostID();
                    Bundle args = new Bundle();
                    args.putString("post_ID", postID);
                    args.putParcelable("user", mUser);
                    pc.setArguments(args);
                    pc.setTargetFragment(SocialFragment.this, 1001);
                    if (getFragmentManager() != null)
                        pc.show(getFragmentManager(), "courseSelector");
                    break;

                case (R.id.post_options):
                    if (getActivity() != null) {
                        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                        popupMenu.inflate(R.menu.post_options_menu);
                        popupMenu.setOnMenuItemClickListener(menuItem -> {
                            switch (menuItem.getItemId()) {
                                case(R.id.edit):
                                    if (getActivity() != null) {
                                        PostFragment postFragment = new PostFragment();
                                        Bundle args1 = new Bundle();
                                        args1.putParcelable("mUser", mUser);
                                        args1.putParcelable("postMap", adapter.getPosts().get(position));
                                        postFragment.setArguments(args1);
                                        ((SquadActivity) getActivity()).setFragment(postFragment, R.id.main_frame, "postFragment");
                                    }
                                    return true;

                                case(R.id.delete):
                                    ConfirmDeletePostDialog dialog = new ConfirmDeletePostDialog();
                                    Bundle args2 = new Bundle();
                                    args2.putParcelable("postMap", adapter.getPosts().get(position));
                                    args2.putParcelable("mUser", mUser);
                                    dialog.setArguments(args2);
                                    dialog.setTargetFragment(this, 5703);
                                    if (getFragmentManager() != null)
                                        dialog.show(getFragmentManager(), "delete_post_dialog");
                                    else Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                    return true;

                                default: return false;
                            }
                        });
                        popupMenu.show();
                    } else
                        Toast.makeText(getActivity(), "Could not display options", Toast.LENGTH_SHORT).show();
                    break;
            }
        };
    }

    public void getLastPostKey() {
        Log.d(TAG, "getLastPostKey: called");
        loadingDialog.show();
        Query query = ref.child("socialPosts").child(mUser.getSquad()).limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    lastKey = ds.getKey();
                    Log.d(TAG, "last key : " + lastKey);
                }
                if (lastKey != null) {
                    no_activity.setVisibility(View.GONE);
                }
                else {
                    no_activity.setVisibility(View.VISIBLE);
                }
                getSocialPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });
    }

    public void getSocialPosts() {
        Log.d(TAG, "getSocialPosts: called");
        posts = new ArrayList<>();

        if (!isLastItemReached) {
            Query postQuery;
            if (TextUtils.isEmpty(lastNode))
                postQuery = ref.child("socialPosts").child(mUser.getSquad()).orderByKey().limitToLast(loadCount);
            else
                postQuery = ref.child("socialPosts").child(mUser.getSquad()).orderByKey().endAt(lastNode).limitToLast(loadCount);

            postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "child count : " + dataSnapshot.getChildrenCount());
                    if (dataSnapshot.hasChildren()) {
                        posts.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.d(TAG, "RETRIEVED KEY : \n" + ds.getKey());
                            Post post = ds.getValue(Post.class);
                            posts.add(new PostUserMap(post));
                        }

                        if (posts.get(posts.size() - 1).getPost().getPostID().equals(lastNode)) {
                            posts.remove(posts.size() - 1);
                            Log.d(TAG, "FOUND DUPLICATE");
                        }

                        if (!posts.isEmpty()) {
                            lastNode = posts.get(0).getPost().getPostID();
                            Log.d(TAG, "Last Node : " + lastNode);
                            if (lastNode.equals(lastKey)) {
                                isLastItemReached = true;
                                Log.d(TAG, "last database key has been retrieved");
                            }

                            Query userQuery;
                            PostUserMap currentPost;
                            for (int i = 0; i < posts.size(); i++) {
                                currentPost = posts.get(i);

                                userQuery = ref.child("users").child(currentPost.getPost().getCreatorID());
                                PostUserMap finalCurrentPost = currentPost;
                                userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        finished++;
                                        if (dataSnapshot.exists()) {
                                            User user = dataSnapshot.getValue(User.class);
                                            if (user != null) {
                                                finalCurrentPost.setName(user.getName());
                                                finalCurrentPost.setUsername(user.getUsername());
                                                finalCurrentPost.setPhotoUrl(user.getPhotoUrl());
                                            }

                                            if (finished == posts.size()) {
                                                finished = 0;
                                                Collections.reverse(posts);
                                                adapter.addItems(posts);
                                                loadingDialog.dismiss();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        loadingDialog.dismiss();
                                        isLoading = false; }
                                });
                            }
                        }

                    } else {
                        Log.d(TAG, "NO CHILDREN...........");
                        loadingDialog.dismiss();
                        isLoading = false;
                        isLastItemReached = true;
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    loadingDialog.dismiss();
                    isLoading = false;
                }
            });

            isLoading = false;

            postQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d(TAG, "onChildAdded: ");
                    no_activity.setVisibility(View.GONE);
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d(TAG, "onChildChanged: ");
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved: ");
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d(TAG, "onChildMoved: ");
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: ");
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }
            });

        } else {
            Log.d(TAG, "MAXED");
            isLoading = false;
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
