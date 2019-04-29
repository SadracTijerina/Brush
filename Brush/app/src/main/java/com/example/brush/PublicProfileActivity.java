package com.example.brush;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PublicProfileActivity extends AppCompatActivity {

    private ImageButton userProfileImage, userMessage, userUpdates, userGallery, userShop;
    private TextView userProfileName, username, userBio;

    private DatabaseReference profileRef;
    private FirebaseAuth mAuth;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

        String userID = getIntent().getStringExtra("userID");
        mAuth = FirebaseAuth.getInstance();
        profileRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        userProfileImage = (ImageButton) findViewById(R.id.public_profile_default_pic);
        userMessage = (ImageButton) findViewById(R.id.public_profile_message);
        userUpdates = (ImageButton) findViewById(R.id.public_profile_updates);
        userGallery = (ImageButton) findViewById(R.id.public_profile_gallery);
        userShop = (ImageButton) findViewById(R.id.public_profile_shop);


        userProfileName = (TextView) findViewById(R.id.public_profile_full_name);
        username = (TextView) findViewById(R.id.public_profile_username);
        userBio = (TextView) findViewById(R.id.public_profile_bio);

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String myProfileImage = dataSnapshot.child("profilePicture").getValue().toString();
                    String myUserName = dataSnapshot.child("Username").getValue().toString();
                    String myProfileName = dataSnapshot.child("Name").getValue().toString();
                    String myBio = dataSnapshot.child("Bio").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.dfp).into(userProfileImage);
                    userProfileName.setText(myProfileName);
                    username.setText("@" + myUserName);
                    userBio.setText(myBio);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToMessageActivity();
            }
        });

        userUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToUpdatesActivity();
            }
        });

        userGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToGalleryActivity();
            }
        });

        userShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToShopActivity();
            }
        });
    }

    private void SendUserToMessageActivity() {
        //Intent intent = new Intent(UserProfileActivity.this, MessageActivity.class);
        //startActivity(intent);
    }

    private void SendUserToUpdatesActivity() {
        //Intent intent = new Intent(UserProfileActivity.this, UpdatesActivity.class);
        //startActivity(intent);
    }

    private void SendUserToGalleryActivity() {
        //Intent intent = new Intent(UserProfileActivity.this, GalleryActivity.class);
        //startActivity(intent);
    }

    private void SendUserToShopActivity() {
        //Intent intent = new Intent(UserProfileActivity.this, ShopActivity.class);
        //startActivity(intent);
    }
}
