package com.example.dsawyer.maddscore.ScoreCards;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dsawyer.maddscore.CHIPP.CHIPPActivity;
import com.example.dsawyer.maddscore.Events.EventsActivity;
import com.example.dsawyer.maddscore.Help.HelpActivity;
import com.example.dsawyer.maddscore.Leaderboards.LeaderboardsActivity;
import com.example.dsawyer.maddscore.Objects.CardObject;
import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.Objects.Scorecard;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Other.LoginActivity;
import com.example.dsawyer.maddscore.Other.SettingsActivity;
import com.example.dsawyer.maddscore.Other.NotificationsActivity;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.BottomNavViewHelper;
import com.example.dsawyer.maddscore.Utils.ScorecardRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ScorecardActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {
    private static final String TAG = "TAG";
    public static final int ACTIVITY_NUM = 5;


    private DatabaseReference ref;
    private String UID;

    Button new_card, log_out;
    ProgressBar progressBar;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ScorecardRecyclerView adapter;

    final int load_count = 21;
    int total = 0, last_visible_item;
    boolean isLoading = false, isMax = false;
    String lastNode, lastKey;
    ArrayList<String> cardIDs;
    ArrayList<Scorecard> scorecards;
    ArrayList<User> cardPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scorecard);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        log_out = findViewById(R.id.logout);
        new_card = findViewById(R.id.new_card_button);
        progressBar = findViewById(R.id.progressBar);

        log_out.setOnClickListener(this);
        new_card.setOnClickListener(this);

        setUpBottomNavView();
        setUpNavDrawer();

        getLastKey();

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ScorecardRecyclerView(this);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                total = layoutManager.getItemCount();
                last_visible_item = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && total <= (last_visible_item + load_count)) {
                    getCards();
                    isLoading = true;
                }

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    public void getLastKey() {
        Log.d(TAG, "getLastKey: ");
        Query query = ref.child("scorecards").child(UID).orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d(TAG, "last child key for user scorecards : " + ds.getKey());
                    lastKey = ds.getKey();
                }

                getCards();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getCards() {
        cardPlayers = new ArrayList<>();
        scorecards = new ArrayList<>();
        cardIDs = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        if (!isMax) {
            final Query query;
            if (TextUtils.isEmpty(lastNode))
                query = ref.child("scorecards").child(UID).limitToFirst(load_count);
            else
                query = ref.child("scorecards").child(UID).startAt(lastNode).limitToFirst(load_count);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     if (dataSnapshot.hasChildren()) {
                         cardIDs.clear();
                         for (DataSnapshot ds : dataSnapshot.getChildren()) {
                             if (ds.getKey() != null)
                                 cardIDs.add(ds.getKey());
                         }

                         lastNode = cardIDs.get((cardIDs.size() - 1));

                         Query query1;
                         for (int i = 0; i < cardIDs.size(); i++) {
                             query1 = ref.child("cardObjects").child(cardIDs.get(i));

                             query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                     if (dataSnapshot.exists()) {
                                         CardObject cardObject = dataSnapshot.getValue(CardObject.class);

                                         if (cardObject != null) {
                                             Query courseQuery = ref.child("courses").child(cardObject.getCourseID());

                                             courseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                 @Override
                                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                     scorecards.clear();
                                                     if (dataSnapshot.exists()) {
                                                         Course course = dataSnapshot.getValue(Course.class);
                                                         if (course != null) {
                                                             scorecards.add(new Scorecard(
                                                                     cardObject,
                                                                     course));
                                                         }
                                                         adapter.addItems(scorecards);
                                                     }
                                                     else {
                                                         Query customCourseQuery = ref.child("customCourses").child(UID).child(cardObject.getCourseID());
                                                         customCourseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                             @Override
                                                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                 scorecards.clear();
                                                                 if (dataSnapshot.exists()) {
                                                                     Course course = dataSnapshot.getValue(Course.class);
                                                                     if (course != null) {
                                                                         scorecards.add(new Scorecard(
                                                                                 cardObject,
                                                                                 course));
                                                                     }
                                                                     adapter.addItems(scorecards);
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
                                     }
                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError databaseError) {

                                 }
                             });
                         }
                         progressBar.setVisibility(View.GONE);
                         isLoading = false;

                     }
                     else {
                         Log.d(TAG, "onDataChange: has NO children");
                         progressBar.setVisibility(View.GONE);
                         isLoading = false;
                         isMax = true;

                     }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;
                }
            });
        }
        else {
            progressBar.setVisibility(View.GONE);
            isLoading = false;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.scorecard_activity);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scorecard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case(R.id.edit_scorecards):
                setFragment(new ScorecardEditFragment());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpNavDrawer() {
        Toolbar toolbar = findViewById(R.id.card_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.scorecard_activity);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpBottomNavView() {
        BottomNavigationView mMainNav = findViewById(R.id.main_nav);
        Menu menu = mMainNav.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);
        BottomNavViewHelper.enableBottomNavView(this, mMainNav);
        overridePendingTransition(0,0);
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

        DrawerLayout drawer = findViewById(R.id.scorecard_activity);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragment(final Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.card_content_frame, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.new_card_button):
                Intent newCardIntent = new Intent(this, NewScorecardCourseSelectActivity.class);
                startActivity(newCardIntent);
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
                else
                    Toast.makeText(ScorecardActivity.this, "An error occurred.", Toast.LENGTH_LONG).show();

                break;
        }
    }
}
