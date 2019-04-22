package com.example.brush;

import android.net.Uri;
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
import android.widget.EditText;

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

    ArrayList<String> fullNameList;
    ArrayList<String> usernameList;
    ArrayList<String> profilePicList;

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

        fullNameList = new ArrayList<>();
        usernameList = new ArrayList<>();
        profilePicList = new ArrayList<>();


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
                if (!s.toString().isEmpty())
                {
                    setAdapter(s.toString());
                }
                else {

                    fullNameList.clear();
                    usernameList.clear();
                    profilePicList.clear();
                    searchResultList.removeAllViews();

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
                profilePicList.clear();
                searchResultList.removeAllViews();

                int counter = 0;
                int i = 0;

                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    i++;

                    String uid = snapshot.getKey();
                    String name = snapshot.child("Name").getValue(String.class);
                    String username = snapshot.child("Username").getValue(String.class);
                    final String profilePic = snapshot.child("profilePicture").getValue(String.class);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    storageReference = storage.getReference();
                    
                    if (username.toLowerCase().contains(searchedString.toLowerCase()))
                    {
                        fullNameList.add(name);
                        usernameList.add(username);

                        storageReference.child("profile images/" + profilePic).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // uri is the link, we just have to change it to a string
                                String Picture = uri.toString();
                                Log.d(TAG, "Picture: "+ Picture);
                                profilePicList.add(Picture);
                                Log.d(TAG, "profilePicListSize " + profilePicList.size());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Log.d(TAG, "onFailure:");
                            }
                        });

                        counter++;
                    }

                    if(i == dataSnapshot.getChildrenCount() )
                        break;

                    if(counter == 20)
                        break;

                }

                Log.d(TAG, "profilePicListSizeeee: " + profilePicList.size());
                Log.d(TAG, "fullnamesize: " + fullNameList.size());
                Log.d(TAG, "usernamesize: " + usernameList.size());

                searchAdapter = new SearchAdapter(SearchUsersActivity.this, fullNameList, usernameList, profilePicList);
                searchResultList.setAdapter(searchAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
