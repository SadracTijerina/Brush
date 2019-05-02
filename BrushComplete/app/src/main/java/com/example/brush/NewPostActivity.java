package com.example.brush;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;

public class NewPostActivity extends AppCompatActivity {

    private ImageButton bidButton;
    private ImageButton galleryButton;
    private ImageButton announcementButton;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mToolbar = findViewById(R.id.new_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create New Post");

        bidButton = findViewById(R.id.bidButton);
        galleryButton = findViewById(R.id.galleryButton);
        announcementButton = findViewById(R.id.announcementButton);

        bidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(NewPostActivity.this, BidPostActivity.class);
                startActivity(loginIntent);
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent registerIntent = new Intent(NewPostActivity.this, GalleryPostActivity.class);
                startActivity(registerIntent);

            }
        });

        announcementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent registerIntent = new Intent(NewPostActivity.this, AnnouncementPostActivity.class);
                startActivity(registerIntent);
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
        Intent mainIntent = new Intent (NewPostActivity.this,MainActivity.class );
        startActivity(mainIntent);
    }
}