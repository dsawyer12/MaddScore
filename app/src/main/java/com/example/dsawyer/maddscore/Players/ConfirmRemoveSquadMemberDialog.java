package com.example.dsawyer.maddscore.Players;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
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

public class ConfirmRemoveSquadMemberDialog extends BottomSheetDialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;
    private User mUser;

    Button confirm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm_remove_squad_member, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        mUser = getUserFromBundle();

        confirm = view.findViewById(R.id.confirm_remove);
        confirm.setOnClickListener(this);
    }

    private User getUserFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null)
            return bundle.getParcelable("user");
        else
            return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case(R.id.confirm_remove):
                if (mUser != null) {
                    // along with the code below, send the user a notification that they have been removed from the squad
                    ref.child("squads")
                            .child(mUser.getMySquad())
                            .child("userList")
                            .child(mUser.getUserID())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: remove mUser from squad successful");
                                ref.child("users")
                                        .child(mUser.getUserID())
                                        .child("mySquad")
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "onComplete: users mySquad node removed");
                                            ref.child("userStats").child(mUser.getUserID()).child("squadRank").setValue(0)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(getContext(), "Player Removed", Toast.LENGTH_SHORT).show();
                                                                exit();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                break;
        }
    }

    public void exit() {
        Intent intent = new Intent(getActivity(), PlayersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
