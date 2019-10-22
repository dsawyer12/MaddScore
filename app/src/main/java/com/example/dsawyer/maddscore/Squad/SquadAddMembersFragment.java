package com.example.dsawyer.maddscore.Squad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.ActiveRequest;
import com.example.dsawyer.maddscore.Objects.Notification;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Other.NotificationsActivity;
import com.example.dsawyer.maddscore.Players.PlayerFragment;
import com.example.dsawyer.maddscore.Players.TempPlayerFragment;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.PlayerListRecyclerView;
import com.example.dsawyer.maddscore.Utils.SquadAddMemberRecyclerView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SquadAddMembersFragment extends Fragment implements
        View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "TAG";

    DatabaseReference ref;
    String UID;

    ImageView back, search;
    EditText searchBox;
    RadioGroup radioGroup;
    RadioButton usernameRadio, phoneRadio;
    ProgressBar progressBar;
    TextView noResults;

    String squadID;
    ArrayList<User> memberList;
    ArrayList<ActiveRequest> activeRequests;
    RecyclerView recyclerView;
    SquadAddMemberRecyclerView adapter;
    SquadAddMemberRecyclerView.OnUserClickedListener listener;
    User mUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_squad_add_members, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        back = view.findViewById(R.id.search_back_btn);
        search = view.findViewById(R.id.search_btn);
        searchBox = view.findViewById(R.id.search_player);
        radioGroup = view.findViewById(R.id.squad_add_radio_group);
        usernameRadio = view.findViewById(R.id.squad_add_username_radio);
        phoneRadio = view.findViewById(R.id.squad_add_phone_radio);
        progressBar = view.findViewById(R.id.progressBar);
        noResults = view.findViewById(R.id.no_results_msg);
        recyclerView = view.findViewById(R.id.search_list);
        usernameRadio.setChecked(true);
        radioGroup.setOnCheckedChangeListener(this);

        back.setOnClickListener(this);
        search.setOnClickListener(this);

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                    searchDatabase();
                return true;
            }
        });

        memberList = new ArrayList<>();
        activeRequests = new ArrayList<>();

        listener = new SquadAddMemberRecyclerView.OnUserClickedListener() {
            @Override
            public void onInvite(final User user) {
                Log.d(TAG, "onInvite: ");
                final long date = System.currentTimeMillis();

                final String notificationID = ref.child("notifications").child(user.getUserID()).push().getKey();

                Notification notification = new Notification(
                        notificationID,
                        UID,
                        mUser.getName(),
                        mUser.getPhotoUrl(),
                        squadID,
                        "New Squad, ",
                        user.getUserID(),
                        3,
                        date);

                ref.child("notifications").child(user.getUserID()).child(notificationID).setValue(notification)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "onComplete: notification sent");
                                if (task.isSuccessful()) {
                                    final ActiveRequest activeRequest =
                                            new ActiveRequest(UID, user.getUserID(), date, notificationID, 3);
                                    ref.child("activeRequests").child(UID).child(notificationID).setValue(activeRequest)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: active request made");
                                                        adapter.updateList(activeRequest, true);
                                                    }
                                                    else {
                                                        Log.d(TAG, "onComplete: active request NOT made");
                                                        adapter.updateList(activeRequest, false);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }

            @Override
            public void onCancel(final String userID, final String notificationID) {
                Log.d(TAG, "onCancel: ");
               ref.child("notifications").child(userID).child(notificationID).removeValue().addOnCompleteListener(
                       new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()) {
                                   Log.d(TAG, "onComplete: removed notification");
                                   ref.child("activeRequests").child(UID).child(notificationID).removeValue().addOnCompleteListener(
                                           new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if (task.isSuccessful()) {
                                                       Log.d(TAG, "onComplete: removed active request");
                                                       adapter.removeRequest(userID, notificationID);
                                                   }
                                               }
                                           }
                                   );
                               }
                           }
                       }
               );
            }
        };

        squadID = getIdFromBundle();

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ref.child("users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        mUser = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void searchDatabase() {
        String keyword = searchBox.getText().toString().trim();
        if (keyword.isEmpty()) {
            Toast.makeText(getActivity(), "Search box is empty", Toast.LENGTH_SHORT).show();
            searchBox.requestFocus();
            return;
        }

        Query query = null;
        if (radioGroup.getCheckedRadioButtonId() == usernameRadio.getId())
            query = ref.child("users").orderByChild("username").equalTo(keyword);
        else if (radioGroup.getCheckedRadioButtonId() == phoneRadio.getId())
            query = ref.child("users").orderByChild("phoneNumber").equalTo(keyword);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                memberList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue(User.class).isRegistered() && !ds.getValue(User.class).getUserID().equals(UID))
                        memberList.add(ds.getValue(User.class));
                }

                if (memberList.isEmpty()) {
                    noResults.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else
                    getActiveRequests();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getActiveRequests() {
        Query query = ref.child("activeRequests").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    activeRequests.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        activeRequests.add(ds.getValue(ActiveRequest.class));
                    }
                    if (adapter != null)
                        adapter.replaceList(memberList, activeRequests);
                    else {
                        adapter = new SquadAddMemberRecyclerView(getActivity(), memberList, activeRequests, listener);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                }
                else {
                    if (adapter != null)
                        adapter.replaceList(memberList, activeRequests);
                    else {
                        adapter = new SquadAddMemberRecyclerView(getActivity(), memberList, activeRequests, listener);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case(R.id.squad_add_username_radio):
                searchBox.getText().clear();
                memberList.clear();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                searchBox.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                break;

            case(R.id.squad_add_phone_radio):
                searchBox.getText().clear();
                memberList.clear();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                searchBox.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.search_back_btn):
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;

            case(R.id.search_btn):
                searchDatabase();
                break;
        }
    }

    public String getIdFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null)
            return bundle.getString("squadID");
        else
            return null;
    }
}













