package com.example.dsawyer.maddscore.Other;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Profile.ProfileActivity;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.UserStats;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TAG";

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String myPrefs = "get_started_screen";

    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private String UID;
    private EditText name, email, password, phoneNumber, username;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "Register: onCreate: started");

        sharedpreferences = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.apply();

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        name = findViewById(R.id.register_text_name);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        phoneNumber = findViewById(R.id.register_phoneNumber);
        username = findViewById(R.id.register_text_username);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.register_submit_button).setOnClickListener(this);
        findViewById(R.id.register_login_button).setOnClickListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void registerUser() {
        final String mName, mEmail, mUsername, mPassword, mPhoneNumber;
        mName = name.getText().toString().trim();
        mUsername = username.getText().toString().trim();
        mEmail = email.getText().toString().trim();
        mPassword = password.getText().toString().trim();
        mPhoneNumber = phoneNumber.getText().toString().trim();

        if (mName.isEmpty()) {
            name.setError(getString(R.string.requires_name));
            name.requestFocus();
            return;
        }

        if (mUsername.isEmpty()) {
            username.setError(getString(R.string.requires_username));
            username.requestFocus();
            return;
        }

        if (mEmail.isEmpty()) {
            email.setError(getString(R.string.requires_email));
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            email.setError(getString(R.string.requires_valid_email));
            email.requestFocus();
            return;
        }

        if(!mPhoneNumber.isEmpty()){
            if (!Patterns.PHONE.matcher(mPhoneNumber).matches()) {
                phoneNumber.setError(getString(R.string.requires_valid_phone_number));
                phoneNumber.requestFocus();
                return;
            }
        }

        if (mPassword.isEmpty()) {
            password.setError(getString(R.string.enter_password));
            password.requestFocus();
            return;
        }

        if (mPassword.length() < 6) {
            password.setError(getString(R.string.password_minimum));
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Register: createAccount: creating user with email and password");
        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Log.d(TAG, "Register: onComplete: task successful");
                    UID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
//                    sendVerificationEmail();
                    long date = System.currentTimeMillis();

                    User newUser = new User(
                            UID,
                            mName,
                            mEmail,
                            mPhoneNumber,
                            mUsername,
                            true,
                             date);

                    ref.child("users").child(UID).setValue(newUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "user add to database");
                                ref.child("userStats").child(UID)
                                        .setValue(new UserStats(UID)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "user stats added to database");
                                            editor.putBoolean(myPrefs, false);
                                            editor.apply();
                                            loadData();
                                        }
                                    }
                                });
                            }
                        }
                    });

                   // ref.child("usernames").setValue(mUsername);

//                    mAuth.signOut();
//                    Log.d(TAG, "Register: onComplete: now logging out user and going to Login activity");
//                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
                }
                else if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(getApplicationContext(), R.string.already_registered_message, Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

//    public boolean checkUsername(String mUsername){
//        if(!mUsername.isEmpty()){
//            if (mUsername.equals("none")) {
//                username.setError("username cannot be 'none'");
//                username.requestFocus();
//                Log.d(TAG, "Register: checkUsername: ERROR: username = 'none'");
//                return true;
//            }
//            Log.d(TAG, "registerUser: checking if username exists already");
//            Query query = ref.child("users").orderByChild("username").equalTo(mUsername);
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if(!dataSnapshot.exists()){
//                        exists = false;
//                        Log.d(TAG, "Register: onDataChange: this username DOES NOT EXISTS");
//                    }
//                    else{
//                        for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
//                            if (singleSnapshot.exists()){
//                                username.setError("This username is already taken");
//                                username.requestFocus();
//                                Log.d(TAG, "Register: checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(User.class).getUsername());
//                                exists = true;
//                            }
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
//        Log.d(TAG, "Register: checkUsername: return default value of false");
//        return exists;
//    }

//    public void sendVerificationEmail(){
//        Log.d(TAG, "Register: sendVerificationEmail: sending verification email");
//        if(mAuth.getCurrentUser() != null){
//            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()){
//                        Toast toast= Toast.makeText(getApplicationContext(),
//                                "A verification email has been sent to " + email.getText(), Toast.LENGTH_LONG);
//                        toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 300);
//                        toast.show();
//                        Log.d(TAG, "Register: onSendingEmailVerifacationComplete: task complete");
//                    }
//                    else{
//                        Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
//        }
//    }

    public void loadData(){
        Log.d(TAG, "Register: loadData: going to Profile Activity");
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_submit_button:
                Log.d(TAG, "Register: onClick: submitting registration information");
                registerUser();
                break;

            case R.id.register_login_button:
                Log.d(TAG, "Register: onClick: going back to the login screen");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }
}
