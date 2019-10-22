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
import android.widget.ListView;
import android.widget.RelativeLayout;

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

public class SquadListFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "TAG";

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private String UID;
    private User creator, mUser;
    private Squad mySquad;
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<String> keys = new ArrayList<>();
    SquadMemberListAdapter adapter;

    private RelativeLayout relLayout, relLayout2;
    private Button join_squad, create_squad;
    private ListView squad_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_squad_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        UID = mAuth.getCurrentUser().getUid();

        relLayout = view.findViewById(R.id.relLayout);
        relLayout2 = view.findViewById(R.id.relLayout2);
        squad_list = view.findViewById(R.id.squad_list);
        join_squad = view.findViewById(R.id.join_squad_button);
        create_squad = view.findViewById(R.id.create_squad_button);
        join_squad.setOnClickListener(this);
        create_squad.setOnClickListener(this);

        getCreatorSquad();
    }

    public void getCreatorSquad(){
        Query query = ref.child("users").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue(User.class).getMySquad() != null){
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
                    setSquadList(mySquad);
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

    public void setSquadList(final Squad mySquad){
        userList.clear();

        Query query = ref.child("users").orderByChild("userID");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    mUser = ds.getValue(User.class);

                    for (String key : mySquad.getUserList().keySet()) {
                        Log.d(TAG, "key : " + key);
                        if (mUser.getUserID().equals(key)){
                            Log.d(TAG, "EQUALS");
                            userList.add(mUser);
                        }
                    }
                }
               getUserStats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getUserStats(){
        final ArrayList<UserStats> stats = new ArrayList<>();
        stats.clear();

        Query query = ref.child("userStats");
       query.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot ds : dataSnapshot.getChildren()){
                   for (int i = 0; i < userList.size(); i++){
                       if (ds.getValue(UserStats.class).getUserID().equals(userList.get(i).getUserID())){
                           stats.add(ds.getValue(UserStats.class));
                       }
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

        Collections.sort(stats);
        adapter = new SquadMemberListAdapter(getActivity(), userList, stats);
        squad_list.setAdapter(adapter);
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
        }
    }
}
