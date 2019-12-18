package com.example.dsawyer.maddscore.Other;

import android.os.Bundle;
import android.os.PatternMatcher;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dsawyer.maddscore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    EditText email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = view.findViewById(R.id.email_input);
        view.findViewById(R.id.forgot_password_cancel).setOnClickListener(this);
        view.findViewById(R.id.forgot_password_send).setOnClickListener(this);
    }

    public void sendEmail() {
        String emailAddress = email.getText().toString().trim();

        if (emailAddress.isEmpty()) {
            email.setError("Field is empty");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            email.setError("Please enter a valid email address");
            email.requestFocus();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "An email has been sent to " + emailAddress, Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
            else
                Toast.makeText(getActivity(), "An error occurred. Please check that your email is correct and try again", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.forgot_password_cancel):
                getDialog().dismiss();
                break;

            case(R.id.forgot_password_send):
                sendEmail();
                break;
        }
    }
}
