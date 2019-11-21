package com.example.hives;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    private Context mContext;
    private List<Comment> mData; ///??? models
    String PostKey;

    public CommentsAdapter(Context mContext, List<Comment> mData, String PostKey) {
        this.mContext = mContext;
        this.mData = mData;
        this.PostKey=  PostKey;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment,parent,false);
        return new CommentViewHolder(row);
    }





    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder holder, final int position) {


        //set values
        holder.ccomment.setText(mData.get(position).getComment());
        holder.cusername.setText(mData.get(position).getUsername());
        holder.cdate.setText(mData.get(position).getDate());
        holder.ctime.setText(mData.get(position).getTime());

        //pop up
        holder.cusername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(mData.get(position).getUid());


               final Dialog myDialog = new Dialog(v.getRootView().getContext());


                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        TextView txtclose, ubio;
                        ImageView userimg;

                        myDialog.setContentView(R.layout.custompopup);

                        if (dataSnapshot.exists()){
                            String Uname = dataSnapshot.child("username").getValue().toString();
                            String Ubio = dataSnapshot.child("bio").getValue().toString();
                            txtclose = myDialog.findViewById(R.id.username);
                            ubio = myDialog.findViewById(R.id.userbio);
                            txtclose.setText(Uname);
                            ubio.setText(Ubio);


                            if (dataSnapshot.hasChild("profileimg")) {
                                String Uimage = dataSnapshot.child("profileimg").getValue().toString();
                                userimg = myDialog.findViewById(R.id.userimage);
                                Picasso.get().load(Uimage).into(userimg);
                            }

                            myDialog.show();
                        }
                        else{
                            Toast.makeText(mContext,"لا يوجد مستخدم",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });




            }
        });




        //delete
       if (mData.get(position).getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            holder.delcom.setVisibility(View.VISIBLE);

       holder.delcom.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());


               // Build an AlertDialog
               //AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

               // Set a title for alert dialog
               builder.setTitle("تنبيه!");

               // Ask the final question
               builder.setMessage("هل انت متأكد من حذف التعليق؟");

               // Set click listener for alert dialog buttons
               DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       switch(which){
                           case DialogInterface.BUTTON_POSITIVE:


                               deleteItem(position);


/*
                               Toast.makeText(mContext, mData.get(position).getKey(), Toast.LENGTH_SHORT).show();

                               mData.remove(position);
                               notifyItemRemoved(position);
                               notifyItemRangeChanged(position,mData.size());
                              */

                               //notifyDataSetChanged();
                               break;

                           case DialogInterface.BUTTON_NEGATIVE:
                               // User clicked the No button
                               break;
                       }
                   }
               };

               // Set the alert dialog yes button click listener
               builder.setPositiveButton("نعم", dialogClickListener);

               // Set the alert dialog no button click listener
               builder.setNegativeButton("لا",dialogClickListener);

               AlertDialog dialog = builder.create();
               // Display the alert dialog on interface
               dialog.show();

           }
       });

           }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView cusername, ccomment,cdate,ctime;
        ImageView delcom;


        public CommentViewHolder(View itemView) {
            super(itemView);
            cusername= itemView.findViewById(R.id.comment_username);
            ccomment= itemView.findViewById(R.id.comment_text);
            cdate= itemView.findViewById(R.id.comment_date);
            ctime= itemView.findViewById(R.id.comment_time);
            delcom= itemView.findViewById(R.id.delcombtn);

        }

    }




    public void deleteItem(int position){
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final String key=mData.get(position).getKey();

        rootRef.child("Comments").child(key).removeValue();

        rootRef.child("Posts")
                .child(PostKey)
                .child("Comments")
                .child(key)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(mContext, "تم حدف تعليقك...", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(mContext, "حدث خطأ...", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    public void delete2(int position){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Comments")
                .child(mData.get(position).getKey())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(mContext, "تم حدف تعليقك...", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(mContext, "حدث خطأ...", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }


}