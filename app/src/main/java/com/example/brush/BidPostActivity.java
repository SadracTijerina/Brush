package com.example.brush;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.net.Uri;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class BidPostActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton bid_image;
    private Button bid_post;
    private EditText bid_description;
    private EditText bid_money;
    private EditText bid_time;
    private RadioButton bid_category1;
    private RadioButton bid_category2;
    private RadioButton bid_category3;
    private RadioButton bid_category4;




    private static final int Gallery_Pick = 1;
    private Uri ImageUri;
    private String description;
    private String money;
    private String time;


    private StorageReference PostImagesReference;

    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postRandomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_post);


        PostImagesReference = FirebaseStorage.getInstance().getReference();

        bid_image = (ImageButton) findViewById(R.id.bid_image);
        bid_post = (Button) findViewById(R.id.a_post);
        bid_description = (EditText) findViewById(R.id.bid_description);
        bid_image = (ImageButton) findViewById(R.id.bid_image);
        bid_post = (Button) findViewById(R.id.a_post);
        bid_description = (EditText) findViewById(R.id.bid_description);
        bid_money = (EditText) findViewById(R.id.bid_money);
        bid_time = (EditText) findViewById(R.id.bid_time) ;
        bid_category1 = (RadioButton) findViewById(R.id.bid_category1) ;
        bid_category2 = (RadioButton) findViewById(R.id.bid_category2) ;
        bid_category3 = (RadioButton) findViewById(R.id.bid_category3) ;
        bid_category4 = (RadioButton) findViewById(R.id.bid_category4) ;





        //mToolbar = (Toolbar) findViewById(R.id.update_post_bar_layout);
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setTitle("Post to Gallery");

        bid_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        bid_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ValidatePostInfo();

            }
        });
    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*)");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            bid_image.setImageURI(ImageUri);

        }
    }

    private void ValidatePostInfo()
    {
        description = bid_description.getText().toString();
        money = bid_money.getText().toString();
        time = bid_time.getText().toString();

        if(ImageUri == null)
        {
            Toast.makeText(BidPostActivity.this, "Please select bid post image...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(description))
        {
            Toast.makeText(BidPostActivity.this, "Please write bid post description...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(money))
        {
            Toast.makeText(BidPostActivity.this, "Please write lowest bid amount...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(time))
        {
            Toast.makeText(BidPostActivity.this, "Please write bid time length in days...", Toast.LENGTH_SHORT).show();
        }
        else
        {
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


        StorageReference filePath = PostImagesReference.child("Post Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");
        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(BidPostActivity.this, "image uploaded successfully to storage...", Toast.LENGTH_SHORT).show();
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(BidPostActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
