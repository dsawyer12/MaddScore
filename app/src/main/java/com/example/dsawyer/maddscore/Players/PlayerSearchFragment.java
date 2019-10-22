package com.example.dsawyer.maddscore.Players;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Other.ApplicationClass;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.PlayerListRecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlayerSearchFragment extends Fragment implements View.OnClickListener {

    private DatabaseReference ref;
    private User player;

    private EditText search;
    private RecyclerView mList;
    private RelativeLayout noneFound;
    private ImageView search_btn;

    private ArrayList<User> playerList;
    private PlayerListRecyclerView.OnItemClicked listener;
    PlayerListRecyclerView adapter;

    public interface OnPlayerSearchSelectionListener{
        void onPlayerSearchSelection(User user);
    }
    OnPlayerSearchSelectionListener playerSelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player_search, container, false);
    }

    @Override
    public void onAttach(Context context) {
        try{
            playerSelected = (OnPlayerSearchSelectionListener) getActivity();
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

        noneFound = view.findViewById(R.id.no_search_found_layout);
        search_btn = view.findViewById(R.id.search_btn);
        mList = view.findViewById(R.id.search_list);
        search = view.findViewById(R.id.search_player);
        search_btn.setOnClickListener(this);

        listener = new PlayerListRecyclerView.OnItemClicked() {
            @Override
            public void onClick(User user) {
                playerSelected.onPlayerSearchSelection(user);
            }
        };

        searchPlayers();
    }

    public void searchPlayers(){
        playerList = new ArrayList<>();

        adapter = new PlayerListRecyclerView(getActivity(), listener, playerList);
        mList.setAdapter(adapter);
        mList.setLayoutManager(new LinearLayoutManager(getActivity()));

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    playerList.clear();
                    adapter.notifyDataSetChanged();

                    String username = search.getText().toString().trim();
                    Query query = ref.child(ApplicationClass.getContext().getResources().getString(R.string.firebase_node_users))
                            .orderByChild(ApplicationClass.getContext().getResources().getString(R.string.firebase_node_username))
                            .equalTo(username);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                player = ds.getValue(User.class);
                                if (player.isRegistered()){
                                    playerList.add(player);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            if (playerList.isEmpty()){
                                noneFound.setVisibility(View.VISIBLE);
                            }
                            else{
                                noneFound.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.search_btn):
                playerList.clear();
                adapter.notifyDataSetChanged();

                String username = search.getText().toString().trim();
                Query query = ref.child(ApplicationClass.getContext().getResources().getString(R.string.firebase_node_users))
                        .orderByChild(ApplicationClass.getContext().getResources().getString(R.string.firebase_node_username))
                        .equalTo(username);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            player = ds.getValue(User.class);
                            playerList.add(player);
                            adapter.notifyDataSetChanged();
                        }
                        if (playerList.isEmpty()){
                            noneFound.setVisibility(View.VISIBLE);
                        }
                        else{
                            noneFound.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
        }
    }
}
