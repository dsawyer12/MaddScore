package com.example.dsawyer.maddscore.ScoreCards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.Scorecard;
import com.example.dsawyer.maddscore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.Gravity.CENTER;

@SuppressLint("ValidFragment")
public class CardChartsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";
    private final int ACTIVITY_NUM;

    private DatabaseReference ref;
    private String UID;

    private Scorecard card;

    RelativeLayout relativeLayoutMain;
    RelativeLayout relativeLayout;
    RelativeLayout relativeLayout2;
    RelativeLayout relativeLayout3;

    RelativeLayout relativeLayout5;
    RelativeLayout relativeLayout6;

    HorizontalScrollView horizontal_scroll;

    CoordinatorLayout coordinator;

    ScrollView scroll_view;

    LinearLayout linear_layout;
    LinearLayout linear_layout2;

    TableLayout playerTable, chartTable;

    Button finishCard;
    Context mContext;

    public interface OnCardFinishedListener{
        void onFinished(Boolean finished);
    }
    OnCardFinishedListener finishListener;

    public CardChartsFragment(
            int ACTIVITY_NUM,
            Scorecard card) {
        this.card = card;
        this.ACTIVITY_NUM = ACTIVITY_NUM;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_charts, container, false);

    }

    @Override
    public void onAttach(Context context) {
        try{
            finishListener = (OnCardFinishedListener) getActivity();
        }
        catch (Exception e) {
            Log.d(TAG, "Exception on attach: " + e.getMessage());
        }
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onCreateView: Card charts fragment");
        mContext = getActivity();

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        relativeLayoutMain = view.findViewById(R.id.relativeLayoutMain);
        relativeLayout = view.findViewById(R.id.relLayout);
        relativeLayout2 = view.findViewById(R.id.relLayout2);
        relativeLayout3 = view.findViewById(R.id.relLayout3);

        relativeLayout5 = view.findViewById(R.id.relLayout5);
        relativeLayout6 = view.findViewById(R.id.relLayout6);

        horizontal_scroll = view.findViewById(R.id.horizontal_scroll);

        coordinator = view.findViewById(R.id.coordinator);

        scroll_view = view.findViewById(R.id.scroll_view);

        linear_layout = view.findViewById(R.id.linear_layout);
        linear_layout2 = view.findViewById(R.id.linear_layout2);

        playerTable = view.findViewById(R.id.player_table);
        chartTable = view.findViewById(R.id.chartTable);

        finishCard = view.findViewById(R.id.finish_card_btn);
        finishCard.setOnClickListener(this);
        if (ACTIVITY_NUM != 6) {
            finishCard.setEnabled(false);
            finishCard.setVisibility(View.GONE);
        }
        else {
            finishCard.setVisibility(View.VISIBLE);
            finishCard.setEnabled(true);
        }

        setTotals();
        createChart1();
        createChart2();

    }

    private void setTotals() {
        int curr, endScore;

        for (int i = 0; i < card.getPlayers().size(); i++) {
            endScore = 0;
            for (int j = 0; j < card.getCourse().getNumHoles(); j++) {
                curr = card.getPlayers().get(i).getHoleScore(j);
                endScore += curr;
            }
            card.getPlayers().get(i).setEndScore(endScore);
        }
    }

    private void createChart1() {

        /**************     Table 1 setup      **************/

        for (int i = 0; i < card.getPlayers().size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.snippet_player_list, new TableRow(mContext), false);

            if (card.getUsers().get(i).getPhotoUrl() != null)
                Glide.with(mContext).load(card.getUsers().get(i).getPhotoUrl()).into( (CircleImageView) view.findViewById(R.id.player_img));
            else
                Glide.with(mContext).load(R.mipmap.default_profile_img).into( (CircleImageView) view.findViewById(R.id.player_img));


            ((TextView)view.findViewById(R.id.player_username)).setText(card.getUsers().get(i).getUsername());
            ((TextView)view.findViewById(R.id.player_name)).setText(card.getUsers().get(i).getName());
            ((TextView)view.findViewById(R.id.player_end_total)).setText(" (" + card.getPlayers().get(i).getEndScore() + ")");
            if (card.getPlayers().get(i).getTotal() == 0)
                ((TextView)view.findViewById(R.id.player_score)).setText("E");
            else
                ((TextView)view.findViewById(R.id.player_score)).setText(String.valueOf(card.getPlayers().get(i).getTotal()));

            playerTable.addView(view);
        }

    }
        /**************     Table 2 setup      **************/

    private void createChart2() {

        TableRow tableRow = new TableRow(mContext);

        tableRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        for (int i = 0; i < card.getCourse().getNumHoles(); i++) {
            TextView textView = new TextView(mContext);
            textView.setText("Hole " + (i + 1));
            textView.setTextSize(14);
            textView.setPadding(25,50,25,50);
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            textView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.accessory_full_border));
            textView.setGravity(CENTER);

            tableRow.addView(textView);

            if(i > 0)
                chartTable.removeView(tableRow);

            chartTable.addView(tableRow);
        }

        for (int i = 0; i < card.getPlayers().size(); i++) {

            TableRow tr = new TableRow(mContext);
            tr.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT, 1));

            for (int j = 0; j < card.getCourse().getNumHoles(); j++) {

                TextView textView = new TextView(mContext);
                textView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                textView.setText(String.valueOf(card.getPlayers().get(i).getHoleScore(j)));
                textView.setTextSize(16);
                if (card.getPlayers().get(i).getHoleScore(j) == card.getPar(j)) {
                    Log.d(TAG, "hole score for player = par");
                    textView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.accessory_full_border_green_and_black));
                    textView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                }
                else if(card.getPlayers().get(i).getHoleScore(j) > 0
                        && card.getPlayers().get(i).getHoleScore(j) <= card.getPar(j) - 2) {
                    Log.d(TAG, "hole score for player =  eagle");
                    textView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.accessory_full_border_blue));
                    textView.setTextColor(ContextCompat.getColor(mContext, R.color.blueButton));
                    textView.setTextColor(ContextCompat.getColor(mContext, R.color.gold));
                }
                else if (card.getPlayers().get(i).getHoleScore(j) == card.getPar(j) - 1) {
                    Log.d(TAG, "hole score for player = birdie");
                    textView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.accessory_full_border_gold));
                    textView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                }
                else if(card.getPlayers().get(i).getHoleScore(j) == card.getPar(j) + 1) {
                    Log.d(TAG, "hole score for player = bogie");
                    textView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.accessory_full_border_center_green));
                    textView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                }
                else if(card.getPlayers().get(i).getHoleScore(j) >= card.getPar(j) + 2) {
                    Log.d(TAG, "hole score for player = double bogie or more");
                    textView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.accessory_full_border_red));
                    textView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                }
                else{
                    Log.d(TAG, "par for hole " + j + " is : " + card.getPar(j));
                    Log.d(TAG, "hole score for player = " + card.getPlayers().get(i).getHoleScore(j));
                    textView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.accessory_full_border));
                    textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                }

                textView.setGravity(CENTER);

                tr.addView(textView);

                if(j > 0)
                    chartTable.removeView(tr);
                chartTable.addView(tr);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.finish_card_btn):
                checkedFinished();
                break;
        }
    }

    public void checkedFinished() {
        Log.d(TAG, "checkedFinished: called");
        ArrayList<Integer> unfinished = new ArrayList<>();
        for (int i = 0; i < card.getPlayers().size(); i++) {
            for (int j = 0; j < card.getCourse().getNumHoles(); j++) {
                if (card.getPlayers().get(i).getHoleScore(j) == 0) {
                    if (!unfinished.contains(j))
                        unfinished.add(j);
                }
            }
        }
        if (!unfinished.isEmpty())
            finishListener.onFinished(false);
        else
         finishListener.onFinished(true);
    }
}
