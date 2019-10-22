package com.example.dsawyer.maddscore.Courses;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsawyer.maddscore.CHIPP.CHIPPActivity;
import com.example.dsawyer.maddscore.Events.EventsActivity;
import com.example.dsawyer.maddscore.Help.HelpActivity;
import com.example.dsawyer.maddscore.Leaderboards.LeaderboardsActivity;
import com.example.dsawyer.maddscore.Objects.Course;
import com.example.dsawyer.maddscore.Other.SettingsActivity;
import com.example.dsawyer.maddscore.Other.NotificationsActivity;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.BottomNavViewHelper;
import com.example.dsawyer.maddscore.Utils.CourseListRecyclerView;
import com.example.dsawyer.maddscore.Utils.CustomMapFragment;
import com.example.dsawyer.maddscore.Utils.Permissions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoursesActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        ClusterManager.OnClusterItemClickListener<Course>,
        ClusterManager.OnClusterClickListener<Course>,
        ClusterManager.OnClusterItemInfoWindowClickListener<Course> {
    private static final String TAG = "TAG";
    public static final int ACTIVITY_NUM = 3;

    private static final int PERMISSION_REQUEST_CODE = 7426;

    private Boolean permission_granted = false, search_view = false;
    private GoogleMap map;
    private FusedLocationProviderClient fpc;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;

    LinearLayout course_list_items, search_filters;
    ProgressBar progressBar;
    RelativeLayout my_courses, nearby_container;
    Button log_out;

    TextView course1_name, course1_address, course1_holes, course1_plays, course1_rating, course1_distance,
            course2_name, course2_address, course2_holes, course2_plays, course2_rating, course2_distance,
            course3_name, course3_address, course3_holes, course3_plays, course3_rating, course3_distance;

    DatabaseReference ref;
    ArrayList<Course> courses;
    ArrayList<String> list;
    ScrollView scrollView;
    ImageView refresh, search, full_map;

    ArrayList<Course> filteredCourses;
    TextView no_results;
    EditText course_header;
    RecyclerView recyclerView;
    CourseListRecyclerView adapter;
    CourseListRecyclerView.OnCourseSelectedListener listener;
    Matcher matcher;
    String searchValue;
    InputMethodManager inputMethodManager;

    ClusterManager<Course> manager;
    private Course item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        log_out = findViewById(R.id.logout);
        my_courses = findViewById(R.id.my_courses);
        search_filters = findViewById(R.id.search_filters);
        scrollView = findViewById(R.id.scroll);
        nearby_container = findViewById(R.id.relative);

        course_header = findViewById(R.id.course_header);
        course_list_items = findViewById(R.id.course_list_item);
        progressBar = findViewById(R.id.progressBar);
        refresh = findViewById(R.id.refreshMap);
        search = findViewById(R.id.search_courses);
        full_map = findViewById(R.id.full_map);
        recyclerView = findViewById(R.id.recyclerView);
        no_results = findViewById(R.id.no_results_msg);

        course1_name = findViewById(R.id.course1_name);
        course1_address = findViewById(R.id.course1_address);
        course1_holes = findViewById(R.id.course1_num_holes);
        course1_plays = findViewById(R.id.course1_times_played);
        course1_rating = findViewById(R.id.course1_rating);
        course1_distance = findViewById(R.id.course1_distance);

        course2_name = findViewById(R.id.course2_name);
        course2_address = findViewById(R.id.course2_address);
        course2_holes = findViewById(R.id.course2_num_holes);
        course2_plays = findViewById(R.id.course2_times_played);
        course2_rating = findViewById(R.id.course2_rating);
        course2_distance = findViewById(R.id.course2_distance);

        course3_name = findViewById(R.id.course3_name);
        course3_address = findViewById(R.id.course3_address);
        course3_holes = findViewById(R.id.course3_num_holes);
        course3_plays = findViewById(R.id.course3_times_played);
        course3_rating = findViewById(R.id.course3_rating);
        course3_distance = findViewById(R.id.course3_distance);

        list = new ArrayList<>();
        filteredCourses = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference();

        mapFragment = (CustomMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        int authenticated = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(CoursesActivity.this);
        if (authenticated == ConnectionResult.SUCCESS) {
            Log.d(TAG, "authenticated");
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(authenticated)) {
            Log.d(TAG, "NOT authenticated but can be resolved");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(CoursesActivity.this, authenticated, 121);
            dialog.show();
        }
        else
            Toast.makeText(this, "Unable to make map request", Toast.LENGTH_SHORT).show();

        my_courses.setOnClickListener(this);
        full_map.setOnClickListener(this);

        setUpBottomNavView();
        setUpNavDrawer();
        get_permissions();
//        if (permission_granted)
//            getNearbyCourses();
    }

    private void get_permissions() {
        Log.d(TAG, "get_permissions: called");
        String[] permissions = Permissions.LOCATION_PERMISSIONS;
        if (ContextCompat.checkSelfPermission(this, Permissions.LOCATION_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Permissions.LOCATION_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "get_permissions: permissions granted");
            permission_granted = true;
            initMap();
        }
        else {
            Log.d(TAG, "get_permissions: permissions NOT granted");
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    public void initMap() {
        Log.d(TAG, "initMap: called");
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: called");
        if (permission_granted) {
            map = googleMap;
            if (map != null) {
                getDeviceLocation();
                if (ActivityCompat.checkSelfPermission(CoursesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(CoursesActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    return;
                map.setMyLocationEnabled(true);
            }
            else
                Toast.makeText(this, "Failed to create map", Toast.LENGTH_SHORT).show();

            ((CustomMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .setListener(new CustomMapFragment.OnTouchListener() {
                        @Override
                        public void onTouch() {
                            scrollView.requestDisallowInterceptTouchEvent(true);
                        }
                    });
        }
    }

    public void getNearbyCourses() {
        Log.d(TAG, "getNearbyCourses: called");
        nearby_container.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        course_list_items.setVisibility(View.GONE);
        courses = new ArrayList<>();
        Query query = ref.child("courses");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, String.valueOf(dataSnapshot.getChildrenCount()));
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Course course = ds.getValue(Course.class);
                    courses.add(course);
                }
                Log.d(TAG, "onDataChange: data retrieved");
                createMarkers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void createMarkers() {
        Log.d(TAG, "createMarkers: called");
        if (!courses.isEmpty()) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(getCurrentLatLng(), 10));
            manager = new ClusterManager<>(this, map);
            manager.getMarkerCollection().setOnInfoWindowAdapter(new CustomMarkerInfoAdapter());

            manager.setOnClusterClickListener(this);
            manager.setOnClusterItemClickListener(this);
            manager.setOnClusterItemInfoWindowClickListener(this);

            map.setOnMarkerClickListener(manager);
            map.setOnCameraIdleListener(manager);
            map.setOnInfoWindowClickListener(manager);
            map.setInfoWindowAdapter(manager.getMarkerManager());

            manager.addItems(courses);

            getCourseDistances();
        }
    }

    public void getCourseDistances() {
        Log.d(TAG, "getCourseDistances: called");
        float meters;
        float distance;
        LatLng current = getCurrentLatLng();

        Location locationA = new Location("A");
        Location locationB = new Location("B");

        locationB.setLatitude(current.latitude);
        locationB.setLongitude(current.longitude);

        for (int i = 0; i < courses.size(); i++) {
            locationA.setLatitude(courses.get(i).getLatitude());
            locationA.setLongitude(courses.get(i).getLongitude());
            meters = locationA.distanceTo(locationB);
            distance = meters * (float) 0.000621371192;
            courses.get(i).setDistance(distance);
        }
        Collections.sort(courses);
        populateCourseInfo();
    }

    private void populateCourseInfo() {
        Log.d(TAG, "populateCourseInfo: called");
        course_list_items.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        refresh.setOnClickListener(this);
        search.setOnClickListener(this);

        course1_name.setText(courses.get(0).getName().trim());
        course1_address.setText(courses.get(0).getAddress().trim());
        course1_holes.setText(courses.get(0).getNumHoles() + " holes");
        course1_plays.setText("played " + 4 + " time(s)");
        course1_distance.setText((Math.round(courses.get(0).getDistance() * 10.0) / 10.0) + " miles");
        if (courses.get(0).getRating() != null)
            course1_rating.setText(String.valueOf(courses.get(0).getRating()));
        else
            course1_rating.setVisibility(View.GONE);


        course2_name.setText(courses.get(1).getName().trim());
        course2_address.setText(courses.get(1).getAddress());
        course2_holes.setText(courses.get(1).getNumHoles() + " holes");
        course2_plays.setText("played " + 12 + " time(s)");
        course2_distance.setText((Math.round(courses.get(1).getDistance() * 10.0) / 10.0) + " miles");
        if (courses.get(1).getRating() != null)
            course2_rating.setText(String.valueOf(courses.get(1).getRating()));
        else
            course2_rating.setVisibility(View.GONE);

        course3_name.setText(courses.get(2).getName().trim());
        course3_address.setText(courses.get(2).getAddress().trim());
        course3_holes.setText(courses.get(2).getNumHoles() + " holes");
        course3_plays.setText("played " + 5 + " time(s)");
        course3_distance.setText((Math.round(courses.get(2).getDistance() * 10.0) / 10.0) + " miles");
        if (courses.get(2).getRating() != null)
            course3_rating.setText(String.valueOf(courses.get(2).getRating()));
        else
            course3_rating.setVisibility(View.GONE);

        CardView course1, course2, course3, seeMore;

        course1 = findViewById(R.id.course1);
        course2 = findViewById(R.id.course2);
        course3 = findViewById(R.id.course3);
        seeMore = findViewById(R.id.more_courses_btn);

        seeMore.setOnClickListener(this);
        course1.setOnClickListener(this);
        course2.setOnClickListener(this);
        course3.setOnClickListener(this);

    }

    public LatLng getCurrentLatLng() {
        Log.d(TAG, "getCurrentLatLng: called");
        String[] permissions = Permissions.LOCATION_PERMISSIONS;
        Location location;
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
        else{
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
               return new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        permission_granted = false;
       switch (requestCode) {
           case(PERMISSION_REQUEST_CODE):
               if (grantResults.length > 0) {
                   for (int i = 0; i <  grantResults.length; i++) {
                       if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            permission_granted = false;
                            return;
                       }
                   }
                   permission_granted = true;
                   initMap();
               }
               break;
       }
    }

    public void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: called");
        fpc = LocationServices.getFusedLocationProviderClient(this);
        try  {
            if (permission_granted) {
                Task location = fpc.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location current_location =  task.getResult();
                            if (current_location != null) {
                                LatLng latLng = new LatLng(current_location.getLatitude(), current_location.getLongitude());
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));
                                getNearbyCourses();
                            }
                            else {
                                Toast.makeText(CoursesActivity.this, "Unable to get current location. Make sure your devices location is enabled.", Toast.LENGTH_LONG).show();
                                nearby_container.setVisibility(View.GONE);
                            }
                        }
                        else {
                            Toast.makeText(CoursesActivity.this, "Unable to get current location. Make sure your devices location is enabled.", Toast.LENGTH_LONG).show();
                            nearby_container.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
        catch(SecurityException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            case(R.id.side_menu_events):
                Intent intent1 = new Intent(this, EventsActivity.class);
                finish();
                startActivity(intent1);
                overridePendingTransition(0,0);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;
            case(R.id.side_menu_Leaderboards):
                Intent intent2 = new Intent(this, LeaderboardsActivity.class);
                finish();
                startActivity(intent2);
                overridePendingTransition(0,0);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;
            case(R.id.side_menu_CHIPP):
                Intent intent3 = new Intent(this, CHIPPActivity.class);
                finish();
                startActivity(intent3);
                overridePendingTransition(0,0);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;
            case(R.id.side_menu_setings):
                Intent intent4 = new Intent(this, SettingsActivity.class);
                finish();
                startActivity(intent4);
                overridePendingTransition(0,0);
                intent4.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;
            case(R.id.side_menu_help):
                Intent intent5 = new Intent(this, HelpActivity.class);
                finish();
                startActivity(intent5);
                overridePendingTransition(0,0);
                intent5.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;
            case(R.id.side_menu_notifications):
                Intent intent6 = new Intent(this, NotificationsActivity.class);
                startActivity(intent6);
                overridePendingTransition(0,0);
                intent6.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.courses_activity);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpNavDrawer() {
        Log.d(TAG, "setUpNavDrawer: called");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.courses_activity);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpBottomNavView() {
        Log.d(TAG, "setUpBottomNavView: called");
        BottomNavigationView mMainNav = findViewById(R.id.main_nav);
        Menu menu = mMainNav.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        BottomNavViewHelper.enableBottomNavView(this, mMainNav);
        overridePendingTransition(0,0);
    }

    @Override
    public boolean onClusterItemClick(Course markerItem) {
        Log.d(TAG, "onClusterItemClick: called");
        item = markerItem;
        return false;
    }

    @Override
    public boolean onClusterClick(Cluster<Course> cluster) {
        Log.d(TAG, "onClusterClick: called");
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                cluster.getPosition(),
                (float) Math.floor(map.getCameraPosition().zoom + 1)), 300, null);
        return true;
    }

    @Override
    public void onClusterItemInfoWindowClick(Course course) {
        Log.d(TAG, "onClusterItemInfoWindowClick: called");

        CourseInfoFragment courseInfoFragment = new CourseInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("course", item);
        courseInfoFragment.setArguments(args);
        setFragment(courseInfoFragment, "course");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.course1):
                CourseInfoFragment courseInfoFragment1 = new CourseInfoFragment();
                Bundle args1 = new Bundle();
                args1.putParcelable("course", courses.get(0));
                courseInfoFragment1.setArguments(args1);
                setFragment(courseInfoFragment1, "course");
                break;

            case(R.id.course2):
                CourseInfoFragment courseInfoFragment2 = new CourseInfoFragment();
                Bundle args2 = new Bundle();
                args2.putParcelable("course", courses.get(1));
                courseInfoFragment2.setArguments(args2);
                setFragment(courseInfoFragment2, "course");
                break;

            case(R.id.course3):
                CourseInfoFragment courseInfoFragment3 = new CourseInfoFragment();
                Bundle args3 = new Bundle();
                args3.putParcelable("course", courses.get(2));
                courseInfoFragment3.setArguments(args3);
                setFragment(courseInfoFragment3, "course");
                break;

            case(R.id.refreshMap):
                map.clear();
                getNearbyCourses();
                break;

            case(R.id.search_courses):
                if (search_view) {
                    Log.d(TAG, "onClick: true");
                    search_view = false;
                    full_map.setVisibility(View.VISIBLE);
                    refresh.setVisibility(View.VISIBLE);
                    no_results.setVisibility(View.GONE);
                    search.setImageResource(R.drawable.ic_search_light);
                    course_header.setEnabled(false);
                    course_header.setText("Courses");

                    inputMethodManager.hideSoftInputFromWindow(course_header.getWindowToken(), 0);

                    recyclerView.setVisibility(View.GONE);
//                    search_filters.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                }
                else {
                    Log.d(TAG, "onClick: false");
                    search_view = true;
                    full_map.setVisibility(View.GONE);
                    refresh.setVisibility(View.GONE);
                    search.setImageResource(R.drawable.ic_cancel);
                    course_header.setEnabled(true);
                    course_header.requestFocus();
                    course_header.getText().clear();

                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    attachCourseList();
                }

//                NearbyCourseListFragment searchFragment = new NearbyCourseListFragment();
//                setFragment(searchFragment, "search");
                break;

            case(R.id.more_courses_btn):
                CourseListFragment moreCourses = new CourseListFragment();
                setFragment(moreCourses, "search");
                break;
            case(R.id.full_map):
                initFullScreenMapView();
                break;
        }
    }

    public void attachCourseList() {
        recyclerView.setVisibility(View.VISIBLE);
//        search_filters.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        filteredCourses.clear();

        listener = new CourseListRecyclerView.OnCourseSelectedListener() {
            @Override
            public void onCourseClicked(Course course) {
                CourseInfoFragment courseInfoFragment = new CourseInfoFragment();
                Bundle args = new Bundle();
                args.putParcelable("course", course);
                courseInfoFragment.setArguments(args);
                setFragment(courseInfoFragment, "course");

                inputMethodManager.hideSoftInputFromWindow(course_header.getWindowToken(), 0);
            }

            @Override
            public void onNewCardCourseSelected(Course course) {}
        };

        adapter = new CourseListRecyclerView(this, ACTIVITY_NUM, listener, filteredCourses);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        course_header.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filteredCourses.clear();
                searchValue = course_header.getText().toString();
                for (int i = 0; i < courses.size(); i++) {
                    matcher = Pattern.compile(searchValue, Pattern.CASE_INSENSITIVE).matcher(courses.get(i).getName());
                    if (matcher.find())
                        filteredCourses.add(courses.get(i));
                }

                adapter.updateList(filteredCourses);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setFragment(final Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.courses_frame, fragment, tag).addToBackStack(null).commit();
    }

    public void initFullScreenMapView() {

    }

    public class CustomMarkerInfoAdapter implements GoogleMap.InfoWindowAdapter {
        private final View view;

        CustomMarkerInfoAdapter() {
            view = getLayoutInflater().inflate(R.layout.layout_course_item, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            Log.d(TAG, "getInfoWindow: called");
            TextView title = view.findViewById(R.id.marker_title);
            TextView address = view.findViewById(R.id.marker_address);
            TextView num_holes = view.findViewById(R.id.marker_num_holes);
            TextView times_played = view.findViewById(R.id.marker_times_played);
            TextView rating = view.findViewById(R.id.marker_rating);
            TextView distance = view.findViewById(R.id.marker_distance);

            if (item != null) {
                title.setText(item.getTitle().trim());
                address.setText(item.getSnippet().trim());
                num_holes.setText(item.getNumHoles() + " holes");
                times_played.setText("played 3 times");
                distance.setText((Math.round(item.getDistance() * 10.0) / 10.0) + " miles");
                if (item.getRating() != null) {
                    rating.setVisibility(View.VISIBLE);
                    rating.setText(String.valueOf(item.getRating()));
                }
                else
                    rating.setVisibility(View.GONE);
            }
            return view;
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: called");
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("search");
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            getSupportFragmentManager().popBackStack();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (search_view) {
            Log.d(TAG, "onBackPressed: ");
            search_view = false;
            full_map.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
//            search_filters.setVisibility(View.GONE);
            no_results.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            search.setImageResource(R.drawable.ic_search_light);

            course_header.setEnabled(false);
            course_header.setText("Courses");

            inputMethodManager.hideSoftInputFromWindow(course_header.getWindowToken(), 0);
            return;
        }
        super.onBackPressed();
    }
}
