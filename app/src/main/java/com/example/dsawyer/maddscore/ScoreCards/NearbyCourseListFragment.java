package com.example.dsawyer.maddscore.ScoreCards;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.CourseListRecyclerView;
import com.example.dsawyer.maddscore.Utils.Permissions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.LOCATION_SERVICE;

public class NearbyCourseListFragment extends Fragment {
    private static final String TAG = "TAG";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int PERMISSION_REQUEST_CODE = 7623;

    private DatabaseReference ref;
    private String UID;
    private ArrayList<Course> allCourses;

    RecyclerView recyclerView;
    CourseListRecyclerView adapter;
    ProgressBar progressBar;
    CourseListRecyclerView.OnCourseSelectedListener listener;

    public interface OnNearbyCourseSelectedListener {
        void onNearbyCourseSelected(Course course);
    }
    OnNearbyCourseSelectedListener courseSelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        try {
            courseSelected = (OnNearbyCourseSelectedListener) getActivity();
        }
        catch(ClassCastException e) {
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

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        get_permissions();
    }

    private void get_permissions() {
        Log.d(TAG, "get_permissions: called");
        String[] permissions = Permissions.LOCATION_PERMISSIONS;
        if (ContextCompat.checkSelfPermission(getActivity(), Permissions.LOCATION_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Permissions.LOCATION_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "get_permissions: permissions granted");
            getCourses();
        }
        else {
            Log.d(TAG, "get_permissions: permissions NOT granted");
            ActivityCompat.requestPermissions(getActivity(), permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        switch (requestCode) {
            case(PERMISSION_REQUEST_CODE):
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    getCourses();
                }
                break;
        }
    }


    public void getCourses() {

        progressBar.setVisibility(View.VISIBLE);

        listener = new CourseListRecyclerView.OnCourseSelectedListener() {
            @Override
            public void onCourseClicked(Course course) {}

            @Override
            public void onNewCardCourseSelected(Course course) {
                courseSelected.onNearbyCourseSelected(course);
            }
        };

        allCourses = new ArrayList<>();
        Query query = ref.child("courses");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Course course;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    course = ds.getValue(Course.class);
                    allCourses.add(course);
                }

                getCourseDistances();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getCourseDistances() {
        Log.d(TAG, "getCourseDistances: called");
        float meters;
        float distance;
        LatLng current = getCurrentLatLng();
        ArrayList<Course> nearestCourses = new ArrayList<>();

        Location locationA = new Location("A");
        Location locationB = new Location("B");

        locationB.setLatitude(current.latitude);
        locationB.setLongitude(current.longitude);

        for (int i = 0; i < allCourses.size(); i++) {
            locationA.setLatitude(allCourses.get(i).getLatitude());
            locationA.setLongitude(allCourses.get(i).getLongitude());
            meters = locationA.distanceTo(locationB);
            distance = meters * (float) 0.000621371192;
            allCourses.get(i).setDistance(distance);
        }
        Collections.sort(allCourses);

        for (int i = 0; i < 50; i++)
            nearestCourses.add(allCourses.get(i));

        // garbage collect the rest.. maybe
        allCourses.clear();

        adapter = new CourseListRecyclerView(getActivity(), ScorecardActivity.ACTIVITY_NUM, listener, nearestCourses);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar.setVisibility(View.GONE);
    }

    public LatLng getCurrentLatLng() {
        Log.d(TAG, "getCurrentLatLng: called");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        Location location;
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, PERMISSION_REQUEST_CODE);
        }
        else{
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
        return null;
    }
}
