package com.example.dsawyer.maddscore.Social;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.ObjectMaps.PostUserMap;
import com.example.dsawyer.maddscore.Objects.Post;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Squad.SocialFragment;
import com.example.dsawyer.maddscore.Utils.LoadingDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TAG";
    private static final int REQUEST_CODE = 1;

    private DatabaseReference ref;

    private EditText post_body;
    private ImageView post_send, add_media;
    private LoadingDialog loadingDialog;

    private User mUser;
    private HashMap<String, Boolean> userList;
    private boolean isEditing;
    private PostUserMap oldPost;

    private OnPostsUpdated listener;
    public interface OnPostsUpdated {
        void onPostAdd(Post newPost);
        void onPostEdit(PostUserMap updatedPost);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SocialFragment) getTargetFragment();
        } catch (Exception e) {
            Log.d(TAG, "onAttach: " + e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userList = new HashMap<>();

        loadingDialog = new LoadingDialog(getActivity());
        LinearLayout content_parent = new LinearLayout(getActivity());

        TextView username = view.findViewById(R.id.player_username);
        TextView name = view.findViewById(R.id.player_name);
        CircleImageView user_image = view.findViewById(R.id.user_img);
        TextView current_date = view.findViewById(R.id.current_date);

        post_body = view.findViewById(R.id.post_body);
        post_send = view.findViewById(R.id.post_send);
        add_media = view.findViewById(R.id.add_media);

        post_body.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        post_body.setImeOptions(EditorInfo.IME_ACTION_SEND);
        post_body.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                postCheck();
                return true;
            }
            return false;
        });

        ref = FirebaseDatabase.getInstance().getReference();

        if (getArguments() != null) {
            mUser = getArguments().getParcelable("mUser");
            oldPost = getArguments().getParcelable("postMap");
            isEditing = oldPost != null;

            if (mUser != null) {
                post_send.setOnClickListener(this);
                add_media.setOnClickListener(this);

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
            case (R.id.post_send):
                postCheck();
                break;
            case (R.id.add_media):
                Toast.makeText(getActivity(), "Add media feature coming soon", Toast.LENGTH_SHORT).show();
//                if (Permissions.checkCameraPermissions(getActivity())) {
//                    selectImage();
//                }
//                else {
//                    Permissions.verifyCameraPermissions(getActivity());
//                }
                break;
        }
    }

//    public void showDateTimePicker() {
//        final Calendar currentDate = Calendar.getInstance();
//        date = Calendar.getInstance();
//        if (getActivity()  != null) {
//            new DatePickerDialog(getActivity(), (view, year, monthOfYear, dayOfMonth) -> {
//                date.set(year, monthOfYear, dayOfMonth);
//
//                new TimePickerDialog(getActivity(), (view1, hourOfDay, minute) -> {
//                    date.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                    date.set(Calendar.MINUTE, minute);
//                    Log.v(TAG, "The chosen one " + date.getTime());
//
//                    strDate = null;
//                    strDate = String.valueOf(DateFormat.format("EEE, MMM dd 'at' h:mm a", date));
//                    post_when.setVisibility(View.VISIBLE);
//                    post_when.setText("When - " + strDate);
//                    Log.d(TAG, "showDateTimePicker: " + strDate);
//                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
//            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
//        }
//    }

    public void postCheck() {
        if (post_body.getText().toString().trim().isEmpty()) {
            Toast toast = Toast.makeText(getActivity(), "Nothing to post.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 400);
            toast.show();
            post_body.requestFocus();
            return;
        }

        if (isEditing)
            editPost();
        else
            postNew();
    }

    public void editPost() {
        loadingDialog.show();
        long date = System.currentTimeMillis();
        String postBody = post_body.getText().toString();

        oldPost.getPost().setDateCreated(date);
        oldPost.getPost().setPostBody(postBody);

        Map<String, Object> updates = new HashMap<>();
        updates.put("/dateCreated/", date);
        updates.put("/postBody/", postBody);

        ref.child("socialPosts").child(mUser.getSquad()).child(oldPost.getPost().getPostID()).updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), "post updated", Toast.LENGTH_SHORT).show();
                listener.onPostEdit(oldPost);

                // remove this fragment and update adapter

//                Intent intent = new Intent(getActivity(), SquadActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
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

            if (postKey != null) {
                final Post post = new Post(postKey,
                        mUser.getUserID(),
                        date,
                        postBody,
                        new HashMap<>(),
                        userList);

                ref.child("socialPosts").child(mUser.getSquad()).child(postKey).setValue(post).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loadingDialog.dismiss();
                        Toast.makeText(getActivity(), "posted", Toast.LENGTH_SHORT).show();
                        listener.onPostAdd(post);

                        // remove this fragment and update adapter

//                        Intent intent = new Intent(getActivity(), SquadActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
                    } else {
                        loadingDialog.dismiss();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                loadingDialog.dismiss();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_LONG).show();
            }
        } else {
            loadingDialog.dismiss();
            if (getActivity() != null)
                Toast.makeText(getActivity(), "Something went wrong while trying to retrieve your user data", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog().getWindow() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            getDialog().getWindow().setAttributes(params);
        }
    }
}
