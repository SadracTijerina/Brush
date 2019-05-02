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
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;

    private CircleImageView NavProfileImage;
    private TextView NavUserName;

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, postsRef, followingRef;
    private StorageReference storageRef;
    private FirebaseStorage storage;

    ArrayList<String> followingUidList;
    private long num;

    String currentUserID;
    String TAG = "333";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        followingRef = FirebaseDatabase.getInstance().getReference().child("Followers").child(currentUserID).child("following");


        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        //postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavUserName = (TextView) navView.findViewById(R.id.nav_username);

        readData(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<String> list) {
                //Log.d(TAG, list.toString());
            }
        });

        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("Name")) {
                        //This is getting the users name and username from database.
                        // Make sure the child matches the databases names
                        String name = dataSnapshot.child("Name").getValue().toString();

                        //This is displaying the users name in the navigation bard
                        NavUserName.setText(name);
                    }
                    if (dataSnapshot.hasChild("profilePicture")) {
                        storage = FirebaseStorage.getInstance();
                        storageRef = storage.getReference();
                        //This is trying to get the image url if it finds it it goes to onSuccess function
                        storageRef.child("profile images/" + currentUserID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // uri is the link, we just have to change it to a string
                                String profilePicture = uri.toString();
                                Picasso.get().load(profilePicture).into(NavProfileImage);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Log.d(TAG, "onFailure:");
                            }
                        });

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });
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
            protected void populateViewHolder(PostViewHolder viewHolder, final Posts model, int position) {
                // **announcement = 1, gallery = 2**

                int choice;
                Log.d(TAG, "after user" + followingUidList);

                postsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        num = dataSnapshot.getChildrenCount();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                int i=0;
                while(i < num)
                {
                    if(model.getPostType().equals("bid")) //all 7
                    {
                        choice =0;
                        viewHolder.setTitle(model.getTitle(), choice);
                        viewHolder.setPrice(model.getPrice(), choice); //
                        viewHolder.setPostimage(model.getPostimage(), choice); //
                        viewHolder.setProfilePicture(model.getProfilePicture(), choice); //
                        viewHolder.setDescription(model.getDescription(), choice); //
                        viewHolder.setUsername(model.getUsername(), choice); //
                        viewHolder.setBuy_Now_Button(choice); //
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                Intent bidIntent = new Intent(MainActivity.this, SelectionActivity.class);
                                Bundle extras = new Bundle();
                                extras.putString("profilePicture", model.getProfilePicture());
                                extras.putString("postImage", model.getPostimage());
                                extras.putString("username", model.getUsername());
                                extras.putString("description", model.getDescription());
                                extras.putString("price", model.getPrice());
                                extras.putString("uid", model.getUid());
                                bidIntent.putExtras(extras);
                                startActivity(bidIntent);
                            }
                        });
                    }
                    else if (model.getPostType().equals("announcement")) //4
                    {
                        choice = 1;
                        //Log.d(TAG, "user id:" + model.getUid());
                        viewHolder.setTitle(model.getTitle(), choice); //
                        viewHolder.setPrice(model.getPrice(), choice);
                        viewHolder.setPostimage(model.getPostimage(), choice);
                        viewHolder.setProfilePicture(model.getProfilePicture(), choice); //
                        viewHolder.setDescription(model.getDescription(), choice); //
                        viewHolder.setUsername(model.getUsername(), choice); //
                        viewHolder.setBuy_Now_Button(choice);
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                Intent intent = new Intent(MainActivity.this, PublicProfileActivity.class);
                                intent.putExtra("userID", model.getUid());
                                startActivity(intent);
                            }
                        });
                    }
                    else if (model.getPostType().equals("gallery")) //5
                    {
                        choice = 2;
                        //Log.d(TAG, "user id:" + model.getUid());
                        viewHolder.setTitle(model.getTitle(), choice);
                        viewHolder.setPrice(model.getPrice(), choice);
                        viewHolder.setPostimage(model.getPostimage(), choice); //
                        viewHolder.setProfilePicture(model.getProfilePicture(), choice); //
                        viewHolder.setDescription(model.getDescription(), choice); //
                        viewHolder.setUsername(model.getUsername(), choice); //
                        viewHolder.setBuy_Now_Button(choice);
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                Intent intent = new Intent(MainActivity.this, PublicProfileActivity.class);
                                intent.putExtra("userID", model.getUid());
                                startActivity(intent);
                            }
                        });
                    }
                    i++;
                }

            }

        };

        postList.setAdapter(firebaseRecyclerAdapter);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendUserToStartActivity();
        } else {
            CheckUserExistence();
        }

    }

    private void readData(final FirebaseCallback firebaseCallback)
    {
        followingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                followingUidList = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    followingUidList.add(childSnapshot.getKey());
                }
                firebaseCallback.onCallback(followingUidList);

                Log.d(TAG, "before user" + followingUidList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private interface FirebaseCallback
    {
        void onCallback(ArrayList<String> list);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;
        String T = "333";

        public PostViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setTitle(String title, int choice) {
            if(choice == 1) {
                TextView postTitle = (TextView) mView.findViewById(R.id.feed_gallery_title);
                postTitle.setText(title);
            }
            else{
                TextView postTitle = (TextView) mView.findViewById(R.id.feed_gallery_title);
                postTitle.setVisibility(View.GONE);
            }
        }

        public void setDescription(String description, int choice) {
            TextView postDescription = (TextView) mView.findViewById(R.id.feed_gallery_description);
            postDescription.setText(description);
        }

        public void setProfilePicture(String profilePicture, int choice) {
            CircleImageView postProfilePicture = (CircleImageView) mView.findViewById(R.id.feed_gallery_profilepicture);
            Picasso.get().load(profilePicture).into(postProfilePicture);
        }

        public void setUsername(String username,  int choice) {
            TextView postUsername = (TextView) mView.findViewById(R.id.feed_gallery_username);
            postUsername.setText(username);
        }

        public void setPostimage(String postImage, int choice) {
            if(choice == 0 || choice == 2) {
                ImageView post_image = (ImageView) mView.findViewById(R.id.feed_gallery_image);
                Picasso.get().load(postImage).into(post_image);
            }
            else
            {
                ImageView post_image = (ImageView) mView.findViewById(R.id.feed_gallery_image);
                post_image.setVisibility(View.GONE);
            }
        }
        public void setPrice(String price, int choice){
            if(choice == 0)
            {
                TextView postPrice = (TextView) mView.findViewById(R.id.feed_bid_price);
                postPrice.setText(price);
            }
            else {
                TextView postPrice = (TextView) mView.findViewById(R.id.feed_bid_price);
                postPrice.setVisibility(View.GONE);
            }
        }
        public void setBuy_Now_Button(int choice)
        {
            if(choice ==0 )
            {
                TextView post_button = (TextView) mView.findViewById(R.id.feed_buy_button);
                post_button.setText("Buy Now");
            }
            else{
                TextView post_button = (TextView) mView.findViewById(R.id.feed_buy_button);
                post_button.setVisibility(View.GONE);
            }

        }
        public void hidePosts()
        {
            mView.setVisibility(View.GONE);
        }
    }

    private void CheckUserExistence()
    {
        final String currentUserId = mAuth.getCurrentUser().getUid();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.hasChild(currentUserId))
                {
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void SendUserToSetupActivity()
    {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
    }

    private void sendUserToStartActivity()
    {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startIntent);
    }

    private void sendUserToMessagesActivity()
    {
        Intent messagesIntent = new Intent(MainActivity.this, ChatActivity.class);
        messagesIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(messagesIntent);
    }

    private void sendUserToFollowersActivity()
    {
        Intent listFollowersIntent = new Intent(MainActivity.this, ListFollowersActivity.class);
        //listFollowersIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(listFollowersIntent);
    }

    private void sendUserToProfileActivity()
    {
        Intent profileIntent = new Intent(MainActivity.this, UserProfileActivity.class);
        //profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        profileIntent.putExtra("Users", currentUserID);
        startActivity(profileIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.nav_home:
                //Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
                ;
                break;

            case R.id.nav_profile:
                //Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show();
                sendUserToProfileActivity();
                break;

            case R.id.nav_post:
                Intent newPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(newPostIntent);
                //Toast.makeText(this, "post", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_followers:
                sendUserToFollowersActivity();
                break;

            case R.id.nav_search:
                Intent searchUsersIntent = new Intent(MainActivity.this, SearchUsersActivity.class);
                startActivity(searchUsersIntent);
                break;

            case R.id.nav_logout:
                mAuth.signOut();
                sendUserToStartActivity();
                break;

        }
    }
}