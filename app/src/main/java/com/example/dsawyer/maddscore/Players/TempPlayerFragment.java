package com.example.dsawyer.maddscore.Players;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class TempPlayerFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;
    private User tempPlayer;

    private TextView username, name;
    private Button link_player, delete_player;
    private ImageView go_back;
    private CircleImageView playerImage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_temp_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        tempPlayer = getPlayerFromBundle();

        username = view.findViewById(R.id.player_userName);
        name = view.findViewById(R.id.player_name);
        playerImage = view.findViewById(R.id.profileImage);
        link_player = view.findViewById(R.id.link_player_btn);
        delete_player = view.findViewById(R.id.delete_friend_btn);
        go_back = view.findViewById(R.id.go_back);

        go_back.setOnClickListener(this);
        link_player.setOnClickListener(this);
        delete_player.setOnClickListener(this);

        if (tempPlayer != null){
            getPlayerCreator();
            if (tempPlayer.getPhotoUrl() != null){
                Glide.with(this).load(tempPlayer.getPhotoUrl()).into(playerImage);
            }
            else{
                Glide.with(this).load(R.mipmap.default_profile_img).into(playerImage);
            }
            username.setText(tempPlayer.getUsername());
            name.setText(tempPlayer.getName());
        }
    }

    private User getPlayerFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null){
            return bundle.getParcelable("tempUser");
        }
        else{
            return null;
        }
    }

    private void getPlayerCreator() {
        if (UID != null){
            if (UID.equals(tempPlayer.getCreator())){
                link_player.setEnabled(true);
                delete_player.setEnabled(true);
            }
            else{
                link_player.setEnabled(false);
                delete_player.setEnabled(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.go_back):
                if (getActivity() != null){
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
                break;

            case (R.id.link_player_btn):

                break;

            case (R.id.delete_friend_btn):
                ConfirmRemoveFriendDialog confirmRemove = new ConfirmRemoveFriendDialog();
                Bundle args = new Bundle();
                if (tempPlayer != null){
                    args.putParcelable("user", tempPlayer);
                    confirmRemove.setArguments(args);
                    confirmRemove.setTargetFragment(this, 121);
                    if (getFragmentManager() != null) {
                        confirmRemove.show(getFragmentManager(), "confirmRemove");
                    }
                }
                break;
        }
    }
}
