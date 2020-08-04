package com.example.dsawyer.maddscore.Squad;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Players.PlayerFragment;
import com.example.dsawyer.maddscore.Players.PlayersActivity;
import com.example.dsawyer.maddscore.Players.TempPlayerFragment;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.SquadMemberListRecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SquadListFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private RelativeLayout current_user_card;
    private User mUser;
    private Squad mSquad;

    private int finished;
    private boolean search_view = false;

    EditText searchField;
    ImageView searchButton, backBtn;
    TextView no_search_results;

    InputMethodManager inputMethodManager;
    Matcher playerNameMatcher, playerUsernameMatcher;
    TextWatcher watcher;

    LinearLayout meta_info;

    private ArrayList<User> memberList, filteredMembers;
    private RecyclerView recyclerView;
    private SquadMemberListRecyclerViewAdapter adapter;
    private SquadMemberListRecyclerViewAdapter.OnSquadMemberClickedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_squad_member_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchField = view.findViewById(R.id.search_field);
        searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);
        backBtn = view.findViewById(R.id.back_arrow);
        backBtn.setOnClickListener(this);
        current_user_card = view.findViewById(R.id.current_user_card);
        recyclerView = view.findViewById(R.id.recyclerView);

        no_search_results = view.findViewById(R.id.no_search_results);
        meta_info = view.findViewById(R.id.meta_info);

        if (getActivity() != null)
            inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        filteredMembers = new ArrayList<>();

        if (this.getArguments() != null) {
            mUser = this.getArguments().getParcelable("mUser");
            mSquad = this.getArguments().getParcelable("mSquad");
            if (mUser != null && mSquad != null) {
                setCurrentUserCard();
                getSquadMembers();
            }else
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    public void setCurrentUserCard() {
        View user_card = getLayoutInflater().inflate(R.layout.snippet_squad_member_item, current_user_card, false);
        LinearLayout active_squad_layout = user_card.findViewById(R.id.active_squad_layout);
        CircleImageView profileImg = user_card.findViewById(R.id.user_img);
        TextView member_username = user_card.findViewById(R.id.member_username);
        TextView member_name = user_card.findViewById(R.id.member_name);
        TextView user_squad_rank = user_card.findViewById(R.id.user_squad_rank);

        if (active_squad_layout != null && getActivity() != null) {
            switch(mUser.getSquadRank()) {
                case (1):
                    active_squad_layout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rank_fade_1));
                    break;
                case (2):
                    active_squad_layout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rank_fade_2));
                    break;
                case (3):
                    active_squad_layout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rank_fade_3));
                    break;
                case (4):
                    active_squad_layout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rank_fade_4));
                    break;
                case (5):
                    active_squad_layout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rank_fade_5));
                    break;
            }
        }

        if (profileImg != null) {
            if (mUser.getPhotoUrl() != null && getActivity() != null)
                Glide.with(getActivity()).load(mUser.getPhotoUrl()).into(profileImg);
        }
        if (member_username != null) member_username.setText(mUser.getUsername());
        if (member_name != null) member_name.setText(mUser.getName());
        if (user_squad_rank != null) user_squad_rank.setText(String.valueOf(mUser.getSquadRank()));

        current_user_card.addView(user_card);
    }

    public void getSquadMembers() {
        listener = this::onPlayerSelected;

        memberList = new ArrayList<>();
        finished = 0;

        ref = FirebaseDatabase.getInstance().getReference();

        String[] memberKeys = mSquad.getMemberList().keySet().toArray(new String[0]);
        Query userQuery;

        for (String key : memberKeys) {
            userQuery = ref.child("users").child(key);
            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    finished++;
                    User member = dataSnapshot.getValue(User.class);
                    if (member != null) memberList.add(member);

                    if (finished == memberKeys.length) {
                        finished = 0;
                        Collections.sort(memberList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        adapter = new SquadMemberListRecyclerViewAdapter(getActivity(), mUser, memberList, listener);
                        recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void onPlayerSelected(User user) {
        inputMethodManager.hideSoftInputFromWindow(searchField.getWindowToken(), 0);

        if(user.isRegistered()) {
            PlayerFragment player = new PlayerFragment();
            Bundle args = new Bundle();
            args.putParcelable("user", user);
            player.setArguments(args);

            Activity parent = getActivity();
            if (parent != null) {
                try {
                    if (parent instanceof SquadActivity)
                        ((SquadActivity)getActivity()).setFragment(player, R.id.main_frame, "playerFragment");
                    else if (parent instanceof PlayersActivity)
                        ((PlayersActivity)getActivity()).setFragment(player, "playerFragment");
                } catch(ClassCastException e) {
                    Log.d(TAG, "onPlayerSelected: exception : " + e.getMessage());
                    Toast.makeText(parent, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else {
            TempPlayerFragment tempPlayer = new TempPlayerFragment();
            Bundle args = new Bundle();
            args.putParcelable("tempUser", user);
            tempPlayer.setArguments(args);

            Activity parent = getActivity();
            if (parent != null) {
                try {
                    if (parent instanceof SquadActivity)
                        ((SquadActivity)getActivity()).setFragment(tempPlayer, R.id.main_frame, "tempPlayerFragment");
                    else if (parent instanceof PlayersActivity)
                        ((PlayersActivity)getActivity()).setFragment(tempPlayer, "tempPlayerFragment");
                } catch(ClassCastException e) {
                    Log.d(TAG, "onPlayerSelected: exception : " + e.getMessage());
                    Toast.makeText(parent, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void removeSearchFilter() {
        if (watcher != null)
            searchField.removeTextChangedListener(watcher);

        search_view = false;
        searchButton.setImageResource(R.drawable.ic_search_light);
        searchField.setEnabled(false);
        searchField.setText("Players");

        meta_info.setVisibility(View.VISIBLE);
        no_search_results.setVisibility(View.GONE);

        inputMethodManager.hideSoftInputFromWindow(searchField.getWindowToken(), 0);

        adapter.updateList(memberList);
    }

    public void setSearchFilter() {
        search_view = true;
        searchButton.setImageResource(R.drawable.ic_cancel);
        searchField.setEnabled(true);
        searchField.requestFocus();
        searchField.getText().clear();

        meta_info.setVisibility(View.GONE);

        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void initWatcher() {
        filteredMembers.clear();

        if (watcher == null) {
            watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filteredMembers.clear();
                    String searchValue = searchField.getText().toString();

                    for (User result : memberList) {
                        playerUsernameMatcher = Pattern.compile(searchValue, Pattern.CASE_INSENSITIVE).matcher(result.getName());
                        playerNameMatcher = Pattern.compile(searchValue, Pattern.CASE_INSENSITIVE).matcher(result.getUsername());

                        if (playerUsernameMatcher.find() || playerNameMatcher.find())
                            filteredMembers.add(result);
                    }

                    if (filteredMembers.isEmpty())
                        no_search_results.setVisibility(View.VISIBLE);
                    else
                        no_search_results.setVisibility(View.GONE);

                    adapter.updateList(filteredMembers);
                }

                @Override
                public void afterTextChanged(Editable s) {  }
            };
        }
        searchField.addTextChangedListener(watcher);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.search_button):

                if (search_view)
                    removeSearchFilter();
                else {
                    setSearchFilter();
                    initWatcher();
                }
                break;

            case(R.id.back_arrow):
                inputMethodManager.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                
                if (getActivity() != null)
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
        }
    }
}
















