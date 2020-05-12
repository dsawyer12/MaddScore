package com.example.dsawyer.maddscore.ScoreCards;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Players.CustomPlayerFragment;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.NewCardPlayerListAdapter;
import com.example.dsawyer.maddscore.Utils.PlayerListRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ScorecardPlayerListFragment extends Fragment {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private User mUser;
    private  String UID;

    ListView playerList;

    ArrayList<User> cardUsers;
    ArrayList<User> listOfUsers;
    ArrayList<String> keys;

    NewCardPlayerListAdapter adapter;

    private TextView noPlayers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scorecard_player_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        noPlayers = view.findViewById(R.id.no_players_text);
        playerList = view.findViewById(R.id.player_list);

        cardUsers = new ArrayList<>();
        listOfUsers = new ArrayList<>();
        keys = new ArrayList<>();

        getActivity().getIntent().putParcelableArrayListExtra("users", cardUsers);

        getUserIds();

        playerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SparseBooleanArray sparseBooleanArray = playerList.getCheckedItemPositions();
                if (sparseBooleanArray.get(position)) {
                    cardUsers.add(listOfUsers.get(position));
                    view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryTransparent));
                }
                else {
                    cardUsers.remove(listOfUsers.get(position));
                    view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.PrimaryBackground));
                }
                getActivity().getIntent().putExtra("users", cardUsers);
            }
        });
    }

    public void getUserIds() {
        Query query = ref.child("friendsList").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keys.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    keys.add(ds.getKey());
                }
                getFriends();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getFriends() {
        listOfUsers.clear();
        Query query = ref.child("users").orderByChild("userID");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    mUser = ds.getValue(User.class);

                    if (mUser != null && mUser.getUserID().equals(UID))
                        listOfUsers.add(mUser);

                    for (int i = 0; i < keys.size(); i++) {
                        if (mUser != null && mUser.getUserID().equals(keys.get(i)))
                            listOfUsers.add(mUser);
                    }
                }

                if (!listOfUsers.isEmpty()) {
                    adapter = new NewCardPlayerListAdapter(getActivity(), listOfUsers);
                    playerList.setAdapter(adapter);
                    playerList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                }
                else{
                    noPlayers.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "onChildAdded: " + user.getName());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
