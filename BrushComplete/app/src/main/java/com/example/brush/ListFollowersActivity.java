package com.example.brush;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListFollowersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView listFollowers;
    private DatabaseReference followersRef, userRef, visitingUserRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String online_user_id;
    private CircleImageView list_followers_picture;
    private TextView list_followers_username, list_followers_fullname;

    ArrayList<String> IDList = new ArrayList<>();

    private String TAG = "-66666666";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_followers);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        followersRef = FirebaseDatabase.getInstance().getReference().child("Followers").child(online_user_id).child("followers");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = findViewById(R.id.list_followers_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listFollowers = findViewById(R.id.list_followers_recycler);
        //list_followers_picture = (CircleImageView) findViewById(R.id.list_followers_pic);
        //list_followers_username = (TextView) findViewById(R.id.list_followers_un);
        //list_followers_fullname = (TextView) findViewById(R.id.list_followers_fn);

        listFollowers.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        listFollowers.setLayoutManager(linearLayoutManager);

        DisplayAllFollowers();
    }

    private void DisplayAllFollowers(){

        FirebaseRecyclerAdapter<Followers, FollowersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Followers, FollowersViewHolder>
                (
                        Followers.class,
                        R.layout.list_followers,
                        FollowersViewHolder.class,
                        followersRef
                )
        {
            @Override
            protected void populateViewHolder(final FollowersViewHolder viewHolder, Followers model, final int position) {

                final String usersIDs = getRef(position).getKey();

                userRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {
                            final String userName = dataSnapshot.child("Username").getValue().toString();
                            final String fullName = dataSnapshot.child("Name").getValue().toString();
                            final String profilePic = dataSnapshot.child("profilePictureLink").getValue().toString();

                            viewHolder.setFullname(fullName);
                            viewHolder.setUsername(userName);
                            viewHolder.setProfileImage(profilePic);

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    CharSequence[] options = new CharSequence[]{

                                            "Go to " + userName + "'s Profile",
                                            "Send " + userName + " a message"
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(ListFollowersActivity.this);
                                    builder.setTitle("Choose an Option");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            if(i == 0)
                                            {
                                                Intent profileIntent = new Intent(ListFollowersActivity.this, PublicProfileActivity.class);
                                                profileIntent.putExtra("userID", usersIDs);
                                                startActivity(profileIntent);
                                            }
                                            if(i == 1)
                                            {
                                                Intent chatIntent = new Intent(ListFollowersActivity.this, ChatActivity.class);
                                                chatIntent.putExtra("userID", usersIDs);
                                                chatIntent.putExtra("userName", userName);
                                                startActivity(chatIntent);
                                            }
                                        }
                                    });

                                    builder.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        listFollowers.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FollowersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FollowersViewHolder(View itemView){

            super(itemView);
            mView = itemView;
        }

        public void setFullname(String fullname){

            TextView myName = mView.findViewById(R.id.list_followers_fn);
            myName.setText(fullname);
        }

        public void setUsername(String username){

            TextView uName = mView.findViewById(R.id.list_followers_un);
            uName.setText("@" + username);
        }

        public void setProfileImage(String profileImage){

            CircleImageView myImage = mView.findViewById(R.id.list_followers_pic);
            Picasso.get().load(profileImage).into(myImage);
        }
    }
}
