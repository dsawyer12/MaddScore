package com.example.dsawyer.maddscore.Squad;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsawyer.maddscore.CHIPP.CHIPPActivity;
import com.example.dsawyer.maddscore.Events.EventsActivity;
import com.example.dsawyer.maddscore.Help.HelpActivity;
import com.example.dsawyer.maddscore.Leaderboards.LeaderboardsActivity;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Other.ApplicationClass;
import com.example.dsawyer.maddscore.Other.LoginActivity;
import com.example.dsawyer.maddscore.Other.NotificationsActivity;
import com.example.dsawyer.maddscore.Other.SettingsActivity;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.BottomNavViewHelper;
import com.example.dsawyer.maddscore.Utils.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SquadActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;
    private User mUser;
    private Squad squad;
    CardView squadCardView;

    TextView squadName, squadMembers;
    private ViewPager viewPager;
    private SectionsPagerAdapter adapter;
    ProgressBar progressBar;
    LinearLayout lin;
    Boolean hasSquad = false;
    Button log_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squad);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        squadName = findViewById(R.id.squad_name);
        squadMembers = findViewById(R.id.squad_members);
        squadCardView = findViewById(R.id.squad_cardView);
        viewPager = findViewById(R.id.viewpager);
        log_out = findViewById(R.id.logout);
        lin = findViewById(R.id.lin);
        progressBar = findViewById(R.id.progressBar);
        log_out.setOnClickListener(this);

        setUpBottomNavView();
        setUpNavDrawer();
        initializeUserData();
    }

    private void setUpBottomNavView(){
        BottomNavigationView mMainNav = findViewById(R.id.main_nav);
//        BottomNavViewHelper.disableShiftMode(mMainNav);
        Menu menu = mMainNav.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        BottomNavViewHelper.enableBottomNavView(this, mMainNav);
        overridePendingTransition(0,0);
    }

    private void setUpNavDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.squad_activity);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeUserData() {
        progressBar.setVisibility(View.VISIBLE);
        Query query = ref.child("users").child(UID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mUser = dataSnapshot.getValue(User.class);
                    if (mUser != null) {
                        if(mUser.getMySquad() != null) {
                            hasSquad = true;
                            invalidateOptionsMenu();
                            setUpTabLayoutForSquad();
                            initializeSquadData();
                        }
                        else
                            setUpDefaultTabLayout();
                    }
                    else{
                        Log.d(TAG, "onDataChange: muser = null");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpTabLayoutForSquad() {
//        Log.d(TAG, "setUpTabLayoutForSquad: called");
//        if (adapter != null) {
//           finish();
//           startActivity(getIntent());
//        }

        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SocialFragment());
        adapter.addFragment(new SquadOverviewFragament());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Social");
        tabLayout.getTabAt(1).setText("News");

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

    private void initializeSquadData() {
        Query query = ref.child("squads").child(mUser.getMySquad());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    lin.setVisibility(View.VISIBLE);
                    squad = dataSnapshot.getValue(Squad.class);
                    if (squad != null) {
                        squadName.setText(squad.getSquadName());
                        squadMembers.setText(squad.getUserList().size() + " members");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpDefaultTabLayout() {
        progressBar.setVisibility(View.GONE);
        lin.setVisibility(View.VISIBLE);
        Log.d(TAG, "setUpDefaultTabLayout: called");
        squadCardView.setVisibility(View.GONE);
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NoSquadFragment());
        viewPager.setAdapter(adapter);
        squadName.setVisibility(View.GONE);
        squadMembers.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.squad_activity);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(hasSquad);
        menu.getItem(1).setVisible(hasSquad);
        menu.getItem(2).setVisible(hasSquad);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.squad_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case(R.id.edit_players):
//                setFragment(new PlayersEditFragment());
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

        DrawerLayout drawer = findViewById(R.id.squad_activity);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragment(final Fragment fragment){
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.frame, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.logout):
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
                    Toast.makeText(SquadActivity.this, "failed to sign out.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
