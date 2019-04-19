package com.example.brush;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class AnnouncementPostActivity extends AppCompatActivity {

    private Button a_post;
    private EditText a_description;
    private RadioButton a_category1;
    private RadioButton a_category2;
    private RadioButton a_category3;
    private RadioButton a_category4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_post);


        a_post = (Button) findViewById(R.id.a_post);
        a_description = (EditText) findViewById(R.id.a_description);
        a_category1 = (RadioButton) findViewById(R.id.a_category1);
        a_category2 = (RadioButton) findViewById(R.id.a_category2);
        a_category3 = (RadioButton) findViewById(R.id.a_category3);
        a_category4 = (RadioButton) findViewById(R.id.a_category4);
    }
}