package com.example.dsawyer.maddscore.ScoreCards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.Player;
import com.example.dsawyer.maddscore.Objects.Scorecard;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.PlayerListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CardAddPlayerListFragment extends Fragment {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;
    private User currentUser;
    private User mUser;
    private Scorecard mCard;

    private PlayerListAdapter adapter;
    private TextView noPlayers;
    ListView playerList;

    public interface OnNewPlayersSorted {
        void onSort(ArrayList<User> sortedPlayers);
    }
    OnNewPlayersSorted listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_add_player_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerList = view.findViewById(R.id.player_list);
        noPlayers = view.findViewById(R.id.no_players_text);

        mCard = getCardFromBundle();

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            getCurrentUser();
        }

    }

    private void getCurrentUser() {
        Query query = ref.child("users").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUser = dataSnapshot.getValue(User.class);
                    getUserIds();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getUserIds() {
        final ArrayList<String> cardUserIds = new ArrayList<>();
        for (int i = 0; i < mCard.getUsers().size(); i++) {
            cardUserIds.add(mCard.getUsers().get(i).getUserID());
        }

        final ArrayList<String> keys = new ArrayList<>();
        final ArrayList<User> cardPlayers = new ArrayList<>();
        final ArrayList<User> listOfUsers = new ArrayList<>();

        Query query = ref.child("friendsList").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                        keys.add(ds.getKey());

                    keys.add(currentUser.getUserID());
                    keys.removeAll(cardUserIds);
                }

                Query query1 = ref.child("users").orderByChild("userID");
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            mUser = ds.getValue(User.class);
                            if (mUser != null) {
                                for (int i = 0; i < keys.size(); i++) {
                                    if (mUser.getUserID().equals(keys.get(i))) {
                                        listOfUsers.add(mUser);
                                    }
                                }
                            }
                        }

                        if (!listOfUsers.isEmpty()) {
                            adapter = new PlayerListAdapter(getActivity(), listOfUsers);
                            playerList.setAdapter(adapter);
                            playerList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                        }
                        else
                            noPlayers.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                playerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        SparseBooleanArray sparseBooleanArray = playerList.getCheckedItemPositions();
                        if (sparseBooleanArray.get(position)) {
                            cardPlayers.add(listOfUsers.get(position));
                            view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryTransparent));
                        }
                        else {
                            cardPlayers.remove(listOfUsers.get(position));
                            view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.PrimaryBackground));
                        }
                        listener.onSort(cardPlayers);
//                        Objects.requireNonNull(getActivity()).getIntent().putExtra("addUsers", cardPlayers);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Scorecard getCardFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null){
            return bundle.getParcelable("mCard");
        }
        else{
            return null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnNewPlayersSorted) getParentFragment();
        }
        catch (ClassCastException e){
            Log.d(TAG, e.getMessage());
        }
    }
}
