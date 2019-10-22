package com.example.dsawyer.maddscore.ScoreCards;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Courses.NewCourseFragment;
import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.CourseListRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomCourseSelectFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;

    ArrayList<Course> customCourses;

    Button new_custom_course_btn;
    TextView no_custom_courses_msg;
    ProgressBar progressBar;

    RecyclerView recyclerView;
    CourseListRecyclerView adapter;
    CourseListRecyclerView.OnCourseSelectedListener listener;
    OnCustomCourseSelectedListener courseSelected;

    public interface OnCustomCourseSelectedListener {
        void onCustomCourseSelected(Course course);
    }

    @Override
    public void onAttach(Context context) {
        try {
            courseSelected = (OnCustomCourseSelectedListener) getActivity();
        }
        catch(ClassCastException e) {
            e.getMessage();
        }
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_course_custom_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        no_custom_courses_msg = view.findViewById(R.id.no_custom_courses_msg);
        new_custom_course_btn = view.findViewById(R.id.create_custom_course_btn);
        new_custom_course_btn.setOnClickListener(this);

        customCourses = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        Query query = ref.child("customCourses").child(UID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    customCourses.clear();
                    no_custom_courses_msg.setVisibility(View.GONE);
                    Course course;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        course = ds.getValue(Course.class);
                        if (course != null)
                            customCourses.add(course);
                    }
                    if (!customCourses.isEmpty()) {
                        no_custom_courses_msg.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        initCourseList();
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        no_custom_courses_msg.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    no_custom_courses_msg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void initCourseList() {

        listener = new CourseListRecyclerView.OnCourseSelectedListener() {
            @Override
            public void onCourseClicked(Course course) {}

            @Override
            public void onNewCardCourseSelected(Course course) {
                courseSelected.onCustomCourseSelected(course);
            }
        };

        adapter = new CourseListRecyclerView(getActivity(), ScorecardActivity.ACTIVITY_NUM, listener, customCourses);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.create_custom_course_btn):
                NewCourseFragment newCourseFragment = new NewCourseFragment();
                Bundle args = new Bundle();
                args.putInt("ACTIVITY_NUM", ScorecardActivity.ACTIVITY_NUM);
                newCourseFragment.setArguments(args);
                if (getActivity() != null)
                    ((NewScorecardCourseSelectActivity)getActivity()).setFragment(newCourseFragment);
                break;
        }
    }
}

















