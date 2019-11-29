package com.example.hives;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationActivity extends AppCompatActivity {
    private BottomNavigationView bottomnav;
    private RecyclerView activitylist;
    private DatabaseReference postRef,commentref;
    private FirebaseAuth mAuth;
    private ArrayList commentlist;
    private FirebaseUser currentuser;
    View nonot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);



        mAuth= FirebaseAuth.getInstance();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();

        postRef= FirebaseDatabase.getInstance().getReference().child("Posts");
        FirebaseUser currentUser=mAuth.getCurrentUser();
        commentref= FirebaseDatabase.getInstance().getReference().child("Comments");

        bottomnav= (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomnav.setSelectedItemId(R.id.nav_notf);
        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });

        nonot=findViewById(R.id.nonotif);
        // nonot.setVisibility(View.INVISIBLE);

        activitylist=(RecyclerView)findViewById(R.id.activityview);
        activitylist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        activitylist.setLayoutManager(linearLayoutManager);


        displayActivity();

    }

    private void displayActivity() {

        final DatabaseReference commentRef=FirebaseDatabase.getInstance().getReference().child("Comments");
        //h
        Query query= commentRef.orderByChild("creatorid").startAt(currentuser.getUid()).endAt(currentuser.getUid() +"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()==0){//no notii
                    activitylist.setVisibility(View.INVISIBLE);
                    nonot.setVisibility(View.VISIBLE);
                }
                else {
                    activitylist.setVisibility(View.VISIBLE);
                    nonot.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        FirebaseRecyclerAdapter<activityComments, ActivityViewHolder>  firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<activityComments, ActivityViewHolder>
                        (
                                activityComments.class,
                                R.layout.activity_layout,
                                ActivityViewHolder.class,
                                query
                        )
                {
                    @Override
                    protected void populateViewHolder(final ActivityViewHolder activityViewHolder, final activityComments comment, int i) {

                        final String key = getRef(i).getKey();

                        activityViewHolder.setUsername(comment.getUsername());
                        activityViewHolder.setComment(comment.getComment());
                        activityViewHolder.setProfileimage(getApplicationContext(),comment.getProfileimage());
                        activityViewHolder.setDate(comment.getDate());
                        activityViewHolder.setTime(comment.getTime());




                        activityViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent ClickPostIntent = new Intent(NotificationActivity.this, ViewPost.class);
                                ClickPostIntent.putExtra("PostKey",comment.getPostkey());
                                startActivity(ClickPostIntent);
                            }
                        });


                    }
                };






        activitylist.setAdapter(firebaseRecyclerAdapter);


    }


    public static class ActivityViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }



        public void setUsername(String username){
            TextView name=(TextView) mView.findViewById(R.id.commentby);
            name.setText(username);
        }


        public void setProfileimage(Context ctx,String profileimage){
            CircleImageView image=(CircleImageView)mView.findViewById(R.id.userimginactivity);
            if(!TextUtils.isEmpty(profileimage))
                Picasso.get().load(profileimage).into(image);

        }

        public void setComment(String comment){
            TextView comnt=(TextView)mView.findViewById(R.id.commentdesc);
            comnt.setText(comment);
        }
        ///

        public void setTime(String time) {
            TextView comnt=(TextView)mView.findViewById(R.id.comment_time);
            comnt.setText(time);
        }

        public void setDate(String date) {

            TextView comnt=(TextView)mView.findViewById(R.id.comment_date);
            comnt.setText(date);
        }


    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_jhives:
                Intent intentjhives=new Intent(NotificationActivity.this, joinedHivesActivity.class);
                startActivity(intentjhives);
                break;


            case R.id.nav_search:
                Intent intentsearch=new Intent(NotificationActivity.this, searchActivity.class);
                startActivity(intentsearch);
                break;

            case R.id.nav_profile:
                Intent intentprofile=new Intent(NotificationActivity.this, ProfileActivity.class);
                startActivity(intentprofile);
                break;
            case R.id.nav_home:
                Intent intenthome=new Intent(NotificationActivity.this, MainActivity.class);
                startActivity(intenthome);
                break;

        }


    }

}



