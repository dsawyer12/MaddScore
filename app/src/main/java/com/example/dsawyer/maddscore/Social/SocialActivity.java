package com.example.dsawyer.maddscore.Social;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dsawyer.maddscore.CHIPP.CHIPPActivity;
import com.example.dsawyer.maddscore.Events.EventsActivity;
import com.example.dsawyer.maddscore.Help.HelpActivity;
import com.example.dsawyer.maddscore.Leaderboards.LeaderboardsActivity;
import com.example.dsawyer.maddscore.Objects.Post;
import com.example.dsawyer.maddscore.Other.NotificationsActivity;
import com.example.dsawyer.maddscore.Other.SettingsActivity;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.BottomNavViewHelper;
import com.example.dsawyer.maddscore.Utils.PostListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SocialActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "TAG";
    private Context mContext = SocialActivity.this;

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private String UID;
    private User mUser;

    private ArrayList<Post> posts;
    RelativeLayout relativeLayout;
    ListView postList;
    PostListAdapter adapter;

    public SocialActivity(){

    }

//    @Override
//    public void postComment(String post_id, String comment) {
//        String commentID = ref.child("postCommentList").child(post_id).push().getKey();
//        ref.child("postCommentList").child(post_id).child(commentID).child("user").setValue(UID);
//        ref.child("postCommentList").child(post_id).child(commentID).child("comment").setValue(comment);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        ref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getCurrentUser().getUid();

        relativeLayout = findViewById(R.id.relLayout);
        postList = findViewById(R.id.post_list);
        posts = new ArrayList<>();
        setUpBottomNavView();
        setUpNavDrawer();
        getUser();

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new PostFragment());
            }
        });
    }

    public void setUpBottomNavView(){
        BottomNavigationView mMainNav = findViewById(R.id.main_nav);
//        BottomNavViewHelper.disableShiftMode(mMainNav);
        Menu menu = mMainNav.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        BottomNavViewHelper.enableBottomNavView(mContext, mMainNav);
        overridePendingTransition(0,0);
    }

    public void setUpNavDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.social_activity);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.social_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

        DrawerLayout drawer = findViewById(R.id.social_activity);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.social_activity);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

    public void getUser(){
        Query query = ref.child("users").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.equals(dataSnapshot.getKey(), UID)){
                    mUser = dataSnapshot.getValue(User.class);
                    getSocial();
                }
                else{
                    relativeLayout.setVisibility(View.VISIBLE);
                    //handle
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getSocial(){
        posts.clear();
        Query query = ref.child("socialPosts").child(mUser.getMySquad());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Post post = ds.getValue(Post.class);
                    posts.add(post);
                }
                Query query1 = ref.child("postCommentList").child(mUser.getMySquad());
                adapter = new PostListAdapter(SocialActivity.this, mUser.getUserID(), posts);
                postList.setAdapter(adapter);
                Log.d(TAG, "posts size : " + posts.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (view.getId()){
//                    case (R.id.post_join):
//                        if (posts.get(position).getUserlist() != null){
//                            if (posts.get(position).getUserlist().containsKey(mUser.getUserID())){
//                                posts.get(position).getUserlist().remove(mUser.getUserID());
//                                ref.child("socialPosts").child(mUser.getMySquad()).child(posts.get(position).getPostKey()).child("userlist").child(mUser.getUserID()).removeValue();
//                                adapter.notifyDataSetChanged();
//                            }
//                            else{
//                                ref.child("socialPosts").child(mUser.getMySquad()).child(posts.get(position).getPostKey()).child("userlist").child(mUser.getUserID()).setValue(true);
//                                posts.get(position).getUserlist().put(mUser.getUserID(), true);
//                                adapter.notifyDataSetChanged();
//                            }
//                        }
//                        else{
//                            HashMap<String, Boolean> userList = new HashMap<>();
//                            userList.put(mUser.getUserID(), true);
//                            posts.get(position).setUserlist(userList);
//                            ref.child("socialPosts").child(mUser.getMySquad()).child(posts.get(position).getPostKey()).child("userlist").child(mUser.getUserID()).setValue(true);
//                            adapter.notifyDataSetChanged();
//                        }
//
//                        break;
//                    case (R.id.post_members_joined):
//                        Bundle args = new Bundle();
//                        ArrayList<String> users = new ArrayList<>(posts.get(position).getUserlist().keySet());
//                        args.putStringArrayList("userlist", users);
//                        PostMemberListDialog list = new PostMemberListDialog();
//                        list.setArguments(args);
//                        list.show(getFragmentManager(), "PostMemberListDialog");
//                        break;

                    case (R.id.post_liked):
                        if (posts.get(position).getUserlist() != null){
                            if (posts.get(position).getUserlist().containsKey(mUser.getUserID())){
                                posts.get(position).getUserlist().remove(mUser.getUserID());
                                //change color
                                //cahnge likes text
                                ref.child("socialPosts").child(mUser.getMySquad()).child(posts.get(position).getPostKey()).child("userlist").child(mUser.getUserID()).removeValue();
                                adapter.notifyDataSetChanged();
                            }
                            else{
                                //change color
                                //cahnge likes text
                                ref.child("socialPosts").child(mUser.getMySquad()).child(posts.get(position).getPostKey()).child("userlist").child(mUser.getUserID()).setValue(true);
                                posts.get(position).getUserlist().put(mUser.getUserID(), true);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        else{
                            HashMap<String, Boolean> userList = new HashMap<>();
                            userList.put(mUser.getUserID(), true);
                            posts.get(position).setUserlist(userList);
                            ref.child("socialPosts").child(mUser.getMySquad()).child(posts.get(position).getPostKey()).child("userlist").child(mUser.getUserID()).setValue(true);
                            adapter.notifyDataSetChanged();
                        }
                        Toast.makeText(SocialActivity.this, "liked", Toast.LENGTH_SHORT).show();
                        break;

                    case (R.id.post_comment):
                       PostCommentDialog pc = new PostCommentDialog();
                       String postKey = posts.get(position).getPostKey();
                       Bundle args = new Bundle();
                       args.putString("post_key", postKey);
                       Log.d(TAG, "POST_KEY: " + postKey);
                       pc.setArguments(args);
//                       pc.show(getFragmentManager(), "MyCustomDialog");
                       break;
                }
            }
        });
    }

    public void setFragment(final Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.social_content_frame, fragment).addToBackStack(null).commit();
    }
}
