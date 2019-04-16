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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
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

    String currentUserID;

    String tagerino = "333";

    final static int Gallery_Pick = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profileimage");

        username = (EditText) findViewById(R.id.setup_username);
        name = (EditText) findViewById(R.id.setup_name);
        profilePicture = (CircleImageView) findViewById(R.id.setup_profile_picture);
        SaveInformationbuttion = (Button) findViewById(R.id.setup_button);

        loadingBar = new ProgressDialog(this);


        SaveInformationbuttion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveAccountSetupInformation();
            }
        });


        //This function opens the the camera/gallery
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
                    if (dataSnapshot.hasChild("profileimage"))
                    {

                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.default_profile_picture).into(profilePicture);
                    }
                    else
                    {
                        Toast.makeText(SetupActivity.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();

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

    public void pickImage() {
        CropImage.startPickImageActivity(this);
    }

    //CROP REQUEST JAVA
    private void croprequest(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //This gets the URI of the image the user selects before cropped
        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            //This directs it to the crop image
            CropImage.activity(ImageUri)
                    .start(this);


            Log.d(tagerino,"Image selection working");
        }

        /*
           So it just goes to the end of this function. It doesn't go to the if or else statement. It just skips it to the end
           of the function
           Something is wrong with CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
           Because resultCode is suppose to be -1 because it equals RESULT_OK

        */

        Log.d(tagerino,"Result code: " + resultCode + " CropImage: "+ CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);

        //This gets the URI of the image that has been cropped
        if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            Log.d(tagerino,"URI of the image that has been cropped is gucci");

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            Log.d(tagerino,"result: " + result + " resultCode: " + resultCode + " RESULT_OK: " + RESULT_OK);

            if(resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we are updating your profile picture");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                //We stored the file into the profile images folder
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SetupActivity.this, "Image stored successfully to Firebase", Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            //This is when we store it into the firebase database
                            UsersRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(!task.isSuccessful())
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SetupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                Intent setupIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                startActivity(setupIntent);
                                                Toast.makeText(SetupActivity.this, "Profile Image stored into Database Successfully", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            String message = task.getException().getMessage();
                            Toast.makeText(SetupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else
            {
                Log.d(tagerino,"URI of the image has failed r.i.p");
                Toast.makeText(SetupActivity.this,"Error occurred: image cant be cropped. Try again" , Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
        else
        {
            Log.d(tagerino, "At else statement boi");
        }

        Log.d(tagerino, "at the end of onActivityResult");
    }

    private void SaveAccountSetupInformation()
    {
        String UserName = username.getText().toString();
        String Name = name.getText().toString();

        if(TextUtils.isEmpty(UserName))
        {
            Toast.makeText(SetupActivity.this,"Please write your username", Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(Name))
        {
            Toast.makeText(SetupActivity.this,"Please write your name", Toast.LENGTH_SHORT);
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