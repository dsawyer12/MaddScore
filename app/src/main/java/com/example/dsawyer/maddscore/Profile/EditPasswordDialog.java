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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dsawyer.maddscore.R;

public class EditPasswordDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private EditText current_password, new_password;
    Button confirm;
    TextView forgot_password;

    public interface OnRequestNewPasswordListener{
        void onRequest(String current_password, String new_password);
    }

    OnRequestNewPasswordListener listener;

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

        forgot_password.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnRequestNewPasswordListener) getActivity();
        }
        catch (ClassCastException e){
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener){
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

                if (current_password.getText().toString().isEmpty()){
                    current_password.setError("Enter your password");
                    current_password.requestFocus();
                    return;
                }
                if (new_password.getText().toString().isEmpty()){
                    new_password.setError("Enter a new password");
                    new_password.requestFocus();
                }
                else{
                    listener.onRequest(current_password.getText().toString(), new_password.getText().toString());
                    getDialog().dismiss();
                }
                break;

            case(R.id.forgot_password):
                // GO TO ACTIVITY
                break;
        }
    }

}
