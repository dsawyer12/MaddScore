package com.example.dsawyer.maddscore.ScoreCards;

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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.Scorecard;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Squad;
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


public class CardAddPlayerSquadListFragment extends Fragment {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;
    private User mUser;
    private Squad mySquad;
    private Scorecard mCard;

    private ArrayList<User> squadMemberList;
    private ArrayList<UserStats> userStats;
    private ArrayList<User> cardUsers;

    SquadMemberListAdapter adapter;

    TextView no_players;
    ListView squadList;

    public interface OnSortSquadList {
        void onSortSquad(ArrayList<User> squadUsers);
    }
    OnSortSquadList listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_add_squad_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        no_players = view.findViewById(R.id.no_players);
        squadList = view.findViewById(R.id.sqd_list);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mCard = getCardFromBundle();
        if ((mCard = getCardFromBundle()) != null)
            getUser();

        cardUsers = new ArrayList<>();

        squadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SparseBooleanArray sparseBooleanArray = squadList.getCheckedItemPositions();
                if (sparseBooleanArray.get(position)) {
                    cardUsers.add(squadMemberList.get(position));
                    view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryTransparent));
                }
                else {
                    cardUsers.remove(squadMemberList.get(position));
                    view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.PrimaryBackground));
                }
                listener.onSortSquad(cardUsers);
            }
        });
    }

    private void getUser() {
        Query query = ref.child("users").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mUser = dataSnapshot.getValue(User.class);
                    if (mUser != null && mUser.getSquad() != null) {
                        getSquad();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSquad() {
        Query query = ref.child("squads").child(mUser.getSquad());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    no_players.setVisibility(View.GONE);
                    mySquad = dataSnapshot.getValue(Squad.class);
                    setSquadList();
                } else
                    no_players.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setSquadList() {
        squadMemberList = new ArrayList<>();

        //get current scorecard player IDs
        ArrayList<String> currentPlayerIds = new ArrayList<>();
        for (int i = 0; i < mCard.getUsers().size(); i++)
            currentPlayerIds.add(mCard.getUsers().get(i).getUserID());

        //remove those current player IDs from the squad list (filter out the ones that are already on the card)
        for (int i = 0; i < currentPlayerIds.size(); i++)
            mySquad.getMemberList().remove(currentPlayerIds.get(i));

        //get the remaining users to display in the listView
        if (!mySquad.getMemberList().isEmpty()) {
            no_players.setVisibility(View.GONE);
            Query query = ref.child("users");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    squadMemberList.clear();
                    User user;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                       user = ds.getValue(User.class);
                        for (String key : mySquad.getMemberList().keySet()) {
                            if (user != null && user.getUserID().equals(key)) {
                                Log.d(TAG, "squad user found : " + user.getName());
                                squadMemberList.add(user);
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
        else
            no_players.setVisibility(View.VISIBLE);
    }

    public void getUserStats() {
        userStats = new ArrayList<>();
        Query query = ref.child("userStats");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserStats stats;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    stats = ds.getValue(UserStats.class);
                    for (int i = 0; i < squadMemberList.size(); i++) {
                        if (stats != null && stats.getUserID().equals(squadMemberList.get(i).getUserID())) {
                            Log.d(TAG, "squad user stats found : " + stats.getUserID());
                            userStats.add(stats);
                        }
                    }
                }

                adapter = new SquadMemberListAdapter(getActivity(), squadMemberList, userStats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public Scorecard getCardFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable("mCard");
        }
        else
            return null;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnSortSquadList) getParentFragment();
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }
}
