package com.example.dsawyer.maddscore.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.dsawyer.maddscore.R;

public class EditEmailDialog extends DialogFragment {
    private static final String TAG = "TAG";

    EditText password;
    Button confirm_password;

    public interface OnConfirmPasswordListener{
        void onConfirmPassword(String password);
    }
    OnConfirmPasswordListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        password = view.findViewById(R.id.password);
        confirm_password = view.findViewById(R.id.confirm_password);
        confirm_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPassword();
            }
        });
    }

   public void verifyPassword(){
        String mPassword = password.getText().toString();
        if (!mPassword.isEmpty()){
            listener.onConfirmPassword(mPassword);
            getDialog().dismiss();
        }
        else{
            password.setError("Please enter your password");
            password.requestFocus();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnConfirmPasswordListener) getActivity();
        }
        catch (ClassCastException e){
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
}
