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
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class FavoritesCourseListFragment extends Fragment {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;
    ArrayList<String> favoriteCourseIds;
    ArrayList<Course> favoriteCourses;
    ProgressBar progressBar;

    TextView no_favorites;

    RecyclerView recyclerView;
    CourseListRecyclerView adapter;
    CourseListRecyclerView.OnCourseSelectedListener listener;
    OnFavoriteCourseSelectedListener courseSelected;

    public interface OnFavoriteCourseSelectedListener {
        void onFavoriteCourseSelected(Course course);
    }

    @Override
    public void onAttach(Context context) {
        try {
            courseSelected = (OnFavoriteCourseSelectedListener) getActivity();
        }
        catch(ClassCastException e) {
            e.getMessage();
        }
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        no_favorites = view.findViewById(R.id.no_courses_msg);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        favoriteCourseIds = new ArrayList<>();

        listener = new CourseListRecyclerView.OnCourseSelectedListener() {
            @Override
            public void onCourseClicked(Course course) {}

            @Override
            public void onNewCardCourseSelected(Course course) {
                courseSelected.onFavoriteCourseSelected(course);

//                ScorecardPlayerAddFragment card_players = new ScorecardPlayerAddFragment();
//                Bundle args = new Bundle();
//                args.putParcelable("course", course);
//                card_players.setArguments(args);
//                ((ScorecardActivity)getActivity()).setFragment(card_players);
//                setFragment(card_players);
            }
        };

        progressBar.setVisibility(View.VISIBLE);

        Query query = ref.child("courseFavorites").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    favoriteCourseIds.clear();
                    String courseId;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        courseId = ds.getKey();
                        favoriteCourseIds.add(courseId);
                    }
                    if (!favoriteCourseIds.isEmpty())
                        getFavoriteCourses();
                    else {
                        no_favorites.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
                else {
                    no_favorites.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void getFavoriteCourses() {
        Query query = ref.child("courses");
        favoriteCourses = new ArrayList<>();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteCourses.clear();
                Course course;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    course = ds.getValue(Course.class);
                    if (course != null) {
                        for (int i = 0; i < favoriteCourseIds.size(); i++) {
                            if (favoriteCourseIds.get(i).equals(course.getCourseId()))
                                favoriteCourses.add(course);
                        }
                    }
                }

                if (!favoriteCourses.isEmpty()) {
                    adapter = new CourseListRecyclerView(getActivity(), ScorecardActivity.ACTIVITY_NUM, listener, favoriteCourses);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                    progressBar.setVisibility(View.GONE);
                }
                else {
                    no_favorites.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}






























