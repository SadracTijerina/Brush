package com.example.brush;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import android.app.ProgressDialog;

import java.util.HashMap;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

public class SettingsActivity extends AppCompatActivity {

    private EditText full_name, username, bio;
    private ImageButton edit_picture;
    private Button settings_confirm;
    private ProgressDialog loadingBar;

    private Toolbar m_toolbar;

    private DatabaseReference user_profile_settings_ref;
    private FirebaseAuth m_auth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;
    private FirebaseStorage firebaseStorage;

    private String current_user_id;

    Uri imageUri;

    static final int gallery_pick =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        m_toolbar = (Toolbar) findViewById(R.id.user_profile_settings_toolbar);
        setSupportActionBar(m_toolbar);
        getSupportActionBar().setTitle("Profile Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        m_auth = FirebaseAuth.getInstance();
        current_user_id = m_auth.getCurrentUser().getUid();
        user_profile_settings_ref = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile images");

        full_name = (EditText) findViewById(R.id.user_profile_settings_full_name);
        username = (EditText) findViewById(R.id.user_profile_settings_username);
        bio = (EditText) findViewById(R.id.user_profile_settings_bio);
        edit_picture = (ImageButton) findViewById(R.id.user_profile_settings_default_pic);
        settings_confirm = (Button) findViewById(R.id.user_profile_settings_confirm);
        loadingBar = new ProgressDialog(this);

        user_profile_settings_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String myProfileImage = dataSnapshot.child("profilePicture").getValue().toString();
                    String myFullName = dataSnapshot.child("Name").getValue().toString();
                    String myUsername = dataSnapshot.child("Username").getValue().toString();
                    String myBio = dataSnapshot.child("Bio").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.dfp).into(edit_picture);
                    full_name.setText(myFullName);
                    username.setText(myUsername);
                    bio.setText(myBio);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        settings_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateAccountInfo();
            }
        });

        edit_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, gallery_pick);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            //The result variable is the result of the crop image.
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we are updating your profile picture");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                imageUri = result.getUri();

                //This is to get the contents of the image
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpeg")
                        .build();

                //This uploads the file with all its content to the firebase storage
                UploadTask uploadTask = UserProfileImageRef.child(current_user_id).putFile(imageUri, metadata);

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        System.out.println("Upload is " + progress + "% done");
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Upload is paused");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        loadingBar.dismiss();
                        Toast.makeText(SettingsActivity.this, "Failure storing image to Firebase", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Handle successful uploads on complete

                        //Storing the image to the database
                        final String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        UsersRef.child("Profile Picture").setValue(current_user_id);

                        edit_picture.setImageURI(imageUri);

                        Toast.makeText(SettingsActivity.this, "Image stored successfully to Firebase", Toast.LENGTH_SHORT).show();

                        Intent selfIntent = new Intent(SettingsActivity.this, SettingsActivity.class);
                        startActivity(selfIntent);

                        loadingBar.dismiss();
                    }
                });
            }
        }
    }

    private void ValidateAccountInfo()
    {
        String myFullName = full_name.getText().toString();
        String myUsername = username.getText().toString();
        String myBio = bio.getText().toString();

        if(TextUtils.isEmpty(myUsername))
        {
            Toast.makeText(this, "Please write your username...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(myFullName))
        {
            Toast.makeText(this, "Please write your full name...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(myBio))
        {
            Toast.makeText(this, "Please enter your bio...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Profile Image");
            loadingBar.setMessage("Please wait, while we are updating your profile picture");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            UpdateAccountInfo(myFullName, myUsername, myBio);
        }
    }

    private void UpdateAccountInfo(String myFullName, String myUsername, String myBio)
    {
        HashMap userMap = new HashMap();
        userMap.put("Username", myUsername);
        userMap.put("Name", myFullName);
        userMap.put("Bio", myBio);
        user_profile_settings_ref.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    SendUserToUserProfileActivity();
                    Toast.makeText(SettingsActivity.this, "Profile settings updated!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
                else
                {
                    Toast.makeText(SettingsActivity.this, "Error, couldn't update profile settings!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });

    }

    private void SendUserToUserProfileActivity()
    {
        Intent profileIntent = new Intent(SettingsActivity.this, UserProfileActivity.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
    }

}