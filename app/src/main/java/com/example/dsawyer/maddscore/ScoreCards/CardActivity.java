package com.example.dsawyer.maddscore.ScoreCards;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.Objects.Player;
import com.example.dsawyer.maddscore.Objects.Scorecard;
import com.example.dsawyer.maddscore.Objects.CardObject;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.UserStats;
import com.example.dsawyer.maddscore.Utils.SectionsStatePagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CardActivity extends AppCompatActivity implements
        CardParSelectorFragment.OnParSelectedListener,
        View.OnClickListener,
        ConfirmCardQuitDialog.OnConfirmListener,
        ConfirmPlayerAddDialog.OnConfirmAddListener,
        ConfirmPlayerRemoveDialog.OnCardRemovePlayers,
        NoPlayersOnCardDialog.NoPlayerOnQuitListener,
        CardChartsFragment.OnCardFinishedListener
{
    private static final String TAG = "TAG";
    private static final int ACTIVITY_NUM = 6;

    private DatabaseReference ref;
    private String UID;

    private String cardID;
    private Scorecard mCard;
    private ArrayList<UserStats> userStats;
    private Course course;

    private CardAddPlayerDialog addPlayerDialog;
    private CardRemovePlayerDialog removePlayerDialog;

    private TextView courseName, courseDate, courseNumHoles, courseHoleNum, finalChart;
    private Button par;

    private SectionsStatePagerAdapter adapter;
    private OnCardChangeListener cardChangeListener;

    public interface OnCardChangeListener {
        void onParChanged(int par);
    }

    public void setCardChangeListener(OnCardChangeListener listener) {
        Log.d(TAG, "setCardChangeListener: called");
        this.cardChangeListener = listener;
    }


    @Override
    public void onConfirm(ArrayList<User> usersToAdd) {
        Log.d(TAG, "onConfirm: users to add - called");
        if (getSupportFragmentManager().findFragmentByTag("cap") != null)
            addPlayerDialog.dismiss();
        mCard.addPlayers(usersToAdd);
        getAddedUserStats(usersToAdd);
        setUpViewpager(mCard);
    }

    @Override
    public void onPlayersRemoved(ArrayList<User> users) {
        Log.d(TAG, "onPlayersRemoved: called");
        if (getSupportFragmentManager().findFragmentByTag("removePlayerDialog") != null)
            removePlayerDialog.dismiss();
        mCard.removePlayers(users);

        if (mCard.getPlayers().size() == 0) {
            mCard.addPlayers(users);
            NoPlayersOnCardDialog notice = new NoPlayersOnCardDialog();
            notice.show(getSupportFragmentManager(), "NoPlayersOnCard");
        }
        else {

            for (int i = 0; i < mCard.getUsers().size(); i++) {
                Log.d(TAG, "user :" + mCard.getUsers().get(i).getUserID());
            }
            for (int i = 0; i < mCard.getPlayers().size(); i++) {
                Log.d(TAG,"player :" +  mCard.getPlayers().get(i).getUserId());
            }

            removeUserStats(users);
            setUpViewpager(mCard);
        }
    }

    @Override
    public void onQuit(Boolean quit) {
        Log.d(TAG, "onQuit: called");
        if (quit)
            exit();
    }

    @Override
    public void onConfirmEndRound(Boolean confirm) {
        Log.d(TAG, "onConfirmEndRound: called");
        if (confirm)
            saveCard();
        else
            exit();
    }

    @Override
    public void onFinished(Boolean finished) {
        Log.d(TAG, "onFinished: called");
        if (finished)
            saveCard();
        else {
            ConfirmCardQuitDialog quit = new ConfirmCardQuitDialog();
            Bundle args = new Bundle();
            args.putParcelable("card", mCard);
            quit.setArguments(args);
            quit.show(getSupportFragmentManager(), "quitScorecard");
        }
    }

    @Override
    public void onParSelected(int par) {
        Log.d(TAG, "onParSelected: called");
        this.par.setText("Par " + par);
        cardChangeListener.onParChanged(par);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        courseName = findViewById(R.id.card_course_name);
        courseDate = findViewById(R.id.card_date);
        courseNumHoles = findViewById(R.id.card_course_size);
        courseHoleNum = findViewById(R.id.card_current_hole);
        par = findViewById(R.id.card_par_options);
        par.setOnClickListener(this);
        finalChart = findViewById(R.id.final_text);

        InitializeCardInfo();
        setUpViewpager(mCard);
        getUserStats();
    }

    private void InitializeCardInfo() {
        Log.d(TAG, "InitializeCardInfo: called");
        cardID = ref.child("scorecards").child(UID).push().getKey();
        long date = System.currentTimeMillis();

        course = getIntent().getParcelableExtra("course");
        ArrayList<User> cardUsers = getIntent().getParcelableArrayListExtra("users");
        ArrayList<User> cardSquadUsers = getIntent().getParcelableArrayListExtra("squadUsers");
        if (cardSquadUsers != null) {
            cardUsers.addAll(cardSquadUsers);
            Map<String, User> map = new HashMap<>();
            for (User user : cardUsers) {
                String key = user.getUserID();
                if (!map.containsKey(key));
                map.put(key, user);
            }
            Collection<User> finalUsers = map.values();
            cardUsers.clear();
            cardUsers.addAll(finalUsers);
        }

        ArrayList<Player> cardPlayers = new ArrayList<>();
        for (int i = 0; i < cardUsers.size(); i++)
            cardPlayers.add(new Player(cardUsers.get(i).getUserID(), course.getNumHoles()));

        ArrayList<Integer> pars = new ArrayList<>();
        for (int i = 0; i < course.getNumHoles(); i++)
            pars.add(i, 3);

        mCard = new Scorecard(UID,
                cardID,
                course,
                date,
                pars,
                cardUsers,
                cardPlayers);

        courseName.setText(course.getName());
        courseDate.setText(String.valueOf(date));
        courseNumHoles.setText(course.getNumHoles() + "Holes");
        courseHoleNum.setText(String.valueOf(mCard.getCurrentHole() + 1));
        par.setText("Par " + mCard.getPar(mCard.getCurrentHole()));
    }

    private void saveCard() {
        Log.d(TAG, "saveCard: called");
        int curr, endScore;

        for (int i = 0; i < mCard.getPlayers().size(); i++) {
            endScore = 0;
            for (int j = 0; j < mCard.getCourse().getNumHoles(); j++) {
                curr = mCard.getPlayers().get(i).getHoleScore(j);
                endScore += curr;
            }
            mCard.getPlayers().get(i).setEndScore(endScore);
        }

        HashMap<String, Boolean> cardUsers = new HashMap<>();
        for (int i = 0; i < mCard.getUsers().size(); i++)
            cardUsers.put(mCard.getUsers().get(i).getUserID(), true);

        long date = System.currentTimeMillis();
        final CardObject cardObject = new CardObject(
                UID,
                mCard.getCardID(),
                mCard.getCourse().getCourseId(),
                mCard.getPars(),
                date,
                cardUsers,
                mCard.getPlayers());

            ref.child("cardObjects")
                    .child(mCard.getCardID())
                    .setValue(cardObject);

        for (int i = 0; i < mCard.getUsers().size(); i++) {
            ref.child("scorecards")
                    .child(mCard.getUsers().get(i).getUserID())
                    .child(mCard.getCardID())
                    .setValue(true);
        }

        ref.child("scorecards")
                .child(UID)
                .child(mCard.getCardID())
                .setValue(true);

        calculateUserStats();
    }

    public void calculateUserStats() {
        Log.d(TAG, "calculateUserStats: called");
        if (userStats != null) {

            List<User> nonRegisteredUsers = mCard.getUsers()
                    .stream().filter(user -> !user.isRegistered()).collect(Collectors.toList());

            for (int m = 0; m < userStats.size(); m++) {
                for (int i = 0; i < mCard.getPlayers().size(); i++) {
                    if (mCard.getPlayers().get(i).getUserId().equals(userStats.get(m).getUserID())) {
                            Log.d(TAG, "STATS FOR : " + mCard.getPlayers().get(i).getUserId());

                            int holesThrown = 0;
                        for (int j : mCard.getPlayers().get(i).getHoleScores()) {
                            if (j > 0)
                                holesThrown++;
                        }
                        if (holesThrown > 0) {
                            if(userStats.get(m).getNumRounds() == 0) {
                                Log.d(TAG, "user does NOT have previous rounds");
                                userStats.get(m).setBestRoundCourse(mCard.getCourse().getCourseId());
                                userStats.get(m).setBestRoundDate(mCard.getDateCreated());
                                userStats.get(m).setBestRoundScore(mCard.getPlayers().get(i).getTotal());

                                userStats.get(m).setScoreAVG(mCard.getPlayers().get(i).getTotal());
                                userStats.get(m).setNumRounds(1);
                                Log.d(TAG, "new Score AVG : " + (userStats.get(m).getScoreAVG()));
                                Log.d(TAG, "new Number of Rounds played : " + (userStats.get(m).getNumRounds()));
                            }
                            else {
                                Log.d(TAG, "user has previous rounds");
                                if (mCard.getPlayers().get(i).getTotal() < userStats.get(m).getBestRoundScore()) {
                                    Log.d(TAG, "this round was better than the last");
                                    userStats.get(m).setBestRoundCourse(mCard.getCourse().getCourseId());
                                    userStats.get(m).setBestRoundDate(mCard.getDateCreated());
                                    userStats.get(m).setBestRoundScore(mCard.getPlayers().get(i).getTotal());
                                }

                                /*****  New score AVG   *****/
                                double tempScoreAVG;
                                if (userStats.get(m).getScoreAVG() == 0) {
                                    tempScoreAVG = 0.1 * userStats.get(m).getNumRounds();
                                }
                                else {
                                    tempScoreAVG = userStats.get(m).getScoreAVG() * userStats.get(m).getNumRounds();
                                }
                                userStats.get(m).setNumRounds(userStats.get(m).getNumRounds() + 1);
                                userStats.get(m).setScoreAVG( (int) Math.round( (tempScoreAVG + mCard.getPlayers().get(i).getTotal()) / (userStats.get(m).getNumRounds()) ) );
                                Log.d(TAG, "new Score AVG : " + (userStats.get(m).getScoreAVG()));
                                Log.d(TAG, "new Number of Rounds played : " + (userStats.get(m).getNumRounds()));
                            }

                                userStats.get(m).setHolesThrown(userStats.get(m).getHolesThrown() + holesThrown);
                                Log.d(TAG, "Number of holes thrown : " + (holesThrown));

                                /***** New Hole In Ones *****/
                                int numHoleIOnOnes = 0;
                                for (int j = 0; j < mCard.getCourse().getNumHoles(); j++) {
                                    if (mCard.getPlayers().get(i).getHoleScore(j) == 1) {
                                        numHoleIOnOnes++;
                                    }
                                }
                                Log.d(TAG, "# of hole in ones : " + numHoleIOnOnes);
                                userStats.get(m).setHoleInOnes(userStats.get(m).getHoleInOnes() + numHoleIOnOnes);
                                Log.d(TAG, "new total hole in ones : " + (userStats.get(m).getHoleInOnes()));

                                /***** New Eagles *****/
                                int numEagles = 0;
                                for (int j = 0; j < mCard.getCourse().getNumHoles(); j++) {
                                    if (mCard.getPlayers().get(i).getHoleScore(j) == mCard.getPar(j) - 2) {
                                        numEagles++;
                                    }
                                }
                                Log.d(TAG, "# of eagles : " + numEagles);
                                userStats.get(m).setEagles(userStats.get(m).getEagles() + numEagles);
                                Log.d(TAG, "new total eagles : " + (userStats.get(m).getEagles()));

                                /***** New EagleAces *****/
                                int numEagleAces = 0;
                                for (int j = 0; j < mCard.getCourse().getNumHoles(); j++) {
                                    if (mCard.getPlayers().get(i).getHoleScore(j) == 1 &&
                                            mCard.getPlayers().get(i).getHoleScore(j) == mCard.getPar(j) - 2) {
                                        numEagleAces++;
                                    }
                                }
                                Log.d(TAG, "# of eagleAces : " + numEagleAces);
                                userStats.get(m).setEagleAces(userStats.get(m).getEagleAces() + numEagleAces);
                                Log.d(TAG, "new total eagleAces : " + (userStats.get(m).getEagleAces()));

                                /***** New Pars *****/
                                int numPars = 0;
                                for (int j = 0; j < mCard.getCourse().getNumHoles(); j++) {
                                    if (mCard.getPlayers().get(i).getHoleScore(j) == mCard.getPar(j)) {
                                        numPars++;
                                    }
                                }
                                Log.d(TAG, "# of pars : " + numPars);
                                userStats.get(m).setPars(userStats.get(m).getPars() + numPars);
                                Log.d(TAG, "new total pars : " + (userStats.get(m).getPars()));

                                /*****  New Birdies  *****/
                                int numBirdies = 0;
                                for (int j = 0; j < mCard.getCourse().getNumHoles(); j++) {
                                    if (mCard.getPlayers().get(i).getHoleScore(j) == (mCard.getPar(j) - 1)) {
                                        numBirdies++;
                                    }
                                }

                                Log.d(TAG, "# of birdies : " + numBirdies);
                                userStats.get(m).setBirdies(userStats.get(m).getBirdies() + numBirdies);
                                Log.d(TAG, "new total birdies : " + (userStats.get(m).getBirdies()));

                                /***** New Bogies *****/
                                int numBogies = 0;
                                for (int j = 0; j < mCard.getCourse().getNumHoles(); j++) {
                                    if (mCard.getPlayers().get(i).getHoleScore(j) == (mCard.getPar(j)) + 1) {
                                        numBogies++;
                                    }
                                }
                                Log.d(TAG, "# of bogies : " + numBogies);
                                userStats.get(m).setBogies(userStats.get(m).getBogies() + numBogies);
                                Log.d(TAG, "new total bogies : " + (userStats.get(m).getBogies()));

                                /***** New Double Bogie or more *****/
                                int numDoubleBogiesPlus = 0;
                                for (int j = 0; j < mCard.getCourse().getNumHoles(); j++) {
                                    if (mCard.getPlayers().get(i).getHoleScore(j) >= (mCard.getPar(j)) + 2) {
                                        numDoubleBogiesPlus++;
                                    }
                                }
                                Log.d(TAG, "# of Doubles or more : " + numDoubleBogiesPlus);
                                userStats.get(m).setDoublePlus(userStats.get(m).getDoublePlus() + numDoubleBogiesPlus);
                                Log.d(TAG, "new total double plues : " + (userStats.get(m).getDoublePlus()));

                                for (int j = 0; j < nonRegisteredUsers.size(); j++) {
                                    if (mCard.getPlayers().get(i).getUserId().equals(nonRegisteredUsers.get(j).getUserID())) {
                                        Log.d(TAG, "storing stats for non registered user : " + nonRegisteredUsers.get(j).getName());
                                        ref.child("userStats").child(mCard.getPlayers().get(i).getUserId()).setValue(userStats.get(m));
                                    }
                                }
                        }
                    }
                }
            }

            List<User> users = mCard.getUsers()
                    .stream().filter(user -> user.getMySquad() != null)
                    .collect(Collectors.toList());

            HashMap<User, Player> userMatch = new HashMap<>();

            for (int i = 0; i < users.size(); i++) {
                for (int j = 0; j < mCard.getPlayers().size(); j++) {
                    if (mCard.getPlayers().get(j).getUserId().equals(users.get(i).getUserID()))
                        userMatch.put(users.get(i), mCard.getPlayers().get(j));
                }
            }

            NavigableMap<String, HashMap<User, Player>> sortedSquads = new TreeMap<>();
            for (Map.Entry<User, Player> objs : userMatch.entrySet()) {
                HashMap<User, Player> userList = sortedSquads.get(objs.getKey().getMySquad());
                if (userList == null)
                    sortedSquads.put(objs.getKey().getMySquad(), userList = new HashMap<>());
                userList.put(objs.getKey(), objs.getValue());
            }

            Log.d(TAG, "sorted squad list : " + sortedSquads.toString());

            ArrayList<Player> players;
            HashMap<String, UserStats> playerStats;
            for (Map.Entry<String, HashMap<User, Player>> map : sortedSquads.entrySet()) {
                players = new ArrayList<>();
                for (Map.Entry<User, Player> subMap : map.getValue().entrySet()) {
                    players.add(subMap.getValue());
                }
                Collections.sort(players);

                playerStats = new HashMap<>();
                for (int i = 0; i < players.size(); i++) {
                    for (int k = 0; k < userStats.size(); k++) {
                        if (userStats.get(k).getUserID().equals(players.get(i).getUserId()))
                            playerStats.put(players.get(i).getUserId(), userStats.get(k));
                    }
                }

                Log.d(TAG, "player stats list : " + playerStats.toString());


                int n = 0, next = 1;
                while(n < players.size() - 1) {
                    while(next < players.size()) {
                        if (players.get(n).getTotal() < players.get(next).getTotal()
                                && playerStats.get(players.get(n).getUserId()).getSquadRank()
                                > playerStats.get(players.get(next).getUserId()).getSquadRank()) {
                            int tempRank = playerStats.get(players.get(next).getUserId()).getSquadRank();
                            playerStats.get(players.get(next).getUserId()).setSquadRank(playerStats.get(players.get(n).getUserId()).getSquadRank());
                            playerStats.get(players.get(n).getUserId()).setSquadRank(tempRank);
                        }
                        next++;
                    }
                    n++;
                    next = (n + 1);
                }

                for (Map.Entry<String, UserStats> entry : playerStats.entrySet()) {
                    Log.d(TAG, "Rank for : " + entry.getKey() + " : " + entry.getValue().getSquadRank());
                    ref.child("userStats").child(entry.getKey()).setValue(entry.getValue());
                }
            }
        }
        else
            Log.d(TAG, "stats list is null");

        exit();
    }

    private void getUserStats() {
        Log.d(TAG, "getUserStats: called");
        userStats = new ArrayList<>();
        Query query = ref.child("userStats");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserStats stats;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    stats = ds.getValue(UserStats.class);
                    if (stats != null) {
                        for (int i = 0; i < mCard.getUsers().size(); i++) {
                            if (stats.getUserID().equals(mCard.getUsers().get(i).getUserID())) {
                                userStats.add(stats);
                                Log.d(TAG, "Retrieved " + mCard.getUsers().get(i).getName() + "'s stats");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getAddedUserStats(final ArrayList<User> users) {
        Log.d(TAG, "getAddedUserStats: called");
        Query query = ref.child("userStats");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    UserStats stats = ds.getValue(UserStats.class);
                    for (int i = 0; i < users.size(); i++) {
                        if (userStats != null && stats != null && stats.getUserID().equals(users.get(i).getUserID()))
                            userStats.add(stats);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removeUserStats(ArrayList<User> users) {
        Log.d(TAG, "removeUserStats: called");
        if (userStats != null) {
            Iterator<UserStats> iterator = userStats.iterator();
            while (iterator.hasNext()) {
                UserStats stats = iterator.next();
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getUserID().equals(stats.getUserID())) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case(R.id.finish_card):
                ConfirmCardQuitDialog quit = new ConfirmCardQuitDialog();
                Bundle args = new Bundle();
                args.putParcelable("cardObject", mCard);
                quit.setArguments(args);
                quit.show(getSupportFragmentManager(), "quitScorecard");
                break;
            case(R.id.add_player):
                addPlayerDialog = new CardAddPlayerDialog();
                Bundle args1 = new Bundle();
                args1.putParcelable("mCard", mCard);
                addPlayerDialog.setArguments(args1);
                addPlayerDialog.show(getSupportFragmentManager(), "cap");
                break;
            case(R.id.remove_player):
                removePlayerDialog = new CardRemovePlayerDialog();
                Bundle args2 = new Bundle();
                args2.putParcelable("mCard", mCard);
                removePlayerDialog.setArguments(args2);
                removePlayerDialog.show(getSupportFragmentManager(), "removePlayerDialog");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setFragment(final Fragment fragment, String tag){
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.card_content_frame, fragment, tag).addToBackStack(null).commit();

    }

    private void setUpViewpager(final Scorecard mCard) {
        ViewPager viewPager = findViewById(R.id.viewpager_container);

        adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < course.getNumHoles(); i++) {
            adapter.addFragment(new CardPlayersFragment(mCard));
        }
        adapter.addFragment(new CardChartsFragment(ACTIVITY_NUM, mCard));

        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < mCard.getCourse().getNumHoles(); i++)
            tabLayout.getTabAt(i).setText(String.valueOf(i + 1));

       tabLayout.getTabAt(mCard.getCourse().getNumHoles()).setText("Final");

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(ContextCompat.getColor(this, R.color.black));
            drawable.setSize(1, 0);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mCard.setCurrentHole(tab.getPosition());
                if (tab.getPosition() == mCard.getCourse().getNumHoles()) {
                    courseHoleNum.setText("-");
                    par.setVisibility(View.INVISIBLE);
                    finalChart.setVisibility(View.VISIBLE);
                }
                else{
                    courseHoleNum.setText(String.valueOf(tab.getPosition() + 1));
                    par.setText("Par " + mCard.getPar(tab.getPosition()));
                    par.setVisibility(View.VISIBLE);
                    finalChart.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void exit() {
        Log.d(TAG, "exit: called");
        Intent intent = new Intent(this, ScorecardActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        ConfirmCardQuitDialog quit = new ConfirmCardQuitDialog();
        Bundle args = new Bundle();
        args.putParcelable("card", mCard);
        quit.setArguments(args);
        quit.show(getSupportFragmentManager(), "quitScorecard");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.card_par_options):
                CardParSelectorFragment parSelect = new CardParSelectorFragment();
                parSelect.show(getSupportFragmentManager(), "parSelector");
                break;
        }
    }
}

