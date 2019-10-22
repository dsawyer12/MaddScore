package com.example.dsawyer.maddscore.Courses;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.Objects.tempCourse;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.CourseListRecyclerView;

import java.util.ArrayList;

public class CourseListFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    ImageView back_btn;
    RecyclerView recyclerView;

    ArrayList<Course> courses;
    CourseListRecyclerView adapter;
    CourseListRecyclerView.OnCourseSelectedListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        back_btn = view.findViewById(R.id.course_back);

        back_btn.setOnClickListener(this);

        courses = ((CoursesActivity)getActivity()).courses;
        if (courses != null) {
            initCourses();
        }
        else
            Log.d(TAG, "courses is null");
    }

    public void initCourses() {
        listener = new CourseListRecyclerView.OnCourseSelectedListener() {
            @Override
            public void onCourseClicked(Course course) {
                CourseInfoFragment courseInfoFragment = new CourseInfoFragment();
                Bundle args = new Bundle();
                args.putParcelable("course", course);
                courseInfoFragment.setArguments(args);
                ((CoursesActivity)getActivity()).setFragment(courseInfoFragment, "course");
            }

            @Override
            public void onNewCardCourseSelected(Course course) {}
        };

        adapter = new CourseListRecyclerView(getActivity(), CoursesActivity.ACTIVITY_NUM, listener, courses);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.course_back):
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
        }
    }
}











