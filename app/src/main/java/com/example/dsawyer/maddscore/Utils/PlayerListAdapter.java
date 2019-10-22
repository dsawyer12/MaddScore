package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Objects.Player;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerListAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private ArrayList<User> listOfUsers;
    private Context context;

    public class ViewHolder {
        TextView player_username;
        TextView player_name;
        LinearLayout rootView;
        CircleImageView player_img;
    }

    public PlayerListAdapter(Context context, ArrayList<User> listOfUsers) {
        inflater = LayoutInflater.from(context);
        this.listOfUsers = listOfUsers;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listOfUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.snippet_player_list_item, null);
            holder.player_img = convertView.findViewById(R.id.player_img);
            holder.player_username = convertView.findViewById(R.id.player_username);
            holder.player_name = convertView.findViewById(R.id.player_name);
            holder.rootView = convertView.findViewById(R.id.rootView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.player_username.setText(listOfUsers.get(position).getUsername());
        holder.player_name.setText(listOfUsers.get(position).getName());

        if (listOfUsers.get(position).getPhotoUrl() != null)
            Glide.with(context).load(listOfUsers.get(position).getPhotoUrl()).into(holder.player_img);
        else
            Glide.with(context).load(R.mipmap.default_profile_img).into(holder.player_img);

        return convertView;
    }

    public String getUsername(int position) {
        return listOfUsers.get(position).getUsername();
    }

    public String getName(int position) {
        return listOfUsers.get(position).getName();
    }

    public long getItemId(int position) {
        return position;
    }

}
