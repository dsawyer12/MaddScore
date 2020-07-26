package com.example.dsawyer.maddscore.Squad;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.Notification;
import com.example.dsawyer.maddscore.Objects.Pin;
import com.example.dsawyer.maddscore.Objects.Post;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.SectionsStatePagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class SquadSearchSelectionFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";
    private static final int NOTIFICATION_TYPE = 3;

    private DatabaseReference ref;
    private FirebaseUser currentUser;
    private User mUser;

    private Squad squad;
    private ArrayList<String> memberIds;
    private ArrayList<User> members;

    ListView squad_list;

    ViewPager viewPager;
    SectionsStatePagerAdapter pagerAdapter;
    ProgressBar progressBar;
    TextView squad_name;
    Button request_join;
    ImageView back_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_squad_search_selection, container, false);

        /** Everything displayed here needs to respect the squads privacy rules set **/
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewpager);
        progressBar = view.findViewById(R.id.progressBar);
        squad_list = view.findViewById(R.id.squad_list);
        squad_name = view.findViewById(R.id.squad_name);
        back_btn = view.findViewById(R.id.back_btn);
        request_join = view.findViewById(R.id.request_join);

        back_btn.setOnClickListener(this);
        request_join.setOnClickListener(this);

        progressBar.setVisibility(View.VISIBLE);

        setupViewPager(view);

        if (this.getArguments() != null) {
            if ((squad = this.getArguments().getParcelable("squad")) != null)
                initialize();
        } else
            Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    public void setupViewPager(View view) {
        pagerAdapter = new SectionsStatePagerAdapter(getChildFragmentManager());

        pagerAdapter.addFragment(new AboutSquadSubFragment());
        pagerAdapter.addFragment(new SquadMembersSubFragment());
        pagerAdapter.addFragment(new SquadEventsSubFragment());

        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("About");
        tabLayout.getTabAt(1).setText("Members");
        tabLayout.getTabAt(2).setText("Events");

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(ContextCompat.getColor(getActivity(), R.color.grey));
            drawable.setSize(2, 0);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
    }

    private void initialize() {
        squad_name.setText(squad.getSquadName());

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                Query query = ref.child(getString(R.string.firebase_node_users)).child(currentUser.getUid());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            mUser = dataSnapshot.getValue(User.class);
                            if (mUser != null) {
                                request_join.setEnabled(true);

                                /** Get data for AboutSquadSubFragment first, then onComplete, call getSquadMembers. Lastly, get data for SquadEventsSubFragment **/
                                getSquadAbout();

//                                getSquadMembers();
                            }else {
                                request_join.setEnabled(false);
                                Toast.makeText(getActivity(), "There was a problem retrieving the users data.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            request_join.setEnabled(false);
                            Toast.makeText(getActivity(), "There was a problem retrieving the users data.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                request_join.setEnabled(false);
                Toast.makeText(getActivity(), "There was a problem retrieving your data.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getSquadAbout() {
        /**
         I am not going to check for empty string here, so i need to make sure whenever a user removes a pin instead of replacing it, to
         remove that node entirely from the database. that way it will either be NULL or !NULL.
         **/
        if (squad.getPinID() != null) {
            Query pinQuery = ref.child("pins").child(squad.getPinID());

            pinQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Pin pin = dataSnapshot.getValue(Pin.class);

                        if (pin != null) {
                            Query postQuery = ref.child("socialPosts").child(squad.getSquadID()).child(pin.getPostID());

                            postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Post post = dataSnapshot.getValue(Post.class);

                                        if (post != null) {
                                            pin.setPost(post);

                                            Query userQuery = ref.child("users").child(post.getCreatorID());

                                            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        User user = dataSnapshot.getValue(User.class);

                                                        if (user != null) {
                                                            initAboutFragment(squad, pin, user);

                                                        } else {
                                                            initAboutFragment(squad, pin, null);
                                                            Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        initAboutFragment(squad, pin, null);
                                                        Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    } else {
                                        initAboutFragment(squad, pin, null);
                                        Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            initAboutFragment(squad, null, null);
                            Toast.makeText(getActivity(), "Could not retrieve pin", Toast.LENGTH_SHORT).show();
                        }
                    } else
                        initAboutFragment(squad, null, null);

                    getSquadMembers();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            initAboutFragment(squad, null, null);
            getSquadMembers();
        }

    }

    public void initAboutFragment(Squad squad, Pin pin, User user) {
        Fragment childFragment;

        if ((childFragment = pagerAdapter.getItem(0)) != null && childFragment instanceof AboutSquadSubFragment)
            ((AboutSquadSubFragment) childFragment).initData(squad, pin, user);
        else
            Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    public void getSquadMembers() {
        Log.d(TAG, "getSquadMembers: called");

        memberIds = new ArrayList<>();
        members = new ArrayList<>();

        memberIds.addAll(squad.getMemberList().keySet());

        Query query = ref.child(getString(R.string.firebase_node_users)).orderByKey();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                members.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (memberIds.contains(ds.getKey()))
                        members.add(ds.getValue(User.class));
                }

                Fragment childFragment;

                if ((childFragment = pagerAdapter.getItem(1)) != null && childFragment instanceof SquadMembersSubFragment)
                    ((SquadMembersSubFragment) childFragment).setMemberList(members);
                 else
                    Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendJoinRequest() {
        long date = System.currentTimeMillis();
        String notification_id = ref.child(getString(R.string.notifications)).push().getKey();

        Notification notification = new Notification(
                notification_id,
                mUser.getUserID(),
                squad.getSquadID(),
                squad.getCreatorId(),
                NOTIFICATION_TYPE,
                date);

        ref.child(getString(R.string.notifications)).child(squad.getCreatorId()).child(notification_id).setValue(notification)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        request_join.setEnabled(true);
                        Toast.makeText(getActivity(), "Request Sent!", Toast.LENGTH_SHORT).show();
                        exit();
                    }
                }).addOnFailureListener(e -> {
                    request_join.setEnabled(true);
                    Toast.makeText(getActivity(), "Failed to Send Request", Toast.LENGTH_SHORT).show();
                });
    }

    public void exit() {
        Intent intent = new Intent(getActivity(), SquadActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.back_btn):
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStackImmediate();
                break;
            case(R.id.request_join):
                if (mUser != null && squad != null){
                    request_join.setEnabled(false);
                    sendJoinRequest();
                }
                else{
                    Toast.makeText(getActivity(), "cannot find squad data", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
