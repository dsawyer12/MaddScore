package com.example.dsawyer.maddscore.ScoreCards;

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
import java.util.Collections;


public class ScorecardSquadPlayerListFragment extends Fragment {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;
    private User mUser;
    private Squad mySquad;
    private ArrayList<User> userList;
    ArrayList<User> cardUsers;
    SquadMemberListAdapter adapter;

    ListView squadList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_add_squad_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userList = new ArrayList<>();
        cardUsers = new ArrayList<>();

        getActivity().getIntent().putParcelableArrayListExtra("squadUsers", cardUsers);

        squadList = view.findViewById(R.id.sqd_list);

        getUser();

        squadList.setOnItemClickListener((parent, view1, position, id) -> {
            SparseBooleanArray sparseBooleanArray = squadList.getCheckedItemPositions();
            if (sparseBooleanArray.get(position)){
                cardUsers.add(userList.get(position));
                view1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryTransparent));
            }
            else if(!sparseBooleanArray.get(position)){
                cardUsers.remove(userList.get(position));
                view1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.PrimaryBackground));
            }
            getActivity().getIntent().putExtra("squadUsers", cardUsers);
        });
    }

    private void getUser() {
        Query query = ref.child("users").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mUser = dataSnapshot.getValue(User.class);
                    if (mUser != null && mUser.getSquad() != null)
                        getSquad();
                    else
                        Log.d(TAG, "user DOES NOT  have a squad");

                }
                else {
                    //handle
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void getSquad() {
        Query query = ref.child("squads").child(mUser.getSquad());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mySquad = dataSnapshot.getValue(Squad.class);
                    setSquadList();
                }
                else {
                    //handle
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void setSquadList() {
        Query query = ref.child("users");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (String key : mySquad.getMemberList().keySet()) {
                        if (ds.getValue(User.class) != null && ds.getValue(User.class).getUserID().equals(key)) {
                            userList.add(ds.getValue(User.class));
                        }
                        else {
                            //handle
                        }
                    }
                }
                Collections.sort(userList);
                adapter = new SquadMemberListAdapter(getActivity(), userList);
                squadList.setAdapter(adapter);
                squadList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}










