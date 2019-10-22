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
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.PlayerListAdapter;

import java.util.ArrayList;

public class ConfirmPlayerRemoveDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    ListView list;
    Button cancel, remove;

    ArrayList<User> playerList;

    public interface OnCardRemovePlayers {
        void onPlayersRemoved(ArrayList<User> users);
    }
    OnCardRemovePlayers listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm_remove_card_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = view.findViewById(R.id.players_remove_list);
        cancel = view.findViewById(R.id.cancel);
        remove = view.findViewById(R.id.remove);
        cancel.setOnClickListener(this);
        remove.setOnClickListener(this);

        if ((playerList = getPlayersFromBundle()) != null) {
            PlayerListAdapter adapter = new PlayerListAdapter(getActivity(), playerList);
            list.setAdapter(adapter);
        }
    }

    private ArrayList<User> getPlayersFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null)
            return bundle.getParcelableArrayList("playersToRemove");
        else
            return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnCardRemovePlayers) getActivity();
        }
        catch (Exception e) {
            Log.d(TAG, "onAttach: exception: " + e.getMessage());
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
            case (R.id.remove):
                if (playerList != null) {
                    listener.onPlayersRemoved(playerList);
                    getDialog().dismiss();
                }
                break;
        }
    }
}
