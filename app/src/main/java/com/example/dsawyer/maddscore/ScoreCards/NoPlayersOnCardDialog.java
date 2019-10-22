package com.example.dsawyer.maddscore.ScoreCards;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.dsawyer.maddscore.R;

public class NoPlayersOnCardDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    Button quit_round, continue_round;

    public interface NoPlayerOnQuitListener {
        void onQuit(Boolean quit);
    }

    NoPlayerOnQuitListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_no_card_players, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        continue_round = view.findViewById(R.id.continue_round);
        quit_round = view.findViewById(R.id.quit_round);

        continue_round.setOnClickListener(this);
        quit_round.setOnClickListener(this);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
           listener = (NoPlayerOnQuitListener) getActivity();
        }
        catch (Exception e){
            Log.d(TAG, "onAttach: " + e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.continue_round):
                getDialog().dismiss();
                break;

            case(R.id.quit_round):
                listener.onQuit(true);
                getDialog().dismiss();
                break;
        }
    }
}
