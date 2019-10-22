package com.example.dsawyer.maddscore.Squad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.dsawyer.maddscore.R;

public class SquadOverviewFragament extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_squad_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RelativeLayout events_rel = view.findViewById(R.id.events_rel);
        RelativeLayout members_rel = view.findViewById(R.id.members_rel);

        events_rel.setOnClickListener(this);
        members_rel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.events_rel):

                break;
            case(R.id.members_rel):

                break;
        }
    }
}
