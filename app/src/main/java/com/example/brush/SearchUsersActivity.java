package com.example.brush;

import android.content.Context;
import android.media.Image;
import android.provider.ContactsContract;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private ImageButton searchButton;
    private EditText inputText;

    private RecyclerView searchResultList;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    ArrayList<String> fullNameList;
    ArrayList<String> usernameList;
    ArrayList<String> profilPicList;

    SearchAdapter searchAdapter;



    String TAG = "333";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        searchResultList = (RecyclerView) findViewById(R.id.search_result_list);


        searchButton = (ImageButton) findViewById(R.id.search_user_button);
        inputText = (EditText) findViewById(R.id.search_box);

        fullNameList = new ArrayList<>();
        usernameList = new ArrayList<>();
        profilPicList = new ArrayList<>();


        mToolbar = (Toolbar) findViewById(R.id.search_users_bar_layout);
        setSupportActionBar(mToolbar);

        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));
        searchResultList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: In function");
                if (!s.toString().isEmpty()) {
                    Log.d(TAG, "afterTextChanged: In if");

                    setAdapter(s.toString());
                } else {
                    Log.d(TAG, "afterTextChanged: In else");

                    fullNameList.clear();
                    usernameList.clear();
                    profilPicList.clear();
                    searchResultList.removeAllViews();

                }
            }
        });

    }

    private void setAdapter(final String searchedString)
    {
        Log.d(TAG, "setAdapter: In function");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Beginning");

                Log.d(TAG, "dataSnapshot: " + dataSnapshot.getChildrenCount());

                fullNameList.clear();
                usernameList.clear();
                profilPicList.clear();
                searchResultList.removeAllViews();

                int counter = 0;
                int i = 0;

                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    i++;

                    String uid = snapshot.getKey();
                    String name = snapshot.child("Name").getValue(String.class);
                    String username = snapshot.child("Username").getValue(String.class);
                    String profilePic = snapshot.child("profilePicture").getValue(String.class);

                    if(name.toLowerCase().contains(searchedString.toLowerCase()))
                    {
                        fullNameList.add(name);
                        usernameList.add(username);
                        profilPicList.add(profilePic);
                        counter++;
                    }
                    else if (username.toLowerCase().contains(searchedString.toLowerCase()))
                    {
                        fullNameList.add(name);
                        usernameList.add(username);
                        profilPicList.add(profilePic);
                        counter++;
                    }

                    if(i == dataSnapshot.getChildrenCount() )
                        break;

                    if(counter == 20)
                        break;

                }

                searchAdapter = new SearchAdapter(SearchUsersActivity.this, fullNameList, usernameList, profilPicList);
                searchResultList.setAdapter(searchAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
