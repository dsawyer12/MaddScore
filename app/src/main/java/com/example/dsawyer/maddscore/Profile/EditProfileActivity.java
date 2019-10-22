package com.example.dsawyer.maddscore.Profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.FilePaths;
import com.example.dsawyer.maddscore.Utils.Permissions;
import com.example.dsawyer.maddscore.Utils.PhotoManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements
        EditEmailDialog.OnConfirmPasswordListener,
        DialogInterface.OnDismissListener,
        EditPasswordDialog.OnRequestNewPasswordListener,
        View.OnClickListener {
    private static final String TAG = "TAG";
    private final int PICK_IMAGE_REQUEST = 71;

    private DatabaseReference ref;
    private String UID;
    private StorageReference storageReference;

    private User mUser;
    private ImageView back_button;
    private CircleImageView profileImage;
    private Button edit_img, editPassword, saveChanges;
    private EditText username, name, phoneNumber, email;

    private String imageURL;
    private byte[] uploadBytes;

    @Override
    public void onConfirmPassword(String password) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d(TAG, "user was reauthenticated");
                                    user.updateEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        ref.child("users").child(UID).child("email").setValue(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    Toast.makeText(EditProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                                                                    exit();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }
                            else{
                                saveChanges.setEnabled(true);
                                Toast.makeText(EditProfileActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequest(final String current_password, final String new_password) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
           AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), current_password);
           user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if (task.isSuccessful()){
                       Log.d(TAG, "user was reauthenticated");
                       user.updatePassword(new_password)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()) {
                                           Toast.makeText(EditProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                           exit();
                                       }
                                   }
                               });
                   }
                   else{
                       saveChanges.setEnabled(true);
                       Toast.makeText(EditProfileActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                   }
               }
           });
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d(TAG, "onDismiss: called");
        if (!saveChanges.isEnabled()){
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
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        else
            Log.d(TAG, "onCreate: Firebase.currentUser = null");

        profileImage = findViewById(R.id.profileImage);
        back_button = findViewById(R.id.back_arrow);
        edit_img = findViewById(R.id.edit_profile_image_btn);
        editPassword = findViewById(R.id.edit_password);

        email = findViewById(R.id.edit_email);
        saveChanges = findViewById(R.id.save_edit_profile_btn);
        username = findViewById(R.id.edit_username);
        name = findViewById(R.id.edit_name);
        phoneNumber = findViewById(R.id.edit_phoneNumber);

        mUser = this.getIntent().getParcelableExtra("curentUser");

        edit_img.setOnClickListener(this);
        back_button.setOnClickListener(this);
        editPassword.setOnClickListener(this);
        saveChanges.setOnClickListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(mUser != null)
            initCurrentInfo();
        else
            Toast.makeText(this, "Could not retrieve user data", Toast.LENGTH_SHORT).show();
    }

    private void initCurrentInfo() {
        if (mUser.getPhotoUrl() != null)
            Glide.with(getApplicationContext()).load(mUser.getPhotoUrl()).into(profileImage);
        else
            Glide.with(getApplicationContext()).load(R.mipmap.default_profile_img).into(profileImage);

        username.setText(mUser.getUsername());
        name.setText(mUser.getName());
        email.setText(mUser.getEmail());
        if (mUser.getPhoneNumber() != null)
            phoneNumber.setText(mUser.getPhoneNumber());
        else
            phoneNumber.setHint("Edit Phone Number");
    }

    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions");
        ActivityCompat.requestPermissions(this, permissions, 1);
    }

    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array");
        for (int i = 0; i < permissions.length; i++){
            String check = permissions[i];
            if (!checkPermssions(check))
                return false;
        }
        return true;
    }

    public boolean checkPermssions(String permission){
        Log.d(TAG, "checkPerimssions: checking permission " + permission);
        int permissionRequest = ActivityCompat.checkSelfPermission(this, permission);
        if (permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPerimssions: permission was not granted for " + permission);
            return false;
        }
        else
            return true;
    }

    private void verifyInformation() {
        Log.d(TAG, "verifyInformation: called");

        String mUsername = username.getText().toString();
        String mName = name.getText().toString();
        String mPhoneNumber = phoneNumber.getText().toString();
        String mEmail = email.getText().toString();

        if (mUsername.isEmpty()){
            username.setError("username is empty");
            username.requestFocus();
            saveChanges.setEnabled(true);
            return;
        }
        if (mName.isEmpty()){
            name.setError("name is empty");
            name.requestFocus();
            saveChanges.setEnabled(true);
            return;
        }
        if (mEmail.isEmpty()){
            email.setError("email is empty");
            email.requestFocus();
            saveChanges.setEnabled(true);
        }
        else if (!mEmail.equals(mUser.getEmail())){
            if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                email.setError(getString(R.string.requires_valid_email));
                email.requestFocus();
                saveChanges.setEnabled(true);
            }
            else{
                EditEmailDialog dialog = new EditEmailDialog();
                Bundle args = new Bundle();
                args.putParcelable("user", mUser);
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "editEmail");
            }
        }

        else{
            Log.d(TAG, "verifyInformation: ");
            ref.child("users").child(UID).child("username").setValue(mUsername);
            ref.child("users").child(UID).child("name").setValue(mName);
            ref.child("users").child(UID).child("phoneNumber").setValue(mPhoneNumber);
            exit();
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
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
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "compressing image");
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            Log.d(TAG, "doInBackground: started");
            if (bitmap == null){
                try{
                    bitmap = PhotoManager.HandleSamplingAndRotationBitmap(getApplicationContext(), uris[0]);
                }
                catch(IOException e){
                    Log.d(TAG, "doInBackground: " + e.getMessage());
                }
            }
            return getBytesFromBitmap(bitmap, 50);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            uploadBytes = bytes;
            uploadTask();
        }
    }

    private void uploadTask(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        final StorageReference sRef = storageReference.child(FilePaths.FIREBASE_IMG_STORAGE + "/" + UID);
        UploadTask uploadTask = sRef.putBytes(uploadBytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageURL = uri.toString();
                        ref.child("users").child(UID).child("PhotoUrl").setValue(imageURL)
                                .addOnSuccessListener( new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(EditProfileActivity.this, "Uplaod Success", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EditProfileActivity.this, "Failed to Uplaod Photo", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            }
        });
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
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
        }
    }

    public void exit(){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
