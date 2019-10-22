package com.example.dsawyer.maddscore.Profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ContentFrameLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Squad.SquadAddFragment;
import com.example.dsawyer.maddscore.Squad.SquadFragment;
import com.example.dsawyer.maddscore.Utils.SquadListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddSquadMemberActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "TAG";

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private String UID;
    private EditText search;
    private ListView mList;
    private RelativeLayout noneFound;
    private Squad mSquad;
    private User mUser;

    ArrayList<Squad> squadList;
    ArrayList<String> squadNames;
    ArrayList<String> creators;

    private ContentFrameLayout contentFrame;

    ImageView go_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_squad_member);

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        UID = mAuth.getCurrentUser().getUid();

        go_back = findViewById(R.id.back_arrow);
        go_back.setOnClickListener(this);


        contentFrame = findViewById(R.id.new_member_frame);
        noneFound = findViewById(R.id.no_search_found_layout);

        mList = findViewById(R.id.search_list);
        search = findViewById(R.id.search_player);

        getUser();
        searchPlayers();
    }

    private void getUser() {
        Query query = ref.child("users").orderByChild("userID");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    User temp = ds.getValue(User.class);

                    if (temp.getUserID().equals(UID)){
                        mUser = temp;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void searchPlayers(){
        squadList = new ArrayList<>();
        squadNames = new ArrayList<>();
        creators = new ArrayList<>();

        final SquadListAdapter adapter = new SquadListAdapter(this, squadNames, creators);
        mList.setAdapter(adapter);

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    squadList.clear();
                    squadNames.clear();
                    creators.clear();
                    adapter.notifyDataSetChanged();

                    final String squadName = search.getText().toString().trim();
                    Query query = ref.child("squads").orderByChild("username").equalTo(squadName);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                mSquad = ds.getValue(Squad.class);
                                squadList.add(mSquad);
                                squadNames.add(mSquad.getSquadName());
                                creators.add(mSquad.getCreatorId());
                                adapter.notifyDataSetChanged();
                            }
                            if (squadList.isEmpty()){
                                noneFound.setVisibility(View.VISIBLE);
                            }
                            else{
                                noneFound.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                return false;
            }
        });

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getSquadInfo(squadList.get(position));
//                playerSelected.onPlayerSearchSelection(playerList.get(position));
            }
        });
    }

    private void getSquadInfo(Squad squad) {
        if (squad.getSquadID().equals(mUser.getMySquad())){
            SquadFragment mySquad = new SquadFragment();
            setFragment(mySquad);
        }
        else{
            SquadAddFragment squadAdd = new SquadAddFragment();
            Bundle args = new Bundle();
            args.putParcelable("squad", squad);
            squadAdd.setArguments(args);
            setFragment(squadAdd);
        }
    }

    public void setFragment(final Fragment fragment){
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.card_charts_content_frame, fragment).addToBackStack(null).commit();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.back_arrow):
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
        }
    }
}
