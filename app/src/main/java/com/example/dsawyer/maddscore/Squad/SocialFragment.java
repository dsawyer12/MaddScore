package com.example.dsawyer.maddscore.Squad;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Post;
import com.example.dsawyer.maddscore.Objects.PostComment;
import com.example.dsawyer.maddscore.Social.PostCommentDialog;
import com.example.dsawyer.maddscore.Social.PostFragment;
import com.example.dsawyer.maddscore.Utils.SocialPostRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SocialFragment extends Fragment {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;
    private User mUser;

    private Context context;
    private ArrayList<Post> posts;
    private RelativeLayout relativeLayout;
    private RecyclerView recyclerView;
    private SocialPostRecyclerView adapter;
    private PostCommentDialog.OnCommentLinstener commentListener;
    private SocialPostRecyclerView.OnItemClickListener listener;
    private LinearLayoutManager manager;

    private TextView no_activity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        ref = FirebaseDatabase.getInstance().getReference();
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        relativeLayout = view.findViewById(R.id.relLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        no_activity = view.findViewById(R.id.no_activty);

        posts = new ArrayList<>();

        getUser();
        
        final FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SquadActivity) Objects.requireNonNull(getActivity())).setFragment(new PostFragment());
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0){
                    fab.hide();
                }
                else {
                    fab.show();
                }
            }
        });

        listener = new SocialPostRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClicked(View v, int position) {
                switch (v.getId()) {
                    case (R.id.post_liked):
                        if (posts.get(position).getUserlist() != null) {
                            if (posts.get(position).getUserlist().containsKey(mUser.getUserID())) {
                                Log.d(TAG, "user like removed");
                                posts.get(position).getUserlist().remove(mUser.getUserID());
                                ref.child("socialPosts").child(mUser.getMySquad()).child(posts.get(position).getPostKey()).child("userlist").child(mUser.getUserID()).removeValue();
//                                adapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "user like added");
                                posts.get(position).getUserlist().put(mUser.getUserID(), true);
                                ref.child("socialPosts").child(mUser.getMySquad()).child(posts.get(position).getPostKey()).child("userlist").child(mUser.getUserID()).setValue(true);
//                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d(TAG, "new userlist added");
                            HashMap<String, Boolean> userList = new HashMap<>();
                            userList.put(mUser.getUserID(), true);
                            posts.get(position).setUserlist(userList);
                            ref.child("socialPosts").child(mUser.getMySquad()).child(posts.get(position).getPostKey()).child("userlist").child(mUser.getUserID()).setValue(true);
//                            adapter.notifyDataSetChanged();
                        }
                        break;

                    case (R.id.post_comment):
                        PostCommentDialog pc = new PostCommentDialog(commentListener, mUser, position);
                        String postKey = posts.get(position).getPostKey();
                        Bundle args = new Bundle();
                        args.putString("post_key", postKey);
                        pc.setArguments(args);
                        pc.setTargetFragment(SocialFragment.this, 1001);
                        pc.show(getFragmentManager(), "courseSelector");
//                        adapter.notifyDataSetChanged();
                        break;
                    case (R.id.post_comments):
                        PostCommentListFragment pcl = new PostCommentListFragment();
                        String postID = posts.get(position).getPostKey();
                        Bundle arg = new Bundle();
                        arg.putString("post_ID", postID);
                        arg.putParcelable("user", mUser);
                        pcl.setArguments(arg);
                        pcl.setTargetFragment(SocialFragment.this, 1002);
                        pcl.show(getFragmentManager(), "commentList");
//                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };

        commentListener = new PostCommentDialog.OnCommentLinstener() {
            @Override
            public void postComment(PostComment postComment, int position) {

                String commentID = ref.child("socialComments").child(mUser.getMySquad()).child(postComment.getPostID()).push().getKey();
                postComment.setCommentID(commentID);
                if (posts.get(position).getComments() != null){
                    posts.get(position).addComment(postComment.getCommentID(), true);
                }
                else{
                    HashMap<String, Boolean> hashMap = new HashMap<>();
                    hashMap.put(postComment.getCommentID(), true);
                    posts.get(position).setComments(hashMap);
                }
                ref.child("socialComments").child(mUser.getMySquad()).child(postComment.getPostID()).child(postComment.getCommentID()).setValue(postComment);
                ref.child("socialPosts").child(mUser.getMySquad()).child(posts.get(position).getPostKey()).child("comments").child(postComment.getCommentID()).setValue(true);
            }
        };
    }

    public void getUser(){
        Query query = ref.child("users").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.equals(dataSnapshot.getKey(), UID)){
                    mUser = dataSnapshot.getValue(User.class);
                    getSocial();
                }
                else{
                    relativeLayout.setVisibility(View.VISIBLE);
                    //handle
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getSocial() {
        Query query = ref.child("socialPosts").child(mUser.getMySquad());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");
                posts.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    posts.add(post);
                    Log.d(TAG, "post added");
                }
                if (posts.isEmpty()) {
                   no_activity.setVisibility(View.VISIBLE);
                   recyclerView.setVisibility(View.GONE);
                }
                else{
                   no_activity.setVisibility(View.GONE);
                   recyclerView.setVisibility(View.VISIBLE);
                }

                adapter = new SocialPostRecyclerView(context, listener, posts, mUser.getUserID());
                recyclerView.setAdapter(adapter);
                manager  = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
                manager.setStackFromEnd(true);
                manager.setReverseLayout(true);
                recyclerView.setLayoutManager(manager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        
        query.addChildEventListener(new ChildEventListener() {
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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
