package com.example.cakeappratings.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cakeappratings.Listner.IRatingListner;
import com.example.cakeappratings.Listner.IrecycleClickListner;
import com.example.cakeappratings.MainActivity;
import com.example.cakeappratings.Modal.Rating;
import com.example.cakeappratings.R;
import com.example.cakeappratings.eventBus.UpdateEvent;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ReviewAdapter  extends RecyclerView.Adapter<ReviewAdapter.MyReviewHolder> {

        private Context context;
        private List<Rating> reviews ;
        private IRatingListner iReviewListner;

        public ReviewAdapter(Context context, List<Rating> reviews, IRatingListner iReviewListner ){
            this.context = context;
            this.reviews = reviews;
            this.iReviewListner = iReviewListner;
        }

        @NonNull
        @Override
        public MyReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyReviewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.review_list,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyReviewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.rating.setText(new StringBuffer().append(reviews.get(position).getRating()));
            holder.review.setText(new StringBuffer().append(reviews.get(position).getDescription()));

            holder.editFeedbackBtn.setVisibility(View.GONE);
            holder.deleteFeedbackBtn.setVisibility(View.GONE);

            if (reviews.get(position).getUid() != "001"){

                holder.editFeedbackBtn.setVisibility(View.VISIBLE);
                holder.deleteFeedbackBtn.setVisibility(View.VISIBLE);
            }else{

                holder.editFeedbackBtn.setVisibility(View.VISIBLE);
                holder.deleteFeedbackBtn.setVisibility(View.VISIBLE);
            }

            holder.deleteFeedbackBtn.setOnClickListener(v ->{
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Delete User review")
                        .setMessage("Do you really want to delete review ?")
                        .setNegativeButton("CANCEL", (dialog1, which) -> dialog1.dismiss())
                        .setPositiveButton("YES", (dialog12, which) -> {
                            notifyItemRemoved(position);
                            deleteFromFirebase(reviews.get(position));
                            dialog12.dismiss();
                        }).create();
                dialog.show();
            });
            holder.editFeedbackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String rating = reviews.get(position).getRating();
                    String review = reviews.get(position).getDescription();
                    String uid = reviews.get(position).getUid();
                    String key = reviews.get(position).getKey();
                    Intent intent = new Intent("editData");
                    intent.putExtra("rating",rating);
                    intent.putExtra("review",review);
                    intent.putExtra("key",key);
                    intent.putExtra("uid",uid);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });
        }

        private void deleteFromFirebase(Rating review) {
            FirebaseDatabase.getInstance().getReference("Review").child("001").child("001").child(review.getKey())
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context,"Review deleted !",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        EventBus.getDefault().postSticky(new UpdateEvent());
                    });
        }

        @Override
        public int getItemCount() {
            return reviews.size();
        }

        public class MyReviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            @BindView(R.id.lblRating)
            TextView rating;
            @BindView(R.id.lblReview)
            TextView review;
            @BindView(R.id.btnEdite)
            ImageView editFeedbackBtn;
            @BindView(R.id.btnDelete)
            ImageView deleteFeedbackBtn;

            IrecycleClickListner irecycleClickListner;

            public void setListner(IrecycleClickListner listner){
                this.irecycleClickListner = listner;
            }

            private Unbinder unbinder;

            public MyReviewHolder(@NonNull View itemView) {
                super(itemView);
                unbinder = ButterKnife.bind(this,itemView);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }
