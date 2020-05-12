package com.example.dsawyer.maddscore.Squad;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Players.PlayerListFragment;
import com.example.dsawyer.maddscore.Players.PlayerSearchFragment;
import com.example.dsawyer.maddscore.R;
import java.util.Objects;

public class SquadMemberSearchFragment extends Fragment {
    private static final String TAG = "TAG";

    final PlayerSearchFragment playerSearchFragment = new PlayerSearchFragment();
    final PlayerListFragment playerListFragment = new PlayerListFragment();

    public SquadMemberSearchFragment() {
    }

    public interface OnPlayerSearchSelectionListener{
        void onPlayerSearchSelection(User user);
    }
    OnPlayerSearchSelectionListener playerSelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_squad_member_search, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        try{
            playerSelected = (OnPlayerSearchSelectionListener) getActivity();
        }
        catch(ClassCastException e){
            e.getMessage();
        }
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView back_arrow = view.findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStackImmediate();
            }
        });

        final TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Find"));
        tabLayout.addTab(tabLayout.newTab().setText("My Friends"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case (0):
                        if (getFragmentManager() != null) {
                            getFragmentManager().beginTransaction().replace(R.id.new_player_frame, playerSearchFragment).commit();
                        }
                        break;
                    case (1):
                        if (getFragmentManager() != null) {
                            getFragmentManager().beginTransaction().replace(R.id.new_player_frame, playerListFragment).commit();
                        }
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }});

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.black));
            drawable.setSize(1, 0);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().replace(R.id.new_player_frame, playerSearchFragment).commit();
        }
    }
}
