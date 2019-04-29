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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class BidPostActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton bid_image;
    private Button bid_post;
    private EditText bid_description;
    private EditText bid_money;
    private EditText bid_time;
    private RadioGroup category_group;
    private RadioButton bid_category1;
    private RadioButton bid_category2;
    private RadioButton bid_category3;
    private RadioButton bid_category4;
    private TextView bid_select;
    private String description;


    private static final int Gallery_Pick = 1;
    private Uri ImageUri;
    private String money;
    private String time;

    private StorageReference BidImagesReference;
    private DatabaseReference bidRef;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postRandomName;
    private String downloadUrl;
    private static final int Gallery_pick = 1;

    private DatabaseReference GalleryRef;

    private String downloadURL;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private ProgressDialog loadingbar;

    private String category;
    private String tempcategory;
    private int selectedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_post);

        mAuth = FirebaseAuth.getInstance();

        current_user_id = mAuth.getCurrentUser().getUid();
        BidImagesReference = FirebaseStorage.getInstance().getReference();
        bidRef = FirebaseDatabase.getInstance().getReference().child("Bids");

        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Post Bidding Item");

        bid_image = (ImageButton) findViewById(R.id.bid_image);
        bid_post = (Button) findViewById(R.id.bid_post);
        bid_description = (EditText) findViewById(R.id.bid_description);
        bid_money = (EditText) findViewById(R.id.bid_money);
        bid_time = (EditText) findViewById(R.id.bid_time) ;
        category_group = (RadioGroup) findViewById(R.id.category_group);
        bid_select = (TextView) findViewById(R.id.bid_select);
        bid_category1 = (RadioButton) findViewById(R.id.bid_category1) ;
        bid_category2 = (RadioButton) findViewById(R.id.bid_category2) ;
        bid_category3 = (RadioButton) findViewById(R.id.bid_category3) ;
        bid_category4 = (RadioButton) findViewById(R.id.bid_category4) ;
        loadingbar = new ProgressDialog(this);

        bid_category1.setTag(1);
        bid_category2.setTag(2);
        bid_category3.setTag(3);
        bid_category4.setTag(4);


        bid_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OpenGallery();
            }
        });

        bid_post.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectedButton = category_group.getCheckedRadioButtonId();
                tempcategory = Integer.toString(selectedButton);
                ValidatePostInfo();
            }
        });
    }

    private void ValidatePostInfo()
    {
        description = bid_description.getText().toString();
        money = bid_money.getText().toString();
        time = bid_time.getText().toString();

        if(ImageUri == null)
        {
            Toast.makeText(this, "Please select bid image...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description))
        {
            Toast.makeText(this, "Please write bid description...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(money))
        {
            Toast.makeText(this, "Please enter bid price...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(time))
        {
            Toast.makeText(this, "Please enter bidding time...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Add new bidding item");
            loadingbar.setMessage("Please wait, while we are adding your new bidding item");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            StoringImageToFirebaseStorage();
        }
    }

    private void StoringImageToFirebaseStorage()
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;
        StorageReference filePath = BidImagesReference.child("Bid Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    downloadUrl = task.getResult().getDownloadUrl().toString();
                    Toast.makeText(BidPostActivity.this,"Image uploaded successfully...",Toast.LENGTH_SHORT);

                    SavingPostInfoToDatabase();
                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(BidPostActivity.this,"Error occurred: " + message,Toast.LENGTH_SHORT);
                }
            }
        });
    }
    private void SavingPostInfoToDatabase()
    {

        HashMap bidMap = new HashMap();
        if ( tempcategory.equals("2131230900"))
        {
            category = "Digital Art";
        }
        else if ( tempcategory.equals("21312309001"))
        {
            category = "Traditional Art";
        }
        else if ( tempcategory.equals("2131230902"))
        {
            category = "Photography";
        }
        else
        {
            category = "Artisan Crafts";
        }
        bidMap.put("uid",current_user_id);
        bidMap.put("date",saveCurrentDate);
        bidMap.put("time",saveCurrentTime);
        bidMap.put("description",description);
        bidMap.put("price",money);
        bidMap.put("time",time);
        bidMap.put("postimage",downloadUrl);
        bidMap.put("category",category);

        bidRef.child(current_user_id + postRandomName).updateChildren(bidMap)
                .addOnCompleteListener(new OnCompleteListener()
                {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful())
                        {
                            SendUserToMainActivity();
                            Toast.makeText(BidPostActivity.this, "New post is created successfully",Toast.LENGTH_SHORT);
                            loadingbar.dismiss();
                        }
                        else
                        {
                            Toast.makeText(BidPostActivity.this, "Error occurred while updating post ",Toast.LENGTH_SHORT);
                            loadingbar.dismiss();
                        }
                    }
                });

    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_pick && resultCode == RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            bid_image.setImageURI(ImageUri);
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
        Intent mainIntent = new Intent (BidPostActivity.this,MainActivity.class );
        startActivity(mainIntent);
    }

}
