package com.example.dsawyer.maddscore.Other;

import android.content.Intent;
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

import com.example.dsawyer.maddscore.Profile.ProfileActivity;
import com.example.dsawyer.maddscore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TAG";

    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.login_email_field);
        editTextPassword = findViewById(R.id.login_password_field);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.login_register_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.requires_email_message));
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.requires_valid_email_message));
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.requires_password));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError(getString(R.string.requires_password_missing_character_message));
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: sign in successful");
                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

//                    try{
//                        if(mAuth.getCurrentUser().isEmailVerified()) {
//                            Log.d(TAG, "Login: OnSigningIn: Email is verified. going to profile acitivty");
//                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);
//                        }
//                        else{
//                            Toast toast= Toast.makeText(LoginActivity.this,
//                                    "Please verify your email before logging in", Toast.LENGTH_LONG);
//                            toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 300);
//                            toast.show();
//                            mAuth.signOut();
//                            Log.d(TAG, "Login: EmailVerification: user was not email verified. signing them out");
//                        }
//                    }
//                    catch(NullPointerException e){
//                        e.getMessage();
//                    }

                } else {
                    Log.d(TAG, "onComplete: task NOT successful");
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
            Log.d(TAG, "onStart: mAuth.getCurrertnUser = null");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_register_button:
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;

            case R.id.login_button:
                userLogin();
                break;
        }
    }
}
