package com.example.dsawyer.maddscore.Other;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Profile.ProfileCreateActivity;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.UserStats;
import com.example.dsawyer.maddscore.Utils.ApplicationPreferences;
import com.example.dsawyer.maddscore.Utils.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TAG";

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private EditText name, email, password, passwordConfirm;
    private String mName, mEmail, mPassword, mPasswordConfirm;

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
        passwordConfirm = findViewById(R.id.register_password_confirm);

        findViewById(R.id.register_submit_button).setOnClickListener(this);
        findViewById(R.id.register_login_button).setOnClickListener(this);
    }

    private void registerUser() {
        Log.d(TAG, "registerUser: ");
        mName = name.getText().toString().trim();
        mEmail = email.getText().toString().trim();
        mPassword = password.getText().toString().trim();
        mPasswordConfirm = passwordConfirm.getText().toString().trim();

        if (mName.isEmpty()) {
            name.setError(getString(R.string.requires_name));
            name.requestFocus();
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

        if (!mPasswordConfirm.equals(mPassword)) {
            passwordConfirm.setError(getString(R.string.password_mismatch));
            passwordConfirm.requestFocus();
            return;
        }

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(task -> {
                    loadingDialog.dismiss();

                    if (task.isSuccessful()) {
                        if (mAuth.getCurrentUser() != null) {
                            createUserProfile();
                        }
                    }
                    else if(task.getException() instanceof FirebaseAuthUserCollisionException)
                        Toast.makeText(this, R.string.already_registered_message, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    public void createUserProfile() {
        Log.d(TAG, "createUserProfile: ");
        long date = System.currentTimeMillis();
        if (mAuth.getCurrentUser() != null) {
            String UID = mAuth.getCurrentUser().getUid();

            User newUser = new User(UID, mName, mEmail, true, date);
            UserStats stats = new UserStats(UID);

            HashMap<String, Object> newUserMap = new HashMap<>();
            newUserMap.put("/users/" + UID, newUser);
            newUserMap.put("/userStats/" + UID, stats);

            ref.updateChildren(newUserMap).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    Log.d(TAG, "user added to database");
                    editor.putBoolean(ApplicationPreferences.GET_STARTED_SCREEN, false);
                    editor.apply();

                    Intent intent = new Intent(this, ProfileCreateActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
            });
        }
    }

//    public void sendVerificationEmail() {
//        if(mAuth.getCurrentUser() != null) {
//            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task -> {
//                if(task.isSuccessful()) {
//                    Toast toast= Toast.makeText(getApplicationContext(),
//                            "A verification email has been sent to " + email.getText(), Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 300);
//                    toast.show();
//                }
//                else if (task.getException() != null)
//                    Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                else
//                    Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
//            });
//        }
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_submit_button:
                registerUser();
                break;

            case R.id.register_login_button:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }
}
