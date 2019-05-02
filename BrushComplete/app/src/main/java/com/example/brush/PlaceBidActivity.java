package com.example.brush;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PlaceBidActivity extends AppCompatActivity {

    private ImageView bid;
    private Button placeBid;
    private EditText bAmount;
    private Toolbar mToolbar;
    private TextView price;
    private TextView bid_price_fb;
    private DatabaseReference BidRef;
    private DatabaseReference PostRef;
    private FirebaseAuth mAuth;

    private String current_user_id;
    private String postName;
    private String amount_cost;
    private String current_bid;
    private ProgressDialog loadingbar;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postTime;
    private String postDate;
    private int new_bid;

    String amount;

    // include in build gradle implementation'com.paypal.sdk:paypal-android-sdk:2.15.3' change minSdkVersion to 16
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_bid);

        bid = (ImageView) findViewById(R.id.bid);
        placeBid = (Button) findViewById(R.id.placeBid);
        bAmount = (EditText) findViewById(R.id.bAmount);
        price = (TextView) findViewById(R.id.price);
        bid_price_fb = (TextView) findViewById(R.id.bid_price_fb);

        loadingbar = new ProgressDialog(this);

        mToolbar = findViewById(R.id.placebid_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Place New Bid");

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        Bundle extras = getIntent().getExtras();
        postName= extras.getString("postRandomName");
        amount_cost= extras.getString("price");
        bid_price_fb.setText(amount_cost);
        postDate= extras.getString("date");
        postTime= extras.getString("time");



        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postName);
        BidRef = FirebaseDatabase.getInstance().getReference().child("Bid").child(postName+postDate+postTime);

        Log.d(postName,"post name");

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        /*PostRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                amount_cost = dataSnapshot.child("price").getValue().toString();
                bid_price_fb.setText(amount_cost);

                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                saveCurrentDate = currentDate.format(calForDate.getTime());
                Calendar calForTime = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                saveCurrentTime = currentTime.format(calForTime.getTime());
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });*/

        placeBid.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                proccessNewBid();
            }
        });

    }

    private void proccessNewBid()
    {
        amount = bAmount.getText().toString();
        if(TextUtils.isEmpty(amount))
        {
            Toast.makeText(this, "Please enter amount of bid...", Toast.LENGTH_SHORT).show();
        }

            Log.d(current_bid,"current bid");
            Log.d(amount_cost,"amount cost");
            Log.d(amount,"amount");

            int lowest_amount = Integer.parseInt(amount_cost);
            new_bid = Integer.parseInt(amount);

            if(new_bid>lowest_amount)
            {
                loadingbar.setTitle("Adding bid");
                loadingbar.setMessage("Please wait, while we are placing your bid");
                loadingbar.show();
                loadingbar.setCanceledOnTouchOutside(true);
                StoreBid();
            }
            else
            {
                Toast.makeText(PlaceBidActivity.this,"This bid is not higher then another user's bid. Sorry, please make a new one if you would like the item..",Toast.LENGTH_SHORT);
            }
        }


    private void StoreBid()
    {
        HashMap newBidMap = new HashMap();

        newBidMap.put("uid",current_user_id);
        newBidMap.put("bid",new_bid);
        newBidMap.put("postName",postName);
        newBidMap.put("time",saveCurrentTime);
        newBidMap.put("price",amount_cost);

        BidRef.updateChildren(newBidMap).addOnCompleteListener(new OnCompleteListener()
        {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {
                    SendUserToMainActivity();
                    Toast.makeText(PlaceBidActivity.this, "New bid was created successfully",Toast.LENGTH_SHORT);
                    loadingbar.dismiss();
                }
                else
                {
                    Toast.makeText(PlaceBidActivity.this, "Error occurred while placing bid ",Toast.LENGTH_SHORT);
                    loadingbar.dismiss();
                }
            }
        });
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
        Intent mainIntent = new Intent (PlaceBidActivity.this,MainActivity.class );
        startActivity(mainIntent);
    }
}

