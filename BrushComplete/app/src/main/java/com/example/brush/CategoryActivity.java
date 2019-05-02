package com.example.brush;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
public class CategoryActivity extends AppCompatActivity {
    private String category = null;
    private RecyclerView mCategoryList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String TAG = "33333";
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mAuth = FirebaseAuth.getInstance();
        mToolbar = (Toolbar) findViewById(R.id.category_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mCategoryList = (RecyclerView) findViewById(R.id.category_list);
        mCategoryList.setHasFixedSize(true);
        mCategoryList.setLayoutManager(new LinearLayoutManager(this));
        category = getIntent().getExtras().getString("Category");
        if(category.equals("Digital"))
        {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Categories").child("d");
        }
        else if(category.equals("Traditional"))
        {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Categories").child("t");
        }
        else if(category.equals("Photography"))
        {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Categories").child("p");
        }
        else
        {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Categories").child("a");
        }
    }
    protected void onStart(){
        super.onStart();
        final FirebaseRecyclerAdapter<Category, CategoryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>
                (
                        Category.class,
                        R.layout.category_post,
                        CategoryViewHolder.class,
                        mDatabase
                )
        {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, final Category model, int position) {
                viewHolder.setDesc(model.getDescription());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setPostImage(model.getPostimage());
                viewHolder.setProfilePicture(model.getProfilePicture());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent profileIntent = new Intent(CategoryActivity.this, PublicProfileActivity.class);
                        profileIntent.putExtra("userID", model.getUid());
                        startActivity(profileIntent);
                    }
                });
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
            post_username.setText(username);
        }
        public void setDesc(String desc)
        {
            TextView post_desc = (TextView) mView.findViewById(R.id.category_description);
            post_desc.setText(desc);
        }
        public void setPostImage(String postImage)
        {
            ImageView category_picture = (ImageView) mView.findViewById(R.id.category_image);
            Picasso.get().load(postImage).into(category_picture);
        }
        public void setProfilePicture(String profilePicture)
        {
            CircleImageView category_profile_picture = (CircleImageView) mView.findViewById(R.id.category_profilepicture);
            Picasso.get().load(profilePicture).into(category_profile_picture);
        }
    }
}
