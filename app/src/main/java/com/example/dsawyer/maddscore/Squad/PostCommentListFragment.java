package com.example.dsawyer.maddscore.Squad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.dsawyer.maddscore.ObjectMaps.PostCommentUserMap;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.PostComment;
import com.example.dsawyer.maddscore.Utils.LoadingDialog;
import com.example.dsawyer.maddscore.Utils.PostCommentListRecyclerView;
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

public class PostCommentListFragment extends DialogFragment {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private FirebaseUser currentUser;
    private User mUser;
    private String post_ID;
    private RecyclerView recyclerView;
    private ArrayList<PostCommentUserMap> comments;
    private LoadingDialog loadingDialog;
    private PostCommentListRecyclerView adapter;
    private LinearLayoutManager layoutManager;

    final int loadCount = 20;
    int totalItems = 0, finished = 0, firstVisibleItem, lastCompleteVisibleItem;
    boolean isLoading = false, isLastItemAccountedFor = false;
    String lastKey, lastNode;

    public PostCommentListFragment() {
        super();
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_comment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.comment_Recycler);
        loadingDialog = new LoadingDialog(getActivity());

        ref = FirebaseDatabase.getInstance().getReference();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            getUserFromBundle();
            if (post_ID != null && currentUser != null && mUser != null) {
                getLastCommentKey();

                layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                adapter = new PostCommentListRecyclerView(getActivity());
                recyclerView.setAdapter(adapter);

                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        totalItems = layoutManager.getItemCount();
                        firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                        lastCompleteVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();

                        if (!isLoading && (lastCompleteVisibleItem <= totalItems) && totalItems >= (firstVisibleItem + loadCount)) {
                            getComments();
                            isLoading = true;
                        }
                    }
                });
            }
            else {
                // error : something went wrong.
            }
        }
    }

    private void getUserFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            post_ID = bundle.getString("post_ID");
            mUser = bundle.getParcelable("user");
        }
    }

    public void getLastCommentKey() {
        Query query = ref.child("socialComments").child(mUser.getSquad()).child(post_ID).orderByKey().limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    lastKey = ds.getKey();
                Log.d(TAG, "last key : " + lastKey);
                getComments();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getComments() {
        loadingDialog.show();
        comments = new ArrayList<>();

        if (!isLastItemAccountedFor) {
            Query commentQuery;
            if (TextUtils.isEmpty(lastNode))
                commentQuery = ref.child("socialComments").child(mUser.getSquad()).child(post_ID).orderByKey().limitToLast(loadCount);
            else
                commentQuery = ref.child("socialComments").child(mUser.getSquad()).child(post_ID).orderByKey().endAt(lastNode).limitToLast(loadCount);

            commentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        comments.clear();

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            PostComment pc = ds.getValue(PostComment.class);
                            comments.add(new PostCommentUserMap(pc));
                        }

                        if (comments.get(comments.size() - 1).getPostComment().getCommentID().equals(lastNode)) {
                            comments.remove(comments.size() - 1);
                            Log.d(TAG, "FOUND DUPLICATE");
                        }

                        lastNode = comments.get(0).getPostComment().getCommentID();
                        Log.d(TAG, "Last Node : " + lastNode);
                        if (lastNode.equals(lastKey)) {
                            isLastItemAccountedFor = true;
                            Log.d(TAG, "last database key has been retrieved");
                        }

                        Query userQuery;
                        PostCommentUserMap currentComment;
                        for (int i = 0; i < comments.size(); i++) {
                            currentComment = comments.get(i);

                            userQuery = ref.child("users").child(currentComment.getPostComment().getUserID());
                            PostCommentUserMap finalCurrentComment = currentComment;
                            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Log.d(TAG, "found USER");
                                        User user = dataSnapshot.getValue(User.class);
                                        if (user != null) {
                                            finalCurrentComment.setName(user.getName());
                                            finalCurrentComment.setUsername(user.getUsername());
                                            finalCurrentComment.setPhotoUrl(user.getPhotoUrl());

                                            finished++;
                                        }

                                        if (finished == comments.size()) {
                                            finished = 0;
                                            Collections.reverse(comments);
                                            adapter.addItems(comments);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    loadingDialog.dismiss();
                                    isLoading = false;
                                }
                            });
                        }

                    } else {
                        Log.d(TAG, "NO CHILDREN...........");
                        loadingDialog.dismiss();
                        isLoading = false;
                        isLastItemAccountedFor = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    loadingDialog.dismiss();
                    isLoading = false;
                }
            });

            isLoading = false;
            loadingDialog.dismiss();

            commentQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d(TAG, "onChildAdded: ");
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
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }
}
