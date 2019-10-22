package com.example.dsawyer.maddscore.Social;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.dsawyer.maddscore.Objects.tempCourse;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.CourseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostCourseSelectorDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";
    private static final int ACTIVITY_NUM = 4;

    private DatabaseReference ref;
    private String UID;

    Button course_selection_cancel;
    RecyclerView recyclerView;

    public interface OnBundleSelectedListener{
        void onCourseSelected(tempCourse tempCourse);
    }
    OnBundleSelectedListener courseSelected;


    public PostCourseSelectorDialog() {
        super();
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_course_selector, container, false);
    }

    @Override
    public void onAttach(Context context) {
        try{
            courseSelected = (OnBundleSelectedListener) getTargetFragment();
        }
        catch(ClassCastException e){
            e.getMessage();
        }
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        course_selection_cancel = view.findViewById(R.id.course_selection_cancel);
        recyclerView = view.findViewById(R.id.recyclerView);
        course_selection_cancel.setOnClickListener(this);


        final ListView courseList = view.findViewById(R.id.course_list);
        final ArrayList<tempCourse> listOfCours = new ArrayList<>();

        Query query = ref.child("courses").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    tempCourse tempCourse = ds.getValue(tempCourse.class);
                    listOfCours.add(tempCourse);

                }
                CourseListAdapter adapter = new CourseListAdapter(getActivity(), listOfCours, ACTIVITY_NUM);
                courseList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                courseSelected.onCourseSelected(listOfCours.get(position));
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.course_selection_cancel):
                getDialog().dismiss();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }
}
