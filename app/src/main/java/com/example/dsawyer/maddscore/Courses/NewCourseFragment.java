package com.example.dsawyer.maddscore.Courses;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.ScoreCards.CustomCourseSelectFragment;
import com.example.dsawyer.maddscore.ScoreCards.ScorecardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewCourseFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;
    private int ACTIVITY_NUM = 0;

    EditText custom_number, name;
    TextView nine, eighteen, other, text;
    ImageView back_button;

    String courseName;
    int customNum = 0, numHoles = 0;
    Button createButton;

    CustomCourseSelectFragment.OnCustomCourseSelectedListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CustomCourseSelectFragment.OnCustomCourseSelectedListener) getActivity();
        }
        catch(ClassCastException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_new, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ref =  FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            UID = (FirebaseAuth.getInstance().getCurrentUser()).getUid();
        }

        back_button = view.findViewById(R.id.back_button);
        createButton = view.findViewById(R.id.new_course_button);

        name = view.findViewById(R.id.course_name);
        custom_number = view.findViewById(R.id.custom_number);
        text = view.findViewById(R.id.text);
        nine = view.findViewById(R.id.nineText);
        eighteen = view.findViewById(R.id.eighteenText);
        other = view.findViewById(R.id.otherText);

        createButton.setOnClickListener(this);
        back_button.setOnClickListener(this);
        nine.setOnClickListener(this);
        eighteen.setOnClickListener(this);
        other.setOnClickListener(this);

        if (this.getArguments() != null)
            this.ACTIVITY_NUM = this.getArguments().getInt("ACTIVITY_NUM");
        Log.d(TAG, String.valueOf(ACTIVITY_NUM));
    }

    private void createCourse() {
        courseName = name.getText().toString().trim();
        if(courseName.isEmpty()) {
            name.setError("Enter tempCourse Name");
            name.requestFocus();
            return;
        }
        if(custom_number.getText().toString().trim().isEmpty()) {
            customNum = 0;
        }
        else {
            numHoles = Integer.parseInt(custom_number.getText().toString().trim());
        }
        if (numHoles > 54) {
            custom_number.setError(getString(R.string.max_number_of_holes));
            numHoles = 0;
            customNum = 0;
            return;
        }
        if(numHoles == 0 && customNum == 0) {
            Toast.makeText(getActivity(), "Select the number of holes", Toast.LENGTH_SHORT).show();
            custom_number.requestFocus();
        }
        else {
            String courseID = ref.child("customCourses").child(UID).push().getKey();
            if (courseID != null) {
                final Course customCourse = new Course(UID, courseID, courseName, numHoles);
                ref.child("customCourses").child(UID).child(courseID).setValue(customCourse)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (getActivity() != null && ACTIVITY_NUM == ScorecardActivity.ACTIVITY_NUM) {
                                        listener.onCustomCourseSelected(customCourse);
                                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                                    }
                                }
                                if (getActivity() != null)
                                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                            }
                        });
            }
            else
                Toast.makeText(getActivity(), "An error occurred.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case(R.id.new_course_button):
                createCourse();
                break;
            case(R.id.back_button):
                if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }

                break;
            case(R.id.nineText):
                custom_number.setVisibility(View.INVISIBLE);
                text.setVisibility(View.INVISIBLE);
                custom_number.setText("");
                eighteen.setBackgroundResource(R.drawable.accessory_left_border);
                other.setBackgroundColor(Color.WHITE);
                nine.setBackgroundResource(R.drawable.accessory_full_border_green_and_black);
                numHoles = 9;
                break;
            case(R.id.eighteenText):
                custom_number.setVisibility(View.INVISIBLE);
                text.setVisibility(View.INVISIBLE);
                custom_number.setText("");
                nine.setBackgroundResource(R.drawable.accessory_left_border);
                other.setBackgroundColor(Color.WHITE);
                eighteen.setBackgroundResource(R.drawable.accessory_full_border_green_and_black);
                numHoles = 18;
                break;
            case(R.id.otherText):
                other.requestFocus();
                custom_number.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
                eighteen.setBackgroundResource(R.drawable.accessory_left_border);
                nine.setBackgroundResource(R.drawable.accessory_left_border);
                other.setBackgroundResource(R.drawable.accessory_full_border_green_and_black);
                numHoles = 0;
                break;
        }
    }
}
