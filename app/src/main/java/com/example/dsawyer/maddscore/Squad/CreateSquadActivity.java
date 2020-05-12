package com.example.dsawyer.maddscore.Squad;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.UIDGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CreateSquadActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TAG";

    private static final int PRIVACY_LEVEL_OPEN = 0;
    private static final int PRIVACY_LEVEL_CLOSED = 1;
    private static final int PRIVACY_LEVEL_PRIVATE = 2;

    ImageView cancel;
    EditText name, description;
    RadioGroup privacy_group;
    RadioButton privacy_open, privacy_closed, privacy_private;
    Button create;
    TextView warning;

    private DatabaseReference ref;
    private FirebaseUser currentUser;

    private Squad squad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_squad);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            currentUser = FirebaseAuth.getInstance().getCurrentUser();

        warning = findViewById(R.id.squad_name_warning);
        cancel = findViewById(R.id.cancel_new_squad_creation);
        create = findViewById(R.id.create_squad);
        name = findViewById(R.id.new_squad_name);
        description = findViewById(R.id.new_squad_description);
        privacy_group = findViewById(R.id.privacyGroup);
        privacy_open = findViewById(R.id.privacy_open);
        privacy_closed = findViewById(R.id.privacy_closed);
        privacy_private = findViewById(R.id.privacy_private);

        cancel.setOnClickListener(this);
        create.setOnClickListener(this);

        privacy_open.setChecked(true);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable mEdit) {
                if (mEdit.toString().trim().length() >= 50)
                    warning.setVisibility(View.VISIBLE);
                else
                    warning.setVisibility(View.GONE);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){  }
            public void onTextChanged(CharSequence s, int start, int before, int count){  }
        });

    }

    public void verifyCredentials() {
        Log.d(TAG, "verifyCredentials: called");
        String squad_name = name.getText().toString().trim();
        String squad_description = description.getText().toString().trim();
        int radioId = privacy_group.getCheckedRadioButtonId();
        int privacy_level = 0;

        long date = System.currentTimeMillis();

        if (squad_name.isEmpty()) {
            name.setError("Required Field");
            name.requestFocus();
            return;
        }

        switch (radioId) {
            case(R.id.privacy_open):
                privacy_level = PRIVACY_LEVEL_OPEN;
                break;
            case(R.id.privacy_closed):
                privacy_level = PRIVACY_LEVEL_CLOSED;
                break;
            case(R.id.privacy_private):
                privacy_level =PRIVACY_LEVEL_PRIVATE;
                break;
        }

        final String squadId = ref.child("squads").push().getKey();
        squad = new Squad(squadId, currentUser.getUid(), squad_name, squad_description, privacy_level, date);
        generateUID();
//        final String publicSquadID = UUID.randomUUID().toString();
    }

    public synchronized void generateUID() {
        Log.d(TAG, "generateUID: called");
        String UID = UIDGenerator.getUID(8);
        ref.child("publicSquadIDs").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    generateUID();
                else {
                    Log.d(TAG, "public squad ID : " + UID);
                    squad.setPublicID(UID);
                    completeSquadSetup();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {  }
        });
    }

    public void completeSquadSetup() {
        Log.d(TAG, "completeSquadSetup: called");
        ref.child("squads").child(squad.getSquadID()).setValue(squad).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> newSquadMap = new HashMap<>();
                newSquadMap.put("/users/" + currentUser.getUid() + "/squad/", squad.getSquadID());
                newSquadMap.put("/users/" + currentUser.getUid() + "/squad_rank/", 1);
                newSquadMap.put("/publicSquadIDs/" + squad.getPublicID(), squad.getSquadID());

                ref.updateChildren(newSquadMap).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Intent intent = new Intent(this, SquadActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.cancel_new_squad_creation):
                finish();
                break;

            case(R.id.create_squad):
                verifyCredentials();
                break;
        }
    }
}












