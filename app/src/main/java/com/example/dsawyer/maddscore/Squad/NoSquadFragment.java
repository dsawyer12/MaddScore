package com.example.dsawyer.maddscore.Squad;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dsawyer.maddscore.Profile.ProfileActivity;
import com.example.dsawyer.maddscore.R;

public class NoSquadFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no_squad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button join_squad = view.findViewById(R.id.join_squad_button);
        Button create_Squad = view.findViewById(R.id.create_squad_button);

        join_squad.setOnClickListener(this);
        create_Squad.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.join_squad_button):
                Intent intent = new Intent(getActivity(), JoinSquadActivity.class);
                startActivity(intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;

            case(R.id.create_squad_button):
                if (getActivity() != null) {
                    startActivity(new Intent(getActivity(), CreateSquadActivity.class));
                    getActivity().finish();
                }

//                SquadCreationDialog new_squad = new SquadCreationDialog();
//                new_squad.setTargetFragment(this, 122);
//                if (getFragmentManager() != null) {
//                    new_squad.show(getFragmentManager(), "new_squad");
//                }
//                else{
//                    Log.d(TAG, "onClick: fragmentManager = null");
//                }
                break;
        }
    }
}
