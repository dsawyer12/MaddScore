package com.example.dsawyer.maddscore.Other;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.Notification;
import com.example.dsawyer.maddscore.Profile.ProfileActivity;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Utils.NotificationRecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity implements
        View.OnClickListener, NotificationRecyclerView.OnRequestClickListener {
    private static final String TAG = "TAG";
    private static final int FRIEND_REQUEST = 1;
    private static final int SQUAD_INVITE = 2;
    private static final int SQUAD_REQUEST = 3;
    private static final int MESSAGE = 4;

    private DatabaseReference ref;
    private String UID;

    private NotificationRecyclerView adapter;
    private NotificationRecyclerView.OnRequestClickListener listener;
    private ArrayList<Notification> myNotifications;

    private RecyclerView recyclerView;
    RelativeLayout no_notifications;
    ImageView go_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        myNotifications = new ArrayList<>();

        recyclerView = findViewById(R.id.notificationRecyclerView);
        no_notifications = findViewById(R.id.no_notifications);
        go_back = findViewById(R.id.go_back);
        go_back.setOnClickListener(this);

        ref = FirebaseDatabase.getInstance().getReference();
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            checkNotifications();
        }

        listener = new NotificationRecyclerView.OnRequestClickListener() {
            @Override
            public void onRequestClicked(Notification n) {
                if (n.getNotificationType() == Notification.MESSAGE) {
                    Toast.makeText(NotificationsActivity.this, n.getSnippet(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onRequestAccept(Notification n) {
                switch (n.getNotificationType()) {
                    case(FRIEND_REQUEST):
                        acceptFriendRequest(n);
                        break;
                    case(SQUAD_INVITE):
                        acceptSquadInvite(n);
                        break;
                    case(SQUAD_REQUEST):
                        acceptSquadRequest(n);
                        break;
                }
            }

            @Override
            public void onRequestDeclined(Notification n) {
                declineRequest(n);
            }
        };
    }

    public void acceptFriendRequest(final Notification n) {
        Log.d(TAG, "acceptFriendRequest: ");

        // add mUser to current users friendList
        ref.child("friendsList").child(UID).child(n.getSender()).setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "current users friendsList updated");

                            //add current user to mUsers friendsList
                            ref.child("friendsList").child(n.getSender()).child(UID).setValue(true)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "mUsers friendsList updated");

                                                // remove the notification from database
                                                removeNotification(n);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void acceptSquadInvite(final Notification n) {
        Log.d(TAG, "acceptSquadInvite: ");

        //get current users squad
        Query query = ref.child("squads").child(n.getSquadID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final Squad squad = dataSnapshot.getValue(Squad.class);
                    if (squad != null) {
                        Log.d(TAG, "Squad was found");

                        // add user to squad user list
                        ref.child("squads").child(squad.getSquadID()).child("userList").child(UID).setValue(true)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "user was added to squad userList");

                                            //add squadID to user profile
                                            ref.child("users").child(UID).child("mySquad").setValue(squad.getSquadID())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "users mySquad node was updated");

                                                                //add squad rank to user profile
                                                                ref.child("userStats").child(UID).child("squadRank").setValue(squad.getMemberList().size() + 1)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Log.d(TAG, "users rank has been set");
                                                                                    removeNotification(n);
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });

                                        }
                                    }
                                });
                    }
                }
                else{
                    Toast.makeText(NotificationsActivity.this, "User squad not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void acceptSquadRequest(final Notification n) {
        Log.d(TAG, "acceptSquadRequest: ");

        //get current users squad
        Query query = ref.child("squads").child(n.getSquadID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final Squad squad = dataSnapshot.getValue(Squad.class);
                    if (squad != null) {
                        Log.d(TAG, "found squad");

                        // add user to squad user list
                        ref.child("squads").child(squad.getSquadID()).child("userList").child(n.getSender()).setValue(true)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "user was added to squad userList");

                                            //add squadID to user profile
                                            ref.child("users").child(n.getSender()).child("mySquad").setValue(squad.getSquadID())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "users mySquad node was updated");

                                                                //add squad rank to user profile
                                                                ref.child("userStats").child(n.getSender()).child("squadRank").setValue(squad.getMemberList().size() + 1)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Log.d(TAG, "users rank has been set");
                                                                                    removeNotification(n);
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });

                                        }
                                    }
                                });
                    }
                }
                else {
                    Toast.makeText(NotificationsActivity.this, "User squad not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void declineRequest(final Notification n) {
        Log.d(TAG, "declineRequest: ");

        ref.child("notifications").child(UID).child(n.getNotificationID()).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            removeActiveRequest(n);
                    }
                });
    }

    public void removeNotification(final Notification n) {
        Log.d(TAG, "removeNotification: ");
        ref.child("notifications").child(UID).child(n.getNotificationID()).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "notification removed from database");
                            removeActiveRequest(n);
                        }
                    }
                });
    }

    public void removeActiveRequest(final Notification n) {
        Log.d(TAG, "removeActiveRequest: ");

        ref.child("activeRequests").child(n.getSender()).child(n.getNotificationID()).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "active request removed from database");
                            // update recyclerView
                            myNotifications.remove(n);
                            if (myNotifications.isEmpty()) {
                                no_notifications.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                            }
                            else {
                                no_notifications.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    private void checkNotifications() {
        Log.d(TAG, "checkNotifications: ");
        Query query = ref.child("notifications").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Notification notification;
                if (dataSnapshot.exists()) {
                    myNotifications.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        notification = ds.getValue(Notification.class);
                        if (notification != null)
                            myNotifications.add(notification);
                    }
                    if (!myNotifications.isEmpty()) {
                        adapter = new NotificationRecyclerView(NotificationsActivity.this, listener, myNotifications);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(NotificationsActivity.this));
                    }
                    else
                        no_notifications.setVisibility(View.VISIBLE);
                }
                else
                    no_notifications.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NotificationsActivity.this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.go_back):
                Intent intent = new Intent(NotificationsActivity.this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRequestClicked(Notification n) {}
    @Override
    public void onRequestAccept(Notification n) {}
    @Override
    public void onRequestDeclined(Notification n) {}
}
