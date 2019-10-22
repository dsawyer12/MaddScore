package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;

import java.util.ArrayList;

public class MemberListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<User> users;

    private class ViewHolder {
        TextView textView1;
        TextView textView2;
    }

    public MemberListAdapter(Context context, ArrayList<User> users) {
        inflater = LayoutInflater.from(context);
        this.users = users;
    }

    public int getCount() {
       return users.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.snippet_player_list, null);
            holder.textView1 = convertView.findViewById(R.id.player_username);
            holder.textView2 = convertView.findViewById(R.id.player_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView1.setText(users.get(position).getUsername());
        holder.textView2.setText(users.get(position).getName());
        return convertView;
    }
}
