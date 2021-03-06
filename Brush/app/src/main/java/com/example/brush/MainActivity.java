package com.example.brush;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
    private DatabaseReference userRef;
    private StorageReference storageRef;
    private FirebaseStorage storage;

    String currentUserID;
    String TAG = "333";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);


        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavUserName = (TextView) navView.findViewById(R.id.nav_username);


        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("Name"))
                    {
                        //This is getting the users name and username from database.
                        // Make sure the child matches the databases names
                        String name = dataSnapshot.child("Name").getValue().toString();

                        //This is displaying the users name in the navigation bard
                        NavUserName.setText(name);
                    }
                    if(dataSnapshot.hasChild("profilePicture"))
                    {
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
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                UserMenuSelector(item);
                return false;
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null)
        {
            sendUserToStartActivity();
        }
        else
        {
            CheckUserExistence();
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
                Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
;
                break;

            case R.id.nav_profile:
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(intent);
                Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_post:
                Intent newPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(newPostIntent);
                Toast.makeText(this, "post", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_followers:
                Toast.makeText(this, "followers", Toast.LENGTH_SHORT).show();
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