package com.example.dsawyer.maddscore.Players;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Objects.UserStats;
import com.example.dsawyer.maddscore.Utils.SquadListRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SquadPlayerListFragment extends Fragment {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;

    private User user;
    private Squad mySquad;

    private ArrayList<User> userList = new ArrayList<>();

    private RecyclerView squad_list;
    private SquadListRecyclerView adapter;
    private SquadListRecyclerView.OnItemClicked listener;

    public interface OnSquadPlayerSelected {
        void onSquadPlayerSelection(User user);
    }
    OnSquadPlayerSelected onSquadPlayerSelected;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_squad_player_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        try {
            onSquadPlayerSelected = (OnSquadPlayerSelected) getActivity();
        }
        catch (Exception e){
            Log.d(TAG, "onAttach: exception " + e.getMessage());
        }
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        squad_list = view.findViewById(R.id.squad_list);

        listener = user -> onSquadPlayerSelected.onSquadPlayerSelection(user);

        getSquad();
    }

    public void getSquad() {
        Query query = ref.child("users").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    user = dataSnapshot.getValue(User.class);

                if (user != null && user.getSquad() != null)
                    getSquadList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSquadList() {
        Query query = ref.child("squads").child(user.getSquad());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mySquad = dataSnapshot.getValue(Squad.class);
                    setSquadList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setSquadList() {
        Query query = ref.child("users");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue(User.class) != null) {
                        for (String key : mySquad.getMemberList().keySet()) {
                            if (ds.getValue(User.class).getUserID().equals(key))
                                userList.add(ds.getValue(User.class));
                        }
                    }
                }

                adapter = new SquadListRecyclerView(getActivity(), listener, userList);
                squad_list.setAdapter(adapter);
                squad_list.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}