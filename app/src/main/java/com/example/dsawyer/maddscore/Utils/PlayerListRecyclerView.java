package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerListRecyclerView extends RecyclerView.Adapter<PlayerListRecyclerView.ViewHolder> {
    private static final String TAG = "TAG";

    private Context context;
    private ArrayList<User> listOfUsers;

    public interface OnItemClicked {
        void onClick(User user);
    }
    OnItemClicked listener;

    public PlayerListRecyclerView(Context context, OnItemClicked listener, ArrayList<User> listOfUsres) {
        this.context = context;
        this.listOfUsers = listOfUsres;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView playerUsername, playerName;
        LinearLayout rootView;
        CircleImageView profileImage;

        public ViewHolder(View itemView) {
            super(itemView);
            playerUsername = itemView.findViewById(R.id.player_username);
            playerName = itemView.findViewById(R.id.player_name);
            rootView = itemView.findViewById(R.id.rootView);
            profileImage = itemView.findViewById(R.id.player_img);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_player_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.playerUsername.setText(listOfUsers.get(position).getUsername());
        holder.playerName.setText(listOfUsers.get(position).getName());
        if (listOfUsers.get(position).getPhotoUrl() != null){
            Glide.with(context).load(listOfUsers.get(position).getPhotoUrl()).into(holder.profileImage);
        }else{
            Glide.with(context).load(R.mipmap.default_profile_img).into(holder.profileImage);
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














