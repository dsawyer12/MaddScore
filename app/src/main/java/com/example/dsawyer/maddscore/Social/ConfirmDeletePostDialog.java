package com.example.dsawyer.maddscore.Social;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.dsawyer.maddscore.ObjectMaps.PostUserMap;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfirmDeletePostDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private User mUser;
    private PostUserMap postMap;

    private Button cancel, delete;

    OnPostDeletedListener listener;
    public interface OnPostDeletedListener {
        void onDelete(PostUserMap post_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm_delete_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cancel = view.findViewById(R.id.cancel);
        delete = view.findViewById(R.id.delete);
        cancel.setOnClickListener(this);
        delete.setOnClickListener(this);

        if (this.getArguments() != null) {
            mUser = this.getArguments().getParcelable("mUser");
            postMap = this.getArguments().getParcelable("postMap");
            if (postMap != null && mUser != null)
                delete.setEnabled(true);
            else Toast.makeText(getActivity(), "Something went wrong trying to retrieve your data.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnPostDeletedListener) getTargetFragment();
        } catch (ClassCastException e) {
                Log.d(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.cancel):
                getDialog().dismiss();
                break;
            case(R.id.delete):
                deletePost();
                break;
        }
    }

    public void deletePost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("socialPosts").child(mUser.getSquad()).child(postMap.getPost().getPostID()).removeValue().addOnCompleteListener(task ->  {
           if (task.isSuccessful()) {
               ref.child("socialComments").child(mUser.getSquad()).child(postMap.getPost().getPostID()).removeValue().addOnCompleteListener(task1 -> {
                   if (task1.isSuccessful()) {
                       Toast.makeText(getActivity(), "deleted", Toast.LENGTH_SHORT).show();
                       listener.onDelete(postMap);
                       getDialog().dismiss();
                   }
               });
           } else Toast.makeText(getActivity(), "An error occurred.", Toast.LENGTH_SHORT).show();
        });
    }
}



















