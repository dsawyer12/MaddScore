package com.example.dsawyer.maddscore.Social;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.Post;
import com.example.dsawyer.maddscore.Objects.PostComment;
import com.example.dsawyer.maddscore.Objects.PostCommentUserMap;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Squad.SocialFragment;
import com.example.dsawyer.maddscore.Utils.LoadingDialog;
import com.example.dsawyer.maddscore.Utils.PostCommentListRecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PostCommentDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private EditText comment_text;
    private ImageView comment_cancel, comment_send;

    private User mUser;
    private Post post;
    private RecyclerView recyclerView;
    private ArrayList<PostCommentUserMap> comments;
    private LoadingDialog loadingDialog;
    private PostCommentListRecyclerView adapter;
    private LinearLayoutManager layoutManager;

    final int loadCount = 20;
    int totalItems = 0, finished = 0, firstVisibleItem, lastCompleteVisibleItem;
    boolean isLoading = false, isLastItemReached = false;
    String lastKey, lastNode;

    private OnPostChangeListener listener;
    public interface OnPostChangeListener {
        void onCommentAdded();
    }

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
            post = args.getParcelable("post");
            mUser = args.getParcelable("user");
        }

        if (post != null && mUser != null) {
            getLastCommentKey();

            layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new PostCommentListRecyclerView(getActivity());
            recyclerView.setAdapter(adapter);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    totalItems = layoutManager.getItemCount();
                    firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    lastCompleteVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();

                    Log.d(TAG, "first visible item: " + firstVisibleItem);
                    Log.d(TAG, "last visible item: " + lastVisibleItem);
                    Log.d(TAG, "total: " + totalItems);

                    if (!isLoading && firstVisibleItem == 0) {
                        getComments();
                        isLoading = true;
                    }

//                    if (!isLoading && (lastCompleteVisibleItem <= totalItems) && totalItems >= (firstVisibleItem + loadCount)) {
//                        getComments();
//                        isLoading = true;
//                    }
                }
            });
        }
        else {
            // error : something went wrong.
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SocialFragment) getTargetFragment();
        } catch (Exception e) {
            Log.d(TAG, "onAttach: " + e.getMessage());
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

    private void publishComment() {
        loadingDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String comment = comment_text.getText().toString().trim();
        if (!comment.isEmpty()) {
            long date = System.currentTimeMillis();

            String commentId = ref.child("socialComments").child(mUser.getSquad()).child(post.getPostID()).push().getKey();

            PostComment postComment = new PostComment(
                    mUser.getUserID(),
                    post.getPostID(),
                    commentId,
                    comment,
                    date
            );

            if (commentId != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("/socialComments/" + mUser.getSquad() + "/" + post.getPostID() + "/" + commentId, postComment);
                map.put("/socialPosts/" + mUser.getSquad() + "/" + post.getPostID() + "/comments/" + commentId, true);

                ref.updateChildren(map).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        PostCommentUserMap userMap = new PostCommentUserMap(postComment, mUser);
                        adapter.addItem(userMap);
                        listener.onCommentAdded();
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
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
        loadingDialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("socialComments")
                .child(mUser.getSquad())
                .child(post.getPostID());

        Query query = ref.orderByKey().limitToLast(2);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    lastKey = ds.getKey();
                    break;
                }
                Log.d(TAG, "last key : " + lastKey);
                getComments();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        .child(post.getPostID())
                        .orderByChild("commentID")
                        .limitToLast(loadCount);
            else
                commentQuery = ref.child("socialComments")
                        .child(mUser.getSquad())
                        .child(post.getPostID())
                        .orderByChild("commentID")
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
                                            User user = dataSnapshot.getValue(User.class);
                                            if (user != null) {
                                                finalCurrentComment.setName(user.getName());
                                                finalCurrentComment.setUsername(user.getUsername());
                                                finalCurrentComment.setPhotoUrl(user.getPhotoUrl());

                                                finished++;
                                            }

                                            if (finished == comments.size()) {
                                                finished = 0;
//                                                Collections.reverse(comments);
                                                adapter.addItems(comments);
                                                isLoading = false;
                                                loadingDialog.dismiss();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        isLoading = false;
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        }
                    } else {
                        Log.d(TAG, "No children");
                        isLoading = false;
                        loadingDialog.dismiss();
                        isLastItemReached = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    isLoading = false;
                    loadingDialog.dismiss();
                }
            });

//            commentQuery.addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                    Log.d(TAG, "onChildAdded: ");
//                    if (adapter != null)
//                        adapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                    Log.d(TAG, "onChildChanged: ");
//                    if (adapter != null)
//                        adapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                    Log.d(TAG, "onChildRemoved: ");
//                    if (adapter != null)
//                        adapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                    Log.d(TAG, "onChildMoved: ");
//                    if (adapter != null)
//                        adapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Log.d(TAG, "onCancelled: ");
//                    if (adapter != null)
//                        adapter.notifyDataSetChanged();
//                }
//            });

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
}
