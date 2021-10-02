package com.example.creamycreation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;

public class Reset extends AppCompatActivity {
    EditText userPhone,userConPass;
    Button savePasswordBtn;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        userPhone = findViewById(R.id.newResetPhone);
        userConPass = findViewById(R.id.confirmResetPhone);
        user = FirebaseAuth.getInstance().getCurrentUser();

        savePasswordBtn = findViewById(R.id.change);
        savePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userPhone.getText().toString().isEmpty()){
                    userPhone.setError("Required Field!");
                    return;
                }
                if(userConPass.getText().toString().isEmpty()){
                    userConPass.setError("Required Field!");
                    return;
                }
                if(userPhone.getText().toString().equals(userConPass.getText().toString())){
                    userConPass.setError("Password Do Not Match");
                    return;
                }
                user.updatePhoneNumber((PhoneAuthCredential) userPhone.getText()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Reset.this,"Phone Number Updated",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Reset.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}