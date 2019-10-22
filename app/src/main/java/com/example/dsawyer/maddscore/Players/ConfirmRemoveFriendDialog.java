package com.example.dsawyer.maddscore.Players;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfirmRemoveFriendDialog extends BottomSheetDialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;
    private User mUser;

    Button confirm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm_friend_remove, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mUser = getUserFromBundle();

        confirm = view.findViewById(R.id.confirm_remove);

        confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case (R.id.confirm_remove):
                if (mUser != null) {
                        Log.d(TAG, "onClick: confirm remove");
                        ref.child("friendsList")
                                .child(UID)
                                .child(mUser.getUserID())
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: user removed from current users friends list");
                                    ref.child("friendsList")
                                            .child(mUser.getUserID())
                                            .child(UID)
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Player Removed", Toast.LENGTH_SHORT).show();
                                                confirm.setEnabled(false);
                                                exit();
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    if (!mUser.isRegistered() && mUser.getCreator().equals(UID)) {
                            ref.child("users")
                                    .child(mUser.getUserID())
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                confirm.setEnabled(false);
                                                exit();
                                            }
                                        }
                                    });
                    }
                }
                break;
        }
    }

    private User getUserFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null){
            return bundle.getParcelable("user");
        }
        else{
            return null;
        }
    }

    public void exit() {
        Intent intent = new Intent(getActivity(), PlayersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
