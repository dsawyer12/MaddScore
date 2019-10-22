package com.example.dsawyer.maddscore.Players;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.ActiveRequest;
import com.example.dsawyer.maddscore.Objects.Notification;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Other.NotificationsActivity;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Social.MessageFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressLint("ValidFragment")
public class PlayerFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;
    private User mUser, currentUser;
    private Squad squad;

    private boolean active_friend = false, active_friend_request = false, active_member = false, active_squad_request = false;

    LinearLayout mainView, add_friend, invite_to_squad, send_message;
    TextView add_friend_txt, invite_to_squad_txt, send_message_txt;
    ImageView go_back, friend_img, squad_img;
    ProgressBar progressBar;

    ArrayList<String> friendsList;
    ArrayList<ActiveRequest> activeRequests;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CircleImageView profileImage = view.findViewById(R.id.profileImage);
        TextView player_username = view.findViewById(R.id.player_userName);
        TextView player_name = view.findViewById(R.id.player_name);
        mainView = view.findViewById(R.id.main_view);
        add_friend = view.findViewById(R.id.add_friend_btn);
        add_friend_txt = view.findViewById(R.id.add_friend_txt);
        invite_to_squad = view.findViewById(R.id.invite_to_squad_btn);
        invite_to_squad_txt = view.findViewById(R.id.invite_to_squad_txt);
        send_message = view.findViewById(R.id.send_message_btn);
        send_message_txt = view.findViewById(R.id.send_message_txt);
        go_back = view.findViewById(R.id.go_back);
        friend_img = view.findViewById(R.id.friend_img);
        squad_img = view.findViewById(R.id.squad_img);
        progressBar = view.findViewById(R.id.progressBar);

        add_friend.setOnClickListener(this);
        invite_to_squad.setOnClickListener(this);
        send_message.setOnClickListener(this);
        go_back.setOnClickListener(this);

        mainView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        mUser = getUserFromBundle();
        if (mUser != null) {
            if (mUser.getPhotoUrl() != null) {
                Glide.with(this).load(mUser.getPhotoUrl()).into(profileImage);
            }
            else {
                Glide.with(this).load(R.mipmap.default_profile_img).into(profileImage);
            }
            player_username.setText(mUser.getUsername());
            player_name.setText(mUser.getName());
            getCurrentUser();
        }
        else {
            Toast.makeText(getActivity(), "Could not load user information", Toast.LENGTH_SHORT).show();
            mainView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void getCurrentUser() {
        Log.d(TAG, "getCurrentUser: ");
        ref.child("users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUser = dataSnapshot.getValue(User.class);
                    getFriendsList();
                }
                else {
                    mainView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Could not load user information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getFriendsList() {
        Log.d(TAG, "getFriendsList: ");
        friendsList = new ArrayList<>();
        ref.child("friendsList").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    friendsList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        friendsList.add(ds.getKey());
                    }
                }
                    getActiveRequests();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getActiveRequests() {
        Log.d(TAG, "getActiveRequests: ");
        activeRequests = new ArrayList<>();
        ref.child("activeRequests").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    activeRequests.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        activeRequests.add(ds.getValue(ActiveRequest.class));
                    }
                }
                    getUserRelation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getUserRelation() {
        Log.d(TAG, "getUserRelation: ");
        if (friendsList.contains(mUser.getUserID())) {
            Log.d(TAG, "getUserRelation: users are friends");
            active_friend = true;
            add_friend_txt.setText("Friends");
            // CHANGE ICON
        }
        else {
            Log.d(TAG, "getUserRelation: users are NOT friends");
            if (!activeRequests.isEmpty()) {
                Log.d(TAG, "getUserRelation: activeRequest list is not empty");
                for (int i = 0; i < activeRequests.size(); i++) {
                    if (activeRequests.get(i).getReceiver().equals(mUser.getUserID()) &&
                            activeRequests.get(i).getNotificationType() == Notification.FRIEND_REQUEST)
                        active_friend_request = true;
                }

                if (active_friend_request) {
                    Log.d(TAG, "getUserRelation: there is already an active friend request");
                    add_friend_txt.setText("Cancel Friend Request");
                    // CHANGE ICON
                }
                else {
                    Log.d(TAG, "getUserRelation: there is NOT an active friend request");
                    add_friend_txt.setText("Add Friend");
                    // CHANGE ICON
                }
            }
            else {
                Log.d(TAG, "getUserRelation: activeRequest list IS empty");
                add_friend_txt.setText("Add Friend");
                // CHANGE ICON
            }
        }
        getCurrentUserSquad();
    }

    public void getCurrentUserSquad() {
        Log.d(TAG, "getCurrentUserSquad: ");
        if (currentUser.getMySquad() != null) {
            Log.d(TAG, "getCurrentUserSquad: current user has a squad");
            ref.child("squads").child(currentUser.getMySquad()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "getCurrentUserSquad: the current users squad was found");
                        squad = dataSnapshot.getValue(Squad.class);
                        if (squad != null && squad.getProprietors().keySet().contains(currentUser.getUserID())) {
                            Log.d(TAG, "getCurrentUserSquad: the current user is a proprietor");
                            getmUserSquad();
                        }
                        else {
                            Log.d(TAG, "getCurrentUserSquad: current user is NOT proprietor or squad was NULL");
                            invite_to_squad.setVisibility(View.GONE);
                            initLayout();
                        }
                    }
                    else {
                        Log.d(TAG, "getCurrentUserSquad: The current users squad was NOT found");
                        invite_to_squad.setVisibility(View.GONE);
                        initLayout();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            Log.d(TAG, "getCurrentUserSquad: Current user does not have a squad");
            invite_to_squad.setVisibility(View.GONE);
            initLayout();
        }
    }

    public void getmUserSquad() {
        Log.d(TAG, "getmUserSquad: ");
        invite_to_squad.setVisibility(View.VISIBLE);
        if (mUser.getMySquad() != null) {
            Log.d(TAG, "getmUserSquad: mUser has a squad");
            if (mUser.getMySquad().equals(currentUser.getMySquad())) {
                Log.d(TAG, "getmUserSquad: mUser and current user are in the same squad");
                active_member = true;
                invite_to_squad_txt.setText("Squad");
                // CHANGE ICON
            }
            else {
                for (int i = 0; i < activeRequests.size(); i++) {
                    if (activeRequests.get(i).getReceiver().equals(mUser.getUserID()) &&
                            activeRequests.get(i).getNotificationType() == Notification.SQUAD_INVITE)
                        active_squad_request = true;
                }
                if (active_squad_request) {
                    Log.d(TAG, "getmUserSquad: There is already an active squad request");
                    invite_to_squad_txt.setText("Cancel Squad Invite");
                    // CHANGE ICON
                }
                else {
                    Log.d(TAG, "getmUserSquad: There is NOT an active squad request");
                    invite_to_squad_txt.setText("Invite to Squad");
                    // CHANGE ICON
                }
            }
        }
        else {
            Log.d(TAG, "getmUserSquad: mUser does NOT have a squad");
            for (int i = 0; i < activeRequests.size(); i++) {
                if (activeRequests.get(i).getReceiver().equals(mUser.getUserID()) &&
                        activeRequests.get(i).getNotificationType() == Notification.SQUAD_INVITE)
                    active_squad_request = true;
            }
            if (active_squad_request) {
                Log.d(TAG, "getmUserSquad: There is already an active squad request");
                invite_to_squad_txt.setText("Cancel Squad Invite");
                // CHANGE ICON
            }
            else {
                Log.d(TAG, "getmUserSquad: There is NOT an active squad request");
                invite_to_squad_txt.setText("Invite to Squad");
                // CHANGE ICON
            }
        }

        initLayout();
    }

    public void initLayout() {
        Log.d(TAG, "initLayout: ");
        progressBar.setVisibility(View.GONE);
        mainView.setVisibility(View.VISIBLE);
    }

    private User getUserFromBundle() {
        Log.d(TAG, "getUserFromBundle: ");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable("user");
        }
        else{
            return null;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case (R.id.add_friend_btn):
                if (!active_friend_request && !active_friend)
                   sendRequest(Notification.FRIEND_REQUEST);
                else if (!active_friend_request && active_friend)
                    unFriend();
                else if (active_friend_request && !active_friend)
                    cancelRequest(Notification.FRIEND_REQUEST);
                break;

            case (R.id.invite_to_squad_btn):
                if (!active_squad_request && !active_member)
                    sendRequest(Notification.SQUAD_INVITE);
                else if (!active_squad_request && active_member)
                    removeSquadMember();
                else if (active_squad_request && !active_member)
                    cancelRequest(Notification.SQUAD_INVITE);
                break;

            case (R.id.send_message_btn):
                sendMessage();
                break;
            case(R.id.go_back):
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
        }
    }

    public void sendRequest(final int NOTIFICATION_TYPE) {
        Log.d(TAG, "sendRequest: ");

        final long date = System.currentTimeMillis();

        final String key = ref.child("notifications").push().getKey();

        if (key != null) {
            Notification notification = null;
            if (NOTIFICATION_TYPE == Notification.FRIEND_REQUEST) {
                notification = new Notification(
                        key,
                        UID,
                        currentUser.getName(),
                        currentUser.getPhotoUrl(),
                        mUser.getUserID(),
                        NOTIFICATION_TYPE,
                        date);
            }
            else if (NOTIFICATION_TYPE == Notification.SQUAD_INVITE) {
                notification = new Notification(
                        key,
                        UID,
                        currentUser.getName(),
                        currentUser.getPhotoUrl(),
                        currentUser.getMySquad(),
                        squad.getSquadName(),
                        mUser.getUserID(),
                        NOTIFICATION_TYPE,
                        date);
            }

            if (notification != null) {
                ref.child("notifications")
                        .child(mUser.getUserID())
                        .child(key)
                        .setValue(notification)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    ActiveRequest activeRequest = new ActiveRequest(
                                            UID,
                                            mUser.getUserID(),
                                            date,
                                            key,
                                            NOTIFICATION_TYPE);

                                    ref.child("activeRequests").child(UID).child(key).setValue(activeRequest)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        switch (NOTIFICATION_TYPE) {
                                                            case(Notification.FRIEND_REQUEST):
                                                                active_friend_request = true;
                                                                add_friend_txt.setText("Cancel Friend Request");
                                                                // CHANGE ICON
                                                                break;
                                                            case(Notification.SQUAD_INVITE):
                                                                active_squad_request = true;
                                                                invite_to_squad_txt.setText("Cancel Squad Invite");
                                                                // CHANGE ICON
                                                                break;
                                                        }
                                                        Toast.makeText(getActivity(), "Request Sent Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                        Toast.makeText(getActivity(), "Failed to Send Request", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                                else
                                    Toast.makeText(getActivity(), "Failed to Send Request", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
        else
            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
    }

    public void cancelRequest(final int NOTIFICATION_TYPE) {
        Log.d(TAG, "cancelRequest: ");

        ref.child("notifications").child(mUser.getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Notification notification;

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                notification = ds.getValue(Notification.class);

                                if (notification != null && notification.getSender().equals(UID) &&
                                        notification.getNotificationType() == NOTIFICATION_TYPE) {

                                    final String notificationID = ds.getKey();
                                    ds.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                                cancelActiveRequest(NOTIFICATION_TYPE, notificationID);
                                            else
                                                Log.d(TAG, "onComplete: cancelRequest: notification was NOT removed");
                                        }
                                    });
                                }
                                else {
                                    // notification is NULL
                                }
                            }
                        }
                        else
                            Log.d(TAG, "onDataChange: cancelFriendRequest: THere are no notifications for this user");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void cancelActiveRequest(final int NOTIFICATION_TYPE, final String notificationID) {
        Log.d(TAG, "cancelActiveRequest: ");

        ref.child("activeRequests").child(UID).child(notificationID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            switch (NOTIFICATION_TYPE) {
                                case(Notification.FRIEND_REQUEST):
                                    active_friend_request = false;
                                    add_friend_txt.setText("Add Friend");
                                    // CHANGE ICON
                                    break;

                                case(Notification.SQUAD_INVITE):
                                    active_squad_request = false;
                                    invite_to_squad_txt.setText("Invite to Squad");
                                    // CHANGE ICON
                                    break;
                            }
                            Toast.makeText(getActivity(), "Request Canceled", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void unFriend() {
        Log.d(TAG, "unFriend: ");
        ConfirmRemoveFriendDialog confirmRemove = new ConfirmRemoveFriendDialog();
        Bundle args = new Bundle();
        if (mUser != null) {
            args.putParcelable("user", mUser);
            confirmRemove.setArguments(args);
            confirmRemove.setTargetFragment(this, 120);
            if (getFragmentManager() != null)
                confirmRemove.show(getFragmentManager(), "confirmRemove");
        }
    }

    public void removeSquadMember() {
        Log.d(TAG, "removeSquadMember: ");
        ConfirmRemoveSquadMemberDialog confirmRemove = new ConfirmRemoveSquadMemberDialog();
        Bundle args = new Bundle();
        if (mUser != null) {
            args.putParcelable("user", mUser);
            confirmRemove.setArguments(args);
            confirmRemove.setTargetFragment(this, 120);
            if (getFragmentManager() != null)
                confirmRemove.show(getFragmentManager(), "confirmRemove");
        }
    }

    public void sendMessage() {
        Log.d(TAG, "sendMessage: ");
        MessageFragment messageFragment = new MessageFragment();
        Bundle args = new Bundle();
        if (mUser != null) {
            args.putParcelable("user", mUser);
            messageFragment.setArguments(args);
            ((PlayersActivity)getActivity()).setFragment(messageFragment);
        }
    }
}
