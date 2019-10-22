package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.UserStats;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SquadListRecyclerView extends RecyclerView.Adapter<SquadListRecyclerView.ViewHolder> {
    private static final String TAG = "TAG";

    private Context context;
    private ArrayList<User> listOfUsers;
    private ArrayList<UserStats> userStats;
    private OnItemClicked listener;

    public interface OnItemClicked{
        void onClick(User user);
    }

    public SquadListRecyclerView(Context context, OnItemClicked listener, ArrayList<User> listOfUsers, ArrayList<UserStats> userStats) {
        Log.d(TAG, String.valueOf(listOfUsers.size()));
        Log.d(TAG, String.valueOf(userStats.size()));
        this.context = context;
        this.listOfUsers = listOfUsers;
        this.userStats = userStats;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView playerUsername, playerName, playerRank;
        CircleImageView playerImage;
        LinearLayout rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            playerUsername = itemView.findViewById(R.id.player_username);
            playerName = itemView.findViewById(R.id.player_name);
            playerRank = itemView.findViewById(R.id.player_rank);
            playerImage = itemView.findViewById(R.id.player_img);
            rootView = itemView.findViewById(R.id.rootView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_squad_player_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.playerUsername.setText(listOfUsers.get(position).getUsername());
        holder.playerName.setText(listOfUsers.get(position).getName());
        holder.playerRank.setText(String.valueOf(userStats.get(position).getSquadRank()));
        if (listOfUsers.get(position).getPhotoUrl() != null){
            Glide.with(context).load(listOfUsers.get(position).getPhotoUrl()).into(holder.playerImage);
        }
        else{
            Glide.with(context).load(R.mipmap.default_profile_img).into(holder.playerImage);
        }
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(listOfUsers.get(holder.getAdapterPosition()));
            }
        });
    }
    @Override
    public int getItemCount() {
        return listOfUsers.size();
    }
}
