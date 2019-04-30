package com.example.brush;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryActivity extends AppCompatActivity {


    private String category = null;

    private RecyclerView mCategoryList;

    private DatabaseReference mDatabase;

    private String TAG = "33333";

    private CircleImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);


        profilePic = (CircleImageView) findViewById(R.id.category_profilepicture);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");

        mCategoryList = (RecyclerView) findViewById(R.id.category_list);
        mCategoryList.setHasFixedSize(true);
        mCategoryList.setLayoutManager(new LinearLayoutManager(this));

        category = getIntent().getExtras().getString("Category");


    }

    protected void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<Category, CategoryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>
                (
                     Category.class,
                        R.layout.category_post,
                        CategoryViewHolder.class,
                        mDatabase
                )
        {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, Category model, int position)
            {

                if(category.equals(model.getCategory()) && model.getPostType().equals("gallery"))
                {
                    Log.d(TAG, "populateViewHolder: ");
                    viewHolder.setDesc(model.getDescription());
                    viewHolder.setUsername(model.getUsername());
                    viewHolder.setPostImage(model.getPostimage());
                    viewHolder.setProfilePicture(model.getProfilePicture());
                }
                else
                {
                    viewHolder.setDesc("null");
                    viewHolder.setPostImage("null");
                    viewHolder.setProfilePicture("null");
                    viewHolder.setUsername("null");

                }
            }
        };

        mCategoryList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CategoryViewHolder extends ViewHolder{

        View mView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String username)
        {
            TextView post_username = (TextView) mView.findViewById(R.id.category_username);


            if(!username.equals("null")) {
                post_username.setText(username);
            }
            else
            {
                mView.setVisibility(View.GONE);
            }
        }

        public void setDesc(String desc)
        {
            TextView post_desc = (TextView) mView.findViewById(R.id.category_description);

            if(!desc.equals("null")) {
                post_desc.setText(desc);
            }
            else
            {
                mView.setVisibility(View.GONE);
            }
        }

        public void setPostImage(String postImage)
        {
            ImageView category_picture = (ImageView) mView.findViewById(R.id.category_image);
            CardView cardView = (CardView) mView.findViewById(R.id.category_post);

            if(!postImage.equals("null")) {
                Picasso.get().load(postImage).into(category_picture);
            }
            else
            {
                mView.setVisibility(View.GONE);

            }

        }

        public void setProfilePicture(String profilePicture)
        {
            ImageView category_profile_picture = (ImageView) mView.findViewById(R.id.category_profilepicture);

            if(!profilePicture.equals("null")) {
                Picasso.get().load(profilePicture).into(category_profile_picture);
            }
            else
            {
                mView.setVisibility(View.GONE);
            }
        }
    }
}
