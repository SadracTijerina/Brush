package com.example.brush;

import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SearchUsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText inputText;

    private RecyclerView searchResultList;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;

    private ImageButton searchDigital, searchArtisan, searchPhotography, searchTraditional;
    private TextView textViewCategories;

    ArrayList<String> fullNameList;
    ArrayList<String> usernameList;
    ArrayList<String> userID;

    SearchAdapter searchAdapter;



    String TAG = "333";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        searchResultList = (RecyclerView) findViewById(R.id.search_result_list);

        inputText = (EditText) findViewById(R.id.search_box);

        searchDigital = (ImageButton) findViewById(R.id.search_digital);
        searchArtisan = (ImageButton) findViewById(R.id.search_artisan);
        searchPhotography = (ImageButton) findViewById(R.id.search_photography);
        searchTraditional = (ImageButton) findViewById(R.id.search_traditional);
        textViewCategories = (TextView) findViewById(R.id.categories_textview);



        fullNameList = new ArrayList<>();
        usernameList = new ArrayList<>();
        userID = new ArrayList<>();


        mToolbar = (Toolbar) findViewById(R.id.search_users_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));
        searchResultList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        searchDigital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show a feed of art related to that category
                Log.d(TAG, "search digital ");
            }
        });

        searchTraditional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show a feed of art related to that category
                Log.d(TAG, "search traditonal ");
            }
        });

        searchPhotography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show a feed of art related to that category
                Log.d(TAG, "search photography ");

            }
        });

        searchArtisan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show a feed of art related to that category
                Log.d(TAG, "search artisan ");
            }
        });


        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                {
                    searchDigital.setVisibility(View.GONE);
                    searchArtisan.setVisibility(View.GONE);
                    searchPhotography.setVisibility(View.GONE);
                    searchTraditional.setVisibility(View.GONE);
                    textViewCategories.setVisibility(View.GONE);
                    setAdapter(s.toString());
                    Search();
                }
                else {
                    fullNameList.clear();
                    usernameList.clear();
                    searchResultList.removeAllViews();

                    searchDigital.setVisibility(View.VISIBLE);
                    searchArtisan.setVisibility(View.VISIBLE);
                    searchPhotography.setVisibility(View.VISIBLE);
                    searchTraditional.setVisibility(View.VISIBLE);
                    textViewCategories.setVisibility(View.VISIBLE);

                }
            }
        });

    }

    private void setAdapter(final String searchedString)
    {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                fullNameList.clear();
                usernameList.clear();
                userID.clear();
                searchResultList.removeAllViews();

                int counter = 0;
                int i = 0;

                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    i++;

                    String uid = snapshot.getKey();
                    String name = snapshot.child("Name").getValue(String.class);
                    String username = snapshot.child("Username").getValue(String.class);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    storageReference = storage.getReference();

                    if (username.toLowerCase().contains(searchedString.toLowerCase()))
                    {
                        fullNameList.add(name);
                        usernameList.add(username);
                        userID.add(uid);
                        counter++;
                    }


                    if(i == dataSnapshot.getChildrenCount() )
                        break;

                    if(counter == 20)
                        break;
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void Search() {
        searchAdapter = new SearchAdapter(SearchUsersActivity.this, fullNameList, usernameList, userID);
        searchResultList.setAdapter(searchAdapter);
    }
}