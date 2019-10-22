package com.example.dsawyer.maddscore.Social;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.MemberListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostMemberListDialog extends DialogFragment{
    private static final String TAG = "TAG";

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private String UID;
    private User mUser;

    private ArrayList<String> list;

    Button post_memberlist_ok;
    ListView playerList;

    public PostMemberListDialog(){
        super();
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_member_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getCurrentUser().getUid();

        list = getUsersFromBundle();
        final ArrayList<User> userlist = new ArrayList<>();

        playerList = view.findViewById(R.id.player_list);
        post_memberlist_ok = view.findViewById(R.id.post_memberlist_ok);
        post_memberlist_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        Query query = ref.child("users").orderByChild("userID");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    mUser = ds.getValue(User.class);
                    for (int i = 0; i < list.size(); i++){
                        if (mUser.getUserID().equals(list.get(i)))
                            userlist.add(mUser);
                    }
                }
                MemberListAdapter adapter = new MemberListAdapter(getActivity(), userlist);
                playerList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<String> getUsersFromBundle(){
        Bundle bundle = this.getArguments();
        if (bundle != null){
            return bundle.getStringArrayList("userlist");
        }
        else{
            return null;
        }
    }
}
