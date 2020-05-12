package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.CardObject;
import com.example.dsawyer.maddscore.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScorecardViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "TAG";

    LinearLayout linearLayout;
    TextView course_name, card_date, num_players;
    CardView rootView;
    LayoutInflater inflater;
    Context context;

    public ScorecardViewHolder(@NonNull View itemView) {
        super(itemView);

        this.context = itemView.getContext();
        inflater = LayoutInflater.from(itemView.getContext());

        linearLayout = itemView.findViewById(R.id.linearLayout);
        course_name = itemView.findViewById(R.id.course_name);
        card_date = itemView.findViewById(R.id.card_date);
//        num_players = itemView.findViewById(R.id.card_num_players);
        rootView = itemView.findViewById(R.id.rootView);
    }

    public void setItem(CardObject cardObject) {
        course_name.setText(cardObject.getCourseID());
        card_date.setText(String.valueOf(cardObject.getDateCreated()));
        num_players.setText(cardObject.getUsers().size() + " players");

        if (cardObject.getPlayers().size() >= 5) {
            Log.d(TAG, "setItem: greater than 5");
            for (int i = 0; i < 5; i++) {
                View view = inflater.inflate(R.layout.snippet_card_player_item, null);
                view.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
//                ((TextView) view.findViewById(R.id.player_username)).setText(cardObject.getPlayers().get(i).getPlayerUsername());

//                if (cardObject.getUsersList().get(i).getPhotoUrl() != null) {
//                    Glide.with(context).load(cardObject.getPlayers().get(i).getPlayerPhotoURL())
//                            .into( ((CircleImageView) view.findViewById(R.id.player_img)) );
//                }
//                else {
//                    Glide.with(context).load(R.mipmap.default_profile_img)
//                            .into( ((CircleImageView) view.findViewById(R.id.player_img)) );
//                }

                if (cardObject.getPlayers().get(i).getTotal() == 0) {
                    ((TextView) view.findViewById(R.id.player_end_score)).setText("E");
                }
                else {
                    ((TextView) view.findViewById(R.id.player_end_score)).setText(
                            String.valueOf(cardObject.getPlayers().get(i).getTotal()));
                }
                ((TextView) view.findViewById(R.id.player_end_total)).setText("(" +
                        cardObject.getPlayers().get(i).getEndScore() + ")");
                linearLayout.addView(view);
            }
        }
        else {
            Log.d(TAG, "setItem: less than 5");
            for (int i = 0; i < cardObject.getPlayers().size(); i++) {
                View view = inflater.inflate(R.layout.snippet_card_player_item, null);
                view.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
//                ((TextView) view.findViewById(R.id.player_username)).setText(cardObject.getPlayers().get(i).getPlayerUsername());

//                if (cardObject.getUsersList().get(i).getPhotoUrl() != null) {
//                    Glide.with(context).load(cardObject.getUsersList().get(i).getPhotoUrl())
//                            .into( ((CircleImageView) view.findViewById(R.id.player_img)) );
//                }
//                else {
//                    Glide.with(context).load(R.mipmap.default_profile_img)
//                            .into( ((CircleImageView) view.findViewById(R.id.player_img)) );
//                }

                if (cardObject.getPlayers().get(i).getTotal() == 0) {
                    ((TextView) view.findViewById(R.id.player_end_score)).setText("E");
                }
                else {
                    ((TextView) view.findViewById(R.id.player_end_score)).setText(
                            String.valueOf(cardObject.getPlayers().get(i).getTotal()));
                }
                ((TextView) view.findViewById(R.id.player_end_total)).setText("(" +
                        cardObject.getPlayers().get(i).getEndScore() + ")");
                linearLayout.addView(view);
            }
        }
    }
}
