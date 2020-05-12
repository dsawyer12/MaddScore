package com.example.dsawyer.maddscore.Players;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.PlayerListRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlayerListFragment extends Fragment {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private User mUser;
    private String UID;

    ArrayList<User> cardUsers;
    ArrayList<User> listOfUsers;
    ArrayList<String> keys;
    RecyclerView playerList;

    PlayerListRecyclerView.OnItemClicked listener;

    TextView noPlayers;

    public interface OnPlayerSelectedListener {
        void onPlayerSelected(User user);
    }
    OnPlayerSelectedListener playerSelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        try{
            playerSelected = (OnPlayerSelectedListener) getActivity();
        }
        catch(ClassCastException e){
            e.getMessage();
        }
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        noPlayers = view.findViewById(R.id.no_players);
        playerList = view.findViewById(R.id.player_list);

        cardUsers = new ArrayList<>();
        listOfUsers = new ArrayList<>();
        keys = new ArrayList<>();

        getUserFriendsList();

        listener = new PlayerListRecyclerView.OnItemClicked() {
            @Override
            public void onClick(User user) {
                playerSelected.onPlayerSelected(user);
            }
        };
    }

    private void getUserFriendsList() {
        Query query = ref.child("friendsList").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    keys.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        keys.add(ds.getKey());
                    }
                    getUsers();
                }
                else{
                    noPlayers.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUsers() {
        Query query = ref.child("users");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listOfUsers.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    mUser = ds.getValue(User.class);
                    if (mUser != null) {
                        for (int i = 0; i < keys.size(); i++) {
                            if (mUser.getUserID().equals(keys.get(i)))
                                listOfUsers.add(mUser);
                        }
                    }
                }
                if (!listOfUsers.isEmpty()) {
                    PlayerListRecyclerView adapter = new PlayerListRecyclerView(getActivity(), listener, listOfUsers);
                    playerList.setAdapter(adapter);
                    playerList.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
                else
                    noPlayers.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
