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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText new_email,new_pass,confirm_pass;
    Button signup;
    ProgressBar load;
    FirebaseFirestore db;

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

        db=FirebaseFirestore.getInstance();
        System.out.println(db);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = new_email.getText().toString();
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

                                        Map<String,Object> map=new HashMap<>();
                                        map.put("email",username);
                                        Map<String,Object> map2=new HashMap<>();
                                        String[] genresname={"Action","Adventure","Animation","Comedy","Crime","Documentary","Drama","Family","Fantasy","History","Horror","Music","Mystery","Romance","Science Fiction","TV Movie","Thriller","War","Western"};
                                        for (int i=0;i<genresname.length;i++){
                                            map2.put(genresname[i],"0");
                                        }
                                        map.put("genre",map2);
                                        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        auth.signOut();
                                                        Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                                        finish();

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });


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
