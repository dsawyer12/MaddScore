package com.example.dsawyer.maddscore.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.CHIPP.CHIPPActivity;
import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.Objects.tempCourse;
import com.example.dsawyer.maddscore.Events.EventsActivity;
import com.example.dsawyer.maddscore.Help.HelpActivity;
import com.example.dsawyer.maddscore.Leaderboards.LeaderboardsActivity;
import com.example.dsawyer.maddscore.Other.LoginActivity;
import com.example.dsawyer.maddscore.Other.NotificationsActivity;
import com.example.dsawyer.maddscore.Other.SettingsActivity;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Squad.SquadActivity;
import com.example.dsawyer.maddscore.Stats.StatsActivity;
import com.example.dsawyer.maddscore.Objects.UserStats;
import com.example.dsawyer.maddscore.Utils.BottomNavViewHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {
    private static final String TAG = "TAG";
    public static final String myPrefs = "get_started_screen";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    private DatabaseReference ref;
    private String UID;

    private User mUser;
    private UserStats userStats;
    private Squad squad;
    Course besRoundCourse;

    Button log_out;
    CardView stats_root, squad_root;
    LinearLayout active_squad_layout, active_stats_layout;
    CircleImageView profileImage;
    ImageView notifications, squad_rank_img;
    TextView username, name, rounds, numFriends, squad_card_title, squad_name, current_rank,
            members, stats_avg, stats_best_score, stats_best_round_course, stats_best_round_date, no_stats_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

//        sharedpreferences = this.getPreferences(Context.MODE_PRIVATE);
//        boolean get_started_shown = sharedpreferences.getBoolean(myPrefs,false);
//        if (!get_started_shown)
//            setFragment(new GetStartedViewPager());
//
//        editor = sharedpreferences.edit();
//        editor.putBoolean(myPrefs, true);
//        editor.apply();

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        stats_root = findViewById(R.id.stats_root);
        squad_root = findViewById(R.id.squad_root);
        active_squad_layout = findViewById(R.id.active_squad_layout);
        active_stats_layout = findViewById(R.id.active_stats_layout);

        profileImage = findViewById(R.id.profileImage);
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        rounds = findViewById(R.id.rounds);
        squad_card_title = findViewById(R.id.squad_card_title);
        squad_name = findViewById(R.id.squad_name);
        current_rank = findViewById(R.id.current_rank);
        members = findViewById(R.id.squad_member_number);
        numFriends = findViewById(R.id.num_friends);
        squad_rank_img = findViewById(R.id.squad_rank_img);
        notifications = findViewById(R.id.notifications);
        stats_avg = findViewById(R.id.stats_AVG);
        stats_best_score = findViewById(R.id.stats_best_score);
        stats_best_round_course = findViewById(R.id.stats_best_score_course);
        stats_best_round_date = findViewById(R.id.stats_best_score_date);
        no_stats_msg = findViewById(R.id.no_stats_msg);
        log_out = findViewById(R.id.logout);

        log_out.setOnClickListener(this);
        stats_root.setOnClickListener(this);
        squad_root.setOnClickListener(this);
        notifications.setOnClickListener(this);

        setUpBottomNavView();
        setUpNavDrawer();
        initializeUserData();
        checkNotifications();
    }

    /*  gather the current users profile data    */
    private void initializeUserData() {
        Log.d(TAG, "initializeUserData: called");
        Query query = ref.child("users").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "onDataChange: user found");
                    mUser = dataSnapshot.getValue(User.class);
                    getUserStats();
                }
                else
                    Log.d(TAG, "onDataChange: No match or UID");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getUserStats() {
        Log.d(TAG, "getUserStats: called");
        Query query = ref.child("userStats").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "user stats found");
                    userStats = dataSnapshot.getValue(UserStats.class);
                    setUpUserProfile();
                }
                else{
                    Log.d(TAG, "user stats NOT found");
                    // user stats were not found. display a message to the user or set fields appropriately
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*  initialize the current users data    */
    public void setUpUserProfile() {
        Log.d(TAG, "setUpUserProfile: called");
        if (mUser.getPhotoUrl() != null) {
            Log.d(TAG, "got user photo");
            Glide.with(this).load(mUser.getPhotoUrl()).into(profileImage);
        }
        else{
            Log.d(TAG, "NO user photo");
            Glide.with(getApplicationContext()).load(R.mipmap.default_profile_img).into(profileImage);
        }

        username.setText(mUser.getUsername());
        name.setText(mUser.getName());
        if (userStats.getNumRounds() != 0) {
            no_stats_msg.setVisibility(View.GONE);
            active_stats_layout.setVisibility(View.VISIBLE);
            rounds.setText(String.valueOf(userStats.getNumRounds()));
        }
        else {
            active_stats_layout.setVisibility(View.GONE);
            no_stats_msg.setVisibility(View.VISIBLE);
        }

        if(mUser.getMySquad() != null)
            initializeSquadData();
        else {
            active_squad_layout.setVisibility(View.GONE);
            squad_card_title.setText("Join a squad!");
//            squad_rel.setBackgroundColor(ContextCompat.getColor(this, R.color.PrimaryBackground));
            Log.d(TAG, "setUpUserProfile: user.getMySquad = null");
        }

        if (userStats.getBestRoundCourse() != null) {
            Log.d(TAG, "best round course found");
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");

            if (userStats.getScoreAVG() == 0)
                stats_avg.setText("E");
            else
                stats_avg.setText(String.valueOf(userStats.getScoreAVG()));

            if (userStats.getBestRoundScore() == 0)
                stats_best_score.setText("E");
            else
                stats_best_score.setText(String.valueOf(userStats.getBestRoundScore()));

            if (userStats.getBestRoundDate() != 0)
                stats_best_round_date.setText(sdf.format(userStats.getBestRoundDate()));

            Query query = ref.child("courses").child(userStats.getBestRoundCourse());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "best round course found in DB");
                        besRoundCourse = dataSnapshot.getValue(Course.class);
                        stats_best_round_course.setText(besRoundCourse.getName());
                    }
                    else {
                        Log.d(TAG, "course not found in DB. Checking personal courses...");
                        Query query = ref.child("customCourses").child(UID).child(userStats.getBestRoundCourse());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Log.d(TAG, "best round course found in personal DB");
                                    besRoundCourse = dataSnapshot.getValue(Course.class);
                                    stats_best_round_course.setText(besRoundCourse.getName());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        Query query = ref.child("friendsList").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    numFriends.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                else
                    Log.d(TAG, "onDataChange: A friendsList does not exist for this user");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeSquadData() {
        Log.d(TAG, "initializeSquadData: called");
        Query query = ref.child("squads").child(mUser.getMySquad());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    squad = dataSnapshot.getValue(Squad.class);
                    squad_name.setText(squad.getSquadName());
                    members.setText(String.valueOf(squad.getUserList().size()) + " member(s)");
                    current_rank.setText("# " + String.valueOf(userStats.getSquadRank()));
                }
                else
                    Log.d(TAG, "onDataChange: initializeSquadData snapshot does not exist");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpBottomNavView() {
        BottomNavigationView mainNav = findViewById(R.id.main_nav);
//        BottomNavViewHelper.disableShiftMode(mainNav);
        Menu menu = mainNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        BottomNavViewHelper.enableBottomNavView(this, mainNav);
        overridePendingTransition(0,0);
    }

    private void setUpNavDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.profile_activity);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void checkNotifications() {
        Query query = ref.child("notifications").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    notifications.setVisibility(View.VISIBLE);
                else
                    notifications.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.profile_activity);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case(R.id.edit_profile):
               Intent intent1 = new Intent(this, EditProfileActivity.class);
               intent1.putExtra("curentUser", mUser);
               startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            case(R.id.side_menu_events):
                Intent intent1 = new Intent(this, EventsActivity.class);
                startActivity(intent1);
                overridePendingTransition(0,0);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;
            case(R.id.side_menu_Leaderboards):
                Intent intent2 = new Intent(this, LeaderboardsActivity.class);
                startActivity(intent2);
                overridePendingTransition(0,0);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;
            case(R.id.side_menu_CHIPP):
                Intent intent3 = new Intent(this, CHIPPActivity.class);
                startActivity(intent3);
                overridePendingTransition(0,0);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;
            case(R.id.side_menu_setings):
                Intent intent4 = new Intent(this, SettingsActivity.class);
                startActivity(intent4);
                overridePendingTransition(0,0);
                intent4.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;
            case(R.id.side_menu_help):
                Intent intent5 = new Intent(this, HelpActivity.class);
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

        DrawerLayout drawer = findViewById(R.id.profile_activity);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.profile_content_frame, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.stats_root):
                startActivity(new Intent(ProfileActivity.this, StatsActivity.class));
                break;
            case (R.id.squad_root):
                startActivity(new Intent(ProfileActivity.this, SquadActivity.class));
                break;
            case (R.id.notifications):
                Intent intent = new Intent(ProfileActivity.this, NotificationsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case(R.id.logout):
                FirebaseAuth.getInstance().signOut();
                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent intent1 = new Intent(this, LoginActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                    Toast toast= Toast.makeText(getApplicationContext(),
                            "Successfully Logged Out!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 300);
                    toast.show();
                    finish();
                    break;
                }
                else
                    Toast.makeText(ProfileActivity.this, "failed to sign out.", Toast.LENGTH_LONG).show();
        }
    }
}
