package com.example.dsawyer.maddscore.UselessButGoodInfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dsawyer.maddscore.Profile.ProfileActivity;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.FilePaths;
import com.example.dsawyer.maddscore.Utils.FileSearch;
import com.example.dsawyer.maddscore.Utils.GridImageAdapter;
import com.example.dsawyer.maddscore.Utils.PhotoManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

public class GalleryFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;
    private StorageReference storage;

    private static final int GRID_COLUMBS = 3;
    private ArrayList<String> directories;
    private String selectedImg;

    ImageView galleryImage, exit_gallery;
    TextView finish_gallery;
    Spinner spinner;
    GridView gridView;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        else{
            Log.d(TAG, "onViewCreated: Firebase.currentUser = null");
        }
        storage = FirebaseStorage.getInstance().getReference();

        exit_gallery = view.findViewById(R.id.exit_gallery_btn);
        finish_gallery = view.findViewById(R.id.finish_gallery_btn);
        galleryImage = view.findViewById(R.id.galleryImageView);
        spinner = view.findViewById(R.id.gallery_spinner);
        gridView = view.findViewById(R.id.gridView);
        progressBar = view.findViewById(R.id.progressBar);

        exit_gallery.setOnClickListener(this);
        finish_gallery.setOnClickListener(this);

        init();

    }

    private void init() {

        directories = new ArrayList<>();

        if (FileSearch.getDirectoryPaths(FilePaths.PICTURES) != null){
            directories = FileSearch.getDirectoryPaths(FilePaths.PICTURES);
        }
        directories.add(FilePaths.CAMERA);
        Collections.reverse(directories);

        ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < directories.size(); i++){
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setUpGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUpGridView(String directory){
        final ArrayList<String> imageURLs = FileSearch.getFilePath(directory);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / GRID_COLUMBS;
        gridView.setColumnWidth(imageWidth);

        final String mAppend = "file:/";
        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, mAppend, imageURLs);
        gridView.setAdapter(adapter);
        if (!imageURLs.isEmpty()){
            setImage(imageURLs.get(0), galleryImage, mAppend);
            selectedImg = imageURLs.get(0);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setImage(imageURLs.get(position), galleryImage, mAppend);
                selectedImg = imageURLs.get(position);
            }
        });
    }

    private void setImage(String imageURL, ImageView image, String append){
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imageURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case(R.id.exit_gallery_btn):
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case(R.id.finish_gallery_btn):
                if (selectedImg != null){
                    setPhoto();
                }
                break;
        }
    }

    private void setPhoto() {
        final StorageReference storageReference = storage.child(FilePaths.FIREBASE_IMG_STORAGE + "/" + UID);
        Bitmap bitmap = PhotoManager.getBitmap(selectedImg);
        byte[] bytes = PhotoManager.getBytesFromBitmap(bitmap, 50);
        UploadTask uploadTask = storageReference.putBytes(bytes);

        //        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//               updateUserDatabase(uri.toString());
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//


        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: Photo upload success");
                updateUserDatabase(storageReference, storageReference.getDownloadUrl().toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Photo upload failed");
            }
        });
    }

    private void updateUserDatabase(StorageReference storageReference, final String firebaseURL) {

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ref.child("users").child(UID).child("PhotoUrl").setValue(firebaseURL).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: update imageURL in database");
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: failed to update database inageURL");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}













