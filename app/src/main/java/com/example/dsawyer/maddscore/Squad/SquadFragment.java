package com.example.dsawyer.maddscore.Squad;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Profile.ProfileActivity;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.UserStats;
import com.example.dsawyer.maddscore.Utils.SquadMemberListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class SquadFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "TAG";

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private String UID;
    private User creator, mUser;
    private Squad mySquad;
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<UserStats> stats = new ArrayList<>();
    SquadMemberListAdapter adapter;

    private RelativeLayout relLayout, relLayout2;
    private Button join_squad, create_squad, add_squad_member;
    private ListView squad_list;
    private TextView squad_name;
    ImageView go_back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_squad, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        UID = mAuth.getCurrentUser().getUid();

        relLayout = view.findViewById(R.id.relLayout);
        relLayout2 = view.findViewById(R.id.relLayout2);
        squad_name = view.findViewById(R.id.squad_name_title);
        squad_list = view.findViewById(R.id.squad_list);
        join_squad = view.findViewById(R.id.join_squad_button);
        create_squad = view.findViewById(R.id.create_squad_button);
        add_squad_member = view.findViewById(R.id.add_squad_member_btn);
        go_back = view.findViewById(R.id.go_back);
        go_back.setOnClickListener(this);
        join_squad.setOnClickListener(this);
        create_squad.setOnClickListener(this);
        add_squad_member.setOnClickListener(this);

        getCreatorSquad();
    }

    public void getCreatorSquad(){
        Log.d(TAG, "getCreatorSquad: called");
        Query query = ref.child("users").orderByChild("userID").equalTo(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    creator = ds.getValue(User.class);
                }

                if (!creator.getMySquad().equals("none")){
                    Log.d(TAG, "user has a squad");
                    relLayout.setVisibility(View.GONE);
                    relLayout2.setVisibility(View.VISIBLE);
                    getSquadList();
                }
                else{
                    Log.d(TAG, "user DOES NOT  have a squad");
                    relLayout.setVisibility(View.VISIBLE);
                    relLayout2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSquadList() {
        Query query = ref.child("squads").child(creator.getMySquad());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    mySquad = dataSnapshot.getValue(Squad.class);
                    getPlayerStats();
                }
                else{
                    //handle
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getPlayerStats(){
        Query query = ref.child("userStats");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stats.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                     for (String key : mySquad.getUserList().keySet()){
                         if (ds.getKey().equals(key)){
                             stats.add(ds.getValue(UserStats.class));
                         }
                     }
                }
                setSquadList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setSquadList(){
        squad_name.setText(mySquad.getSquadName());

        Query query = ref.child("users").orderByChild("userID");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    for (String key : mySquad.getUserList().keySet()) {
                        if (ds.getValue(User.class).getUserID().equals(key)){
                            userList.add(mUser);
                        }
                    }
                }
                Collections.sort(stats);

                adapter = new SquadMemberListAdapter(getActivity(), userList, stats);
                squad_list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.join_squad_button):
                    Intent intent = new Intent(getActivity(), JoinSquadActivity.class);
                    startActivity(intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    break;
            case(R.id.create_squad_button):
//                ((ProfileActivity)getActivity()).setFragment(new SquadNameCreateFragment());
                    break;
            case(R.id.add_squad_member_btn):
                ((ProfileActivity)getActivity()).setFragment(new SquadMemberSearchFragment());
//                Intent intent1 = new Intent(getActivity(), AddSquadMemberActivity.class);
//                startActivity(intent1);
//                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;
            case(R.id.go_back):
                Intent intent1 = new Intent(getActivity(), ProfileActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                break;
        }
    }
}
