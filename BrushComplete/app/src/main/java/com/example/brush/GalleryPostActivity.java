package com.example.brush;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.net.Uri;
import android.widget.Toast;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class GalleryPostActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton post_image;
    private Button post_post;
    private EditText post_description;
    private RadioGroup category_group;
    private RadioButton post_category1;
    private RadioButton post_category2;
    private RadioButton post_category3;
    private RadioButton post_category4;
    private TextView post_select;

    private static final int Gallery_pick = 1;
    private Uri ImageUri;
    private StorageReference PostImagesreference;
    private DatabaseReference GalleryRef;
    private DatabaseReference UsersRef;
    private DatabaseReference ProfileRef;
    private DatabaseReference aRef;
    private DatabaseReference dRef;
    private DatabaseReference tRef;
    private DatabaseReference pRef;

    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postRandomName;
    private String downloadUrl;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private ProgressDialog loadingbar;
    private String category;
    private String tempcategory;
    private String username;
    private String profilePicture;
    private String digital;
    private String traditional;
    private String photography;
    private String craft;
    private long num;

    private int selectedButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_post);

        mAuth = FirebaseAuth.getInstance();

        current_user_id = mAuth.getCurrentUser().getUid();
        PostImagesreference = FirebaseStorage.getInstance().getReference();
        GalleryRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        aRef = FirebaseDatabase.getInstance().getReference().child("Categories").child("a");
        dRef = FirebaseDatabase.getInstance().getReference().child("Categories").child("d");
        pRef = FirebaseDatabase.getInstance().getReference().child("Categories").child("p");
        tRef = FirebaseDatabase.getInstance().getReference().child("Categories").child("t");
        ProfileRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id).child("Type").child("G");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        /*GalleryRef.child("postRandomName").child("uid").setValue();
        GalleryRef.child("postRandomName").child("date").setValue(" ");
        GalleryRef.child("postRandomName").child("time").setValue(" ");
        GalleryRef.child("postRandomName").child("description").setValue(" ");
        GalleryRef.child("postRandomName").child("postimage").setValue(" ");
        GalleryRef.child("postRandomName").child("category").setValue(" ");*/


        mToolbar = findViewById(R.id.g_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Post to Gallery");

        post_image = findViewById(R.id.post_image);
        post_post = findViewById(R.id.post_post);
        post_description = findViewById(R.id.post_description);
        post_select = findViewById(R.id.post_select);
        category_group = findViewById(R.id.category_group);
        post_category1 = findViewById(R.id.post_category1);
        post_category2 = findViewById(R.id.post_category2);
        post_category3 = findViewById(R.id.post_category3);
        post_category4 = findViewById(R.id.post_category4);

        post_category1.setTag(1);
        post_category2.setTag(2);
        post_category3.setTag(3);
        post_category4.setTag(4);

        digital = Integer.toString(R.id.post_category1);
        traditional = Integer.toString(R.id.post_category2);
        photography= Integer.toString(R.id.post_category3);
        craft = Integer.toString(R.id.post_category4);

        loadingbar = new ProgressDialog(this);


        post_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OpenGallery();
            }
        });

        post_post.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectedButton = category_group.getCheckedRadioButtonId();
                tempcategory = Integer.toString(selectedButton);
                Log.d(tempcategory,"category");
                ValidatePostInfo();
            }
        });
        UsersRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                username = dataSnapshot.child("Username").getValue().toString();
                profilePicture = dataSnapshot.child("profilePictureLink").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }


    private void ValidatePostInfo()
    {
        String description = post_description.getText().toString();

        if(ImageUri == null)
        {
            Toast.makeText(this, "Please select post image...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description))
        {
            Toast.makeText(this, "Please write post description...", Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingbar.setTitle("Add new post");
            loadingbar.setMessage("Please wait, while we are adding your new post");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            StoringImageToFirebaseStorage(description);
        }

    }

    private void StoringImageToFirebaseStorage(final String description)
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;
        StorageReference filePath = PostImagesreference.child("Post Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    downloadUrl = task.getResult().getDownloadUrl().toString();
                    Toast.makeText(GalleryPostActivity.this,"Image uploaded successfully...",Toast.LENGTH_SHORT);

                    SavingPostInfoToDatabase(description);
                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(GalleryPostActivity.this,"Error occurred: " + message,Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void SavingPostInfoToDatabase(final String description)
    {
        HashMap galleryMap = new HashMap();

        if ( tempcategory.equals(digital))
        {
            category = "Digital Art";
        }
        else if ( tempcategory.equals(traditional))
        {
            category = "Traditional Art";
        }
        else if ( tempcategory.equals(photography))
        {
            category = "Photography";
        }
        else
        {
            category = "Artisan Crafts";
        }


        galleryMap.put("uid",current_user_id);
        galleryMap.put("date",saveCurrentDate);
        galleryMap.put("time",saveCurrentTime);
        galleryMap.put("description",description);
        galleryMap.put("postimage",downloadUrl);
        galleryMap.put("category",category);
        galleryMap.put("username",username);
        galleryMap.put("profilePicture",profilePicture);
        galleryMap.put("postType","gallery");


        //galleryMap.put("profileImage",userProfileImage);
        //galleryMap.put("fullname",Userfullname);


        if ( tempcategory.equals(digital))
        {
            dRef.child(current_user_id + postRandomName).updateChildren(galleryMap);
        }
        else if ( tempcategory.equals(traditional))
        {
            tRef.child(current_user_id + postRandomName).updateChildren(galleryMap);
        }
        else if ( tempcategory.equals(photography))
        {
            pRef.child(current_user_id + postRandomName).updateChildren(galleryMap);
        }
        else
        {
            aRef.child(current_user_id + postRandomName).updateChildren(galleryMap);
        }

        ProfileRef.child(current_user_id + postRandomName).updateChildren(galleryMap);
        GalleryRef.child(current_user_id + postRandomName).updateChildren(galleryMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {
                    SendUserToMainActivity();
                    Toast.makeText(GalleryPostActivity.this, "New post is created successfully",Toast.LENGTH_SHORT);
                    loadingbar.dismiss();
                }
                else
                {
                    Toast.makeText(GalleryPostActivity.this, "Error occurred while updating post ",Toast.LENGTH_SHORT);
                    loadingbar.dismiss();
                }
            }
        });
    }

    private void OpenGallery()
    {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
        //Intent galleryIntent = new Intent();
        //galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        //galleryIntent.setType("image/*");
        //startActivityForResult(galleryIntent,Gallery_pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                loadingbar.setTitle("Gallery Post");
                loadingbar.setMessage("Please wait while we are cropping your image...");
                loadingbar.show();
                loadingbar.setCanceledOnTouchOutside(true);

                ImageUri = result.getUri();
                post_image.setImageURI(ImageUri);

                loadingbar.dismiss();
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id= item.getItemId();
        if(id == android.R.id.home)
        {
            SendUserToMainActivity();

        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent (GalleryPostActivity.this,NewPostActivity.class );
        startActivity(mainIntent);
    }
}