package com.example.hives;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHive extends AppCompatActivity {
    private TextView HiveName,HiveInfo,HiveCreator;
    private CircleImageView HivePic;
    private Button createPost,joinHive,UnjoinHive;
    private ImageView editHivebtn;
    private FirebaseUser currentuser,currentuserNouf;
    private FirebaseAuth mAuth;
    private DatabaseReference HivesRef,UserRef,JoinHiveRef,noufRef;
    private String joinedUser,VHiveName,CurrentUserId,creatorId,currentHiveCreatorId;
    String currentState;
    String Hivedes,createuser;
    String hivenm;
    private Toolbar mtoolbar;
    private FirebaseUser Fuser;
    private DatabaseReference allPostdatabaseRef;
    private RecyclerView  showPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_hive);

        HiveName=(TextView) findViewById(R.id.viewhivename);
        HiveInfo=(TextView) findViewById(R.id.viewhiveInfo);
        HiveCreator=(TextView)findViewById(R.id.viewCreator);
        HivePic=(CircleImageView)findViewById(R.id.viewHivePic);
        createPost=(Button)findViewById(R.id.Addpostbtn);
        joinHive=(Button)findViewById(R.id.joinHivebtn);
        editHivebtn=(ImageView) findViewById(R.id.EditHivebtn);
        UnjoinHive = (Button) findViewById(R.id.UnjoinHivebtn);
        currentState="NotJoined";

        mAuth=FirebaseAuth.getInstance();

        //Noura
        CurrentUserId = mAuth.getCurrentUser().getUid();
        VHiveName = getIntent().getExtras().get("HiveName").toString();
        HivesRef = FirebaseDatabase.getInstance().getReference().child("HIVES").child(VHiveName);
        JoinHiveRef = FirebaseDatabase.getInstance().getReference("join");
        //noufRef = FirebaseDatabase.getInstance().getReference("Users").child(CurrentUserId).child("Joine Hives");
        currentHiveCreatorId = HivesRef.child("uid").toString();


        editHivebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editintent=new Intent(ViewHive.this,EditHiveActivity.class);
                editintent.putExtra("HiveName",VHiveName);
                startActivity(editintent);
            }
        });

        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editintent=new Intent(ViewHive.this,AddPost.class);
                editintent.putExtra("HiveName",VHiveName);
                startActivity(editintent);
            }
        });



        //initializeFields();

        HivesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(mAuth.getCurrentUser().getUid().equals(dataSnapshot.child("uid").getValue().toString())){
                        joinHive.setVisibility(View.INVISIBLE);
                        editHivebtn.setVisibility(View.VISIBLE);
                        UnjoinHive.setVisibility(View.INVISIBLE);
                    }
                    if (currentState=="joined") {
                        UnjoinHive.setVisibility(View.VISIBLE);
                        UnjoinHive.setEnabled(true);
                    }
                    String Hiveimge=dataSnapshot.child("image").getValue().toString();
                    hivenm=dataSnapshot.child("title").getValue().toString();
                    if (dataSnapshot.hasChild("hiveinfo")){
                        Hivedes=dataSnapshot.child("hiveinfo").getValue().toString();}
                    else{
                        Hivedes="لا توجد نبذة...";
                    }
                    if (dataSnapshot.hasChild("username")){
                        createuser=dataSnapshot.child("username").getValue().toString();}
                    else{
                        createuser="لا يوجد";
                    }

                    if(dataSnapshot.hasChild("image")){
                        Picasso.get().load(Hiveimge).into(HivePic);
                        Glide.with(getApplicationContext()).load(Hiveimge).into(HivePic);}


                    HiveName.setText(hivenm);
                    HiveInfo.setText(Hivedes);
                    HiveCreator.setText("ملكة الخلية:"+createuser);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        mtoolbar=(Toolbar)findViewById(R.id.viewHiveToolBar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(" ");

        MaintainStatus();

        joinHive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CurrentUserId.equals(currentHiveCreatorId)) {
                    if (currentState.equals("NotJoined")) {
                        joinHive.setEnabled(true);
                        currentuser=FirebaseAuth.getInstance().getCurrentUser();
                        UserRef= FirebaseDatabase.getInstance().getReference().child("HIVES").child(VHiveName);
                        UserRef.child(currentuser.getUid()).setValue(currentuser.getUid());
                        Intent intent = new Intent(ViewHive.this,joinedHivesActivity.class);
                        intent.putExtra("currenthive",VHiveName);
                        UnjoinHive.setVisibility(View.VISIBLE);
                        UnjoinHive.setEnabled(true);
                        joinHive.setVisibility(View.INVISIBLE);
                        currentState="joined";
                    }
                }
                UnjoinHive.setVisibility(View.VISIBLE);
            }
        });

        UnjoinHive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CurrentUserId.equals(currentHiveCreatorId)) {
                    if (currentState.equals("joined")) {
                        //UnjoinHive.setEnabled(true);
                        currentuser=FirebaseAuth.getInstance().getCurrentUser();
                        UserRef= FirebaseDatabase.getInstance().getReference().child("HIVES").child(VHiveName);
                        UserRef.child(currentuser.getUid()).removeValue();
                        // Intent intent = new Intent(ViewHive.this,joinedHivesActivity.class);
                        // intent.putExtra("currenthive",VHiveName);
                        UnjoinHive.setVisibility(View.INVISIBLE);
                        joinHive.setVisibility(View.VISIBLE);
                        currentState="NotJoined";
                    }
                }
                joinHive.setVisibility(View.VISIBLE);
            }
        });

        showPost = findViewById(R.id.showpost);
        showPost.setHasFixedSize(true); //???
        showPost.setLayoutManager(new LinearLayoutManager(this));
        DisplayHivespost();

    }
    private void MaintainStatus() {
         HivesRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()&&(!mAuth.getCurrentUser().getUid().equals(dataSnapshot.child("uid").getValue().toString()))){
                if(dataSnapshot.hasChild(CurrentUserId)){
                    currentState ="joined";
                    joinHive.setVisibility(View.INVISIBLE);
                    UnjoinHive.setVisibility(View.VISIBLE);


                }else {
                    currentState="NotJoined";
                    // Toast.makeText(ViewHive.this,"I m in else ", Toast.LENGTH_SHORT).show();

                }
                if (currentState=="joined") {
                    UnjoinHive.setVisibility(View.VISIBLE);
                    UnjoinHive.setEnabled(true);
                    joinHive.setVisibility(View.INVISIBLE);
                    joinHive.setEnabled(false);
                    //  Toast.makeText(ViewHive.this,"I m in Second if  ", Toast.LENGTH_SHORT).show();
                }
                else if(currentState=="NotJoined") {
                    joinHive.setVisibility(View.VISIBLE);
                    joinHive.setEnabled(true);
                    UnjoinHive.setVisibility(View.INVISIBLE);
                    UnjoinHive.setEnabled(false);
                    // Toast.makeText(ViewHive.this,"I m in laaaaaast  ", Toast.LENGTH_SHORT).show();
                }
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

}

    private void DisplayHivespost() {
        mAuth = FirebaseAuth.getInstance();
        Fuser =mAuth.getCurrentUser();
        String current = Fuser.getUid();
        allPostdatabaseRef =  FirebaseDatabase.getInstance().getReference().child("Posts");

        FirebaseRecyclerAdapter<AllPosts, MainActivity.PostViweHolder> FirebaseRecycleAdapter
                = new FirebaseRecyclerAdapter<AllPosts, MainActivity.PostViweHolder>
                (
                        AllPosts.class,
                        R.layout.post_row,///
                        MainActivity.PostViweHolder.class,
                        allPostdatabaseRef.orderByChild("hivename").startAt(VHiveName).endAt(VHiveName +"\uf8ff")
                ) {
            @Override
            protected void populateViewHolder(final MainActivity.PostViweHolder postViweHolder, final AllPosts module, final int i) {

                final String PostKey = getRef(i).getKey();


                postViweHolder.setDate(module.getDate());
                postViweHolder.setHiveimage(getApplicationContext(), module.getHiveimage());
                postViweHolder.setDescription(module.getDescription());
                postViweHolder.setHivename(module.getHivename());
                postViweHolder.setPostimage(getApplicationContext(), module.getPostimage());
                postViweHolder.setTime(module.getTime());
                postViweHolder.setUsername(module.getUsername());


                postViweHolder.mViwe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ClickPostIntent = new Intent(ViewHive.this, ViewPost.class);
                        ClickPostIntent.putExtra("PostKey",PostKey);
                        startActivity(ClickPostIntent);
                    }
                });

            }
        };

        showPost.setAdapter(FirebaseRecycleAdapter);
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
        public void setUsername(String username){
            TextView name = mViwe.findViewById(R.id.user_name);
            name.setText(username);
        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home){
            finish();
        }



        return super.onOptionsItemSelected(item);


    }

    private void initializeFields() {
        HiveName=(TextView) findViewById(R.id.hiveName);
        HiveInfo=(TextView) findViewById(R.id.hiveInfo);
        HiveCreator=(TextView)findViewById(R.id.viewCreator);
        HivePic=(CircleImageView)findViewById(R.id.hivepic);
        createPost=(Button)findViewById(R.id.Addpostbtn);
        joinHive=(Button)findViewById(R.id.joinHivebtn);

    }
}
