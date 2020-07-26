package com.example.dsawyer.maddscore.Profile;

import android.content.Intent;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.CHIPP.CHIPPActivity;
import com.example.dsawyer.maddscore.Objects.Course;
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
import com.example.dsawyer.maddscore.Objects.UserStats;
import com.example.dsawyer.maddscore.Stats.StatsActivity;
import com.example.dsawyer.maddscore.Utils.ApplicationPreferences;
import com.example.dsawyer.maddscore.Utils.BottomNavViewHelper;
import com.example.dsawyer.maddscore.Utils.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private FirebaseUser currentUser;
    private User mUser;
    private UserStats userStats;
    private Squad squad;
    private Course bestRoundCourse;

    private static boolean HIDE_SQUAD_CARD;
    private static boolean HIDE_STATS_CARD;

    LoadingDialog loadingDialog;
    LinearLayout linear_feed;
    ImageView notifications;

    CircleImageView profileImage;
    TextView username, name, rounds, numFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        loadingDialog = new LoadingDialog(this);
        linear_feed = findViewById(R.id.linear_feed);
        notifications = findViewById(R.id.notifications);

        profileImage = findViewById(R.id.profileImage);
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        rounds = findViewById(R.id.rounds);
        numFriends = findViewById(R.id.num_friends);

        notifications.setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);

        setUpBottomNavView();
        setUpNavDrawer();

        ApplicationPreferences.getSharedPreferences(this, ApplicationPreferences.HIDE_SQUAD_CARD);
        if (ApplicationPreferences.getProfilePreferences(ApplicationPreferences.HIDE_SQUAD_CARD))
            HIDE_SQUAD_CARD = true;

        ApplicationPreferences.getSharedPreferences(this, ApplicationPreferences.HIDE_STATS_CARD);
        if (ApplicationPreferences.getProfilePreferences(ApplicationPreferences.HIDE_STATS_CARD))
            HIDE_STATS_CARD = true;

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            initUserData();
            checkNotifications();
            getUserFriends();
        }
        else
            displayUserDataRetrievalWarning();

//        sharedpreferences = this.getPreferences(Context.MODE_PRIVATE);
//        boolean get_started_shown = sharedpreferences.getBoolean(myPrefs,false);
//        if (!get_started_shown)
//            setFragment(new GetStartedViewPager());
//
//        editor = sharedpreferences.edit();
//        editor.putBoolean(myPrefs, true);
//        editor.apply();
    }

    private void setUpBottomNavView() {
        BottomNavigationView mainNav = findViewById(R.id.main_nav);
//        BottomNavViewHelper.disableShiftMode(mainNav);
        Menu menu = mainNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        BottomNavViewHelper.enableBottomNavView(this, mainNav);
        // override and remove the transition animation
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

    private void initUserData() {
        Log.d(TAG, "initializeUserData: called");
        loadingDialog.show();

        Query query = ref.child("users").child(currentUser.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    mUser = dataSnapshot.getValue(User.class);

                    if (mUser != null) {

                        if (!mUser.isCompleteAccount()) {
                            loadingDialog.dismiss();
                            Log.d(TAG, "onDataChange: user has not yet completed their account");
                            startActivity(new Intent(ProfileActivity.this, ProfileCreateActivity.class));
                            finish();
                        }
                        else {
                            name.setText(mUser.getName());
                            username.setText(mUser.getUsername());

                            if (mUser.getPhotoUrl() != null)
                                Glide.with(getApplicationContext()).load(mUser.getPhotoUrl()).into(profileImage);

                            initSquadData();
                            getUserStats();
                        }
                    } else {
                        Log.d(TAG, "onDataChange: null ptr");
                        loadingDialog.dismiss();
                    }
                }
                else {
                    Log.d(TAG, "onDataChange: No match or UID");
                    loadingDialog.dismiss();
                    displayUserDataRetrievalWarning();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });
    }

    private void initSquadData() {
        Log.d(TAG, "initSquadData: called");
        View squadCardView = getLayoutInflater().inflate(R.layout.layout_squad_card, linear_feed, true);
        View squadView = squadCardView.findViewById(R.id.squad_root);

        if (HIDE_SQUAD_CARD)
            squadView.setVisibility(View.GONE);

        if (mUser.getSquad() != null) {
            Log.d(TAG, "user has a squad");
            Query query = ref.child("squads").child(mUser.getSquad());

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        squad = dataSnapshot.getValue(Squad.class);

                        if (squad != null) {
                            CardView rootView = squadCardView.findViewById(R.id.squad_root);
                            LinearLayout active_squad_layout = squadCardView.findViewById(R.id.active_squad_layout);
                            LinearLayout linear_parent = squadCardView.findViewById(R.id.linear_parent);
                            active_squad_layout.setVisibility(View.VISIBLE);
                            TextView squad_name = active_squad_layout.findViewById(R.id.squad_name);
                            squad_name.setText(squad.getSquadName());
                            TextView squad_member_cnt = active_squad_layout.findViewById(R.id.squad_member_cnt);
                            squad_member_cnt.setText(squad.getMemberList().size() + " member(s)");
                            TextView user_squad_rank = active_squad_layout.findViewById(R.id.user_squad_rank);

                            switch(mUser.getSquad_rank()) {
                                case (1):
                                    linear_parent.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rank_fade_1));
                                    break;
                                case (2):
                                    linear_parent.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rank_fade_2));
                                    break;
                                case (3):
                                    linear_parent.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rank_fade_3));
                                    break;
                                case (4):
                                    linear_parent.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rank_fade_4));
                                    break;
                                case (5):
                                    linear_parent.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rank_fade_5));
                                    break;
                            }
                            user_squad_rank.setText(String.valueOf(mUser.getSquad_rank()));
                            rootView.setOnClickListener(ProfileActivity.this);
                        }
                    }
                    else {
                        Log.d(TAG, "onDataChange: initSquadData snapshot does not exist");
                        loadingDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    loadingDialog.dismiss();
                }
            });
        }
        else {
            Log.d(TAG, "did not find user squad");
            LinearLayout squad_card_titleBar = squadCardView.findViewById(R.id.squad_card_titleBar);
            squad_card_titleBar.setVisibility(View.VISIBLE);
            LinearLayout no_stats_layout = squadCardView.findViewById(R.id.no_squad_layout);
            no_stats_layout.setVisibility(View.VISIBLE);
            Button more_info_squad_btn = squadCardView.findViewById(R.id.squads_more_info_btn);
            more_info_squad_btn.setOnClickListener(this);
        }

