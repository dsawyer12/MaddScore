package com.example.dsawyer.maddscore.Courses;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.Objects.CourseReview;
import com.example.dsawyer.maddscore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CourseReviewDialogue extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;
    private Course course;
    private double rating = 0.0;
    private String review;
    private CourseReview userReview;

    private TextView courseName, character_limit;
    private ImageView one_star, two_star, three_star, four_star, five_star;
    private EditText written_review;
    private Button cancel, submit;

    public interface OnReviewCompleteListener {
        void onComplete(String writtenReview, double rating);
    }
    OnReviewCompleteListener listener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialogue_course_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        courseName = view.findViewById(R.id.courseName);
        one_star = view.findViewById(R.id.rate_one_star);
        two_star = view.findViewById(R.id.rate_two_star);
        three_star = view.findViewById(R.id.rate_three_star);
        four_star = view.findViewById(R.id.rate_four_star);
        five_star = view.findViewById(R.id.rate_five_star);
        character_limit = view.findViewById(R.id.character_limit);

        written_review = view.findViewById(R.id.written_review);
        cancel = view.findViewById(R.id.cancel_btn);
        submit = view.findViewById(R.id.submit_btn);

        one_star.setOnClickListener(this);
        two_star.setOnClickListener(this);
        three_star.setOnClickListener(this);
        four_star.setOnClickListener(this);
        five_star.setOnClickListener(this);
        cancel.setOnClickListener(this);
        submit.setOnClickListener(this);

        written_review.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (userReview != null
                        && userReview.getReview() != null
                        && !written_review.getText().toString().trim().equals(userReview.getReview())) {
                    submit.setEnabled(true);
                }

                if (written_review.getText().toString().trim().length() == 300)
                    character_limit.setVisibility(View.VISIBLE);
                else
                    character_limit.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // check whether user has already submitted a rating and/or review.
        // if they have then set "submit" button enabled and change text to "Update"

        course = getCourseFromBundle();
        if (course == null) {
            Toast.makeText(getActivity(), "An error occurred.", Toast.LENGTH_SHORT).show();
            getDialog().dismiss();
        }
        else {
            courseName.setText(course.getName());
            userReview = getUserReviewFromBundle();
            if (userReview != null) {
                setUserReview();
            }
        }
    }

    public void setUserReview() {

        if (userReview.getReview() != null)
            written_review.setText(userReview.getReview());

        switch ((int) userReview.getRating()) {
            case (1):
                rating = 1.0;
                one_star.setImageResource(R.drawable.ic_favorite_green);
                two_star.setImageResource(R.drawable.ic_rate_open);
                three_star.setImageResource(R.drawable.ic_rate_open);
                four_star.setImageResource(R.drawable.ic_rate_open);
                five_star.setImageResource(R.drawable.ic_rate_open);
                break;

            case (2):
                rating = 2.0;
                one_star.setImageResource(R.drawable.ic_favorite_green);
                two_star.setImageResource(R.drawable.ic_favorite_green);
                three_star.setImageResource(R.drawable.ic_rate_open);
                four_star.setImageResource(R.drawable.ic_rate_open);
                five_star.setImageResource(R.drawable.ic_rate_open);
                break;

            case (3):
                rating = 3.0;
                one_star.setImageResource(R.drawable.ic_favorite_green);
                two_star.setImageResource(R.drawable.ic_favorite_green);
                three_star.setImageResource(R.drawable.ic_favorite_green);
                four_star.setImageResource(R.drawable.ic_rate_open);
                five_star.setImageResource(R.drawable.ic_rate_open);
                break;

            case (4):
                rating = 4.0;
                one_star.setImageResource(R.drawable.ic_favorite_green);
                two_star.setImageResource(R.drawable.ic_favorite_green);
                three_star.setImageResource(R.drawable.ic_favorite_green);
                four_star.setImageResource(R.drawable.ic_favorite_green);
                five_star.setImageResource(R.drawable.ic_rate_open);
                break;

            case (5):
                rating = 5.0;
                one_star.setImageResource(R.drawable.ic_favorite_green);
                two_star.setImageResource(R.drawable.ic_favorite_green);
                three_star.setImageResource(R.drawable.ic_favorite_green);
                four_star.setImageResource(R.drawable.ic_favorite_green);
                five_star.setImageResource(R.drawable.ic_favorite_green);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.rate_one_star):
                rating = 1.0;
                submit.setEnabled(true);
                one_star.setImageResource(R.drawable.ic_favorite_green);
                two_star.setImageResource(R.drawable.ic_rate_open);
                three_star.setImageResource(R.drawable.ic_rate_open);
                four_star.setImageResource(R.drawable.ic_rate_open);
                five_star.setImageResource(R.drawable.ic_rate_open);
                break;

            case(R.id.rate_two_star):
                rating = 2.0;
                submit.setEnabled(true);
                one_star.setImageResource(R.drawable.ic_favorite_green);
                two_star.setImageResource(R.drawable.ic_favorite_green);
                three_star.setImageResource(R.drawable.ic_rate_open);
                four_star.setImageResource(R.drawable.ic_rate_open);
                five_star.setImageResource(R.drawable.ic_rate_open);
                break;

            case(R.id.rate_three_star):
                rating = 3.0;
                submit.setEnabled(true);
                one_star.setImageResource(R.drawable.ic_favorite_green);
                two_star.setImageResource(R.drawable.ic_favorite_green);
                three_star.setImageResource(R.drawable.ic_favorite_green);
                four_star.setImageResource(R.drawable.ic_rate_open);
                five_star.setImageResource(R.drawable.ic_rate_open);
                break;

            case(R.id.rate_four_star):
                rating = 4.0;
                submit.setEnabled(true);
                one_star.setImageResource(R.drawable.ic_favorite_green);
                two_star.setImageResource(R.drawable.ic_favorite_green);
                three_star.setImageResource(R.drawable.ic_favorite_green);
                four_star.setImageResource(R.drawable.ic_favorite_green);
                five_star.setImageResource(R.drawable.ic_rate_open);
                break;

            case(R.id.rate_five_star):
                rating = 5.0;
                submit.setEnabled(true);
                one_star.setImageResource(R.drawable.ic_favorite_green);
                two_star.setImageResource(R.drawable.ic_favorite_green);
                three_star.setImageResource(R.drawable.ic_favorite_green);
                four_star.setImageResource(R.drawable.ic_favorite_green);
                five_star.setImageResource(R.drawable.ic_favorite_green);
                break;

            case(R.id.cancel_btn):
                getDialog().dismiss();
                break;

            case(R.id.submit_btn):
                submitReview();
                break;
        }
    }

    public Course getCourseFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null)
            return bundle.getParcelable("course");
        else
            return null;
    }

    public CourseReview getUserReviewFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null)
            return bundle.getParcelable("userReview");
        else
            return null;
    }

    public void submitReview() {
        listener.onComplete(written_review.getText().toString().trim(), rating);
        dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnReviewCompleteListener) getTargetFragment();
        }
        catch(Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }
}















