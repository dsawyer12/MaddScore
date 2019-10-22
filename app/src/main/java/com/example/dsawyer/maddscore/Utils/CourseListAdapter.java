package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Objects.tempCourse;
import com.example.dsawyer.maddscore.R;

import java.util.ArrayList;

public class CourseListAdapter extends BaseAdapter {
    private static final String TAG = "TAG";

    private LayoutInflater inflater;
    private ArrayList<tempCourse> listOfCours;
    private int ACTIVITY_NUM;

    private class ViewHolder {
        TextView textView1;
        TextView textView2;
        TextView textView3;
        LinearLayout favorites_layout;
    }

    public CourseListAdapter(Context context, ArrayList<tempCourse> listOfCours, int ACTIVITY_NUM) {
        inflater = LayoutInflater.from(context);
        this.listOfCours = listOfCours;
        this.ACTIVITY_NUM = ACTIVITY_NUM;
        for (int i = 0; i < listOfCours.size(); i++){
            Log.d(TAG, listOfCours.get(i).getName());
        }
    }

    public int getCount() {
        return listOfCours.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public void setFavorite(int position, Boolean value){
        listOfCours.get(position).setFavorite(value);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public Boolean getfavorites(int position){
        return listOfCours.get(position).getFavorite();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_course_list_item_plus, null);

            holder.textView1 = convertView.findViewById(R.id.course_name);
//            holder.textView2 = convertView.findViewById(R.id.num_holes);
//            holder.textView3 = convertView.findViewById(R.id.times_played_course);
//            holder.favorites_layout = convertView.findViewById(R.id.favorites_layout);
//
//            holder.favorites_layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ((ListView) parent).performItemClick(v, position, 0);
//                }
//            });
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView1.setText(listOfCours.get(position).getName());
        holder.textView2.setText(String.valueOf(listOfCours.get(position).getNumHoles()) + " Holes");
        holder.textView3.setText("Played " + String.valueOf(listOfCours.get(position).getNumRounds()) + " times");
//        if (ACTIVITY_NUM == 4){
//            holder.favorites_layout.setVisibility(View.GONE);
//        }
//        else{
            if (getfavorites(position)){
                holder.favorites_layout.setVisibility(View.VISIBLE);
            }
            else{
                holder.favorites_layout.setVisibility(View.GONE);
            }
//        }
        return convertView;
    }
}
