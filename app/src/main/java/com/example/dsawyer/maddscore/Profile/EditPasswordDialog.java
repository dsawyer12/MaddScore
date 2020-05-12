package com.example.dsawyer.maddscore.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Other.ForgotPasswordDialog;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.LoadingDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditPasswordDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private EditText current_password, new_password;
    Button confirm;
    TextView forgot_password;
    private LoadingDialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        current_password = view.findViewById(R.id.current_password);
        new_password = view.findViewById(R.id.new_password);
        forgot_password = view.findViewById(R.id.forgot_password);
        confirm = view.findViewById(R.id.confirm_password);
        loadingDialog = new LoadingDialog(getActivity());

        forgot_password.setOnClickListener(this);
        confirm.setOnClickListener(this);
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
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.confirm_password):

                if (current_password.getText().toString().isEmpty()) {
                    current_password.setError("Enter your password");
                    current_password.requestFocus();
                    return;
                }
                if (new_password.getText().toString().isEmpty()) {
                    new_password.setError("Enter a new password");
                    new_password.requestFocus();
                } else requestPasswordChange(current_password.getText().toString(), new_password.getText().toString());
                break;

            case(R.id.forgot_password):
                ForgotPasswordDialog dialog = new ForgotPasswordDialog();
                dialog.show(getActivity().getSupportFragmentManager(), "forgot_password");
                break;
        }
    }

    public void requestPasswordChange(String currentPassword, String newPassword) {
        loadingDialog.show();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "user was re-authenticated");
                    user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast toast = Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 400);
                            toast.show();
                            loadingDialog.dismiss();
                            getDialog().dismiss();
                        }

                    });
                } else {
                    loadingDialog.dismiss();
                    Toast toast = Toast.makeText(getActivity(), "Invalid Password", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 400);
                    toast.show();
                }
            });
        }
    }
}
