package com.example.dsawyer.maddscore.ScoreCards;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dsawyer.maddscore.R;

import java.util.ArrayList;
import java.util.Objects;

public class CardIncompleteFragment extends android.support.v4.app.DialogFragment {
    private static final String TAG = "TAG";

    ArrayList<Integer> unfinished;

    Button complete_button;

    public CardIncompleteFragment(){
        super();
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_incomplete, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unfinished = Objects.requireNonNull(getArguments()).getIntegerArrayList("unfinished");
        for (int i = 0; i < unfinished.size(); i++){
            Log.d(TAG, "unfinished: " + unfinished.get(i));
        }

        complete_button = view.findViewById(R.id.continueButton);
        complete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }
}

