package com.example.dsawyer.maddscore.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Objects.UserStats;
import com.example.dsawyer.maddscore.Profile.EditEmailDialog;
import com.example.dsawyer.maddscore.Profile.ProfileActivity;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.ApplicationPreferences;
import com.example.dsawyer.maddscore.Utils.FilePaths;
import com.example.dsawyer.maddscore.Utils.LoadingDialog;
import com.example.dsawyer.maddscore.Utils.Permissions;
import com.example.dsawyer.maddscore.Utils.PhotoManager;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileCreateActivity extends AppCompatActivity implements
        View.OnClickListener,
        EditEmailDialog.OnConfirmListener {
    private static final String TAG = "TAG";

    TextView emailField;
    EditText nameField, usernameField, phoneField;
    ImageView editTop;
    CircleImageView profileImage;
    private DatabaseReference ref;
    private FirebaseUser currentUser;
    private User user;
    InputMethodManager inputMethodManager;

    private String imageURL;
    private byte[] uploadBytes;

    @Override
    public void onConfirmEmailUpdate(String email) {
        emailField.setText(email);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_create);

        ref = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        nameField = findViewById(R.id.name_field);
        emailField = findViewById(R.id.email_field);
        usernameField = findViewById(R.id.username_field);
        phoneField = findViewById(R.id.phone_field);
        profileImage = findViewById(R.id.profileImage);
        editTop = findViewById(R.id.edit_profile_image_btn);

        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        editTop.setOnClickListener(this);
        emailField.setOnClickListener(this);
        findViewById(R.id.register_finish).setOnClickListener(this);

        if (currentUser != null) {
            ref.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            nameField.setText(user.getName().trim());
                            emailField.setText(user.getEmail().trim());
                            if (user.getPhotoUrl() != null)
                                Glide.with(getApplicationContext()).load(user.getPhotoUrl()).into(profileImage);
                        }
                    }
                    else Log.d(TAG, "could not retrieve user data");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {  }
            });
        }
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "select Image"), Permissions.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Permissions.PICK_IMAGE_REQUEST &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {
            Uri uri = data.getData();
            PhotoSizer photoSizer = new PhotoSizer();
            photoSizer.execute(uri);
            Bitmap bitmap = null;
            try {
                bitmap = PhotoManager.HandleSamplingAndRotationBitmap(getApplicationContext(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Glide.with(this).load(bitmap).into(profileImage);
        }
        else
            Log.d(TAG, "file is null: ");
    }

    public class PhotoSizer extends AsyncTask<Uri, Integer, byte[]> {
        Bitmap bitmap = null;

        @Override
        protected byte[] doInBackground(Uri... uris) {
            if (bitmap == null) {
                try {
                    bitmap = PhotoManager.HandleSamplingAndRotationBitmap(getApplicationContext(), uris[0]);
                }
                catch(IOException e) {
                    Log.d(TAG, "doInBackground: " + e.getMessage());
                }
            }
            return PhotoManager.getBytesFromBitmap(bitmap, 50);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            uploadBytes = bytes;
            uploadTask();
        }
    }

    private void uploadTask() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String UID = user.getUserID();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        final StorageReference sRef = storageReference.child(FilePaths.FIREBASE_IMG_STORAGE + "/" + UID);
        UploadTask uploadTask = sRef.putBytes(uploadBytes);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            progressDialog.dismiss();
            sRef.getDownloadUrl().addOnSuccessListener(uri -> {
                imageURL = uri.toString();
                ref.child("users").child(UID).child("PhotoUrl").setValue(imageURL)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Upload Success", Toast.LENGTH_SHORT).show());
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Failed to Upload Photo", Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
            progressDialog.setMessage("Uploaded " + (int) progress + "%");
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case (Permissions.PICK_IMAGE_REQUEST): {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                }
                else
                    Log.d(TAG, "onRequestPermissionsResult: NOT GRANTED");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.edit_profile_image_btn):
                if (Permissions.checkCameraPermissions(this))
                    selectImage();
                else
                    Permissions.verifyCameraPermissions(this);
                break;

            case(R.id.email_field):
                EditEmailDialog editEmailDialog = new EditEmailDialog();
                Bundle args = new Bundle();
                args.putString("currentEmail", emailField.getText().toString().trim());
                editEmailDialog.setArguments(args);
                editEmailDialog.show(getSupportFragmentManager(), "editEmail");
                break;

            case R.id.register_finish:
                completeUser();
                break;
        }
    }

    public void completeUser() {
        String name, username, phone;
        name = nameField.getText().toString().trim();
        username = usernameField.getText().toString().trim();
        phone = phoneField.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Please create a username for your account", Toast.LENGTH_SHORT).show();
            usernameField.requestFocus();
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("/users/" + user.getUserID() + "/name/", name);
        map.put("/users/" + user.getUserID() + "/username/", username);
        map.put("/users/" + user.getUserID() + "/phoneNumber/", phone);
        map.put("/users/" + user.getUserID() + "/completeAccount/", true);

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        ref.updateChildren(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadingDialog.dismiss();
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            }
            else
                loadingDialog.dismiss();
        });
    }
}
