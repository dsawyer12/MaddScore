package com.example.dsawyer.maddscore.Squad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.PostComment;
import com.example.dsawyer.maddscore.Utils.PostCommentListRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostCommentListFragment extends DialogFragment{
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;
    private User mUser;
    private String post_ID;
    private RecyclerView recyclerView;
    private ArrayList<PostComment> comments;

    public PostCommentListFragment(){
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
        ref = FirebaseDatabase.getInstance().getReference();
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mUser = getUserFromBundle();
        comments = new ArrayList<>();

        recyclerView = view.findViewById(R.id.comment_Recycler);
        getComments();
    }

    private User getUserFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null){
            post_ID = bundle.getString("post_ID");
            return bundle.getParcelable("user");
        }
        else{
            return null;
        }
    }

    public void getComments(){
        Query query = ref.child("socialComments").child(mUser.getMySquad()).child(post_ID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    PostComment pc = ds.getValue(PostComment.class);
                    comments.add(pc);
                    Log.d(TAG, "Comment Added");
                }
                PostCommentListRecyclerView adapter = new PostCommentListRecyclerView(getActivity(), comments, mUser);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
