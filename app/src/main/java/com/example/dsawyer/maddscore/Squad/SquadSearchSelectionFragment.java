package com.example.dsawyer.maddscore.Squad;

import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.Notification;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.UserStats;
import com.example.dsawyer.maddscore.Utils.SquadMemberListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SquadSearchSelectionFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";
    private static final int NOTIFICATION_TYPE = 3;

    private DatabaseReference ref;
    private String UID;
    private User currentUser;

    private Squad squad;
    private ArrayList<String> members;
    private ArrayList<User> squad_users;
    private ArrayList<UserStats> stats;
    ListView squad_list;

    CircleImageView creator_photo;
    TextView squad_name, creator_name, creator_username, num_members;
    Button request_join;
    ImageView back_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_squad_search_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Query query = ref.child(getString(R.string.firebase_node_users)).child(UID);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        currentUser = dataSnapshot.getValue(User.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        squad_list = view.findViewById(R.id.squad_list);
        creator_photo = view.findViewById(R.id.creator_photo);
        squad_name = view.findViewById(R.id.squad_name);
        creator_name = view.findViewById(R.id.creator_name);
        creator_username = view.findViewById(R.id.creator_username);
        num_members = view.findViewById(R.id.num_members);
        back_btn = view.findViewById(R.id.back_btn);
        request_join = view.findViewById(R.id.request_join);

        back_btn.setOnClickListener(this);
        request_join.setOnClickListener(this);

        squad = getSquadFromBundle();
        if (squad != null){
            initialize();
        }
        else{
            Log.d(TAG, "onViewCreated: squad is null");
        }
    }

    private void initialize() {
        members = new ArrayList<>();
        squad_users = new ArrayList<>();
        stats = new ArrayList<>();

        members.addAll(squad.getMemberList().keySet());

        squad_name.setText(squad.getSquadName());
        num_members.setText(squad.getMemberList().size() + " Member(s)");
//        creator_username.setText(squad.getCreatorUsername());
//        creator_name.setText(squad.getCreatorName());
//        if (squad.getCreatorPhotoId() != null){
//            Glide.with(getActivity()).load(squad.getCreatorPhotoId()).into(creator_photo);
//        }
//        else{
//            Glide.with(getActivity()).load(R.mipmap.default_profile_img).into(creator_photo);
//
//        }

        Query query = ref.child(getString(R.string.firebase_node_users));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                squad_users.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    for (String key : members){
                        if (ds.getKey().equals(key)){
                            squad_users.add(ds.getValue(User.class));
                        }
                    }
                }

                if (!squad_users.isEmpty()){
                    getUserStats();
                }
                else{
                    Log.d(TAG, "squad member list is empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getUserStats(){
        Query query = ref.child("userStats");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stats.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    for (String key : members){
                        if (ds.getKey().equals(key)){
                            stats.add(ds.getValue(UserStats.class));
                        }
                    }
                }
                Collections.sort(stats);
                SquadMemberListAdapter adapter = new SquadMemberListAdapter(getActivity(), squad_users, stats);
                squad_list.setAdapter(adapter);
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
                currentUser.getUserID(),
                currentUser.getName(),
                currentUser.getPhotoUrl(),
                squad.getSquadID(),
                squad.getSquadName(),
                squad.getCreatorId(),
                NOTIFICATION_TYPE,
                date);

        ref.child(getString(R.string.notifications)).child(squad.getCreatorId()).child(notification_id).setValue(notification)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            request_join.setEnabled(true);
                            Toast.makeText(getActivity(), "Request Sent!", Toast.LENGTH_SHORT).show();
                            exit();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                request_join.setEnabled(true);
                Toast.makeText(getActivity(), "Failed to Send Request", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void exit(){
        Intent intent = new Intent(getActivity(), SquadActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public Squad getSquadFromBundle(){
        Bundle bundle = this.getArguments();
        if (bundle != null){
            squad = bundle.getParcelable("squad");
            return squad;
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.back_btn):
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStackImmediate();
                break;
            case(R.id.request_join):
                if (currentUser != null && squad != null){
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
