package com.example.authenticationapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {
    EditText mfullname,memail,mpassword,mphone;
    Button mregisterbtn,check;
    TextView mloginbtn;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    ProgressBar progressbar;
    String userid;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mfullname=findViewById(R.id.editTextText);
        memail=findViewById(R.id.editTextTextEmailAddress);
        mpassword=findViewById(R.id.editTextTextPassword);
        mphone=findViewById(R.id.editTextPhone);
        mregisterbtn=findViewById(R.id.button);
        mloginbtn=findViewById(R.id.textView3);
        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        progressbar=findViewById(R.id.progressBar);
        mloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });
       if(fauth.getCurrentUser()!=null)
        {
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        mregisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = memail.getText().toString().trim();
                String password = mpassword.getText().toString().trim();
                String fname=mfullname.getText().toString().trim();
                String phoneno=mphone.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    memail.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    memail.setError("Password is Required");
                    return;
                }
                if (password.length() < 6) {
                    memail.setError("Password must be greater or equal to 6");
                    return;


                }
                progressbar.setVisibility(View.VISIBLE);
                fauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser puser=fauth.getCurrentUser();
                            puser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Register.this, "Verification mail has been sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not sent"+e.getMessage());
                                }
                            });
                            Toast.makeText(Register.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                            userid=fauth.getCurrentUser().getUid();
                            DocumentReference documentrefrence=fstore.collection("user").document(userid);
                            Map<String,Object> user=new HashMap<>();
                            user.put("name",fname);
                            user.put("email",email);
                            user.put("phone",phoneno);
                            documentrefrence.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Register.this, "User Profile Created"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            Intent intent = new Intent(Register.this, MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(Register.this, "Error occured"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
