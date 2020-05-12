package com.example.dsawyer.maddscore.UselessButGoodInfo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.Permissions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Objects;

public class ProfileEditFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    private ImageView profileImage;
    private Button edit_img;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImage = view.findViewById(R.id.profileImage);
        edit_img = view.findViewById(R.id.edit_profile_image_btn);
        edit_img.setOnClickListener(this);

        ImageView backArrow = view.findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStackImmediate();
            }
        });

        initImageLoader();
        setProfileImage();
    }

    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions");
        ActivityCompat.requestPermissions(getActivity(), permissions, 1);
    }

    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array");
        for (int i = 0; i < permissions.length; i++){
            String check = permissions[i];
            if (!checkPerimssions(check)){
                return false;
            }
        }
       return true;
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setProfileImage(){
        String imageURL = "https://www.springboard.com/images/springboard/default-profile-mentor-rounded@2x.70dc0c67.png";
        UniversalImageLoader.setImage(imageURL, profileImage, null, "");
    }

    public boolean checkPerimssions(String permission){
        Log.d(TAG, "checkPerimssions: checking permission " + permission);
        int permissionRequest = ActivityCompat.checkSelfPermission(getActivity(), permission);
        if (permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPerimssions: permission was not granted for " + permission);
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.edit_profile_image_btn):
                if (checkPermissionsArray(Permissions.CAMERA_PERMISSIONS)){
                    chooseImage();
                }
                else{
                    verifyPermissions(Permissions.CAMERA_PERMISSIONS);
                }
                break;
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
}
