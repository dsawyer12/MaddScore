package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dsawyer.maddscore.R;

import java.util.ArrayList;

public class SquadListAdapter extends BaseAdapter {
    private static final String TAG = "TAG";

    private LayoutInflater inflater;
//    ArrayList<Squad> squadList;
    ArrayList<String> squadNames;
    ArrayList<String> creators;

    private class ViewHolder {
        TextView textView1;
        TextView textView2;
    }

    public SquadListAdapter(Context context, ArrayList<String> squadNames, ArrayList<String> creators) {
        inflater = LayoutInflater.from(context);
        this.squadNames = squadNames;
        this.creators = creators;
    }

    public int getCount() {
       return squadNames.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
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
            convertView = inflater.inflate(R.layout.snippet_squad_list, null);
            holder.textView1 = convertView.findViewById(R.id.player_username);
            holder.textView2 = convertView.findViewById(R.id.player_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView1.setText(squadNames.get(position));
        holder.textView2.setText(creators.get(position));
        return convertView;
    }
}
