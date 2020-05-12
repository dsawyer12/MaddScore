package com.example.dsawyer.maddscore.Profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.Other.ForgotPasswordDialog;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.FilePaths;
import com.example.dsawyer.maddscore.Utils.Permissions;
import com.example.dsawyer.maddscore.Utils.PhotoManager;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements
        EditEmailDialog.OnConfirmListener,
        DialogInterface.OnDismissListener,
        View.OnClickListener {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private User mUser;

    private ImageView back_button, edit_img;
    private CircleImageView profileImage;
    private Button editPassword, saveChanges;
    private EditText username, name, phoneNumber;
    TextView email;

    private String imageURL;
    private byte[] uploadBytes;

    @Override
    public void onConfirmEmailUpdate(String email) {
        this.email.setText(email);
        mUser.setEmail(email);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d(TAG, "onDismiss: called");
        if (!saveChanges.isEnabled()) {
            saveChanges.setEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ref = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser = this.getIntent().getParcelableExtra("mUser");

        profileImage = findViewById(R.id.profileImage);
        back_button = findViewById(R.id.back_arrow);
        edit_img = findViewById(R.id.edit_profile_image_btn);
        editPassword = findViewById(R.id.edit_password);
        saveChanges = findViewById(R.id.save_edit_profile_btn);

        name = findViewById(R.id.name_field);
        username = findViewById(R.id.username_field);
        phoneNumber = findViewById(R.id.phone_field);
        email = findViewById(R.id.email_field);

        edit_img.setOnClickListener(this);
        back_button.setOnClickListener(this);
        editPassword.setOnClickListener(this);
        saveChanges.setOnClickListener(this);
        email.setOnClickListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(mUser != null)
            initCurrentInfo();
        else
            Toast.makeText(this, "Could not retrieve user data", Toast.LENGTH_SHORT).show();
    }

    private void initCurrentInfo() {
        if (mUser.getPhotoUrl() != null)
            Glide.with(this).load(mUser.getPhotoUrl()).into(profileImage);
        username.setText(mUser.getUsername());
        name.setText(mUser.getName());
        email.setText(mUser.getEmail());
        if (mUser.getPhoneNumber() != null)
            phoneNumber.setText(mUser.getPhoneNumber());
        else
            phoneNumber.setHint("Edit Phone Number");
    }

    private void verifyInformation() {
        Log.d(TAG, "verifyInformation: called");

        String mUsername = username.getText().toString();
        String mName = name.getText().toString();
        String mPhoneNumber = phoneNumber.getText().toString();
        String mEmail = email.getText().toString();

        if (mUsername.isEmpty()) {
            username.setError("username is empty");
            username.requestFocus();
            saveChanges.setEnabled(true);
            return;
        }
        if (mName.isEmpty()) {
            name.setError("name is empty");
            name.requestFocus();
            saveChanges.setEnabled(true);
            return;
        }
        if (mEmail.isEmpty()) {
            email.setError("email is empty");
            email.requestFocus();
            saveChanges.setEnabled(true);
        }
        else if (!mEmail.equals(mUser.getEmail())) {
            if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                email.setError(getString(R.string.requires_valid_email));
                email.requestFocus();
                saveChanges.setEnabled(true);
            }
            else {
                EditEmailDialog dialog = new EditEmailDialog();
                Bundle args = new Bundle();
                args.putParcelable("user", mUser);
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "editEmail");
            }
        }

        else {
            Log.d(TAG, "verifyInformation: ");
            ref.child("users").child(currentUser.getUid()).child("username").setValue(mUsername);
            ref.child("users").child(currentUser.getUid()).child("name").setValue(mName);
            ref.child("users").child(currentUser.getUid()).child("phoneNumber").setValue(mPhoneNumber);
            exit();
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
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        final StorageReference sRef = storageReference.child(FilePaths.FIREBASE_IMG_STORAGE + "/" + currentUser.getUid());
        UploadTask uploadTask = sRef.putBytes(uploadBytes);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            progressDialog.dismiss();
            sRef.getDownloadUrl().addOnSuccessListener(uri -> {
                imageURL = uri.toString();
                ref.child("users").child(currentUser.getUid()).child("PhotoUrl").setValue(imageURL)
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
            case(R.id.edit_profile_image_btn):
                if (Permissions.checkCameraPermissions(this)) {
                    selectImage();
                }
                else {
                    Permissions.verifyCameraPermissions(this);
                }
                break;
            case(R.id.back_arrow):
                exit();
                break;
            case(R.id.save_edit_profile_btn):
                Log.d(TAG, "onClick: saved button");
                saveChanges.setEnabled(false);
                verifyInformation();
                break;
            case(R.id.edit_password):
                EditPasswordDialog editPasswordDialog = new EditPasswordDialog();
                editPasswordDialog.show(getSupportFragmentManager(), "editPassword");
                break;
            case(R.id.email_field):
                EditEmailDialog editEmailDialog = new EditEmailDialog();
                Bundle args = new Bundle();
                args.putString("currentEmail", email.getText().toString().trim());
                editEmailDialog.setArguments(args);
                editEmailDialog.show(getSupportFragmentManager(), "editEmail");
                break;
        }
    }

    public void exit() {
        startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
        finish();
    }
}
