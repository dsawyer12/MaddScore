package com.example.dsawyer.maddscore.Squad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.Squad;
import com.example.dsawyer.maddscore.R;

import java.util.Objects;

public class SquadAddFragment extends Fragment implements View.OnClickListener{

    private Squad mSquad;
    Button go_back;
    TextView members;
    ListView list;

    public SquadAddFragment(){
        super();
        setArguments(new Bundle());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_squad_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSquad = getSquadFromBundle();

        go_back = view.findViewById(R.id.go_back);
        members = view.findViewById(R.id.members);
        list = view.findViewById(R.id.player_list);

        go_back.setOnClickListener(this);

//        members.setText(String.valueOf(mSquad.getUserRankList().size()));

    }

    private Squad getSquadFromBundle() {
        Bundle bundle = new Bundle();
        return bundle.getParcelable("squad");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.go_back):
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStackImmediate();
                break;
        }
    }
}
