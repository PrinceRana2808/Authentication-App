package com.example.authenticationapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
EditText lemail,lpassword;
Button login;
TextView signin,fgtpass;
ProgressBar progressbar;
FirebaseAuth fauth;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signin=findViewById(R.id.textView3);
        lemail=findViewById(R.id.editTextTextEmailAddress);
        lpassword=findViewById(R.id.editTextTextPassword);
        fgtpass=findViewById(R.id.textView8);
        login=findViewById(R.id.button);
        fauth= FirebaseAuth.getInstance();
        progressbar=findViewById(R.id.progressBar2);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = lemail.getText().toString().trim();
                String password = lpassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    lemail.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    lemail.setError("Password is Required");
                    return;
                }
                if (password.length() < 6) {
                    lemail.setError("Password must be greater or equal to 6");
                    return;


                }
                progressbar.setVisibility(View.VISIBLE);
                fauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful())
                      {
                          Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                          Intent intent = new Intent(Login.this, MainActivity.class);
                          startActivity(intent);
                      }
                      else{
                          Toast.makeText(Login.this, "Error occured"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                      }
                    }
                });
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
        fgtpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetmail=new EditText(view.getContext());
                AlertDialog.Builder passwordresetdialog=new AlertDialog.Builder(view.getContext());
                passwordresetdialog.setTitle("Reset Password?");
                passwordresetdialog.setMessage("Enter your email to reset password");
                passwordresetdialog.setView(resetmail);
                passwordresetdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      String mail=resetmail.getText().toString();
                      fauth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void unused) {
                              Toast.makeText(Login.this, "Reset Link Sent to your mail", Toast.LENGTH_SHORT).show();
                          }
                      }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              Toast.makeText(Login.this, "Error!Reset Link is not Sent"+e.getMessage(), Toast.LENGTH_SHORT).show();
                          }
                      });
                    }
                });
                passwordresetdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Login.this, Login.class);
                        startActivity(intent);
                    }
                });
                passwordresetdialog.create().show();
            }
        });



    }

}