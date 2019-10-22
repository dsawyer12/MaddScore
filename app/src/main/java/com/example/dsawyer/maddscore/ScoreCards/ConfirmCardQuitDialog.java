package com.example.dsawyer.maddscore.ScoreCards;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.Scorecard;
import com.example.dsawyer.maddscore.R;

import java.util.ArrayList;

public class ConfirmCardQuitDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    Button cancel, confirm, confirm_no_save;
    TextView unfinishedTxt;
    CardView cardView;

    private Scorecard card;
    ArrayList<Integer> unfinished;

    public interface OnConfirmListener {
        void onConfirmEndRound(Boolean confirm);
    }

    OnConfirmListener listener;

    public ConfirmCardQuitDialog(){
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm_card_quit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        card = getCardFromBundle();

        cancel = view.findViewById(R.id.cancel);
        confirm = view.findViewById(R.id.confirm);
        confirm_no_save = view.findViewById(R.id.confirm_no_save);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        confirm_no_save.setOnClickListener(this);

        cardView = view.findViewById(R.id.cardview);
        unfinishedTxt = view.findViewById(R.id.unfinishedText);

        unfinished = new ArrayList<>();

        getUnfinished();

    }

    private void getUnfinished() {
        unfinished.clear();
        for (int i = 0; i < card.getPlayers().size(); i++) {
            for (int j = 0; j < card.getCourse().getNumHoles(); j++) {
                if (card.getPlayers().get(i).getHoleScore(j) == 0) {
                    if (!unfinished.contains(j))
                        unfinished.add(j);
                }
            }
        }
        if (!unfinished.isEmpty()) {
            cardView.setVisibility(View.VISIBLE);
            unfinishedTxt.setText("Mising record on Hole(s):" + "\n\n");
            for (int j = 0; j < unfinished.size(); j++) {
                unfinishedTxt.append(String.valueOf(unfinished.get(j) + 1) + "   ");
            }
        }
        else{
            cardView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnConfirmListener) getActivity();
        }
        catch (Exception e){
            Log.d(TAG, "Exception onAttach " + e.getMessage());
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
        switch (v.getId()) {
            case (R.id.cancel):
                getDialog().dismiss();
                break;
            case (R.id.confirm):
                listener.onConfirmEndRound(true);
                getDialog().dismiss();
                break;
            case (R.id.confirm_no_save):
                listener.onConfirmEndRound(false);
        }
    }

    public Scorecard getCardFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null){
            return bundle.getParcelable("card");
        }
        else
            return null;
    }
}
