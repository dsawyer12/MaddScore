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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.PostUserMap;
import com.example.dsawyer.maddscore.Objects.tempCourse;
import com.example.dsawyer.maddscore.Objects.Post;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Squad.SquadActivity;
import com.example.dsawyer.maddscore.Utils.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostFragment extends Fragment implements PostCourseSelectorDialog.OnBundleSelectedListener, View.OnClickListener {
    private static final String TAG = "TAG";
    private static final int REQUEST_CODE = 1;

    private DatabaseReference ref;

    private CircleImageView user_image;
    private TextView username, name, post_where, post_when, current_date, post_location, post_time;
    private EditText post_body;
    private ImageView post_send;
    private LoadingDialog loadingDialog;

    private User mUser;
    private tempCourse mTempCourse;
    private String strDate;
    private HashMap<String, Boolean> userList;
    private boolean isEditing;
    private PostUserMap oldPost;
    Calendar date;

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

        mTempCourse = new tempCourse();
        userList = new HashMap<>();

        loadingDialog = new LoadingDialog(getActivity());
        username = view.findViewById(R.id.player_username);
        name = view.findViewById(R.id.player_name);
        user_image = view.findViewById(R.id.user_img);
        current_date = view.findViewById(R.id.current_date);
        post_where = view.findViewById(R.id.post_where);
        post_when = view.findViewById(R.id.post_when);
        post_body = view.findViewById(R.id.post_body);
//        post_location = view.findViewById(R.id.post_location);
        post_time = view.findViewById(R.id.post_time);
        post_send = view.findViewById(R.id.post_send);

        ref = FirebaseDatabase.getInstance().getReference();

        if (getArguments() != null) {
            mUser = getArguments().getParcelable("mUser");
            oldPost = getArguments().getParcelable("postMap");
            isEditing = oldPost != null;

            if (mUser != null) {
                post_where.setOnClickListener(this);
                post_when.setOnClickListener(this);
//              post_location.setOnClickListener(this);
                post_time.setOnClickListener(this);
                post_send.setOnClickListener(this);

                post_body.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.toString().length() == 0) post_send.setEnabled(false);
                        else post_send.setEnabled(true);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {  }
                });

                long date = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, 'at' h:mm a", Locale.US);
                String dateString = sdf.format(date);
                current_date.setText(dateString);

                if (mUser.getPhotoUrl() != null && getActivity() != null)
                    Glide.with(getActivity()).load(mUser.getPhotoUrl()).into(user_image);
                username.setText(mUser.getUsername());
                name.setText(mUser.getName());
            }

            if (isEditing) {
                if (oldPost != null) {
                    post_body.setText(oldPost.getPost().getPostBody());
                    // if available, also set the course location and play time.
                    post_body.requestFocus();
                } else Toast.makeText(getActivity(), "Could not retrieve post", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (getActivity() != null)
                Toast.makeText(getActivity(), "Something went wrong while trying to retrieve your user data.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case (R.id.post_location):
//                setWhere();
//                break;
            case (R.id.post_time):
                showDateTimePicker();
                break;
            case (R.id.post_send):
                if (post_body.getText().toString().trim().isEmpty()) {
                    Toast toast = Toast.makeText(getActivity(), "Write something before posting", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 400);
                    toast.show();
                    post_body.requestFocus();
                    return;
                }
                if (isEditing) editPost();
                else postNew();
                break;
        }
    }

    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        if (getActivity()  != null) {
            new DatePickerDialog(getActivity(), (view, year, monthOfYear, dayOfMonth) -> {
                date.set(year, monthOfYear, dayOfMonth);

                new TimePickerDialog(getActivity(), (view1, hourOfDay, minute) -> {
                    date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    date.set(Calendar.MINUTE, minute);
                    Log.v(TAG, "The chosen one " + date.getTime());

                    strDate = null;
                    strDate = String.valueOf(DateFormat.format("EEE, MMM dd 'at' h:mm a", date));
                    post_when.setVisibility(View.VISIBLE);
                    post_when.setText("When - " + strDate);
                    Log.d(TAG, "showDateTimePicker: " + strDate);
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
        }
    }

//    public void setWhere() {
//        PostCourseSelectorDialog courseSelectorDialog = new PostCourseSelectorDialog();
//        courseSelectorDialog.setTargetFragment(PostFragment.this, 1);
//        courseSelectorDialog.show(getChildFragmentManager(), "courseSelector");
//    }

    public void editPost() {
        loadingDialog.show();
        long date = System.currentTimeMillis();
        String postBody = post_body.getText().toString();

        oldPost.getPost().setDateCreated(date);
        oldPost.getPost().setPostBody(postBody);
//        oldPost.getPost().setPlayTime(strDate);

        Map<String, Object> updates = new HashMap<>();
        updates.put("/dateCreated/", date);
        updates.put("/postBody/", postBody);

        ref.child("socialPosts").child(mUser.getSquad()).child(oldPost.getPost().getPostID()).updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), "post updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SquadActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void postNew() {
        loadingDialog.show();
        if (mUser != null) {
            final String postKey = ref.child("socialPosts").child(mUser.getSquad()).push().getKey();
            long date = System.currentTimeMillis();
            String postBody = post_body.getText().toString();

            final Post post = new Post(postKey,
                    mUser.getUserID(),
                    mTempCourse.getName(),
                    date,
                    postBody,
                    strDate,
                    new HashMap<>(),
                    userList);

            ref.child("socialPosts").child(mUser.getSquad()).child(postKey).setValue(post).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    loadingDialog.dismiss();
                    Toast.makeText(getActivity(), "posted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), SquadActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    loadingDialog.dismiss();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            loadingDialog.dismiss();
            if (getActivity() != null)
                Toast.makeText(getActivity(), "Something went wrong while trying to retrieve your user data", Toast.LENGTH_LONG).show();
        }
    }
}
