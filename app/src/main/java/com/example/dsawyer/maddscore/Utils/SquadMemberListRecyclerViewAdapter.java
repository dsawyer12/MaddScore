package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class SquadMemberListRecyclerViewAdapter extends RecyclerView.Adapter<SquadMemberListRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "TAG";

    private Context context;
    private User currentUser;
    private ArrayList<User> memberList;
    private OnSquadMemberClickedListener listener;

    public interface OnSquadMemberClickedListener {
        void onSquadMemberClicked(User user);
    }

    public SquadMemberListRecyclerViewAdapter(Context context, User currentUser, ArrayList<User> memberList, OnSquadMemberClickedListener listener) {
        this.context = context;
        this.currentUser = currentUser;
        this.memberList = memberList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView root;
        LinearLayout active_squad_layout, squad_name_layout, player_rank_layout;
        CircleImageView profileImg;
        TextView member_username;
        TextView member_name;
        TextView user_squad_rank;
        TextView you_placeholder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.rootView);
            active_squad_layout = itemView.findViewById(R.id.active_squad_layout);
            profileImg = itemView.findViewById(R.id.user_img);
            member_username = itemView.findViewById(R.id.member_username);
            member_name = itemView.findViewById(R.id.member_name);
            user_squad_rank = itemView.findViewById(R.id.user_squad_rank);
            you_placeholder = itemView.findViewById(R.id.you_placeholder);
            squad_name_layout = itemView.findViewById(R.id.squad_name_layout);
            player_rank_layout = itemView.findViewById(R.id.player_rank_layout);
        }
    }

    public void updateList(ArrayList<User> list) {
        this.memberList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_squad_member_item, parent, false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        if (memberList.get(holder.getAdapterPosition()).getUserID().equals(currentUser.getUserID())) {
            holder.active_squad_layout.setBackground(ContextCompat.getDrawable(context, R.drawable.your_rank_placeholder));

            holder.you_placeholder.setVisibility(View.VISIBLE);
            holder.profileImg.setVisibility(View.GONE);
            holder.squad_name_layout.setVisibility(View.GONE);
            holder.player_rank_layout.setVisibility(View.GONE);
        }
        else {
            holder.you_placeholder.setVisibility(View.GONE);
            holder.profileImg.setVisibility(View.VISIBLE);
            holder.squad_name_layout.setVisibility(View.VISIBLE);
            holder.player_rank_layout.setVisibility(View.VISIBLE);

            switch (memberList.get(holder.getAdapterPosition()).getSquadRank()) {
                case (1):
                    holder.active_squad_layout.setBackground(ContextCompat.getDrawable(context, R.drawable.rank_fade_1));
                    break;
                case (2):
                    holder.active_squad_layout.setBackground(ContextCompat.getDrawable(context, R.drawable.rank_fade_2));
                    break;
                case (3):
                    holder.active_squad_layout.setBackground(ContextCompat.getDrawable(context, R.drawable.rank_fade_3));
                    break;
                case (4):
                    holder.active_squad_layout.setBackground(ContextCompat.getDrawable(context, R.drawable.rank_fade_4));
                    break;
                case (5):
                    holder.active_squad_layout.setBackground(ContextCompat.getDrawable(context, R.drawable.rank_fade_5));
                    break;
                default:
                    holder.active_squad_layout.setBackground(ContextCompat.getDrawable(context, R.drawable.accessory_faded_background));
            }

            holder.member_username.setText(memberList.get(holder.getAdapterPosition()).getUsername());
            holder.member_name.setText(memberList.get(holder.getAdapterPosition()).getName());
            if (memberList.get(holder.getAdapterPosition()).getPhotoUrl() != null)

                Glide.with(context).load(memberList.get(holder.getAdapterPosition()).getPhotoUrl()).into(holder.profileImg);
            else
                Glide.with(context).load(R.mipmap.default_profile_img).into(holder.profileImg);

            holder.user_squad_rank.setText(String.valueOf(memberList.get(holder.getAdapterPosition()).getSquadRank()));
        }

        holder.root.setOnClickListener(v -> listener.onSquadMemberClicked(memberList.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
}



















