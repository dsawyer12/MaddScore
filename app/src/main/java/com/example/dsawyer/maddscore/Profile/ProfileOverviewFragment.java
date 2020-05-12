package com.example.dsawyer.maddscore.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.UselessButGoodInfo.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileOverviewFragment extends Fragment {

    private static final String TAG = "TAG";

    FirebaseAuth mAuth;
    private DatabaseReference ref;
    private String UID;
    private User user;
    private Squad squad;
    private CircleImageView profileImage;
    private ImageView squad_rank_img;
    private AppBarLayout appBar;
    private ImageView go_back;

    private TextView username, rounds, squad_name, members, current_rank, best_score, course_name, date;
    private RelativeLayout relLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference();

        go_back = view.findViewById(R.id.overview_go_back);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStackImmediate();
            }
        });

        relLayout = view.findViewById(R.id.relLayout);
        appBar = view.findViewById(R.id.appBar);

        if (getArgsFromBundle() != 0){
            appBar.setVisibility(View.VISIBLE);
        }

        profileImage = view.findViewById(R.id.profileImage);
        username = view.findViewById(R.id.username);
        rounds = view.findViewById(R.id.rounds);
        squad_name = view.findViewById(R.id.squad_name);
        members = view.findViewById(R.id.members);
        current_rank = view.findViewById(R.id.current_rank);
        squad_rank_img = view.findViewById(R.id.squad_rank_img);
        best_score = view.findViewById(R.id.best_score);
        course_name = view.findViewById(R.id.course_name);
        date = view.findViewById(R.id.date);

        initializeUserData();
        initImageLoader();
        setProfileImage();

    }

    private void initializeUserData() {
        Log.d(TAG, "initializeUserData: called");
        Query query = ref.child("users").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getKey().equals(UID)){
                    user = dataSnapshot.getValue(User.class);
                    setUpUserProfile(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setUpUserProfile(User user){
        Log.d(TAG, "setUpUserProfile: called");
        if (user.getPhotoUrl() != null){
            Glide.with(this).load(user.getPhotoUrl()).into(profileImage);
        }
        else{
            Glide.with(this).load(R.mipmap.default_profile_img).into(profileImage);
        }
        username.setText(user.getUsername());
//        rounds.setText(Integer.toString(user.getNumRounds()) + " Rounds Played");
//        best_score.setText(Integer.toString(user.getBestScore()));
        if(user.getSquad() != null) {
            relLayout.setVisibility(View.VISIBLE);
            initializeSquadData();
        }
        else{
            relLayout.setVisibility(View.GONE);
        }
    }

    private void initializeSquadData() {
        Log.d(TAG, "initializeSquadData: called");
        Query query = ref.child("squads").child(user.getSquad());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getKey().equals(user.getSquad())){
                    squad = dataSnapshot.getValue(Squad.class);
                    setUpUserSquad(squad);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setUpUserSquad(Squad squad){
        squad_name.setText(squad.getSquadName());
//        current_rank.setText("# " + Integer.toString(user.getSquadRank()));
//        members.setText(Integer.toString(squad.getUserRankList().size()));
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setProfileImage(){
        String imageURL = "https://www.springboard.com/images/springboard/default-profile-mentor-rounded@2x.70dc0c67.png";
        UniversalImageLoader.setImage(imageURL, profileImage, null, "");
    }

    private int getArgsFromBundle(){
        Bundle bundle = this.getArguments();
        if (bundle != null){
            return bundle.getInt("ACTIVITY_NUM");
        }
        else{
            return 0;
        }
    }
}




