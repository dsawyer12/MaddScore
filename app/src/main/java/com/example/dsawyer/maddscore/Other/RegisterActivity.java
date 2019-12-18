package com.example.dsawyer.maddscore.Other;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.UserStats;
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
    public static final String myPrefs = "get_started_screen";

    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private String UID;
    private EditText name, email, password, passwordConfirm;

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
        final String mName, mEmail, mPassword, mPasswordConfirm;
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
        Log.d(TAG, "Register: createAccount: creating user with email and password");
        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(task -> {
                    loadingDialog.dismiss();

                    if (task.isSuccessful()) {
                        if (mAuth.getCurrentUser() != null)
                            UID = mAuth.getCurrentUser().getUid();

                        long date = System.currentTimeMillis();
                        if (UID != null) {
                            User newUser = new User(UID, mName, mEmail, true, date);
                            UserStats stats = new UserStats(UID);

                            HashMap<String, Object> newUserMap = new HashMap<>();
                            newUserMap.put("/users/" + UID, newUser);
                            newUserMap.put("/userStats/" + UID, stats);

                            ref.updateChildren(newUserMap).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d(TAG, "user added to database");
                                    editor.putBoolean(myPrefs, false);
                                    editor.apply();
                                    sendVerificationEmail();
                                    mAuth.signOut();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                                else
                                    Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                    else if(task.getException() instanceof FirebaseAuthUserCollisionException)
                        Toast.makeText(getApplicationContext(), R.string.already_registered_message, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    public void sendVerificationEmail() {
        if(mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Toast toast= Toast.makeText(getApplicationContext(),
                            "A verification email has been sent to " + email.getText(), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 300);
                    toast.show();
                }
                else if (task.getException() != null)
                    Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
            });
        }
    }

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
