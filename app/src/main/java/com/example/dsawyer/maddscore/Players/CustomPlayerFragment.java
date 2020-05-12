package com.example.dsawyer.maddscore.Players;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Objects.UserStats;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.ScoreCards.ScorecardActivity;
import com.example.dsawyer.maddscore.Utils.FilePaths;
import com.example.dsawyer.maddscore.Utils.PhotoManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class CustomPlayerFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";
    private static final int PICK_IMAGE_REQUEST = 12;

    private DatabaseReference ref;
    private StorageReference storageReference;
    private String tempUID, currentUserId;

    private CircleImageView profileImage;
    private EditText playerName, playerUsername;
    private Button addImg, createPlayer;

    private User tempUser;
    private Uri uri;
    private String imageURL;
    private byte[] uploadBytes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        return inflater.inflate(R.layout.fragment_player_custom, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ref = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        tempUID = ref.child(getString(R.string.firebase_node_tempUsers)).push().getKey();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        profileImage = view.findViewById(R.id.profileImage);
        playerName = view.findViewById(R.id.player_name);
        playerUsername = view.findViewById(R.id.playerUsername);
        addImg = view.findViewById(R.id.button);
        createPlayer = view.findViewById(R.id.create_player);

        addImg.setOnClickListener(this);
        createPlayer.setOnClickListener(this);
    }

    private void createUser() {
        String mName = playerName.getText().toString().trim();
        String mUsername = playerUsername.getText().toString().trim();

        if (mName.isEmpty()) {
            playerName.setError(getString(R.string.requires_Player_name));
            playerName.requestFocus();
            return;
        }
        if (mUsername.isEmpty()) {
            mUsername = mName;
        }

        long date = System.currentTimeMillis();

        tempUser = new User(
                false,
                currentUserId,
                tempUID,
                mName,
                mUsername,
                 date);

        ref.child("users")
                .child(tempUID)
                .setValue(tempUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    ref.child("userStats").child(tempUID).setValue(new UserStats(tempUID))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                ref.child("friendsList").child(currentUserId).child(tempUID).setValue(true)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    if (uri != null)
                                                        uploadNewPhoto(uri);
                                                    else
                                                        exit();
                                                }
                                                else {
                                                    exit();
                                                }
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });
    }

    private void uploadNewPhoto(Uri uri) {
        PhotoSizer photoSizer = new PhotoSizer(null);
        photoSizer.execute(uri);
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    public class PhotoSizer extends AsyncTask<Uri, Integer, byte[]> {
        Bitmap bitmap;

        PhotoSizer(Bitmap bitmap) {
            if (bitmap != null) {
                this.bitmap = bitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "compressing image");
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            Log.d(TAG, "doInBackground: started");
            if (bitmap == null) {
                try {
                    bitmap = PhotoManager.HandleSamplingAndRotationBitmap(getActivity(), uris[0]);
                }
                catch(IOException e) {
                    Log.d(TAG, "doInBackground: " + e.getMessage());
                }
            }
            byte[] bytes = null;
            bytes = getBytesFromBitmap(bitmap, 50);
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            uploadBytes = bytes;
            uploadTask();
        }
    }

    private void uploadTask() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        final StorageReference sRef = storageReference.child(FilePaths.FIREBASE_IMG_STORAGE + "/" + tempUID);
        UploadTask uploadTask = sRef.putBytes(uploadBytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageURL = uri.toString();
                        ref.child(getString(R.string.firebase_node_users))
                                .child(tempUID)
                                .child(getString(R.string.firebase_node_photoUrl))
                                .setValue(imageURL)
                                .addOnSuccessListener( new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(), "Upload Success", Toast.LENGTH_SHORT).show();
                                        exit();
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed to Upload Photo", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            }
        });
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                Bitmap bitmap = PhotoManager.HandleSamplingAndRotationBitmap(getActivity(), uri);
                Glide.with(this).load(bitmap).into(profileImage);
            }
            catch(Exception e) {
                Log.d(TAG, "onActivityResult: exception " + e.getMessage());
            }
        }
        else {
            Toast.makeText(getActivity(), "An error occurred.", Toast.LENGTH_SHORT).show();
        }
    }

    public void exit() {
        Log.d(TAG, "exit: from Players activity");
        Intent intent = new Intent(getActivity(), PlayersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.button):
                chooseImage();
                break;
            case(R.id.create_player):
                createUser();
                break;
        }
    }
}