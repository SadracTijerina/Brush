package com.example.brush;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.net.Uri;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GalleryPostActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton post_image;
    private Button post_post;
    private EditText post_description;
    private RadioButton post_category1;
    private RadioButton post_category2;
    private RadioButton post_category3;
    private RadioButton post_category4;


    private static final int Gallery_Pick = 1;
    private Uri ImageUri;
    private String description;

    private StorageReference PostImagesReference;

    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postRandomName;

    private String TAG = "333";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_post);

        PostImagesReference = FirebaseStorage.getInstance().getReference();

        post_image = (ImageButton) findViewById(R.id.post_image);
        post_post = (Button) findViewById(R.id.a_post);
        post_description = (EditText) findViewById(R.id.post_description);
        post_category1 = (RadioButton) findViewById(R.id.post_category1);
        post_category2 = (RadioButton) findViewById(R.id.post_category2);
        post_category3 = (RadioButton) findViewById(R.id.post_category3);
        post_category4 = (RadioButton) findViewById(R.id.post_category4);


        //mToolbar = (Toolbar) findViewById(R.id.update_post_bar_layout);
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setTitle("Post to Gallery");


        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        post_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInfo();

            }
        });
    }

    private void ValidatePostInfo() {
        description = post_description.getText().toString();

        if (ImageUri == null) {
            Toast.makeText(GalleryPostActivity.this, "Please select post image", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(description)) {
            Toast.makeText(GalleryPostActivity.this, "Please write post description...", Toast.LENGTH_SHORT).show();
        } else {
            StoringImagetoFirebaseStorage();
        }


    }

    private void StoringImagetoFirebaseStorage() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;


        StorageReference filePath = PostImagesReference.child("Gallery Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");
        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(GalleryPostActivity.this, "image uploaded successfully to storage...", Toast.LENGTH_SHORT).show();
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(GalleryPostActivity.this, "error occurred: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*)");
        Log.d(TAG, "Before");
        startActivityForResult(galleryIntent,Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            post_image.setImageURI(ImageUri);
        }
    }


    /*
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        Log.d(TAG, "After");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            post_image.setImageURI(ImageUri);
        }
    }*/

}


