package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.UserStats;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SquadMemberListAdapter extends BaseAdapter {
    private static final String TAG = "TAG";

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<User> userList;
    private ArrayList<UserStats> userStats;

    private class ViewHolder {
        TextView username;
        TextView name;
        TextView rank;
        CircleImageView playerImage;
    }

    public SquadMemberListAdapter(Context context, ArrayList<User> userList, ArrayList<UserStats> userStats) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.userList = userList;
        this.userStats = userStats;
    }

    public int getCount() {
       return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public String getUsername(int position) {
        return userList.get(position).getUsername();
    }

    public String getName(int position) {
        return userList.get(position).getName();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.snippet_squad_player_list_item, null);
            holder.username = convertView.findViewById(R.id.player_username);
            holder.name = convertView.findViewById(R.id.player_name);
            holder.rank = convertView.findViewById(R.id.player_rank);
            holder.playerImage = convertView.findViewById(R.id.player_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.username.setText(userList.get(position).getUsername());
        holder.name.setText(userList.get(position).getName());
        for (int i = 0; i < userList.size(); i++) {
            if (userStats.get(position).getUserID().equals(userList.get(i).getUserID())) {
                holder.rank.setText(String.valueOf(userStats.get(position).getSquadRank()));
            }
        }
        holder.rank.setText(String.valueOf(userStats.get(position).getSquadRank()));
        if (userList.get(position).getPhotoUrl() != null) {
            Glide.with(context).load(userList.get(position).getPhotoUrl()).into(holder.playerImage);
        }
        else {
            Glide.with(context).load(R.mipmap.default_profile_img).into(holder.playerImage);
        }
        return convertView;
    }
}
