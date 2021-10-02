package com.example.cakeappratings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cakeappratings.Modal.Shipping;
import com.example.cakeappratings.eventBus.UpdateEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShippingActivity extends AppCompatActivity {
    @BindView(R.id.txtName)
    EditText txtName;
    @BindView(R.id.txtContact)
    EditText txtContact;
    @BindView(R.id.txtMail)
    EditText txtMail;
    @BindView(R.id.txtCity)
    EditText txtCity;
    @BindView(R.id.txtZip)
    EditText txtZip;
    @BindView(R.id.txtAddress)
    EditText txtAddress;
    @BindView(R.id.txtProvince)
    Spinner txtProvince;
    @BindView(R.id.btnAddShip)
    Button btnAddShip;
    @BindView(R.id.btnBack)
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);

        ButterKnife.bind(this);

        Spinner spinner = (Spinner) findViewById(R.id.txtProvince);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.province_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        btnBack.setOnClickListener(v->finish());

        btnAddShip.setOnClickListener(v -> {
            final String name = txtName.getText().toString();
            final String contact = txtContact.getText().toString();
            final String mail = txtMail.getText().toString();
            final String city = txtCity.getText().toString();
            final String address = txtAddress.getText().toString();
            final String zip = txtZip.getText().toString();
            final String province = spinner.getSelectedItem().toString();

            if (TextUtils.isEmpty(name)) {
                txtName.setError("Name is Required.");
                return;
            }
            if (TextUtils.isEmpty(contact)) {
                txtContact.setError("Contact number is Required.");
                return;
            }
            if (contact.length() < 10 || contact.length() > 15) {
                txtContact.setError("Enter valid contact number ex :- 0000000000 ");
                return;
            }
            if (TextUtils.isEmpty(mail)) {
                txtMail.setError("Email is Required.");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                txtMail.setError("Enter valid e mail ex :- someone@mail.com.");
                return;
            }
            if (TextUtils.isEmpty(province)) {
                ((TextView)spinner.getSelectedView()).setError("Select Province");
                return;
            }
            if (TextUtils.isEmpty(city)) {
                txtCity.setError("City is Required.");
                return;
            }
            if (TextUtils.isEmpty(zip)) {
                txtZip.setError("Zip/Postal code is Required.");
                return;
            }
            if (TextUtils.isEmpty(address)) {
                txtAddress.setError("Address is Required.");
                return;
            }



            addShipping(name,contact,mail,city,address,zip,province);

        });
    }


    private void addShipping(String name,String contact,String mail,String city,String address,String zip,String province) {
        DatabaseReference reviewref = FirebaseDatabase.getInstance().getReference("Shipping");


        String key = reviewref.push().getKey();
        Shipping shipping = new Shipping();
        shipping.setName(name);
        shipping.setContact(contact);
        shipping.setAddress(address);
        shipping.setCity(city);
        shipping.setEmail(mail);
        shipping.setZip(zip);
        shipping.setProvince(province);

        reviewref.child(key).setValue(shipping)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this,"Shipping details added success",Toast.LENGTH_SHORT).show();
                    clearData();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,"Shipping details added fail",Toast.LENGTH_SHORT).show();
                });
    }

    private void clearData() {
        txtName.setText("");
        txtMail.setText("");
        txtContact.setText("");
        txtCity.setText("");
        txtAddress.setText("");
        txtZip.setText("");
    }
}