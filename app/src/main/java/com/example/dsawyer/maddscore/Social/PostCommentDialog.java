package com.example.dsawyer.maddscore.Social;

import android.content.Context;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dsawyer.maddscore.ObjectMaps.PostUserMap;
import com.example.dsawyer.maddscore.Objects.Post;
import com.example.dsawyer.maddscore.Objects.PostComment;
import com.example.dsawyer.maddscore.ObjectMaps.PostCommentUserMap;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Squad.SocialFragment;
import com.example.dsawyer.maddscore.Utils.LoadingDialog;
import com.example.dsawyer.maddscore.Utils.PostCommentListRecyclerView;
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
import java.util.Map;

public class PostCommentDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private EditText comment_text;
    private ImageView comment_cancel, comment_send;

    private User mUser;
    private PostUserMap postMap;
    private RecyclerView recyclerView;
    private ArrayList<PostCommentUserMap> comments;

    private LoadingDialog loadingDialog;
    private PostCommentListRecyclerView adapter;
    private LinearLayoutManager layoutManager;

    final int loadCount = 21;
    int totalItems = 0, finished = 0, lastVisibleItem;
    boolean isLoading = false, isLastItemReached = false;
    String lastKey, lastNode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_post_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.comment_Recycler);
        comment_cancel = view.findViewById(R.id.comment_cancel);
        comment_text = view.findViewById(R.id.comment_text);
        comment_send = view.findViewById(R.id.comment_send);

        loadingDialog = new LoadingDialog(getActivity());

        comment_send.setOnClickListener(this);
        comment_cancel.setOnClickListener(this);

        Bundle args = this.getArguments();
        if (args != null) {
            postMap = args.getParcelable("postMap");
            mUser = args.getParcelable("user");
        }

        if (postMap != null && mUser != null) {
            getLastCommentKey();

            layoutManager = new LinearLayoutManager(getActivity());
//            layoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new PostCommentListRecyclerView(getActivity());
            recyclerView.setAdapter(adapter);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    totalItems = layoutManager.getItemCount();
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                    if (!isLoading && !isLastItemReached && (lastVisibleItem + 1) >= totalItems) {
                        isLoading = true;
                        getComments();
                    }
                }
            });
        }
        else {
            // error : something went wrong.
        }
    }

    private void publishComment() {
        Log.d(TAG, "publishComment: called");
        loadingDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String comment = comment_text.getText().toString().trim();
        if (!comment.isEmpty()) {
            long date = System.currentTimeMillis();

            String commentId = ref.child("socialComments").child(mUser.getSquad()).child(postMap.getPost().getPostID()).push().getKey();

            PostComment postComment = new PostComment(
                    mUser.getUserID(),
                    postMap.getPost().getPostID(),
                    commentId,
                    comment,
                    date
            );

            if (commentId != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("/socialPosts/" + mUser.getSquad() + "/" + postMap.getPost().getPostID() + "/comments/" + commentId, true);
                map.put("/socialComments/" + mUser.getSquad() + "/" + postMap.getPost().getPostID() + "/" + commentId, postComment);

                ref.updateChildren(map).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        if (adapter != null) {
                            adapter.addItem(new PostCommentUserMap(postComment, mUser));
                            adapter.notifyDataSetChanged();
                        }
                        comment_text.getText().clear();
                        loadingDialog.dismiss();
                    }
                    else {
                        loadingDialog.dismiss();
                        Toast.makeText(getActivity(), "Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        } else {
            loadingDialog.dismiss();
            Toast.makeText(getActivity(), "Nothing to post.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLastCommentKey() {
        Log.d(TAG, "getLastCommentKey: called");
        isLoading = true;
        loadingDialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("socialComments")
                .child(mUser.getSquad())
                .child(postMap.getPost().getPostID());

        Query query = ref.orderByKey().limitToFirst(2);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    Log.i(TAG, "onDataChange: node not found");

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    lastKey = ds.getKey();
                    Log.d(TAG, "last key : " + lastKey);
                    break;
                }

                if (lastKey != null)
                    getComments();
                else {
                    isLoading = false;
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });
    }

    private void getComments() {
        Log.d(TAG, "getComments: called");
        loadingDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        comments = new ArrayList<>();

        if (!isLastItemReached) {
            Query commentQuery;
            if (TextUtils.isEmpty(lastNode))
                commentQuery = ref.child("socialComments")
                        .child(mUser.getSquad())
                        .child(postMap.getPost().getPostID())
                        .orderByKey()
                        .limitToLast(loadCount);
            else
                commentQuery = ref.child("socialComments")
                        .child(mUser.getSquad())
                        .child(postMap.getPost().getPostID())
                        .orderByKey()
                        .endAt(lastNode)
                        .limitToLast(loadCount);

            commentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "child count : " + dataSnapshot.getChildrenCount());
                    if (dataSnapshot.hasChildren()) {
                        comments.clear();

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.d(TAG, "RETRIEVED KEY : " + ds.getKey());
                            PostComment pc = ds.getValue(PostComment.class);

                            if (pc != null)
                                comments.add(new PostCommentUserMap(pc));
                        }

                        if (comments.get(comments.size() - 1).getPostComment().getCommentID().equals(lastNode)) {
                            comments.remove(comments.size() - 1);
                            Log.d(TAG, "FOUND DUPLICATE");
                        }

                        if (!comments.isEmpty()) {
                            lastNode = comments.get(0).getPostComment().getCommentID();
                            Log.d(TAG, "Last Node : " + lastNode);
                            if (lastNode.equals(lastKey)) {
                                isLastItemReached = true;
                                Log.d(TAG, "last database key has been retrieved");
                            }

                            DatabaseReference userRef;

                            for (PostCommentUserMap userMap : comments) {
                                userRef = FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(userMap.getPostComment().getUserID());

                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        finished++;

                                        if (dataSnapshot.exists()) {
                                            User user = dataSnapshot.getValue(User.class);
                                            if (user != null) {
                                                userMap.setName(user.getName());
                                                userMap.setUsername(user.getUsername());
                                                userMap.setPhotoUrl(user.getPhotoUrl());
                                            }

                                            if (finished == comments.size()) {
                                                finished = 0;
                                                Collections.reverse(comments);
                                                adapter.addItems(comments);
                                                loadingDialog.dismiss();
                                                isLoading = false;
                                            }
                                        } else {
                                            loadingDialog.dismiss();
                                            isLoading = false;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        loadingDialog.dismiss();
                                        isLoading = false; }
                                });
                            }
                        }

                        loadingDialog.dismiss();
                        isLoading = false;
                    } else {
                        Log.d(TAG, "No children");
                        loadingDialog.dismiss();
                        isLoading = false;
                        isLastItemReached = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    isLoading = false;
                    loadingDialog.dismiss();
                }
            });

            commentQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d(TAG, "onChildAdded: ");
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d(TAG, "postComment dialog : onChildChanged: ");
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "postComment dialog : onChildRemoved: ");
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d(TAG, "postComment dialog : onChildMoved: ");
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "postComment dialog : onCancelled: ");
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }
            });

        } else {
            isLoading = false;
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog().getWindow() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            getDialog().getWindow().setAttributes(params);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.comment_cancel):
                getDialog().dismiss();
                break;
            case(R.id.comment_send):
                publishComment();
                break;
        }
    }
}
