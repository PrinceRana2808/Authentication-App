package com.example.authenticationapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    TextView fname, femail, fphoneno,verifymsg;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userid;
    Button resendcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Set the layout first

        // Initialize views after setting the content view
        fname = findViewById(R.id.textView5);
        femail = findViewById(R.id.textView7);
        fphoneno = findViewById(R.id.textView6);
        verifymsg=findViewById(R.id.verifyemail);
        resendcode=findViewById(R.id.verifybtn);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userid = Objects.requireNonNull(fauth.getCurrentUser()).getUid();
        FirebaseUser user=fauth.getCurrentUser();
        if(!user.isEmailVerified())
        {
            verifymsg.setVisibility(View.VISIBLE);
            resendcode.setVisibility(View.VISIBLE);
            resendcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(view.getContext(), "Verification mail has been sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email not sent"+e.getMessage());
                        }
                    });
                }
            });
        }

        // Reference the Firestore document
        DocumentReference documentReference = fstore.collection("user").document(userid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    // Access the document fields correctly
                    fphoneno.setText(value.getString("phone"));
                    fname.setText(value.getString("name"));
                    femail.setText(value.getString("email"));
                }
            }
        });
    }

    // Logout method
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
}
