package com.example.brush;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText userInput;
    private Button button;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        userInput = (EditText) findViewById(R.id.forgot_password_input);
        button = (Button) findViewById(R.id.forgot_password_button);
        toolbar = findViewById(R.id.forgot_password_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Password");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String email = userInput.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Please input an email", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ForgotPasswordActivity.this, "Email sent to reset your password", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivty.class));
                            }
                            else
                            {
                                String error = task.getException().getMessage();
                                Toast.makeText(ForgotPasswordActivity.this, "Error Occured: " + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
