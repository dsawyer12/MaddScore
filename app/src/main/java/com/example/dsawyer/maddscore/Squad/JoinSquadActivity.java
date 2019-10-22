package com.example.dsawyer.maddscore.Squad;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.SquadSearchListRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JoinSquadActivity extends AppCompatActivity {
    private static final String TAG = "TAG";

    private Squad squad;

    EditText search_squad;
    ImageView back_btn;
    RelativeLayout no_search_found;
    RecyclerView recyclerView;

    private ArrayList<Squad> squadList;
    private SquadSearchListRecyclerView.OnSquadSelectedListener listener;

    private DatabaseReference ref;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squad_join);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        back_btn = findViewById(R.id.back_button);
        search_squad = findViewById(R.id.search_squad);
        no_search_found = findViewById(R.id.no_search_found_layout);
        recyclerView = findViewById(R.id.recyclerView);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinSquadActivity.this, SquadActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        listener = new SquadSearchListRecyclerView.OnSquadSelectedListener() {
            @Override
            public void onSquadSelected(Squad squad) {
                Log.d(TAG, "onSquadSelected: "  + squad.getSquadName());
                SquadSearchSelectionFragment selection = new SquadSearchSelectionFragment();
                Bundle args = new Bundle();
                args.putParcelable("squad", squad);
                selection.setArguments(args);
                setFragment(selection);
            }
        };

        searchSquads();
    }

    public void searchSquads(){
        squadList = new ArrayList<>();

        final SquadSearchListRecyclerView adapter = new SquadSearchListRecyclerView(this, squadList, listener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        search_squad.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    squadList.clear();
                    adapter.notifyDataSetChanged();

                    String squad_name = search_squad.getText().toString();
                    Query query = ref.child(getString(R.string.firebase_node_squads))
                            .orderByChild(getString(R.string.firebase_node_squad_name))
                            .equalTo(squad_name);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()){
                                squad = ds.getValue(Squad.class);
                                squadList.add(squad);
                                adapter.notifyDataSetChanged();
                            }
                            if (squadList.isEmpty()){
                                no_search_found.setVisibility(View.VISIBLE);
                            }
                            else {
                                no_search_found.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                return false;
            }
        });
    }

    public void setFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.frame, fragment).addToBackStack(null).commit();
    }
}
