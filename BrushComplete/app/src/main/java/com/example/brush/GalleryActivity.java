package com.example.brush;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GalleryActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;

    private CircleImageView NavProfileImage;
    private TextView NavUserName;

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, postsRef;
    private StorageReference storageRef;
    private FirebaseStorage storage;

    private String profile_ID;
    private String username;

    ArrayList<String> followingUidList;
    private long num;

    String currentUserID;
    String TAG = "333";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        profile_ID = getIntent().getExtras().get("userID").toString();
        username = getIntent().getExtras().get("userName").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profile_ID).child("Type").child("G");


        mToolbar = findViewById(R.id.pg_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        postList = findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Posts, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostViewHolder>(

                Posts.class,
                R.layout.feed_gallery_post,
                PostViewHolder.class,
                postsRef
        ) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final Posts model, int position) {
                // **announcement = 1, gallery = 2**

                postsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int choice;
                        num = dataSnapshot.getChildrenCount();
                        int i = 0;
                        while (i < num) {
                            choice = 2;
                            String number = Long.toString(num);
                            Log.d(number, "num");
                            Log.d(TAG, "user id:" + model.getUid());
                            viewHolder.setTitle(model.getTitle(), choice); //
                            viewHolder.setPrice(model.getPrice(), choice);
                            viewHolder.setPostimage(model.getPostimage(), choice);
                            viewHolder.setProfilePicture(model.getProfilePicture(), choice); //
                            viewHolder.setDescription(model.getDescription(), choice); //
                            viewHolder.setUsername(model.getUsername(), choice); //
                            viewHolder.setBuy_Now_Button(choice);
                            i++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

        };
        postList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;
        String T = "333";

        public void setTitle(String title, int choice) {
            if (choice == 1) {
                TextView postTitle = mView.findViewById(R.id.feed_gallery_title);
                postTitle.setText(title);
            } else {
                TextView postTitle = mView.findViewById(R.id.feed_gallery_title);
                postTitle.setVisibility(View.GONE);
            }
        }

        public PostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDescription(String description, int choice) {
            TextView postDescription = (TextView) mView.findViewById(R.id.feed_gallery_description);
            postDescription.setText(description);
        }

        public void setProfilePicture(String profilePicture, int choice) {
            CircleImageView postProfilePicture = (CircleImageView) mView.findViewById(R.id.feed_gallery_profilepicture);
            Picasso.get().load(profilePicture).into(postProfilePicture);
        }

        public void setUsername(String username, int choice) {
            TextView postUsername = (TextView) mView.findViewById(R.id.feed_gallery_username);
            postUsername.setText(username);
        }

        public void setPostimage(String postImage, int choice) {
            if (choice == 0 || choice == 2) {
                ImageView post_image = (ImageView) mView.findViewById(R.id.feed_gallery_image);
                Picasso.get().load(postImage).into(post_image);
            } else {
                ImageView post_image = (ImageView) mView.findViewById(R.id.feed_gallery_image);
                post_image.setVisibility(View.GONE);
            }
        }

        public void setPrice(String price, int choice) {
            if (choice == 0) {
                TextView postPrice = (TextView) mView.findViewById(R.id.feed_bid_price);
                postPrice.setText(price);
            } else {
                TextView postPrice = (TextView) mView.findViewById(R.id.feed_bid_price);
                postPrice.setVisibility(View.GONE);
            }
        }

        public void setBuy_Now_Button(int choice) {
            if (choice == 0) {
                TextView post_button = (TextView) mView.findViewById(R.id.feed_buy_button);
                post_button.setText("Buy Now");
            } else {
                TextView post_button = (TextView) mView.findViewById(R.id.feed_buy_button);
                post_button.setVisibility(View.GONE);
            }

        }

        public void hidePosts() {
            mView.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id= item.getItemId();
        if(id == android.R.id.home)
        {
            SendUserToActivity();

        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToActivity()
    {
        Intent mainIntent = new Intent (GalleryActivity.this,MainActivity.class );
        startActivity(mainIntent);
    }
}