package com.example.hives;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import android.net.Uri;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ImageView settings;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef , HiveRef;
    private FirebaseUser Fuser;
    TextView uesrName;
    TextView userBio;
    CircleImageView userImage;
    private RecyclerView hivelist;
    Button myHives ;
    Button myPosts ;
    private BottomNavigationView bottomnav;
    private DatabaseReference allPostdatabaseRef;
    private RecyclerView  showuPost;

    TextView nohive,nopost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_profile);


        nohive=findViewById(R.id.nohive);
        nopost=findViewById(R.id.nopost);

        uesrName = findViewById(R.id.username);
        userBio = findViewById(R.id.bio);
        settings = findViewById(R.id.settings);
        userImage = findViewById(R.id.userimage);
        bottomnav=(BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomnav.setSelectedItemId(R.id.nav_profile);
        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });
        mAuth = FirebaseAuth.getInstance();
        Fuser =mAuth.getCurrentUser();
        String current = Fuser.getUid();
        allPostdatabaseRef =  FirebaseDatabase.getInstance().getReference().child("Posts");
        final Query queryp =allPostdatabaseRef.orderByChild("uid").startAt(current).endAt(current +"\uf8ff");
        queryp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()==0){
                    nopost.setVisibility(View.VISIBLE);
                    nohive.setVisibility(View.INVISIBLE);
                }
                else{
                    nopost.setVisibility(View.INVISIBLE);
                    nohive.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myPosts = findViewById(R.id.myposts);
        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPosts.setBackgroundColor(getResources().getColor(R.color.clicked));
                myHives.setBackgroundColor(getResources().getColor(R.color.notclicked));
                hivelist.setAlpha(0);
                showuPost.setAlpha(1);
                showuPost.setVisibility(View.VISIBLE);
                queryp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount()==0){
                            nopost.setVisibility(View.VISIBLE);
                            nohive.setVisibility(View.INVISIBLE);
                        }
                        else{
                            nopost.setVisibility(View.INVISIBLE);
                            nohive.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });

        HiveRef = FirebaseDatabase.getInstance().getReference().child("HIVES");
        final Query queryh=HiveRef.orderByChild("uid").startAt(current).endAt(current +"\uf8ff");



        myHives= findViewById(R.id.myhives);
        myHives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                myHives.setBackgroundColor(getResources().getColor(R.color.clicked));
                myPosts.setBackgroundColor(getResources().getColor(R.color.notclicked));
                hivelist.setAlpha(1);
                showuPost.setAlpha(0);
                showuPost.setVisibility(View.GONE);
                hivelist.setVisibility(View.VISIBLE);

                queryh.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount()==0){
                            nohive.setVisibility(View.VISIBLE);
                            nopost.setVisibility(View.INVISIBLE);
                        }
                        else {
                            nohive.setVisibility(View.INVISIBLE);
                            nopost.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


        hivelist = findViewById(R.id.myRecycleView);
        hivelist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        hivelist.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();

        mRef =FirebaseDatabase.getInstance().getReference().child("Users").child(Fuser.getUid());




        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username,bio;
                if (dataSnapshot.exists()){
                    username = dataSnapshot.child("username").getValue().toString();
                    bio =dataSnapshot.child("bio").getValue().toString();
                    uesrName.setText(username);
                    userBio.setText(bio);

                    if(dataSnapshot.hasChild("profileimg")){
                        String Image = dataSnapshot.child("profileimg").getValue().toString();
                        Picasso.get().load(Image).into(userImage);
                        Glide.with(getApplicationContext()).load(Image).into(userImage);}}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(ProfileActivity.this, settingsActivity.class);
                startActivity(settings);
            }
        });

        showuPost = findViewById(R.id.postview);
        showuPost.setHasFixedSize(true); //???
        showuPost.setLayoutManager(new LinearLayoutManager(this));
        DisplayUserHives(queryh);
        DisplayUserPost(queryp);

        myPosts.setBackgroundColor(getResources().getColor(R.color.clicked));
        myHives.setBackgroundColor(getResources().getColor(R.color.notclicked));
        hivelist.setAlpha(0);
        showuPost.setAlpha(1);
        //  showuPost.setVisibility(View.VISIBLE);        /////////////////////////////

    }

    private void DisplayUserPost(Query queryp) {

        FirebaseRecyclerAdapter<AllPosts, MainActivity.PostViweHolder> FirebaseRecycleAdapter
                = new FirebaseRecyclerAdapter<AllPosts, MainActivity.PostViweHolder>
                (
                        AllPosts.class,
                        R.layout.post_row,///
                        MainActivity.PostViweHolder.class,
                        queryp
                ) {
            @Override
            protected void populateViewHolder(final MainActivity.PostViweHolder postViweHolder, final AllPosts module, final int i) {

                final String PostKey = getRef(i).getKey();



                DatabaseReference usr=FirebaseDatabase.getInstance().getReference().child("Users").child(module.getUid());
                usr.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postViweHolder.setUsername(dataSnapshot.child("username").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                DatabaseReference hivesRef=FirebaseDatabase.getInstance().getReference().child("HIVES").child(module.getHivename());
                hivesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        postViweHolder.setHiveimage(getApplicationContext(), dataSnapshot.child("image").getValue().toString());


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                postViweHolder.setDate(module.getDate());

                postViweHolder.setDescription(module.getDescription());
                postViweHolder.setHivename(module.getHivename());
                postViweHolder.setPostimage(getApplicationContext(), module.getPostimage());
                postViweHolder.setTime(module.getTime());


                postViweHolder.mViwe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ClickPostIntent = new Intent(ProfileActivity.this, ViewPost.class);
                        ClickPostIntent.putExtra("PostKey",PostKey);
                        startActivity(ClickPostIntent);
                    }
                });

            }
        };

        showuPost.setAdapter(FirebaseRecycleAdapter);
    }

    public static class PostViweHolder extends RecyclerView.ViewHolder {
        View mViwe;
        public PostViweHolder(@NonNull View PostView) {
            super(PostView);
            mViwe= PostView;
        }
        public void setDate(String date){
            TextView Date=mViwe.findViewById(R.id.post_date);
            Date.setText(date);

        }
        public void setDescription(String description) {
            TextView des =mViwe.findViewById(R.id.post_content);
            des.setText(description);

        }
        public void setHiveimage(Context ctx, String hiveimage){
            ImageView post_image = (ImageView) mViwe.findViewById(R.id.hiveimg);
            // Glide.with(ctx).load(hiveimage).into(post_image);
            Picasso.get().load(hiveimage).into(post_image);
        }
        public void setHivename(String hivename) {
            TextView name = mViwe.findViewById(R.id.hive_name);
            name.setText(hivename);
        }
        public void setPostimage(Context ctx, String postimage){
            ImageView img= mViwe.findViewById(R.id.post_image);
            //Glide.with(ctx).load(postimage).into(img);
            Picasso.get().load(postimage).into(img);
        }
        public void setTime(String time){
            TextView Time = mViwe.findViewById(R.id.post_title);
            Time.setText(time);
        }
    }


    private void DisplayUserHives(Query queryh) {

        FirebaseRecyclerAdapter<HivesRetrieve, HiveViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<HivesRetrieve, HiveViewHolder>(
                        HivesRetrieve.class,
                        R.layout.item_row,
                        HiveViewHolder.class,
                        queryh

                ) {
                    @Override
                    protected void populateViewHolder(HiveViewHolder hiveViewHolder, HivesRetrieve hivesRetrieve, final int i) {
                        hiveViewHolder.setHiveName(hivesRetrieve.getTitle());
                        hiveViewHolder.setHiveImage(getApplicationContext(), hivesRetrieve.getImage());

                        hiveViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String hivename=getRef(i).getKey();
                                Intent profielintent=new Intent(ProfileActivity.this,ViewHive.class);
                                profielintent.putExtra("HiveName",hivename);
                                startActivity(profielintent);
                            }
                        });
                    }
                };
        hivelist.setAdapter(firebaseRecyclerAdapter);
    }

    public static class HiveViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public HiveViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setHiveName(String hiveName){
            TextView hivesname = mView.findViewById(R.id.post_title);
            hivesname.setText(hiveName);
        }

        public void setHiveImage(Context cnx ,String hiveImage) {

            ImageView hivesimage = mView.findViewById(R.id.post_image);
            Picasso.get().load(hiveImage).into(hivesimage);
        }

        public void setUsername(String username){
            TextView name = mView.findViewById(R.id.user_name);
            name.setText(username);
        }
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(ProfileActivity.this,SigninActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()){

            case R.id.nav_jhives:
                Intent intentjhives=new Intent(ProfileActivity.this, joinedHivesActivity.class);
                startActivity(intentjhives);
                break;

            case R.id.nav_search:
                Intent intentsearch=new Intent(ProfileActivity.this, searchActivity.class);
                startActivity(intentsearch);
                break;

            case R.id.nav_home:

                Intent intentprofile=new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intentprofile);
                break;
            case R.id.nav_notf:
                Intent intentnot=new Intent(ProfileActivity.this, NotificationActivity.class);
                startActivity(intentnot);
                break;
        }

    }


}
