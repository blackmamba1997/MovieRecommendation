package com.example.movierecommendation.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.movierecommendation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText new_email,new_pass,confirm_pass;
    Button signup;
    ProgressBar load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        new_email = findViewById(R.id.new_email);
        new_pass = findViewById(R.id.new_password);
        confirm_pass = findViewById(R.id.password_confirm);
        signup = findViewById(R.id.signupbutton);
        load = findViewById(R.id.loading);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = new_email.getText().toString();
                String pass = new_pass.getText().toString();
                String cpass = confirm_pass.getText().toString();
                boolean cemail = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE).matcher(username).matches();
                System.out.println(username+" "+pass);
                if (cemail && pass.equals(cpass)) {

                    signup.setVisibility(View.INVISIBLE);
                    load.setVisibility(View.VISIBLE);
                    auth.createUserWithEmailAndPassword(username, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        auth.signOut();
                                        Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                        finish();

                                    } else {

                                        signup.setVisibility(View.VISIBLE);
                                        load.setVisibility(View.INVISIBLE);
                                        Toast.makeText(RegisterActivity.this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println(e);
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Something is Wrong. Check your Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
