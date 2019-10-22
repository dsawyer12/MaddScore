package com.example.dsawyer.maddscore.Players;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dsawyer.maddscore.CHIPP.CHIPPActivity;
import com.example.dsawyer.maddscore.Events.EventsActivity;
import com.example.dsawyer.maddscore.Help.HelpActivity;
import com.example.dsawyer.maddscore.Leaderboards.LeaderboardsActivity;
import com.example.dsawyer.maddscore.Other.LoginActivity;
import com.example.dsawyer.maddscore.Other.SettingsActivity;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Other.NotificationsActivity;
import com.example.dsawyer.maddscore.Profile.ProfileActivity;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Squad.NoSquadFragment;
import com.example.dsawyer.maddscore.Utils.BottomNavViewHelper;
import com.example.dsawyer.maddscore.Utils.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PlayersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        PlayerListFragment.OnPlayerSelectedListener,
        PlayerSearchFragment.OnPlayerSearchSelectionListener,
        SquadPlayerListFragment.OnSquadPlayerSelected,
        View.OnClickListener {
    private static final String TAG = "TAG";
    public static final int ACTIVITY_NUM = 2;

    private DatabaseReference ref;
    private String UID;
    private User mUser;

    Button log_out;

    @Override
    public void onPlayerSelected(User user) {
        if(user.isRegistered()) {
            PlayerFragment player = new PlayerFragment();
            Bundle args = new Bundle();
            args.putParcelable("user", user);
            player.setArguments(args);
            setFragment(player);
        }
        else {
            TempPlayerFragment tempPlayer = new TempPlayerFragment();
            Bundle args = new Bundle();
            args.putParcelable("tempUser", user);
            tempPlayer.setArguments(args);
            setFragment(tempPlayer);
        }
    }

    @Override
    public void onSquadPlayerSelection(User user) {
        if (user.getUserID().equals(UID)){
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        else{
            PlayerFragment playerAdd = new PlayerFragment();
            Bundle args = new Bundle();
            args.putParcelable("user", user);
            playerAdd.setArguments(args);
            setFragment(playerAdd);
        }
    }

    @Override
    public void onPlayerSearchSelection(User user) {
        if (user.getUserID().equals(UID)) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        else if (user.isRegistered()) {
            PlayerFragment playerFragment = new PlayerFragment();
            Bundle args = new Bundle();
            args.putParcelable("user", user);
            playerFragment.setArguments(args);
            setFragment(playerFragment);
        }
        else {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        log_out = findViewById(R.id.logout);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        log_out.setOnClickListener(this);
        fab.setOnClickListener(this);

        setUpBottomNavView();
        setUpNavDrawer();
        getCurrentUser();
    }

    private void getCurrentUser() {
        Query query = ref.child("users").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mUser = dataSnapshot.getValue(User.class);
                    if (mUser != null) {
                        if(mUser.getMySquad() != null)
                            setUpTabLayoutForSquad();
                        else
                            setUpDefaultTabLayout();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpBottomNavView() {
        BottomNavigationView bottomNav = findViewById(R.id.main_nav);
//        BottomNavViewHelper.disableShiftMode(bottomNav);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        BottomNavViewHelper.enableBottomNavView(this, bottomNav);
        overridePendingTransition(0,0);
    }

   public void setUpTabLayoutForSquad() {
       ViewPager viewPager = findViewById(R.id.player_viewpager_container);
       SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
       adapter.addFragment(new PlayerListFragment());
       adapter.addFragment(new SquadPlayerListFragment());
       viewPager.setAdapter(adapter);

       TabLayout tabLayout = findViewById(R.id.tabs);
       tabLayout.setupWithViewPager(viewPager);
       tabLayout.getTabAt(0).setText("Friends");
       tabLayout.getTabAt(1).setText("Squad");

       View root = tabLayout.getChildAt(0);
       if (root instanceof LinearLayout) {
           ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
           GradientDrawable drawable = new GradientDrawable();
           drawable.setColor(ContextCompat.getColor(this, R.color.grey));
           drawable.setSize(2, 0);
           ((LinearLayout) root).setDividerPadding(10);
           ((LinearLayout) root).setDividerDrawable(drawable);
       }
   }

    private void setUpDefaultTabLayout() {
        ViewPager viewPager = findViewById(R.id.player_viewpager_container);
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PlayerListFragment());
        adapter.addFragment(new NoSquadFragment());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Friends");
        tabLayout.getTabAt(1).setText("Squad");

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getColor(R.color.grey));
            drawable.setSize(2, 0);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
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
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.players_activity);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        super.onBackPressed();
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
                setFragment(new PlayersEditFragment());
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

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.player_content_frame, fragment).addToBackStack(null).commit();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.floatingActionButton):
                setFragment(new NewPlayerFragment());
                break;

            case(R.id.logout):
                FirebaseAuth.getInstance().signOut();
                if(FirebaseAuth.getInstance().getCurrentUser() == null){
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
