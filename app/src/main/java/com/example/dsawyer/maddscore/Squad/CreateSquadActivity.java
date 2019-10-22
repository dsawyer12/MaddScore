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
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Players.PlayerSearchFragment;
import com.example.dsawyer.maddscore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.UUID;

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
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_squad);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

            public void beforeTextChanged(CharSequence s, int start, int count, int after){

            }

            public void onTextChanged(CharSequence s, int start, int before, int count){

            }
        });

    }

    public void verifyCredentials() {
        HashMap<String, Boolean> userList;
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

        userList = new HashMap<>();
        userList.put(UID, true);

        final String squadId = ref.child("squads").push().getKey();
        final String publicSquadID = UUID.randomUUID().toString();

        Squad squad = new Squad(squadId, publicSquadID, squad_name, UID, date, squad_description, privacy_level, userList);

        ref.child("squads").child(squadId).setValue(squad).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    ref.child("users").child(UID).child("mySquad").setValue(squadId).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                setUserRank();
                        }
                    });
                }
            }
        });
    }

    private void setUserRank() {
        Query query = ref.child("userStats").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.getRef().child("squadRank").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "squad creation success");
                                Intent intent = new Intent(CreateSquadActivity.this, SquadActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else
                                Log.d(TAG, "onComplete: setUserRank NOT successful");
                        }
                    });
                }
                else{
                    Log.d(TAG, "onDataChange: UID does not match");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setFragment(final Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.new_squad_frame, fragment, tag).addToBackStack(null).commit();
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












