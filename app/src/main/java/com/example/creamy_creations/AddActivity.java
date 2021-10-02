package com.example.creamy_creations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    EditText name,size,price,image;
    Button btnAdd,btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        name = (EditText)findViewById(R.id.txtName);
        size = (EditText)findViewById(R.id.txtSize);
        price = (EditText)findViewById(R.id.txtPrice);
        image = (EditText)findViewById(R.id.txtimgurl);

        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnBack = (Button)findViewById(R.id.btnBack);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
                clearAll();

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void insertData(){
        final String txtName = name.getText().toString();
        final String txtSize = size.getText().toString();
        final String txtPrice = price.getText().toString();
        final String txtimgurl = image.getText().toString();


        Map<String,Object> map = new HashMap<>();
        map.put("name",name.getText().toString());
        map.put("size",size.getText().toString());
        map.put("price",price.getText().toString());
        map.put("image",image.getText().toString());


        if (txtName.isEmpty()) {
            name.setError("Name is Required");
            return;
        } else if (txtSize.isEmpty()) {
            size.setError("Size is Required");
            return;
        } else if (txtPrice.isEmpty()) {
            price.setError("Price is Required");
            return;
        } else if (txtimgurl.isEmpty()) {
            image.setError("ImageUrl is Required");
            return;
        } else {

        FirebaseDatabase.getInstance().getReference().child("cakes").push()
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddActivity.this, "Data Add Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( Exception e) {
                        Toast.makeText(AddActivity.this, "Error While Insertion", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    }
    private void clearAll()
    {
        name.setText("");
        size.setText("");
        price.setText("");
        image.setText("");
    }

}