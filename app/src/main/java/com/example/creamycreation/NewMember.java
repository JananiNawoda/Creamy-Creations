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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class NewMember extends AppCompatActivity {

    EditText registerFullName,registerEmail,registerPassword,registerConPass,registerPhone,registerAddress;
    Button registerUserBtn,gotoLogin;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member);

        registerFullName = findViewById(R.id.newName);
        registerEmail = findViewById(R.id.emailAddress);
        registerPassword = findViewById(R.id.newPassword);
        registerConPass = findViewById(R.id.confirmPassword);
        registerPhone = findViewById(R.id.newPhone);
        registerAddress = findViewById(R.id.newAddress);
        registerUserBtn = findViewById(R.id.registerbtn);
        gotoLogin =findViewById(R.id.gotoLogin);




        fAuth = FirebaseAuth.getInstance();

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });

        registerUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //extract data

                String fullName = registerFullName.getText().toString();
                String email = registerEmail.getText().toString();
                String password = registerPassword.getText().toString();
                String conPass = registerConPass.getText().toString();
                String phone = registerPhone.getText().toString();
                String address = registerAddress.getText().toString();

                if (fullName.isEmpty()){
                    registerFullName.setError("Full Name Is Required");
                    return;
                }
                if (email.isEmpty()){
                    registerEmail.setError("Email Is Required");
                    return;
                }
                if (password.isEmpty()){
                    registerEmail.setError("Password Field Is Required");
                    return;
                }
                if (conPass.isEmpty()){
                    registerConPass.setError("Confirm Password Field Is Required");
                    return;
                }
                if (phone.isEmpty()){
                    registerPhone.setError("Phone Is Required");
                    return;
                }
                if (address.isEmpty()){
                    registerAddress.setError("Address Is Required");
                    return;
                }
                if(!password.equals(conPass)){
                    registerConPass.setError("Password Not Same");
                    return;
                }
                //register the customer using firebase
                Toast.makeText(NewMember.this, "Data Added", Toast.LENGTH_SHORT).show();
                
                fAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //send user to next page
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewMember.this,e.getMessage(),Toast.LENGTH_SHORT ).show();
                    }
                });


            }
        });
    }
}