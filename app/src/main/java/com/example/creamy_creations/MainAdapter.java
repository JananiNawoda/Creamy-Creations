package com.example.creamy_creations;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainAdapter extends FirebaseRecyclerAdapter<MainMenu,MainAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MainAdapter(@NonNull  FirebaseRecyclerOptions<MainMenu> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull  myViewHolder holder, final int position, @NonNull MainMenu model) {
       holder.name.setText(model.getName());
       holder.size.setText(model.getSize());
       holder.price.setText(model.getPrice());
       
       Glide.with(holder.img.getContext())
               .load(model.getImage())
               .placeholder(R.drawable.common_google_signin_btn_icon_dark)
               .circleCrop()
               .error(R.drawable.common_google_signin_btn_icon_dark_normal)
               .into(holder.img);

//update
       holder.btnEdit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final DialogPlus dialogPlus = DialogPlus.newDialog((holder.img.getContext()))
                       .setContentHolder(new ViewHolder(R.layout.update_popup))
                       .setExpanded(true,2000)
                       .create();

              // dialogPlus.show();
               View view = dialogPlus.getHolderView();

               EditText name = view.findViewById(R.id.txtName);
               EditText size = view.findViewById(R.id.txtSize);
               EditText price = view.findViewById(R.id.txtPrice);
               EditText image = view.findViewById(R.id.txtimgurl);

               Button btnupdate = view.findViewById(R.id.btnUpdate);

               name.setText(model.getName());
               size.setText(model.getSize());
               price.setText(model.getPrice());
               image.setText(model.getImage());

               dialogPlus.show();

               btnupdate.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Map<String,Object> map = new HashMap<>();
                       map.put("name",name.getText().toString());
                       map.put("size",size.getText().toString());
                       map.put("price",price.getText().toString());
                       map.put("image",image.getText().toString());

                       FirebaseDatabase.getInstance().getReference().child("cakes")
                               .child(getRef(position).getKey()).updateChildren(map)
                               .addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void unused) {
                                       Toast.makeText(holder.name.getContext(), "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                                       dialogPlus.dismiss();
                                   }
                               })
                               .addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure( Exception e) {
                                       Toast.makeText(holder.name.getContext(), "Error While Updating", Toast.LENGTH_SHORT).show();
                                       dialogPlus.dismiss();
                                   }
                               });
                   }
               });
           }
       });
//delete
       holder.btnDelete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               AlertDialog.Builder builder = new AlertDialog.Builder(holder.name.getContext());
               builder.setTitle("Are You Sure?");
               builder.setMessage("Deleted data can't Undo.");

               builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       FirebaseDatabase.getInstance().getReference().child("cakes")
                               .child(getRef(position).getKey()).removeValue();
                   }
               });

               builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       Toast.makeText(holder.name.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                   }
               });
               builder.show();
           }
       });
       
    }

    @NonNull
    
    @Override
    public myViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        
        CircleImageView img;
        TextView name,size,price;

        Button btnEdit,btnDelete;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            
            img = (CircleImageView)itemView.findViewById(R.id.img1);
            name = (TextView)itemView.findViewById(R.id.nametext);
            size = (TextView)itemView.findViewById(R.id.size);
            price = (TextView)itemView.findViewById(R.id.price);

            btnEdit = (Button)itemView.findViewById(R.id.btnEdit);
            btnDelete = (Button)itemView.findViewById(R.id.btnDelete);

        }
    }
}
