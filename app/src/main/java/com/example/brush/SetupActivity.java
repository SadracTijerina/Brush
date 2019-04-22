package com.example.brush;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.app.Activity;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import java.io.IOException;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;


import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText username, name;
    private CircleImageView profilePicture;
    private Button SaveInformationbuttion;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;
    private FirebaseStorage firebaseStorage;

    String currentUserID;
    boolean hasImage = false;

    String TAG = "333";

    Uri imageUri;

    final static int Gallery_Pick = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile images");

        username = (EditText) findViewById(R.id.setup_username);
        name = (EditText) findViewById(R.id.setup_name);
        profilePicture = (CircleImageView) findViewById(R.id.setup_profile_picture);
        SaveInformationbuttion = (Button) findViewById(R.id.setup_button);

        loadingBar = new ProgressDialog(this);


        //When button is clicked, call function
        SaveInformationbuttion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveAccountSetupInformation();
            }
        });


        //When profile picture is clicked, call the check android version
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checkAndroidVersion();
            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("profilePicture"))
                    {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        //This is trying to get the image url if it finds it it goes to onSuccess function
                        storageRef.child("profile images/" + currentUserID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // uri is the link, we just have to change it to a string
                                String Picture = uri.toString();
                                Picasso.get().load(Picture).into(profilePicture);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Log.d(TAG, "onFailure:");
                            }
                        });

                        hasImage = true;

                    }
                    else
                    {
                        Toast.makeText(SetupActivity.this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
                        hasImage = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }


    public void checkAndroidVersion(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 555);
            } catch (Exception e)
            {

            }
            //If versions good we call the pick image function
        } else {
            pickImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 555 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        } else {
            checkAndroidVersion();
        }
    }

    //This crops and picks the image and afterwards goes to the onActivityResult function
    public void pickImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //This gets the URI of the image that has been cropped
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
                UploadTask uploadTask = UserProfileImageRef.child(currentUserID).putFile(imageUri, metadata);

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
                        Toast.makeText(SetupActivity.this, "Failure storing image to Firebase", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Handle successful uploads on complete

                        //Storing the image to the database
                        final String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        UsersRef.child("profilePicture").setValue(currentUserID);

                        profilePicture.setImageURI(imageUri);

                        Toast.makeText(SetupActivity.this, "Image stored successfully to Firebase", Toast.LENGTH_SHORT).show();
                        //Intent setupIntent = new Intent(SetupActivity.this, SetupActivity.class);
                        //startActivity(setupIntent);

                        loadingBar.dismiss();
                    }
                });
            }
        }
    }

    private void SaveAccountSetupInformation()
    {
        String UserName = username.getText().toString();
        String Name = name.getText().toString();

        if(TextUtils.isEmpty(UserName))
        {
            Toast.makeText(SetupActivity.this,"Please write your username", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Name))
        {
            Toast.makeText(SetupActivity.this,"Please write your name", Toast.LENGTH_SHORT).show();
        }
        else if (!hasImage)
        {
            Toast.makeText(SetupActivity.this,"Please upload a picture", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Saving your information");
            loadingBar.setMessage("Please wait, while we are creating your account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            //If we want to add more to database do something like this
            HashMap userMap = new HashMap();
            userMap.put("Name", Name);
            userMap.put("Username", UserName);
            userMap.put("Bio", "");

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Your account set up has been successful", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();;

                    }
                }
            });

        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}