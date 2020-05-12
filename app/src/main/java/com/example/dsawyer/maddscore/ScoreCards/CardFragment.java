package com.example.dsawyer.maddscore.ScoreCards;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.ContentFrameLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.Scorecard;
import com.example.dsawyer.maddscore.R;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CardFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    LinearLayout linearLayout, card_players_header;
    ContentFrameLayout contentFrameLayout;
    TextView courseName, dateCreated;
    ImageView expand;
    Scorecard mCard;
    private int ACTIVITY_NUM;

    public CardFragment(){
        super();
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        courseName = view.findViewById(R.id.course_name);
        dateCreated = view.findViewById(R.id.card_date_created);
        linearLayout = view.findViewById(R.id.linear_layout);
        card_players_header =view.findViewById(R.id.card_players_header);
        card_players_header.setOnClickListener(this);
        contentFrameLayout = view.findViewById(R.id.card_charts_content_frame);
        expand = view.findViewById(R.id.players_expand);
        ImageView go_back = view.findViewById(R.id.go_back);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: pressed");
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStackImmediate();
            }
        });

        try{
            ACTIVITY_NUM = getACTIVITY_NUM();
            mCard = getCardFromBundle();
//            courseName.setText(mCard.getCourseName());
            dateCreated.setText(String.valueOf(mCard.getDateCreated()));
        }
        catch (NullPointerException e){
            e.getMessage();
        }

        for (int i = 0; i < mCard.getPlayers().size(); i++){
            View v = getLayoutInflater().inflate(R.layout.snippet_card_list_players, null);

            CircleImageView player_photo = v.findViewById(R.id.user_img);
//            if (mCard.getPlayers().get(i).getPlayerPhotoURL() != null){
//                Glide.with(this).load(mCard.getPlayers().get(i).getPlayerPhotoURL()).into(player_photo);
//            }
//            else{
//                Glide.with(this).load(R.mipmap.default_profile_img).into(player_photo);
//            }

            TextView player_username = v.findViewById(R.id.player_username);
//            player_username.setText(mCard.getPlayers().get(i).getPlayerUsername());

            TextView player_name = v.findViewById(R.id.player_name);
//            player_name.setText(mCard.getPlayers().get(i).getPlayerName());

            TextView player_total_score = v.findViewById(R.id.player_end_total);
            player_total_score.setText(" (" + String.valueOf(mCard.getPlayers().get(i).getEndScore()) + ")");

            TextView player_end_score = v.findViewById(R.id.player_end_score);
            if (mCard.getPlayers().get(i).getTotal() == 0){
                player_end_score.setText("E");
            }
            else{
                player_end_score.setText(String.valueOf(mCard.getPlayers().get(i).getTotal()));
            }

            linearLayout.addView(v);

        }

        CardChartsFragment charts = new CardChartsFragment(ACTIVITY_NUM, mCard);

        setFragment(charts);
    }

    private Scorecard getCardFromBundle(){
        Bundle bundle = this.getArguments();
        if (bundle != null){
            return bundle.getParcelable("card");
        }
        else{
            return null;
        }
    }

    private int getACTIVITY_NUM(){
        Bundle bundle = this.getArguments();
        if (bundle != null){
            return bundle.getInt("ACTIVITY_NUM");
        }
        else{
            return -1;
        }
    }

    public void setFragment(final Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.card_charts_content_frame, fragment).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.card_players_header):
                if (!linearLayout.isShown()){
                    linearLayout.setVisibility(View.VISIBLE);
                    expand.setImageResource(R.drawable.ic_up_collapse_arrow);
                }
                else{
                    linearLayout.setVisibility(View.GONE);
                    expand.setImageResource(R.drawable.ic_down_expand_arrow);
                }
                break;
        }
    }
}
