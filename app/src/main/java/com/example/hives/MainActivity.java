package com.example.hives;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
//import com.google.firebase.database.Query;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomnav;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private DatabaseReference allPostdatabaseRef;
    private RecyclerView Post;
    private FirebaseUser Fuser;
    private ArrayList<String> joinpost=new ArrayList<>();

    private DatabaseReference ClickPostRef;
    private DatabaseReference PostRef;
    Dialog myDialog;
    Dialog imageDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDialog = new Dialog(this);
        imageDialog = new Dialog(this);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        mAuth=FirebaseAuth.getInstance();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        if (mAuth.getCurrentUser()==null){
            finish();
            sendUserToStartActivity();
        }


        drawerLayout=(DrawerLayout) findViewById(R.id.drawable_layout);
        bottomnav= (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });


    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser==null) {
           sendUserToStartActivity();
        }
       else {
           if (currentUser.isEmailVerified()){
          CheckUserExistence();}
           else {
               sendUserToStartActivity();
           }
       }
    }
    private void sendUserToStartActivity() {
        Intent loginIntent = new Intent(MainActivity.this,SigninActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        BrowsePost();
    }
    private void CheckUserExistence() {
        //mAuth is Firebase
        final String current_user_id=mAuth.getCurrentUser().getUid();
        String loggedinuser=mAuth.getCurrentUser().getUid();
        OneSignal.sendTag("User_ID",loggedinuser);
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(current_user_id))
                {
                    SendUserToSetUpActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        BrowsePost();
    }
    private void SendUserToSetUpActivity() {
        Intent setupIntent=new Intent(MainActivity.this,SetUpActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        BrowsePost();
    }
    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_jhives:
                Intent intentjhives=new Intent(MainActivity.this, joinedHivesActivity.class);
                startActivity(intentjhives);
                break;


           case R.id.nav_search:
                Intent intentsearch=new Intent(MainActivity.this, searchActivity.class);
                startActivity(intentsearch);
                break;

            case R.id.nav_profile:
               Intent intentprofile=new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intentprofile);
                break;
            case R.id.nav_notf:
                Intent intentact=new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intentact);
                break;
        }

    }
    private void openImage(String image) {

        ImageView userimg;

        imageDialog.setContentView(R.layout.openimage);
        userimg =imageDialog.findViewById(R.id.postimage);
        Picasso.get().load(image).into(userimg);
        imageDialog.show();
    }
    public void openDialog(String UserId) {

        ClickPostRef =  FirebaseDatabase.getInstance().getReference().child("Users").child(UserId);
        ClickPostRef.addValueEventListener(new ValueEventListener() {
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
                    Toast.makeText(MainActivity.this,"لا يوجد مستخدم",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }


    private void BrowsePost() {
        //Query searchHivesAndInfiQuere = allPostdatabaseRef;
        mAuth = FirebaseAuth.getInstance();
        Fuser =mAuth.getCurrentUser();

        allPostdatabaseRef =  FirebaseDatabase.getInstance().getReference().child("Posts");
        Post = findViewById(R.id.postview);
        Post.setHasFixedSize(true); //???
        Post.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference UsersRefhive;
        if (Fuser != null)
        UsersRefhive = FirebaseDatabase.getInstance().getReference().child("Users").child(Fuser.getUid()).child("Joined Hives");


        FirebaseRecyclerAdapter<AllPosts, PostViweHolder> FirebaseRecycleAdapter
                = new FirebaseRecyclerAdapter<AllPosts, PostViweHolder>
                (
                        AllPosts.class,
                        R.layout.post_row,///
                        PostViweHolder.class,
                        allPostdatabaseRef.orderByChild("time")
                ) {
            @Override
            protected void populateViewHolder(final PostViweHolder postViweHolder,final AllPosts module, final int i) {

                final String PostKey = getRef(i).getKey();
                /*allPostdatabaseRef.child("hivename").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){*/

                postViweHolder.setDate(module.getDate());
                postViweHolder.setHiveimage(getApplicationContext(), module.getHiveimage());
                postViweHolder.setDescription(module.getDescription());
                postViweHolder.setHivename(module.getHivename());
                postViweHolder.setPostimage(getApplicationContext(), module.getPostimage());
                postViweHolder.setTime(module.getTime());
                postViweHolder.setUsername(module.getUsername());

                postViweHolder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  Toast.makeText(MainActivity.this,module.getPostimage(),Toast.LENGTH_LONG).show();
                        String Image = module.getPostimage();
                        if (Image != null){
                            openImage(Image);}
                    }
                });

                postViweHolder.name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
                        PostRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              //  Toast.makeText(MainActivity.this,"heeeereee",Toast.LENGTH_LONG).show();
                                if (dataSnapshot.exists()){
                                String Uid =  dataSnapshot.child("uid").getValue().toString();
                              //  Toast.makeText(MainActivity.this,Uid,Toast.LENGTH_LONG).show();
                                openDialog(Uid);}
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });
                postViweHolder.mViwe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ClickPostIntent = new Intent(MainActivity.this, ViewPost.class);
                        ClickPostIntent.putExtra("PostKey",PostKey);
                        startActivity(ClickPostIntent);
                    }
                });
                   /* }}

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/




            }
        };

        Post.setAdapter(FirebaseRecycleAdapter);
    }

    public static class PostViweHolder extends RecyclerView.ViewHolder {
    View mViwe;
    TextView name;
        ImageView img;
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
         img= mViwe.findViewById(R.id.post_image);
         if(postimage==null)
             img.getLayoutParams().height=0;
        Picasso.get().load(postimage).into(img);
    }
    public void setTime(String time){
        TextView Time = mViwe.findViewById(R.id.post_title);
        Time.setText(time);
    }
        public void setUsername(String username){
             name = mViwe.findViewById(R.id.user_name);
            name.setText(username);
        }

}
}