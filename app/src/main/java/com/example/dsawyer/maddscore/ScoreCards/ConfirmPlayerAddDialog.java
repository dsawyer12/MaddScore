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
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.dsawyer.maddscore.Objects.Player;
import com.example.dsawyer.maddscore.Objects.Scorecard;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.PlayerListAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConfirmPlayerAddDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private ArrayList<User> users;
    private ArrayList<User> squadUsers;
    private ArrayList<User> finalPlayers;

    ListView list;

    public interface OnConfirmAddListener {
        void onConfirm(ArrayList<User> usersToAdd);
    }
    OnConfirmAddListener confirm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm_card_add_players, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button cancel = view.findViewById(R.id.cancel);
        Button add = view.findViewById(R.id.add);
        list = view.findViewById(R.id.players_add_list);

        getUsersFromBundle();

        PlayerListAdapter adapter = new PlayerListAdapter(getActivity(), users);
        list.setAdapter(adapter);

        cancel.setOnClickListener(this);
        add.setOnClickListener(this);
    }

    private void getUsersFromBundle() {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            users = bundle.getParcelableArrayList("newPlayerList");
            squadUsers = bundle.getParcelableArrayList("newSquadPlayerList");

            finalPlayers = new ArrayList<>();

            if (users != null)
                finalPlayers.addAll(users);

            if (squadUsers != null)
                finalPlayers.addAll(squadUsers);

            Map<String, User> map = new HashMap<>();
            for (User user : finalPlayers) {
                String key = user.getUserID();
                if (!map.containsKey(key))
                    map.put(key, user);
            }
            Collection<User> newUsers = map.values();
            finalPlayers.clear();
            finalPlayers.addAll(newUsers);

       }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            confirm = (OnConfirmAddListener) getActivity();
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
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
            case (R.id.cancel):
                getDialog().dismiss();
                break;
            case (R.id.add):
                confirm.onConfirm(finalPlayers);
                getDialog().dismiss();
                break;
        }
    }
}
