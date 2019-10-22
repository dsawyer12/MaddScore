package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Player;
import com.example.dsawyer.maddscore.Objects.Scorecard;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CardPlayerListRecyclerView extends RecyclerView.Adapter<CardPlayerListRecyclerView.ViewHolder> {
    private static final String TAG = "TAG";

    private Context context;
    private OnItemClickListener listener;
    private Scorecard card;

    public interface OnItemClickListener {
        void onItemClicked(View v, int position);
    }

    public CardPlayerListRecyclerView(
            Context context,
            OnItemClickListener listener,
            Scorecard card) {

        this.context = context;
        this.listener = listener;
        this.card = card;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView player_image;
        TextView playerUsername, playerName, playerTotalScore, playerHoleScore;
        ImageView plus;
        ImageView minus;

        public ViewHolder(View itemView) {
            super(itemView);

            player_image = itemView.findViewById(R.id.player_img);
            playerUsername = itemView.findViewById(R.id.card_player_username);
            playerName = itemView.findViewById(R.id.card_player_name);
            playerTotalScore = itemView.findViewById(R.id.card_player_total_score);
            playerHoleScore = itemView.findViewById(R.id.card_hole_score_edit);
            plus = itemView.findViewById(R.id.plus_btn);
            minus = itemView.findViewById(R.id.minus_btn);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_card_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (card.getUsers().get(holder.getAdapterPosition()).getPhotoUrl() != null)
            Glide.with(context).load(card.getUsers().get(holder.getAdapterPosition()).getPhotoUrl()).into(holder.player_image);
        else
            Glide.with(context).load(R.mipmap.default_profile_img).into(holder.player_image);

        holder.playerUsername.setText(card.getUsers().get(holder.getAdapterPosition()).getUsername());
        holder.playerName.setText(card.getUsers().get(holder.getAdapterPosition()).getName());

        if (card.getCurrentHole() != card.getCourse().getNumHoles()) {

            if (card.getPlayers().get(holder.getAdapterPosition()).getTotal() == 0)
                holder.playerTotalScore.setText("E");
            else
                holder.playerTotalScore.setText(String.valueOf(card.getPlayers().get(holder.getAdapterPosition()).getTotal()));


            if (card.getPlayers().get(holder.getAdapterPosition()).getHoleScore(card.getCurrentHole()) == 0)
                holder.playerHoleScore.setText("-");
            else
                holder.playerHoleScore.setText(String.valueOf(card.getPlayers().get(holder.getAdapterPosition()).getHoleScore(card.getCurrentHole())));

        }

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(v, holder.getAdapterPosition());
                Log.d(TAG, "onClick: plus");

            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(v, holder.getAdapterPosition());
                Log.d(TAG, "onClick: minus");
            }
        });
    }
    @Override
    public int getItemCount() {
        return card.getUsers().size();
    }
}
