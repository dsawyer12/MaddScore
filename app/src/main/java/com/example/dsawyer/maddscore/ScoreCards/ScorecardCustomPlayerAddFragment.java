package com.example.dsawyer.maddscore.ScoreCards;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Players.CustomPlayerFragment;
import com.example.dsawyer.maddscore.R;

import java.util.ArrayList;

public class ScorecardCustomPlayerAddFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    Button create_player;
    ArrayList<User> customUsers;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scorecard_custom_player_add, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        create_player = view.findViewById(R.id.create_player_btn);
        create_player.setOnClickListener(this);

        customUsers = new ArrayList<>();

        if (getActivity() != null && getActivity().getIntent() != null) {
            getActivity().getIntent().putParcelableArrayListExtra("customCardUsers", customUsers);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.create_player_btn):
                if (getActivity() != null) {
                    CustomPlayerFragment customPlayerFragment = new CustomPlayerFragment();
                    Bundle args = new Bundle();
                    args.putInt("ACTIVITY_NUM", ScorecardActivity.ACTIVITY_NUM);
                    customPlayerFragment.setArguments(args);

                    ((NewScorecardPlayerSelectActivity)getActivity()).setFragment(customPlayerFragment);
                }
                break;
        }
    }
}























