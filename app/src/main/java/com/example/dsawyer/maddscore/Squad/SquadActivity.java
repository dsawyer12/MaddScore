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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsawyer.maddscore.CHIPP.CHIPPActivity;
import com.example.dsawyer.maddscore.Events.EventsActivity;
import com.example.dsawyer.maddscore.Help.HelpActivity;
import com.example.dsawyer.maddscore.Leaderboards.LeaderboardsActivity;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Other.LoginActivity;
import com.example.dsawyer.maddscore.Other.NotificationsActivity;
import com.example.dsawyer.maddscore.Other.SettingsActivity;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Social.PostFragment;
import com.example.dsawyer.maddscore.Utils.BottomNavViewHelper;
import com.example.dsawyer.maddscore.Utils.LoadingDialog;
import com.example.dsawyer.maddscore.Utils.SectionsPagerAdapter;
import com.example.dsawyer.maddscore.Utils.SquadMemberListRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SquadActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SquadMemberListRecyclerViewAdapter.OnSquadMemberClickedListener,
        View.OnClickListener {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private FirebaseUser currentUser;
    private User mUser;
    private Squad squad;

    private boolean hasSquad = false;
    private ImageView squad_events_btn, squad_members_btn;
    private RelativeLayout no_squad_layout;
//    private LoadingDialog loadingDialog;

    @Override
    public void onSquadMemberClicked(User user) {
        Toast.makeText(this, user.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squad);

        squad_events_btn = findViewById(R.id.squad_events_btn);
        squad_members_btn = findViewById(R.id.squad_members_btn);
        no_squad_layout = findViewById(R.id.no_squad_layout);

        findViewById(R.id.logout).setOnClickListener(this);
        findViewById(R.id.join_squad_button).setOnClickListener(this);
        findViewById(R.id.create_squad_button).setOnClickListener(this);
        squad_events_btn.setOnClickListener(this);
        squad_members_btn.setOnClickListener(this);

        setUpBottomNavView();
        setUpNavDrawer();

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            initUserData();
        } else
            Toast.makeText(this, "Something went wrong while trying to retrieve your account data.", Toast.LENGTH_LONG).show();
    }

    private void setUpBottomNavView() {
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

    private void initUserData() {
//        loadingDialog = new LoadingDialog(this);
//        loadingDialog.show();

        Query query = ref.child("users").child(currentUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mUser = dataSnapshot.getValue(User.class);
                    if (mUser != null) {
                        if(mUser.getSquad() != null) {
                            hasSquad = true;
                            invalidateOptionsMenu();
                            initializeSquadData();
                        } else {
//                            loadingDialog.dismiss();
                            no_squad_layout.setVisibility(View.VISIBLE);
//                            setFragment(new NoSquadFragment(), R.id.frame);
                        }
                    }
                    else {
//                        loadingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Something went wrong while trying to retrieve your user data.", Toast.LENGTH_LONG).show();
                    }
                } else {
//                    loadingDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Something went wrong while trying to retrieve your user data.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                loadingDialog.dismiss();
            }
        });
    }

    private void initializeSquadData() {
        Query query = ref.child("squads").child(mUser.getSquad());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    squad = dataSnapshot.getValue(Squad.class);
                    if (squad != null) {
//                        loadingDialog.dismiss();
                        squad_events_btn.setVisibility(View.VISIBLE);
                        squad_members_btn.setVisibility(View.VISIBLE);
                        SocialFragment socialFragment = new SocialFragment();
                        Bundle args = new Bundle();
                        args.putParcelable("squad", squad);
                        args.putParcelable("mUser", mUser);
                        socialFragment.setArguments(args);
                        setFragment(socialFragment, R.id.frame, "socialFragment");
                    } else {
//                        loadingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Something went wrong while trying to retrieve your squad data.", Toast.LENGTH_LONG).show();
                    }
                } else {
//                    loadingDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Something went wrong while trying to retrieve your squad data.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                loadingDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.squad_activity);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (getSupportFragmentManager().findFragmentByTag("postFragment") != null) {
            getSupportFragmentManager().popBackStackImmediate();
            return;
        }

        if (getSupportFragmentManager().findFragmentByTag("squadMemberList") != null) {
            getSupportFragmentManager().popBackStackImmediate();
            return;
        }

        if (getSupportFragmentManager().findFragmentByTag("socialFragment") != null)
            super.onBackPressed();

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
        switch (item.getItemId()) {
            case(R.id.edit_players):
//                setFragment(new PlayersEditFragment());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //            case R.id.copy_MID:
//                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//                ClipData clip = ClipData.newPlainText("MaddScore ID", user.getMID());
//                clipboardManager.setPrimaryClip(clip);
//                break;
//
//            case R.id.share_MID:
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.putExtra(Intent.EXTRA_TEXT, user.getMID());
//                intent.setType("text/plain");
//                startActivity(Intent.createChooser(intent, "MaddScore ID"));
//                break;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
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

    public void setFragment(final Fragment fragment, int id, String tag) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        /********** IF HAVING ISSUES WITH STATE, REPLACE .commit() WITH .commitAllowingStateLoss(). this may produce further issues though *************/
        fragmentManager.beginTransaction().add(id, fragment, tag).addToBackStack(null).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.logout):
                FirebaseAuth.getInstance().signOut();
                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast toast= Toast.makeText(getApplicationContext(),"Successfully Logged Out!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 300);
                    toast.show();
                    finish();
                    break;
                }
                else Toast.makeText(getApplicationContext(), "failed to sign out.", Toast.LENGTH_LONG).show();
                break;

            case(R.id.squad_events_btn):
                break;

            case(R.id.squad_members_btn):
                SquadListFragment squadListFragment = new SquadListFragment();
                Bundle args = new Bundle();
                args.putParcelable("mSquad", squad);
                args.putParcelable("mUser", mUser);
                squadListFragment.setArguments(args);
                setFragment(squadListFragment, R.id.main_frame, "squadMemberList");
                break;

            case(R.id.join_squad_button):
                startActivity( new Intent(this, JoinSquadActivity.class));
                break;

            case(R.id.create_squad_button):
                startActivity(new Intent(this, CreateSquadActivity.class));
                break;
        }
    }
}
























