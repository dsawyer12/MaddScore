package com.example.dsawyer.maddscore.ScoreCards;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.Player;
import com.example.dsawyer.maddscore.Objects.Scorecard;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.PlayerListAdapter;

import java.util.ArrayList;

public class CardRemovePlayerDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    ArrayList<User> playerToRemove = new ArrayList<>();
    Scorecard mCard;
    ListView list;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_card_remove_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button removePlayer = view.findViewById(R.id.removePlayerBtn);
        list = view.findViewById(R.id.remove_player_list);
        removePlayer.setOnClickListener(this);

        if ((mCard = getCardFromBundle()) != null) {
            PlayerListAdapter adapter = new PlayerListAdapter(getActivity(), mCard.getUsers());
            list.setAdapter(adapter);
            list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SparseBooleanArray sparseBooleanArray = list.getCheckedItemPositions();
                if (sparseBooleanArray.get(position)) {
                    playerToRemove.add(mCard.getUsers().get(position));
                    view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryTransparent));
                }
                else {
                    playerToRemove.remove(mCard.getUsers().get(position));
                    view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.PrimaryBackground));
                }
            }
        });
    }

    public Scorecard getCardFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null)
            return bundle.getParcelable("mCard");
        else
            return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = (getDialog().getWindow()).getAttributes();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Override
    public void onClick(View v) {
        if (!playerToRemove.isEmpty()) {
            ConfirmPlayerRemoveDialog confirmPlayerRemoveDialog = new ConfirmPlayerRemoveDialog();
            Bundle args = new Bundle();
            args.putParcelableArrayList("playersToRemove", playerToRemove);
            confirmPlayerRemoveDialog.setArguments(args);
            confirmPlayerRemoveDialog.show(getChildFragmentManager(), "confirmRemove");
        }
        else
            Toast.makeText(getActivity(), "Please Select a player", Toast.LENGTH_SHORT).show();
    }
}
