package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Courses.CoursesActivity;
import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.Objects.tempCourse;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.ScoreCards.ScorecardActivity;

import java.util.ArrayList;

public class CourseListRecyclerView extends RecyclerView.Adapter<CourseListRecyclerView.ViewHolder>{
    private static final String TAG = "TAG";

    private Context context;
    private OnCourseSelectedListener listener;
    private ArrayList<Course> courses;
    private int ACTIVITY_NUM;

    public interface OnCourseSelectedListener {
        void onCourseClicked(Course course);
        void onNewCardCourseSelected(Course course);
    }

    public CourseListRecyclerView(Context context, int ACTIVITY_NUM, OnCourseSelectedListener listener, ArrayList<Course> courses) {
        this.context = context;
        this.ACTIVITY_NUM = ACTIVITY_NUM;
        this.listener = listener;
        this.courses = courses;
    }

    public void updateList(ArrayList<Course> list) {
        this.courses = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, numHoles, timesPlayed, rating, distance;
        LinearLayout rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.marker_title);
            address = itemView.findViewById(R.id.marker_address);
            numHoles = itemView.findViewById(R.id.marker_num_holes);
            timesPlayed = itemView.findViewById(R.id.marker_times_played);
            rating = itemView.findViewById(R.id.marker_rating);
            distance = itemView.findViewById(R.id.marker_distance);
            rootView = itemView.findViewById(R.id.rootView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_course_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
       holder.name.setText(courses.get(position).getName());
       if (courses.get(position).getAddress() != null) {
           holder.address.setVisibility(View.VISIBLE);
           holder.address.setText(courses.get(position).getAddress().trim());
       }
       else
           holder.address.setVisibility(View.GONE);

        holder.numHoles.setText(courses.get(position).getNumHoles() + " holes");
       holder.timesPlayed.setText("Played 7 times");

       if (courses.get(position).getRating() != null) {
           holder.rating.setVisibility(View.VISIBLE);
           holder.rating.setText(String.valueOf(courses.get(position).getRating()));
       }
       else
           holder.rating.setVisibility(View.GONE);

       if (courses.get(position).getDistance() != 0.0) {
           holder.distance.setVisibility(View.VISIBLE);
           holder.distance.setText(Math.round(courses.get(holder.getAdapterPosition()).getDistance() * 10.0) / 10.0 + " miles");
       }
       else
           holder.distance.setVisibility(View.GONE);

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ACTIVITY_NUM == CoursesActivity.ACTIVITY_NUM)
                    listener.onCourseClicked(courses.get(holder.getAdapterPosition()));
                else if (ACTIVITY_NUM == ScorecardActivity.ACTIVITY_NUM)
                    listener.onNewCardCourseSelected(courses.get(holder.getAdapterPosition()));
            }
        });

    }
    @Override
    public int getItemCount() {
        return courses.size();
    }
}
