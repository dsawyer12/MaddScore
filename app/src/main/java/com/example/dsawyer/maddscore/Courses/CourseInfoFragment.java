package com.example.dsawyer.maddscore.Courses;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.Objects.CourseReview;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Other.ApplicationClass;
import com.example.dsawyer.maddscore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CourseInfoFragment extends Fragment implements
        View.OnClickListener, CourseReviewDialogue.OnReviewCompleteListener {

    private static final String TAG = "TAG";
    private DatabaseReference ref;
    private String UID;
    private Course course;
    private ArrayList<CourseReview> reviews;
    private CourseReview userReview;
    private boolean isFavorite = false;
    private LinkedHashMap<CourseReview, User> topReviews;

    ImageView back_btn, rating_img, favorite_img;

    LinearLayout favorite, rate, stats, edit, reviews_root_layout;
    TextView course_name, course_num_holes, course_rating, course_total_ratings, no_reviews_msg;
    ScrollView scrollView;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        course_name = view.findViewById(R.id.course_name);
        course_num_holes = view.findViewById(R.id.course_num_holes);
        course_rating = view.findViewById(R.id.course_rating);
        course_total_ratings = view.findViewById(R.id.course_total_ratings);
        rating_img = view.findViewById(R.id.rating_img);
        favorite = view.findViewById(R.id.favorite);
        rate = view.findViewById(R.id.rate);
        stats = view.findViewById(R.id.stats);
        edit = view.findViewById(R.id.edit);
        back_btn = view.findViewById(R.id.back_btn);
        favorite_img = view.findViewById(R.id.favorite_img);
        reviews_root_layout = view.findViewById(R.id.reviews_root_layout);
        no_reviews_msg = view.findViewById(R.id.no_reviews_msg);
        scrollView = view.findViewById(R.id.scrollView);
        progressBar = view.findViewById(R.id.progressBar);

        back_btn.setOnClickListener(this);
        favorite.setOnClickListener(this);
        rate.setOnClickListener(this);
        stats.setOnClickListener(this);
        edit.setOnClickListener(this);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        course = getCourseFromBundle();
        if (course != null)
            initCourseInfo();
        else
            Log.d(TAG, "onViewCreated: null course");
    }

    public Course getCourseFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null)
            return bundle.getParcelable("course");
        else
            return null;
    }

    public void initCourseInfo() {
        course_name.setText(course.getName());
        course_num_holes.setText(course.getNumHoles() + " holes");
        if (course.getRating() != null)
            course_rating.setText(String.valueOf(course.getRating()));
        if(course.getNumRatings() != null &&  course.getNumRatings() != 0)
            course_total_ratings.setText("("+course.getNumRatings()+")");
        else {
            course_rating.setVisibility(View.GONE);
            course_total_ratings.setVisibility(View.GONE);
        }

        // Make buttons disabled until all information is retrieved starting from the below code

        scrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        ref.child("courseFavorites").child(UID).child(course.getCourseId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            isFavorite = true;
                            favorite_img.setColorFilter(ContextCompat.getColor(ApplicationClass.getContext(), R.color.colorPrimary));
                        }
                        else {
                            isFavorite = false;
                            favorite_img.setColorFilter(ContextCompat.getColor(ApplicationClass.getContext(), R.color.white));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        reviews = new ArrayList<>();
        Query query = ref.child("courseReviews").child(course.getCourseId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    reviews.clear();
                    CourseReview review;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        review = ds.getValue(CourseReview.class);
                        reviews.add(review);
                    }
                }
                initReviews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void initReviews() {
        if (!reviews.isEmpty()) {
            topReviews = new LinkedHashMap<>();
            for (int i = 0; i < reviews.size(); i++) {
                if (reviews.get(i).getReviewerId().equals(UID)) {
                    rating_img.setColorFilter(ContextCompat.getColor(ApplicationClass.getContext(), R.color.colorPrimary));
                    userReview = reviews.get(i);
                }
            }

            Query query = ref.child("users");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user;
                    topReviews.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        user = ds.getValue(User.class);
                        if (user != null) {
                            for (int i = 0; i < reviews.size(); i++) {
                                if (user.getUserID().equals(reviews.get(i).getReviewerId()) && !reviews.get(i).getReview().trim().isEmpty()) {
                                    topReviews.put(reviews.get(i), user);
                                    Log.d(TAG, "onDataChange: review added : " + reviews.get(i).getReview());
                                }
                            }
                        }
                    }

                    if (topReviews.isEmpty())
                        no_reviews_msg.setVisibility(View.VISIBLE);

                    else if (topReviews.size() >= 3) {
                        no_reviews_msg.setVisibility(View.GONE);

                        for (int i = 0; i < 3; i++)
                            createUserReview();
                    }
                    else {
                        no_reviews_msg.setVisibility(View.GONE);
                        for (int i = 0; i < reviews.size(); i++)
                            createUserReview();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
            no_reviews_msg.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }

    public void createUserReview() {
        Log.d(TAG, "createUserReview: ");
        View view = getLayoutInflater().inflate(R.layout.snippet_review_item, reviews_root_layout, false);
        for (User mUser : topReviews.values()) {
            if (mUser.getPhotoUrl() != null) {
                Glide.with(ApplicationClass.getContext()).load(mUser.getPhotoUrl())
                        .into((CircleImageView) view.findViewById(R.id.review_profile_img));
            }
            else {
                Glide.with(ApplicationClass.getContext()).load(R.mipmap.default_profile_img)
                        .into((CircleImageView) view.findViewById(R.id.review_profile_img));
            }
            ((TextView)view.findViewById(R.id.review_name)).setText(mUser.getName());
        }

        for (CourseReview mReview : topReviews.keySet()) {
            ((TextView)view.findViewById(R.id.review_rating)).setText(String.valueOf(mReview.getRating()));
            ((TextView)view.findViewById(R.id.review_body)).setText(mReview.getReview());
            SimpleDateFormat sdf = new SimpleDateFormat("EEE - MMM dd, yyyy");
            String dateString = sdf.format(mReview.getDate());
            ((TextView)view.findViewById(R.id.review_date)).setText(dateString);
        }

        reviews_root_layout.addView(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.back_btn):
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
            case(R.id.favorite):
                if (isFavorite) {
                    isFavorite = false;
                    favorite_img.setColorFilter(ContextCompat.getColor(ApplicationClass.getContext(), R.color.white));
                    ref.child("courseFavorites").child(UID).child(course.getCourseId()).removeValue();
                }
                else {
                    isFavorite = true;
                    favorite_img.setColorFilter(ContextCompat.getColor(ApplicationClass.getContext(), R.color.colorPrimary));
                    ref.child("courseFavorites").child(UID).child(course.getCourseId()).setValue(true);
                }

                break;

            case(R.id.rate):
                CourseReviewDialogue courseReviewDialogue = new CourseReviewDialogue();
                Bundle args = new Bundle();
                args.putParcelable("course", course);
                args.putParcelable("userReview", userReview);
                courseReviewDialogue.setArguments(args);
                courseReviewDialogue.setTargetFragment(this, 1324);
                courseReviewDialogue.show(getActivity().getSupportFragmentManager(), "review_dialogue");
                break;

            case(R.id.stats):
                CourseStatsFragment statsFragment = new CourseStatsFragment();
                Bundle statsArgs = new Bundle();
                statsArgs.putParcelable("course", course);
                statsFragment.setArguments(statsArgs);
                if (getActivity() != null) {
                    ((CoursesActivity) getActivity()).setFragment(statsFragment, "courseStats");
                }
                break;

            case(R.id.edit):

                break;
        }
    }

    @Override
    public void onComplete(String writtenReview, final double rating) {
        Log.d(TAG, "onComplete: Review from fragment");

        long date = System.currentTimeMillis();

        final CourseReview review = new CourseReview(course.getCourseId(), UID, date, rating, writtenReview);

        if (course.getRating() != null && course.getNumRatings() != null) {
            Log.d(TAG, "The course has at least one rating");
            if (userReview != null) {
                Log.d(TAG, "At least one rating is the current users");
                double newCourseRating = ((course.getRating() * course.getNumRatings())
                        - userReview.getRating() + rating) / course.getNumRatings();

                Log.d(TAG, String.valueOf(newCourseRating));

                course.setRating(Math.round(newCourseRating * 10) / 10.0);
                ref.child("courses").child(course.getCourseId()).child("rating").setValue(newCourseRating)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                ref.child("courseReviews").child(course.getCourseId()).child(UID).setValue(review)
                                        .addOnCompleteListener(task14 -> {
                                            if (task14.isSuccessful()) {
                                                course_rating.setVisibility(View.VISIBLE);
                                                course_rating.setText(String.valueOf(course.getRating()));
                                                course_total_ratings.setVisibility(View.VISIBLE);
                                                course_total_ratings.setText("("+course.getNumRatings()+")");
                                                Toast.makeText(getActivity(), "Thanks for your feedback!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });

            }
            else {
                Log.d(TAG, "The course has ratings, but not from this user");
                double value = (course.getRating() * course.getNumRatings() + rating) / (course.getNumRatings() + 1);
                course.setRating(Math.round(value * 10) / 10.0);

                ref.child("courses").child(course.getCourseId()).child("rating").setValue(course.getRating())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                ref.child("courses").child(course.getCourseId()).child("numRatings")
                                        .setValue(course.getNumRatings() + 1)
                                        .addOnCompleteListener(task13 -> {
                                            if (task13.isSuccessful()) {
                                                ref.child("courseReviews").child(course.getCourseId()).child(UID).setValue(review)
                                                        .addOnCompleteListener(task12 -> {
                                                            if (task12.isSuccessful()) {
                                                                course_rating.setVisibility(View.VISIBLE);
                                                                course_rating.setText(String.valueOf(course.getRating()));
                                                                course_total_ratings.setVisibility(View.VISIBLE);
                                                                course_total_ratings.setText("("+course.getNumRatings()+")");
                                                                Toast.makeText(getActivity(), "Thanks for your feedback!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
            }
        }
        else {
            Log.d(TAG, "The course has NO ratings");

            course.setRating(rating);
            course.setNumRatings(1);
            ref.child("courses").child(course.getCourseId()).child("rating")
                    .setValue(course.getRating())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ref.child("courses").child(course.getCourseId()).child("numRatings")
                                    .setValue(course.getNumRatings())
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            ref.child("courseReviews").child(course.getCourseId()).child(UID).setValue(review)
                                                    .addOnCompleteListener(task11 -> {
                                                        if (task11.isSuccessful()) {
                                                            course_rating.setVisibility(View.VISIBLE);
                                                            course_rating.setText(String.valueOf(course.getRating()));
                                                            course_total_ratings.setVisibility(View.VISIBLE);
                                                            course_total_ratings.setText("("+course.getNumRatings()+")");
                                                            Toast.makeText(getActivity(), "Thanks for your feedback!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
        }
    }
}


















