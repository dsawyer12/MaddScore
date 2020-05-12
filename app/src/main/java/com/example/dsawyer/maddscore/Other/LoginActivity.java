package com.example.dsawyer.maddscore.Other;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Profile.ProfileActivity;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;

public class
LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TAG";

    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword;
    private Button resendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.login_email_field);
        editTextPassword = findViewById(R.id.login_password_field);
        resendEmail = findViewById(R.id.email_resend);

        findViewById(R.id.login_register_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.forgot_password).setOnClickListener(this);
        resendEmail.setOnClickListener(this);

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

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
          loadingDialog.dismiss();

            if (task.isSuccessful()) {
                Log.d(TAG, "onComplete: sign in successful");
                startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                finish();
                    try {
                        if(mAuth.getCurrentUser() != null) {
//                            if (mAuth.getCurrentUser().isEmailVerified()) {
//                                resendEmail.setVisibility(View.GONE);
//                                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                            else {
//                                resendEmail.setVisibility(View.VISIBLE);
//                                Toast toast= Toast.makeText(LoginActivity.this,
//                                        "Please verify your email before logging in", Toast.LENGTH_SHORT);
//                                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 300);
//                                toast.show();
//                                Log.d(TAG, "Login: EmailVerification: user was not email verified. signing them out");
//                            }
                        }
                    }
                    catch(NullPointerException e) {
                        e.getMessage();
                    }

            } else {
                Log.d(TAG, "onComplete: task NOT successful");
                Toast toast = Toast.makeText(LoginActivity.this,
                        "No Record found. Please check your email and password and try again", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 300);
                toast.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            Log.d(TAG, "onStart: currentUser is not null");
            // && mAuth.getCurrentUser.isEmailedVerified
            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
            finish();
        } else Log.d(TAG, "onStart: currentUser is null");

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

            case R.id.forgot_password:
                ForgotPasswordDialog dialog = new ForgotPasswordDialog();
                dialog.show(getSupportFragmentManager(), "forgot_password");
                break;

            case R.id.email_resend:
                if(mAuth.getCurrentUser() != null) {
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            Toast toast= Toast.makeText(getApplicationContext(),
                                    "A new verification email has been sent.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 300);
                            toast.show();
                            resendEmail.setVisibility(View.GONE);
                        }
                        else if (task.getException() != null)
                            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
                    });
                }
                break;
        }
    }
}
