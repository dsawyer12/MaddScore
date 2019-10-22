package com.example.dsawyer.maddscore.Social;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.tempCourse;
import com.example.dsawyer.maddscore.Objects.Post;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Squad.SquadActivity;
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
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostFragment extends Fragment implements PostCourseSelectorDialog.OnBundleSelectedListener, View.OnClickListener {
    private static final String TAG = "TAG";
    private static final int REQUEST_CODE = 1;

    private DatabaseReference ref;
    private String UID;


    LinearLayout post_location, post_time;
    CircleImageView user_image;
    TextView username, name, post_where, post_when, current_date;
    EditText post_body;
    Button post_send;

    private User mUser;
    private tempCourse mTempCourse;
    private String dateString, postBody, strDate;
    private HashMap<String, Boolean> userlist;
    Calendar date;

    public PostFragment() {
        super();
        setArguments(new Bundle());
    }

    @Override
    public void onCourseSelected(tempCourse tempCourse) {
        mTempCourse = tempCourse;
        post_where.setVisibility(View.VISIBLE);
        post_where.setText("Where:  " + tempCourse.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUser = new User();
        mTempCourse = new tempCourse();
        userlist = new HashMap<>();

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        username = view.findViewById(R.id.player_username);
        name = view.findViewById(R.id.player_name);
        user_image = view.findViewById(R.id.player_img);
        current_date = view.findViewById(R.id.current_date);
        post_where = view.findViewById(R.id.post_where);
        post_when = view.findViewById(R.id.post_when);
        post_body = view.findViewById(R.id.post_body);
        post_location = view.findViewById(R.id.post_location);
        post_time = view.findViewById(R.id.post_time);
        post_send = view.findViewById(R.id.post_send);

        post_where.setOnClickListener(this);
        post_when.setOnClickListener(this);
        post_location.setOnClickListener(this);
        post_time.setOnClickListener(this);
        post_send.setOnClickListener(this);

        post_body.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() == 0){
                    post_send.setEnabled(false);
                } else {
                    post_send.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd, yyyy h:mm a");
        dateString = sdf.format(date);
        current_date.setText(dateString);

        getUser();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.post_location):
                setWhere();
                break;
            case (R.id.post_time):
                showDateTimePicker();
                break;
            case (R.id.post_send):
                post_send.setEnabled(false);
                postNew();
                break;
        }
    }

    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);

                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        Log.v(TAG, "The chosen one " + date.getTime());

                        strDate = null;
                        strDate = String.valueOf(DateFormat.format("EEE MMM dd h:mm a", date));
                        post_when.setVisibility(View.VISIBLE);
                        post_when.setText("When:    " + String.valueOf(strDate));
                        Log.d(TAG, "showDateTimePicker: " + strDate);
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    public void setWhere() {
        PostCourseSelectorDialog prac = new PostCourseSelectorDialog();
        prac.setTargetFragment(PostFragment.this, 1);
        prac.show(getFragmentManager(), "courseSelector");
    }

    public void getUser() {
        Query query = ref.child("users").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    mUser = dataSnapshot.getValue(User.class);
                    if (mUser != null){
                        if (mUser.getPhotoUrl() != null){
                            Glide.with(getActivity()).load(mUser.getPhotoUrl()).into(user_image);
                        }
                        else{
                            Glide.with(getActivity()).load(R.mipmap.default_profile_img).into(user_image);
                        }
                        username.setText(mUser.getUsername());
                        name.setText(mUser.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void postNew() {
        if (mUser != null){
            final String postKey = ref.child("socialPosts").child(mUser.getMySquad()).push().getKey();
            long date = System.currentTimeMillis();
            postBody = post_body.getText().toString();
//            boolean checked = checkBox.isChecked();
            //add mUser img to the post constructor
            final Post post = new Post(postKey,
                    mUser.getUserID(),
                    mTempCourse.getName(),
                    date,
                    postBody,
                    strDate,
                    0,
                    new HashMap<String, Boolean>(),
                    userlist);
            ref.child("socialPosts").child(mUser.getMySquad()).child(postKey).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(getActivity(), SquadActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        //handle
                    }
                }
            });
        }
        else{
            //handle
        }
    }
}
