package com.example.dsawyer.maddscore.Social;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.dsawyer.maddscore.Objects.PostComment;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;

import java.text.SimpleDateFormat;

public class PostCommentDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    EditText comment_text;
    Button comment_send;
    ImageView comment_cancel;
//    private String post_id;
    private int position;
    private PostComment postComment;
    private User user;
    private OnCommentLinstener listener;


    public interface OnCommentLinstener{
        void postComment(PostComment postComment, int position);
    }

    public PostCommentDialog(){
        super();
        setArguments(new Bundle());
    }

    @SuppressLint("ValidFragment")
    public PostCommentDialog(OnCommentLinstener listener, User user, int position){
        super();
        setArguments(new Bundle());
        this.listener = listener;
        this.user = user;
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        long date = System.currentTimeMillis();

        postComment = new PostComment();
        postComment.setPostID(this.getArguments().getString("post_key"));
        postComment.setCommentDate(date);
        postComment.setName(user.getName());
        postComment.setPhotoID(user.getPhotoUrl());
        postComment.setUserID(user.getUserID());
        postComment.setUsername(user.getUsername());


//        post_id = this.getArguments().getString("post_key");
//        Log.d(TAG, "_key: " + post_id);
        comment_cancel = view.findViewById(R.id.comment_cancel);
        comment_text = view.findViewById(R.id.comment_text);
        comment_send = view.findViewById(R.id.comment_send);
        comment_send.setOnClickListener(this);
        comment_cancel.setOnClickListener(this);

        comment_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() == 0){
                    comment_send.setEnabled(false);
                } else {
                    comment_send.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.comment_cancel):
                getDialog().dismiss();
                break;
            case(R.id.comment_send):
                String comment = comment_text.getText().toString();
                postComment.setCommentBody(comment);
                listener.postComment(postComment, position);
                getDialog().dismiss();
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (OnCommentLinstener) getActivity();
        }
        catch (Exception e){
            Log.d(TAG, "onAttach: EXCEPTION " + e.getMessage());
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