//        ImageView squad_card_option = squadCardView.findViewById(R.id.squad_card_options);
//        squad_card_option.setOnClickListener(this);
    }

    public void getUserStats() {
        Log.d(TAG, "getUserStats: called");
        View statsCardView = getLayoutInflater().inflate(R.layout.layout_stats_card, linear_feed, true);
        View statsView = statsCardView.findViewById(R.id.stats_root);

        if (HIDE_STATS_CARD)
            statsView.setVisibility(View.GONE);

        Query query = ref.child("userStats").child(currentUser.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Log.d(TAG, "user stats found");

                    userStats = dataSnapshot.getValue(UserStats.class);

                    if (userStats != null) {
                        rounds.setText(String.valueOf(userStats.getNumRounds()));

                        if (userStats.getNumRounds() == 0) {
                            TextView no_stats_data = statsCardView.findViewById(R.id.no_stats_msg);
                            no_stats_data.setVisibility(View.VISIBLE);
                        }
                        else {
                            LinearLayout active_stats_layout = statsCardView.findViewById(R.id.active_stats_layout);
                            active_stats_layout.setVisibility(View.VISIBLE);
                            TextView score_avg = statsCardView.findViewById(R.id.score_AVG);

                            if (userStats.getScoreAVG() == 0)
                                score_avg.setText("E");
                            else
                                score_avg.setText(String.valueOf(userStats.getScoreAVG()));

                            TextView best_round_score = statsCardView.findViewById(R.id.stats_best_score);

                            if (userStats.getBestRoundScore() == 0)
                                best_round_score.setText("E");
                            else
                                best_round_score.setText(String.valueOf(userStats.getBestRoundScore()));

                            if (userStats.getBestRoundDate() != 0) {
                                TextView best_round_date = statsCardView.findViewById(R.id.stats_best_score_date);
                                SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.US);
                                best_round_date.setText(sdf.format(userStats.getBestRoundDate()));
                            }

                            if (userStats.getBestRoundCourse() != null) {

                                Query query = ref.child("courses").child(userStats.getBestRoundCourse());

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {
                                            Log.d(TAG, "best round course found in DB");
                                            bestRoundCourse = dataSnapshot.getValue(Course.class);
                                            TextView best_round_course = statsCardView.findViewById(R.id.stats_best_score_course);
                                            best_round_course.setText(bestRoundCourse.getName());
                                        }
                                        else {
                                            Log.d(TAG, "course not found in DB. Checking personal courses...");

                                            Query query = ref.child("customCourses").child(currentUser.getUid()).child(userStats.getBestRoundCourse());

                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.exists()) {
                                                        Log.d(TAG, "best round course found in personal DB");
                                                        bestRoundCourse = dataSnapshot.getValue(Course.class);
                                                        TextView best_round_course = statsCardView.findViewById(R.id.stats_best_score_course);
                                                        best_round_course.setText(bestRoundCourse.getName());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    loadingDialog.dismiss();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                            else
                                loadingDialog.dismiss();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });
        CardView rootView = statsCardView.findViewById(R.id.stats_root);
//        ImageView stats_card_options = statsCardView.findViewById(R.id.stats_card_options);
//        stats_card_options.setOnClickListener(this);
        rootView.setOnClickListener(this);
    }

    public void getUserFriends() {
        Query query = ref.child("friendsList").child(currentUser.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                    numFriends.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                else
                    loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });
    }

    private void checkNotifications() {
        Query query = ref.child("notifications").child(currentUser.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    notifications.setVisibility(View.VISIBLE);
                else
                    notifications.setVisibility(View.GONE);

                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
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

        if (HIDE_SQUAD_CARD)
            menu.getItem(1).setChecked(true);

        if (HIDE_STATS_CARD)
            menu.getItem(2).setChecked(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case(R.id.edit_profile):
                Intent intent1 = new Intent(this, EditProfileActivity.class);
                intent1.putExtra("mUser", mUser);
                startActivity(intent1);
                break;

            case(R.id.hide_squad_card):
                if (item.isChecked()) {
                    HIDE_SQUAD_CARD = false;
                    item.setChecked(false);
                    View squadCardView = linear_feed.findViewById(R.id.squad_root);
                    squadCardView.setVisibility(View.VISIBLE);
                }
                else {
                    HIDE_SQUAD_CARD = true;
                    item.setChecked(true);
                    View statsCardView = linear_feed.findViewById(R.id.squad_root);
                    statsCardView.setVisibility(View.GONE);
                }
                ApplicationPreferences.getSharedPreferences(this, ApplicationPreferences.HIDE_SQUAD_CARD);
                ApplicationPreferences.setPreference(ApplicationPreferences.HIDE_SQUAD_CARD, HIDE_SQUAD_CARD);
                break;

            case(R.id.hide_stats_card):
                if (item.isChecked()) {
                    HIDE_STATS_CARD = false;
                    item.setChecked(false);
                    View statsCardView = linear_feed.findViewById(R.id.stats_root);
                    statsCardView.setVisibility(View.VISIBLE);
                }
                else {
                    HIDE_STATS_CARD = true;
                    item.setChecked(true);
                    View statsCardView = linear_feed.findViewById(R.id.stats_root);
                    statsCardView.setVisibility(View.GONE);
                }
                ApplicationPreferences.getSharedPreferences(this, ApplicationPreferences.HIDE_STATS_CARD);
                ApplicationPreferences.setPreference(ApplicationPreferences.HIDE_SQUAD_CARD, HIDE_STATS_CARD);
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

    public void displayUserDataRetrievalWarning() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView warningText = new TextView(this);
        warningText.setLayoutParams(params);
        warningText.setPadding(100, 300, 100, 300);
        warningText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        warningText.setTextSize(16);
        warningText.setText("There was a problem retrieving your account information.");
        linear_feed.addView(warningText);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.squad_root):
                break;

            case (R.id.squads_more_info_btn):
                startActivity(new Intent(ProfileActivity.this, SquadActivity.class));
                break;

//            case (R.id.squad_card_options):
//                Toast.makeText(this, "squad options", Toast.LENGTH_SHORT).show();
//                break;
//
//            case (R.id.stats_card_options):
//                Toast.makeText(this, "stats options", Toast.LENGTH_SHORT).show();
//                break;

            case (R.id.stats_root):
                startActivity(new Intent(ProfileActivity.this, StatsActivity.class));
                break;

            case (R.id.notifications):
                Intent intent = new Intent(this, NotificationsActivity.class);
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
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 300);
                    toast.show();
                    finish();
                    break;
                }
                else
                    Toast.makeText(ProfileActivity.this, "failed to sign out.", Toast.LENGTH_LONG).show();
        }
    }
}

