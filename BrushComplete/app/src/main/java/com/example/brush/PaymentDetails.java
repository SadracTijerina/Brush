package com.example.brush;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class PaymentDetails extends AppCompatActivity
{
    TextView txtId, txtAmount, txtStatus;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        txtId = findViewById(R.id.txtId);
        txtAmount = findViewById(R.id.txtAmount);
        txtStatus = findViewById(R.id.txtStatus);


        mToolbar = findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Payment Process");


        //Get Intent
        Intent intent = getIntent();

        try
        {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"),intent.getStringExtra("PaymentAmount"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void showDetails(JSONObject response, String paymentAmount)
    {
        try
        {
            txtId.setText(response.getString("id"));
            txtStatus.setText(response.getString("state"));
            txtId.setText("$"+paymentAmount);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }


    }
}

