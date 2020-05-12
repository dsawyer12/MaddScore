package com.example.dsawyer.maddscore.ScoreCards;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.Player;
import com.example.dsawyer.maddscore.Objects.Scorecard;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Players.CustomPlayerFragment;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.SectionsPagerAdapter;

import java.util.ArrayList;

public class CardAddPlayerDialog extends DialogFragment implements
        CardAddPlayerListFragment.OnNewPlayersSorted,
        CardAddPlayerSquadListFragment.OnSortSquadList {
    private static final String TAG = "TAG";

    Button addPlayer;

    private Scorecard mCard;
    private ArrayList<User> newPlayerList = new ArrayList<>();
    private ArrayList<User> newSquadPlayerList = new ArrayList<>();

    @Override
    public void onSort(ArrayList<User> sortedPlayers) {
        newPlayerList = sortedPlayers;
    }

    @Override
    public void onSortSquad(ArrayList<User> squadUsers) {
        newSquadPlayerList = squadUsers;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_card_add_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addPlayer = view.findViewById(R.id.addPlayerBtn);

        if (this.getArguments() != null)
            mCard = this.getArguments().getParcelable("mCard");


        if (mCard != null) {
            ViewPager viewPager = view.findViewById(R.id.viewpager);
            SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());

            CardAddPlayerListFragment cardAddPlayerListFragment = new CardAddPlayerListFragment();
            CardAddPlayerSquadListFragment cardAddPlayerSquadListFragment = new CardAddPlayerSquadListFragment();
            CustomPlayerFragment customPlayerFragment = new CustomPlayerFragment();

            cardAddPlayerListFragment.setTargetFragment(getParentFragment(), 11);
            cardAddPlayerSquadListFragment.setTargetFragment(getParentFragment(), 12);
            customPlayerFragment.setTargetFragment(getParentFragment(), 13);

            Bundle args = new Bundle();
            args.putParcelable("mCard", mCard);
            cardAddPlayerListFragment.setArguments(args);
            cardAddPlayerSquadListFragment.setArguments(args);
            customPlayerFragment.setArguments(args);

            adapter.addFragment(cardAddPlayerListFragment);
            adapter.addFragment(cardAddPlayerSquadListFragment);
            adapter.addFragment(customPlayerFragment);
            viewPager.setAdapter(adapter);

            TabLayout tabLayout = view.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.getTabAt(0).setText("Friends");
            tabLayout.getTabAt(1).setText("Squad");
            tabLayout.getTabAt(2).setText("Custom");

            View root = tabLayout.getChildAt(0);
            if (root instanceof LinearLayout) {
                ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(ContextCompat.getColor(getActivity(), R.color.grey));
                drawable.setSize(2, 0);
                ((LinearLayout) root).setDividerPadding(10);
                ((LinearLayout) root).setDividerDrawable(drawable);
            }
        }

        addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newPlayerList.isEmpty() || !newSquadPlayerList.isEmpty()) {
                    ConfirmPlayerAddDialog confirmPlayerAddDialog = new ConfirmPlayerAddDialog();
                    Bundle args = new Bundle();
                    args.putParcelableArrayList("newPlayerList", newPlayerList);
                    args.putParcelableArrayList("newSquadPlayerList", newSquadPlayerList);
                    args.putParcelable("mCard", mCard);
                    confirmPlayerAddDialog.setArguments(args);
                    confirmPlayerAddDialog.show(getChildFragmentManager(), "confirmAdd");
                }
                else
                    Toast.makeText(getActivity(), "Select the players you wish to add", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }
}
