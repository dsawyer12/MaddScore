package com.example.dsawyer.maddscore.ScoreCards;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dsawyer.maddscore.Objects.Scorecard;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.CardPlayerListRecyclerView;

import java.util.Objects;

@SuppressLint("ValidFragment")
public class CardPlayersFragment extends Fragment implements CardActivity.OnCardChangeListener {
    private static final String TAG = "TAG";

    private Scorecard card;
    private CardPlayerListRecyclerView adapter;

    public CardPlayersFragment(Scorecard card) {
        this.card = card;
    }

    @Override
    public void onParChanged(int par) {
        updateUserPar(par);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_player_list, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: CardPlayersFragment");

        RecyclerView recyclerView = view.findViewById(R.id.recycler);

        CardPlayerListRecyclerView.OnItemClickListener listener = new CardPlayerListRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClicked(View v, int position) {
                int viewID = v.getId();

                if (viewID == R.id.minus_btn) {
                    if (card.getPlayers().get(position).getHoleScore(card.getCurrentHole()) > 1) {
                        Log.d(TAG, "hole score > 1");
                        card.getPlayers().get(position).updateHoleScore(card.getCurrentHole(),
                                (card.getPlayers().get(position).getHoleScore(card.getCurrentHole())) - 1);

                        card.getPlayers().get(position).setTotal(card.getPlayers().get(position).getTotal() - 1);
                    }
                    if (card.getPlayers().get(position).getHoleScore(card.getCurrentHole()) == 0)
                        card.getPlayers().get(position).updateHoleScore(card.getCurrentHole(), card.getPar(card.getCurrentHole()));

                    adapter.notifyDataSetChanged();
                }
                else if (viewID == R.id.plus_btn) {
                    if (card.getPlayers().get(position).getHoleScore(card.getCurrentHole()) == 0)
                        card.getPlayers().get(position).updateHoleScore(card.getCurrentHole(), card.getPar(card.getCurrentHole()));

                    else {
                        card.getPlayers().get(position).updateHoleScore(card.getCurrentHole(),
                                card.getPlayers().get(position).getHoleScore(card.getCurrentHole()) + 1);

                        card.getPlayers().get(position).setTotal((card.getPlayers().get(position).getTotal()) + 1);

                    }

                    adapter.notifyDataSetChanged();
                }
            }
        };

        adapter = new CardPlayerListRecyclerView(getActivity(), listener, card);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        CardActivity mActivity = (CardActivity) getActivity();
        if (mActivity != null)
            mActivity.setCardChangeListener(this);
    }

    public void updateUserPar(int par) {

        for (int i = 0; i < card.getPars().size(); i++) {
            Log.d(TAG, "PARS BEFORE : " + card.getPars().get(i));
        }

        int prevPar = card.getPar(card.getCurrentHole());
        for (int i = 0; i < card.getPlayers().size(); i++) {
            if(card.getPlayers().get(i).getHoleScore(card.getCurrentHole()) != 0)
                card.getPlayers().get(i).setTotal(card.getPlayers().get(i).getTotal() - (par - prevPar));
            else
                Log.d(TAG, "updateUserPar: update score is 0");

        }
        card.updatePar(card.getCurrentHole(), par);
        adapter.notifyDataSetChanged();

        for (int i = 0; i < card.getPars().size(); i++) {
            Log.d(TAG, "PAR AFTER : " + card.getPars().get(i));
        }
    }


}
