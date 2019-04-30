package com.example.brush;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AnnouncementPostActivity extends AppCompatActivity {

    private Button a_post;
    private Toolbar mToolbar;
    private EditText a_description;
    private EditText a_title;
    private DatabaseReference ARef;
    private DatabaseReference UsersRef;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private ProgressDialog loadingbar;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postRandomName;
    private String username;
    private String profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_post);


        a_post = (Button) findViewById(R.id.a_post);
        a_description = (EditText) findViewById(R.id.a_description);
        a_title = (EditText) findViewById(R.id.a_title);

        mAuth = FirebaseAuth.getInstance();

        current_user_id = mAuth.getCurrentUser().getUid();

        ARef = FirebaseDatabase.getInstance().getReference().child("Posts");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Post Announcements");

        loadingbar = new ProgressDialog(this);

        a_post.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ValidatePost();
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

    private void ValidatePost()
    {
        String description = a_description.getText().toString();
        String title= a_title.getText().toString();

        if(TextUtils.isEmpty(description))
        {
            Toast.makeText(this, "Please write announcement...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(title))
        {
            Toast.makeText(this, "Please give the announcement a title...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Add announcement");
            loadingbar.setMessage("Please wait, while we are adding your new announcement");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            SavingAnnouncementToDatabase(description, title);
        }
    }

    private void SavingAnnouncementToDatabase(String description, String title)
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;



        HashMap aMap = new HashMap();

        aMap.put("uid",current_user_id);
        aMap.put("date",saveCurrentDate);
        aMap.put("time",saveCurrentTime);
        aMap.put("description",description);
        aMap.put("title",title);
        aMap.put("username",username);
        aMap.put("profilePicture",profilePicture);
        aMap.put("postType","announcement");

        ARef.child(current_user_id + postRandomName).updateChildren(aMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {
                    SendUserToMainActivity();
                    Toast.makeText(AnnouncementPostActivity.this, "New announcement is created successfully",Toast.LENGTH_SHORT);
                    loadingbar.dismiss();
                }
                else
                {
                    Toast.makeText(AnnouncementPostActivity.this, "Error occurred while creating announcement ",Toast.LENGTH_SHORT);
                    loadingbar.dismiss();
                }
            }
        });
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
        Intent mainIntent = new Intent (AnnouncementPostActivity.this,MainActivity.class );
        startActivity(mainIntent);
    }
}