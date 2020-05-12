package com.example.dsawyer.maddscore.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.LoadingDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditEmailDialog extends DialogFragment {
    private static final String TAG = "TAG";

    private EditText passwordField, emailField;
    private String currentEmail;

    public interface OnConfirmListener {
//        void onConfirmPassword(String password);
        void onConfirmEmailUpdate(String email);
    }
    OnConfirmListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog().getWindow() != null)
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        emailField = view.findViewById(R.id.emailField);
        passwordField = view.findViewById(R.id.passwordField);
        view.findViewById(R.id.confirm_password).setOnClickListener(v -> verifyPassword());

        if ( (currentEmail = getCurrentEmailFromBundle()) != null)
            emailField.setText(currentEmail);
    }

   public void verifyPassword() {
       DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
       FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
       String mEmail = emailField.getText().toString().trim();
       String mPassword = passwordField.getText().toString().trim();

       if (mEmail.isEmpty()) {
           emailField.setError(getString(R.string.requires_email_message));
           emailField.requestFocus();
           return;
       }

       if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
           emailField.setError(getString(R.string.requires_valid_email_message));
           emailField.requestFocus();
           return;
       }

       if (mPassword.isEmpty()) {
           passwordField.setError(getString(R.string.requires_password));
           passwordField.requestFocus();
           return;
       }

       if (currentUser != null && currentUser.getEmail() != null) {
           LoadingDialog loadingDialog = new LoadingDialog(getActivity());
           loadingDialog.show();
           AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), mPassword);
           currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   Log.d(TAG, "verifyPassword: user was reAuthenticated");
                   currentUser.updateEmail(mEmail).addOnCompleteListener(task2 ->
                           ref.child("users").child(currentUser.getUid()).child("email").setValue(mEmail)
                                   .addOnCompleteListener(task3 -> {
                       if (task3.isSuccessful()) {
                           Toast toast = Toast.makeText(getActivity(), "Email updated successfully", Toast.LENGTH_LONG);
                           toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 400);
                           toast.show();
                           listener.onConfirmEmailUpdate(mEmail);
                           loadingDialog.dismiss();
                           getDialog().dismiss();
                       } else {
                           loadingDialog.dismiss();
                           Toast toast = Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_LONG);
                           toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 400);
                           toast.show();
                       }
                   }));
               } else {
                   loadingDialog.dismiss();
                   Toast toast = Toast.makeText(getActivity(), "Verify that your password is correct and try again.", Toast.LENGTH_LONG);
                   toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 400);
                   toast.show();
               }
           });
       } else {
           Toast toast = Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_LONG);
           toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 400);
           toast.show();
       }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnConfirmListener) getActivity();
        }
        catch (ClassCastException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams)params);
    }

    public String getCurrentEmailFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null)
            return bundle.getString("currentEmail");
        else
            return null;
    }
}
