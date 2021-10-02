package com.example.cakeappratings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cakeappratings.Listner.IRatingListner;
import com.example.cakeappratings.Modal.Rating;
import com.example.cakeappratings.adapter.ReviewAdapter;
import com.example.cakeappratings.eventBus.UpdateEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements com.example.cakeappratings.Listner.IRatingListner {
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.txtRating)
    EditText review;
    @BindView(R.id.btnAddRating)
    Button btnAddRating;
    @BindView(R.id.btnUpdate)
    Button btnUpdate;
    @BindView(R.id.btnCancle)
    Button btnCancle;
    @BindView(R.id.recycleReview)
    RecyclerView recycleReview;
    @BindView(R.id.editPanel)
    LinearLayout editPanel;
    @BindView(R.id.btnCart)
    ImageView btnCart;

    Rating upRating = new Rating();
    IRatingListner IRatingListner;
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(EventBus.getDefault().hasSubscriberForEvent(UpdateEvent.class))
            EventBus.getDefault().removeStickyEvent(UpdateEvent.class);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void updateReview(UpdateEvent event){
        loadItemFromFirebase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("editData"));

        btnAddRating.setOnClickListener(v -> {
            final String userReview = review.getText().toString().trim();
            final int noofstars = rating.getNumStars();
            final float getrating = rating.getRating();

            if (TextUtils.isEmpty(userReview)) {
                review.setError("Enter Review");
                return;
            }
            Rating rating = new Rating();
            rating.setUid("001");
            rating.setDescription(userReview);
            rating.setRating(Float.toString (getrating) );
            addReview(rating);
        });

        btnCart.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,ShippingActivity.class)));
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPanel.setVisibility(View.GONE);
                btnAddRating.setVisibility(View.VISIBLE);
                clearInput();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userReview = review.getText().toString().trim();
                final float getrating = rating.getRating();

                if (TextUtils.isEmpty(userReview)) {
                    review.setError("Enter Review");
                    return;
                }
                Rating rating = new Rating();
                rating.setUid(upRating.getUid());
                rating.setDescription(userReview);
                rating.setRating(Float.toString (getrating) );
                rating.setKey(upRating.getKey());



                FirebaseDatabase.getInstance().getReference("Review").child("001").child("001").child(upRating.getKey())
                        .setValue(rating)
                        .addOnSuccessListener(aVoid -> {
                            loadItemFromFirebase();
                            EventBus.getDefault().postSticky(new UpdateEvent());
                            IRatingListner.onReviewLoadFail("Rating updated");
                            clearInput();
                        });

            }
        });
        loadItemFromFirebase();
    }
    public void clearInput(){
        rating.setRating(0);
        review.setText("");
        editPanel.setVisibility(View.GONE);
        btnAddRating.setVisibility(View.VISIBLE);
    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String ret = intent.getStringExtra("rating");
            String rev = intent.getStringExtra("review");
            String key = intent.getStringExtra("key");
            String uid = intent.getStringExtra("uid");
            

            editPanel.setVisibility(View.VISIBLE);
            btnAddRating.setVisibility(View.GONE);

            rating.setRating(Float.parseFloat(ret));
            review.setText(rev);

            if(key != "") {
                upRating.setKey(key);
                upRating.setDescription(rev);
                upRating.setRating(ret);
                upRating.setUid(uid);
            }



        }
    };

    private void init(){
        ButterKnife.bind(this);

        IRatingListner = this;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycleReview.setLayoutManager(linearLayoutManager);
        recycleReview.addItemDecoration(new DividerItemDecoration(this,linearLayoutManager.getOrientation()));

    }

    private void loadItemFromFirebase() {

        List<Rating> reviews = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Review").child("001").child("001")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for (DataSnapshot reviewSnapshot : snapshot.getChildren()){
                                Rating review = reviewSnapshot.getValue(Rating.class);
                                reviews.add(review);
                            }
                            IRatingListner.onReviewLoadSuccess(reviews);
                        }else{
                            IRatingListner.onReviewLoadFail("Cant find reviews");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        IRatingListner.onReviewLoadFail(error.getMessage());
                    }
                });
    }
    public void addReview( Rating rating ) {

        DatabaseReference reviewref = FirebaseDatabase.getInstance().getReference("Review").child("001");
        String key = reviewref.push().getKey();
        Rating review = new Rating();
        review.setRating(rating.getRating());
        review.setDescription(rating.getDescription());
        review.setUid("001");
        review.setKey(key);

        reviewref.child("001").child(key).setValue(review)
                .addOnSuccessListener(aVoid -> {
                    IRatingListner.onReviewLoadFail("Review add success");
                    loadItemFromFirebase();
                    clearInput();
                })
                .addOnFailureListener(e -> {
                    IRatingListner.onReviewLoadFail("Review add fail");
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItemFromFirebase();
    }
    @Override
    public void onReviewLoadSuccess(List<Rating> reviews) {
        ReviewAdapter adapter = new ReviewAdapter(this,reviews,IRatingListner);
        recycleReview.setAdapter(adapter);
    }

    @Override
    public void onReviewLoadFail(String message) {
        Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
    }
}