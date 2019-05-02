package com.example.brush;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import com.squareup.picasso.Picasso;


public class ClickPostActivity extends AppCompatActivity
{
    private ImageView image;
    private TextView description;
    private Button edit;
    private Button delete;
    private Toolbar mToolbar;

    private DatabaseReference CPostRef;
    private FirebaseAuth mAuth;

    private String PostKey;
    private String current_user_id;
    private String database_u_id;
    private String pdescription;
    private String pimage;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        CPostRef = FirebaseDatabase.getInstance().getReference().child("Gallery").child(PostKey);


        image = findViewById(R.id.clickp_image);
        description = findViewById(R.id.clickp_description);
        edit = findViewById(R.id.clickp_edit);
        delete = findViewById(R.id.clickp_delete);

        mToolbar = findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Feed");

        delete.setVisibility(View.INVISIBLE);
        edit.setVisibility(View.INVISIBLE);


        CPostRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists()) {
                    pdescription = dataSnapshot.child("description").getValue().toString();
                    pimage = dataSnapshot.child("postimage").getValue().toString();
                    database_u_id = dataSnapshot.child("uid").getValue().toString();

                    if (current_user_id.equals(database_u_id)) {
                        delete.setVisibility(View.VISIBLE);
                        edit.setVisibility(View.VISIBLE);
                    }
                    description.setText(pdescription);
                    Picasso.get().load(pimage).into(image);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DeleteCurrentPost();
            }
        });

        edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditCurrentPost(pdescription);
            }
        });


    }

    private void EditCurrentPost(String pdescription)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post");

        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(pdescription);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                CPostRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this, "Post edited successfully", Toast.LENGTH_SHORT);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
    }

    private void DeleteCurrentPost()
    {
        CPostRef.removeValue();
        Toast.makeText(this,"Post deleted successfully",Toast.LENGTH_SHORT);
        sendUserToMainActivity();
    }

    private void sendUserToMainActivity()
    {
        Intent mainIntent = new Intent(ClickPostActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
