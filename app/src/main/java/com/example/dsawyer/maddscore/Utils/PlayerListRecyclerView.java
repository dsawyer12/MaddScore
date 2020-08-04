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
import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerListRecyclerView extends RecyclerView.Adapter<PlayerListRecyclerView.ViewHolder> {
    private static final String TAG = "TAG";

    private Context context;
    private ArrayList<User> friendsList;

    public interface OnItemClicked {
        void onClick(User user);
    }
    OnItemClicked listener;

    public PlayerListRecyclerView(Context context, OnItemClicked listener, ArrayList<User> friendsList) {
        this.context = context;
        this.friendsList = friendsList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playerUsername, playerName;
        LinearLayout rootView;
        CircleImageView profileImage;

        public ViewHolder(View itemView) {
            super(itemView);
            playerUsername = itemView.findViewById(R.id.player_username);
            playerName = itemView.findViewById(R.id.player_name);
            rootView = itemView.findViewById(R.id.rootView);
            profileImage = itemView.findViewById(R.id.user_img);
        }
    }

    public void updateList(ArrayList<User> list) {
        this.friendsList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_player_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.playerUsername.setText(friendsList.get(position).getUsername());
        holder.playerName.setText(friendsList.get(position).getName());

        if (friendsList.get(position).getPhotoUrl() != null) {
            Glide.with(context).load(friendsList.get(position).getPhotoUrl()).into(holder.profileImage);
        }else
            Glide.with(context).load(R.mipmap.default_profile_img).into(holder.profileImage);

        holder.rootView.setOnClickListener(v -> listener.onClick(friendsList.get(holder.getAdapterPosition())));
    }
    @Override
    public int getItemCount() {
        return friendsList.size();
    }
}














