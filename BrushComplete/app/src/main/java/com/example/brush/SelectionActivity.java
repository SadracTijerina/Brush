package com.example.brush;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectionActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private CircleImageView SelectionProfileImage;
    private TextView Selection_Username;
    private TextView Selection_description;
    private TextView Selection_price;
    private ImageView Selection_image;
    private Button Selection_buy_now;
    private Button Selection_place_bid;
    private String postName;
    private String date;
    private String time;
    private String price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);



        SelectionProfileImage = (CircleImageView) findViewById(R.id.selection_profilepicture);
        Selection_Username = (TextView) findViewById(R.id.selection_username);
        Selection_image = (ImageView) findViewById(R.id.selection_image);
        Selection_description = (TextView) findViewById(R.id.selection_description);
        Selection_price = (TextView) findViewById(R.id.selection_bid_price);
        Selection_buy_now = (Button) findViewById(R.id.selection_buy_now);
        Selection_place_bid = (Button) findViewById(R.id.selection_place_bid);

        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username");
        String profile_image = extras.getString("profilePicture");
        String image = extras.getString("postImage");
        String description = extras.getString("description");
        String date = extras.getString("date");
        String time = extras.getString("time");

        price = extras.getString("price");
        postName = extras.getString("uid");

        Selection_Username.setText(username);
        Selection_description.setText(description);
        Selection_price.setText("$"+price);


        Picasso.get().load(profile_image).into(SelectionProfileImage);
        Picasso.get().load(image).into(Selection_image);



        mToolbar = findViewById(R.id.selection_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        Selection_buy_now.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SendtoConfig();
            }
        });

        Selection_place_bid.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SendtoPlaceBid();
            }
        });
    }


    private void SendtoPlaceBid()
    {
        Intent intent = new Intent (SelectionActivity.this,PlaceBidActivity.class );
        Bundle extras = new Bundle();
        extras.putString("postRandomName", postName);
        extras.putString("date", date);
        extras.putString("time", time);
        extras.putString("price", price);

        intent.putExtras(extras);
        startActivity(intent);
    }

    private void SendtoConfig()
    {
        Intent intent = new Intent (SelectionActivity.this,ConfigActivity.class );
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int id= item.getItemId();
        if(id == android.R.id.home)
        {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent (SelectionActivity.this,MainActivity.class );
        startActivity(mainIntent);
    }
}
