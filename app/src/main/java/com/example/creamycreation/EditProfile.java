package com.example.creamycreation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfile extends AppCompatActivity {

    EditText registerFullName,registerEmail,registerPassword,registerConPass,registerPhone,registerAddress;
    Button saveProfileBtn;
    FirebaseUser user;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        registerFullName = findViewById(R.id.newName);
        registerEmail = findViewById(R.id.emailAddress);
        registerPassword = findViewById(R.id.newPassword);
        registerConPass = findViewById(R.id.confirmPassword);
        registerPhone = findViewById(R.id.newPhone);
        registerAddress = findViewById(R.id.newAddress);

        saveProfileBtn = findViewById(R.id.saveProfileBtn);
        user = FirebaseAuth.getInstance().getCurrentUser();


        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (registerFullName.getText().toString().isEmpty()){
                    registerFullName.setError("Full Name Is Required");
                    return;
                }
                if (registerEmail.getText().toString().isEmpty()){
                    registerEmail.setError("Email Is Required");
                    return;
                }
                if (registerEmail.getText().toString().isEmpty()){
                    registerEmail.setError("Password Field Is Required");
                    return;
                }
                if (registerConPass.getText().toString().isEmpty()){
                    registerConPass.setError("Confirm Password Field Is Required");
                    return;
                }
                if (registerPhone.getText().toString().isEmpty()){
                    registerPhone.setError("Phone Is Required");
                    return;
                }
                if (registerAddress.getText().toString().isEmpty()){
                    registerAddress.setError("Address Is Required");
                    return;
                }  user.updateEmail(saveProfileBtn.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditProfile.this,"Password Updated",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}