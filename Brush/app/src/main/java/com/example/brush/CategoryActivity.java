package com.example.brush;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class CategoryActivity extends AppCompatActivity {

    private String mPost_key = null;

    private String category = null;

    private RecyclerView mCategoryList;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Gallery");

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
            protected void populateViewHolder(CategoryViewHolder viewHolder, Category model, int position) {
                if(category.equals(model.getCategory()))
                {
                    viewHolder.setDesc(model.getDescription());
                    viewHolder.setUsername(model.getUsername());
                    viewHolder.setPostImage(model.getPostimage());
                    viewHolder.setProfilePicture(model.getUid());
                }
            }
        };

        mCategoryList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            //itemView = mView;

            mView = itemView;
        }

        public void setUsername(String username){

            TextView post_username = (TextView) mView.findViewById(R.id.category_username);
            post_username.setText(username);
        }

        public void setDesc(String desc){
            TextView post_desc = (TextView) mView.findViewById(R.id.category_description);
            post_desc.setText(desc);
        }

        public void setPostImage(String postImage)
        {
            ImageView category_picture = (ImageView) mView.findViewById(R.id.category_image);
            Picasso.get().load(postImage).into(category_picture);

        }

        public void setProfilePicture(String uid)
        {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile images/" + uid);

            String profile_picture = storageReference.getDownloadUrl().toString();

            Log.d("3333", "profilepicturelink " + storageReference.getDownloadUrl());

            ImageView category_profile_picture = (ImageView) mView.findViewById(R.id.category_profilepicture);

            Picasso.get().load(profile_picture).into(category_profile_picture);
        }

    }
}
