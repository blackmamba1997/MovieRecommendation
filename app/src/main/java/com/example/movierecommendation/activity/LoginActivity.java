package com.example.movierecommendation.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.movierecommendation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton ;
    ProgressBar loadingProgressBar ;
    FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth= FirebaseAuth.getInstance();
        usernameEditText = findViewById(R.id.username);
        passwordEditText= findViewById(R.id.password);
        loginButton=findViewById(R.id.login);
        loadingProgressBar= findViewById(R.id.loading);
        final String username=usernameEditText.getText().toString();
        final String password=usernameEditText.getText().toString();
        boolean correctuser= Pattern.matches("[a-zA-Z0-9._]@[a-zA-Z][.]com",username);
        boolean correctpass= Pattern.matches("[a-zA-Z0-9._#]",password);
        if(correctuser && correctpass){

            mAuth.signInWithEmailAndPassword(username,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(//task successful){

                            }else{

                            }
                        }
                    });
        }



    }
}
