package com.example.dsawyer.maddscore.Players;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsawyer.maddscore.CHIPP.CHIPPActivity;
import com.example.dsawyer.maddscore.Events.EventsActivity;
import com.example.dsawyer.maddscore.Help.HelpActivity;
import com.example.dsawyer.maddscore.Leaderboards.LeaderboardsActivity;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Other.LoginActivity;
import com.example.dsawyer.maddscore.Other.SettingsActivity;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Other.NotificationsActivity;
import com.example.dsawyer.maddscore.Profile.ProfileActivity;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Squad.SquadListFragment;
import com.example.dsawyer.maddscore.Squad.SquadPlayerListFragment;
import com.example.dsawyer.maddscore.Utils.BottomNavViewHelper;
import com.example.dsawyer.maddscore.Utils.PlayerListRecyclerView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        PlayerSearchFragment.OnPlayerSearchSelectionListener,
        SquadPlayerListFragment.OnSquadPlayerSelected,
        View.OnClickListener {
    private static final String TAG = "TAG";
    public static final int ACTIVITY_NUM = 2;

    private DatabaseReference ref;
    private FirebaseUser currentUser;
    private User mUser;
    private Squad squad;

    private PlayerListRecyclerView.OnItemClicked listener;

    private ArrayList<String> friendIDs;
    private ArrayList<User> friendsList, filteredPlayers;
    private boolean search_view = false;

    InputMethodManager inputMethodManager;
    Matcher playerNameMatcher, playerUsernameMatcher;
    TextWatcher watcher;

    LinearLayout topLayout;
    RelativeLayout bottom_nav;
    FloatingActionMenu fab_menu;

    PlayerListRecyclerView adapter;
    RecyclerView recyclerView;
    Button log_out;
    TextView numFriends, noPlayers, noSearchResults;
    EditText searchField;
    ImageView searchButton;
    CardView mySquad;

    @Override
    public void onSquadPlayerSelection(User user) {
        if (user.getUserID().equals(currentUser.getUid())) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        else{
            PlayerFragment playerAdd = new PlayerFragment();
            Bundle args = new Bundle();
            args.putParcelable("user", user);
            playerAdd.setArguments(args);
            setFragment(playerAdd, "playerFragmentAdd");
        }
    }

    @Override
    public void onPlayerSearchSelection(User user) {
        if (user.getUserID().equals(currentUser.getUid())) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        else if (user.isRegistered()) {
            PlayerFragment playerFragment = new PlayerFragment();
            Bundle args = new Bundle();
            args.putParcelable("user", user);
            playerFragment.setArguments(args);
            setFragment(playerFragment, "playerFragmentSearch");
        }
        else {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        ref = FirebaseDatabase.getInstance().getReference();

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        filteredPlayers = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        numFriends = findViewById(R.id.num_friends);
        noPlayers = findViewById(R.id.no_players);
        noSearchResults = findViewById(R.id.no_search_results);
        searchField = findViewById(R.id.search_field);
        mySquad = findViewById(R.id.my_squad);
        mySquad.setOnClickListener(this);
        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);
        log_out = findViewById(R.id.logout);
        log_out.setOnClickListener(this);

        topLayout = findViewById(R.id.topLayout);
        bottom_nav = findViewById(R.id.bottom_nav);
        fab_menu = findViewById(R.id.fab_menu);

        FloatingActionButton search_player_fab = findViewById(R.id.search_player_fab);
        FloatingActionButton create_player_fab = findViewById(R.id.create_player_fab);
        search_player_fab.setOnClickListener(this);
        create_player_fab.setOnClickListener(this);

        setUpBottomNavView();
        setUpNavDrawer();

        listener = this::onPlayerSelected;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            getCurrentUserInfo();
        }
        else
            Toast.makeText(this, "Could not retrieve user data.", Toast.LENGTH_SHORT).show();
    }

    private void getCurrentUserInfo() {
        Query query = ref.child("users").child(currentUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    mUser = dataSnapshot.getValue(User.class);

                    if (mUser != null) {
                        getUserSquad();
                        getUserFriendsList();
                    } else
                        Toast.makeText(PlayersActivity.this, "Could not retrieve user data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getUserSquad() {
        ref.child("squads").child(mUser.getSquad()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mySquad.setVisibility(View.VISIBLE);
                    squad = dataSnapshot.getValue(Squad.class);
                } else
                    mySquad.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserFriendsList() {
        friendIDs = new ArrayList<>();

        Query query = ref.child("friendsList").child(currentUser.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    numFriends.setText("Friends (" + dataSnapshot.getChildrenCount() + ")");
                    friendIDs.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        friendIDs.add(ds.getKey());
                    }

                    getUserData();
                }
                else
                    noPlayers.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getUserData() {
        friendsList = new ArrayList<>();
        Query query = ref.child("users");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendsList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);

                    if (user != null && friendIDs.contains(user.getUserID()))
                        friendsList.add(user);
                }

                if (!friendsList.isEmpty()) {
                    adapter = new PlayerListRecyclerView(PlayersActivity.this, listener, friendsList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(PlayersActivity.this));
                }
                else
                    noPlayers.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onPlayerSelected(User user) {
        inputMethodManager.hideSoftInputFromWindow(searchField.getWindowToken(), 0);

        if(user.isRegistered()) {
            PlayerFragment player = new PlayerFragment();
            Bundle args = new Bundle();
            args.putParcelable("user", user);
            player.setArguments(args);
            setFragment(player, "playerFragment");
        }
        else {
            TempPlayerFragment tempPlayer = new TempPlayerFragment();
            Bundle args = new Bundle();
            args.putParcelable("tempUser", user);
            tempPlayer.setArguments(args);
            setFragment(tempPlayer, "tempPlayerFragment");
        }
    }

    private void setUpBottomNavView() {
        BottomNavigationView bottomNav = findViewById(R.id.main_nav);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        BottomNavViewHelper.enableBottomNavView(this, bottomNav);
        overridePendingTransition(0,0);
    }

    private void setUpNavDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.players_activity);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.players_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case(R.id.edit_players):
                setFragment(new PlayersEditFragment(), "playersEditFragment");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case(R.id.side_menu_events):
                Intent intent1 = new Intent(this, EventsActivity.class);
                finish();
                startActivity(intent1);
                overridePendingTransition(0,0);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;

            case(R.id.side_menu_Leaderboards):
                Intent intent2 = new Intent(this, LeaderboardsActivity.class);
                finish();
                startActivity(intent2);
                overridePendingTransition(0,0);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;

            case(R.id.side_menu_CHIPP):
                Intent intent3 = new Intent(this, CHIPPActivity.class);
                finish();
                startActivity(intent3);
                overridePendingTransition(0,0);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;

            case(R.id.side_menu_setings):
                Intent intent4 = new Intent(this, SettingsActivity.class);
                finish();
                startActivity(intent4);
                overridePendingTransition(0,0);
                intent4.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;

            case(R.id.side_menu_help):
                Intent intent5 = new Intent(this, HelpActivity.class);
                finish();
                startActivity(intent5);
                overridePendingTransition(0,0);
                intent5.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;

            case(R.id.side_menu_notifications):
                Intent intent6 = new Intent(this, NotificationsActivity.class);
                startActivity(intent6);
                overridePendingTransition(0,0);
                intent6.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.players_activity);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragment(final Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.player_content_frame, fragment, tag).addToBackStack(null).commit();
    }

    public void removeSearchFilter() {
        if (watcher != null)
            searchField.removeTextChangedListener(watcher);

        search_view = false;
        searchButton.setImageResource(R.drawable.ic_search_light);
        searchField.setEnabled(false);
        searchField.setText("Players");

        topLayout.setVisibility(View.VISIBLE);
        bottom_nav.setVisibility(View.VISIBLE);
        fab_menu.setVisibility(View.VISIBLE);
        noSearchResults.setVisibility(View.GONE);

        inputMethodManager.hideSoftInputFromWindow(searchField.getWindowToken(), 0);

        adapter.updateList(friendsList);
    }

    public void setSearchFilter() {
        search_view = true;
        searchButton.setImageResource(R.drawable.ic_cancel);
        searchField.setEnabled(true);
        searchField.requestFocus();
        searchField.getText().clear();

        topLayout.setVisibility(View.GONE);
        bottom_nav.setVisibility(View.GONE);
        fab_menu.setVisibility(View.GONE);

        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void initWatcher() {
        filteredPlayers.clear();

        if (watcher == null) {
            watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filteredPlayers.clear();
                    String searchValue = searchField.getText().toString();

                    for (User result : friendsList) {
                        playerUsernameMatcher = Pattern.compile(searchValue, Pattern.CASE_INSENSITIVE).matcher(result.getName());
                        playerNameMatcher = Pattern.compile(searchValue, Pattern.CASE_INSENSITIVE).matcher(result.getUsername());

                        if (playerUsernameMatcher.find() || playerNameMatcher.find())
                            filteredPlayers.add(result);
                    }

                    if (filteredPlayers.isEmpty())
                        noSearchResults.setVisibility(View.VISIBLE);
                    else
                        noSearchResults.setVisibility(View.GONE);

                    adapter.updateList(filteredPlayers);
                }

                @Override
                public void afterTextChanged(Editable s) {  }
            };
        }
            searchField.addTextChangedListener(watcher);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.players_activity);

        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);

        if (search_view) {
            removeSearchFilter();
            return;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("squadListFragment");
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            inputMethodManager.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
        }
        super.onBackPressed();
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

            case(R.id.my_squad):
                SquadListFragment squadListFragment = new SquadListFragment();
                Bundle args = new Bundle();
                args.putParcelable("mSquad", squad);
                args.putParcelable("mUser", mUser);
                squadListFragment.setArguments(args);
                setFragment(squadListFragment, "squadListFragment");
                break;

            case (R.id.search_player_fab):
                Toast.makeText(this, "Search Players", Toast.LENGTH_SHORT).show();
//                setFragment(new NewPlayerFragment());
                break;

            case (R.id.create_player_fab):
                Toast.makeText(this, "Create New Player", Toast.LENGTH_SHORT).show();
//                setFragment(new NewPlayerFragment());
                break;

            case(R.id.logout):
                FirebaseAuth.getInstance().signOut();

                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);

                    Toast toast= Toast.makeText(getApplicationContext(),
                            "Successfully Logged Out!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 300);
                    toast.show();

                    finish();
                    break;
                }
                else{
                    Toast.makeText(PlayersActivity.this, "failed to sign out.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
