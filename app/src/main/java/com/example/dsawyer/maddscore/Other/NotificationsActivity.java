package com.example.dsawyer.maddscore.Other;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dsawyer.maddscore.ObjectMaps.NotificationUserMap;
import com.example.dsawyer.maddscore.Objects.Notification;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Profile.ProfileActivity;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Utils.LoadingDialog;
import com.example.dsawyer.maddscore.Utils.NotificationRecyclerView;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

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
    private ArrayList<NotificationUserMap> myNotifications;

    private RecyclerView recyclerView;
    RelativeLayout no_notifications;
    ImageView go_back;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        myNotifications = new ArrayList<>();

        loadingDialog = new LoadingDialog(this);
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
            public void onRequestClicked(NotificationUserMap n) {

                if (n.getNotification().getNotificationType() == Notification.MESSAGE) {
                    Toast.makeText(NotificationsActivity.this, n.getNotification().getSnippet(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onRequestAccept(NotificationUserMap n) {
                switch (n.getNotification().getNotificationType()) {
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
            public void onRequestDeclined(NotificationUserMap n) {
                declineRequest(n);
            }
        };
    }

    public void acceptFriendRequest(final NotificationUserMap n) {
        Log.d(TAG, "acceptFriendRequest: called");
        loadingDialog.show();

        HashMap<String, Object> map = new HashMap<>();
        map.put("/friendsList/" + UID + "/" + n.getNotification().getSenderID(), true);
        map.put("/friendsList/" + n.getNotification().getSenderID() + "/" + UID, true);

        ref.updateChildren(map).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Log.d(TAG, "friendsList updated");
                removeNotification(n);
            } else {
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        }).addOnCanceledListener(() -> loadingDialog.dismiss());
    }

    public void acceptSquadInvite(final NotificationUserMap n) {
        Log.d(TAG, "acceptSquadInvite: called");
        loadingDialog.show();

        Query query = ref.child("squads").child(n.getNotification().getSquadID());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    final Squad squad = dataSnapshot.getValue(Squad.class);

                    if (squad != null) {
                        Log.d(TAG, "Squad was found");

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("/squads/" + squad.getSquadID() + "/memberList/" + UID, true);
                        map.put("/users/" + UID + "/squad", squad.getSquadID());
                        map.put("/users/" + UID + "/squadRank", squad.getMemberList().size() + 1);

                        ref.updateChildren(map).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Squad data updated.");
                                removeNotification(n);
                            }
                        }).addOnCanceledListener(() -> loadingDialog.dismiss());
                    } else {
                        Log.d(TAG, "onDataChange: null squad");
                        loadingDialog.dismiss();
                    }
                }
                else {
                    Toast.makeText(NotificationsActivity.this, "User squad not found", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });
    }

    public void acceptSquadRequest(final NotificationUserMap n) {
        Log.d(TAG, "acceptSquadRequest: called");
        loadingDialog.show();

        Query query = ref.child("squads").child(n.getNotification().getSquadID());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    final Squad squad = dataSnapshot.getValue(Squad.class);

                    if (squad != null) {
                        Log.d(TAG, "found squad");

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("/squads/" + squad.getSquadID() + "/memberList/" + n.getNotification().getSenderID(), true);
                        map.put("/users/" + n.getNotification().getSenderID() + "/squad", squad.getSquadID());
                        map.put("/users/" + n.getNotification().getSenderID() + "/squadRank", squad.getMemberList().size() + 1);

                        ref.updateChildren(map).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "squad data updated");
                                removeNotification(n);
                            }
                        }).addOnCanceledListener(() -> loadingDialog.dismiss());
                    } else {
                        Log.d(TAG, "onDataChange: null squad");
                        loadingDialog.dismiss();
                    }
                }
                else {
                    Toast.makeText(NotificationsActivity.this, "User squad not found", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });
    }

    public void declineRequest(final NotificationUserMap n) {
        Log.d(TAG, "declineRequest: called");
        loadingDialog.show();

        ref.child("notifications").child(UID).child(n.getNotification().getNotificationID()).removeValue()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful())
                        removeActiveRequest(n);
                    else {
                        Log.d(TAG, "declineRequest: task unsuccessful");
                        loadingDialog.dismiss();
                    }
                });
    }

    public void removeNotification(final NotificationUserMap n) {
        Log.d(TAG, "removeNotification: called");

        ref.child("notifications").child(UID).child(n.getNotification().getNotificationID()).removeValue()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "notification removed from database");
                        removeActiveRequest(n);
                    } else {
                        Log.d(TAG, "removeNotification: task unsuccessful");
                        loadingDialog.dismiss();
                    }
                });
    }

    public void removeActiveRequest(final NotificationUserMap n) {
        Log.d(TAG, "removeActiveRequest: called");

        ref.child("activeRequests").child(n.getNotification().getSenderID()).child(n.getNotification().getNotificationID()).removeValue()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "active request removed from database");

                        myNotifications.remove(n);

                        if (myNotifications.isEmpty())
                            no_notifications.setVisibility(View.VISIBLE);
                        else
                            no_notifications.setVisibility(View.GONE);

                        adapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                    } else {
                        Log.d(TAG, "removeActiveRequest: task unsuccessful");
                        loadingDialog.dismiss();
                    }
                });
    }

    private void checkNotifications() {
        Log.d(TAG, "checkNotifications: called");
        loadingDialog.show();

        Query notificationQuery = ref.child("notifications").child(UID);

        notificationQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded: called");
                Notification notification = dataSnapshot.getValue(Notification.class);

                if (notification != null) {
                    Query userQuery = ref.child("users").child(notification.getSenderID());

                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                User user = dataSnapshot.getValue(User.class);

                                if (user != null) {
                                    NotificationUserMap userMap = new NotificationUserMap(
                                            notification,
                                            user.getName(),
                                            user.getUsername(),
                                            user.getPhotoUrl());

                                    if (notification.getNotificationType() == Notification.SQUAD_INVITE) {
                                        Query squadQuery = ref.child("squads").child(notification.getSquadID());

                                        squadQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.exists()) {
                                                    Squad squad = dataSnapshot.getValue(Squad.class);

                                                    if (squad != null) {
                                                        userMap.setSquadName(squad.getSquadName());
                                                        addRecyclerViewItem(userMap);
                                                    }
                                                    else
                                                        Log.d(TAG, "squad was null");
                                                } else
                                                    Log.d(TAG, "onDataChange: cant find squad");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    } else
                                        addRecyclerViewItem(userMap);

                                } else
                                    Log.d(TAG, "user is null");
                            } else
                                Log.d(TAG, "user not found");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            loadingDialog.dismiss();
                        }
                    });
                } else
                    Log.d(TAG, "null notification");

                loadingDialog.dismiss();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged: called");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: called");
                if (adapter != null && adapter.getItemCount() == 0)
                    no_notifications.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildMoved: called");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: called");
            }
        });

        if (adapter == null || adapter.getItemCount() == 0)
            no_notifications.setVisibility(View.VISIBLE);

        loadingDialog.dismiss();
    }

    public void addRecyclerViewItem(NotificationUserMap userMap) {
        myNotifications.add(userMap);

        if (adapter != null)
            adapter.notifyDataSetChanged();
        else {
            adapter = new NotificationRecyclerView(NotificationsActivity.this, listener, myNotifications);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(NotificationsActivity.this));
        }

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
    public void onRequestClicked(NotificationUserMap n) {}
    @Override
    public void onRequestAccept(NotificationUserMap n) {}
    @Override
    public void onRequestDeclined(NotificationUserMap n) {}
}
