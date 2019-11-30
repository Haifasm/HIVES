package com.example.hives;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPost extends AppCompatActivity implements PopUpDelPost.ExampleDialogListener{

    private ImageView ViewPostImage;
    private CircleImageView ViewUserImage; //this is hive image instead
    private TextView ViewPostDisc, ViewPostUsername, ViewPostDate, ViewPostTime, ViewPHiveName;
    private ImageView Deletepostbutton;

    private String PostKey, currentuserid;

    private DatabaseReference ClickPostRef,PostRef,UserRef,disLikeRef,LikeRef,PostReference,PostRef2;

    private RecyclerView CommentsList;
    CommentsAdapter commentsAdapter;
    List<Comment> listComment;

    private FirebaseUser currentuser;
    private FirebaseAuth mAuth;
    private String currentuserID;
    private String uid;
    private ImageView sendcomment;
    private EditText commentinput;

    private DatabaseReference commentRef;

    private String username,profileimage,creatorid;
    private String savecurrentdate,savecurrenttime;
    private String usernotificationid;

    Dialog myDialog;
    DatabaseReference HiveRef;
    String Hivenamee,hname;
    Dialog imageDialog;

    ImageView delcom;

    //Noura
    private TextView NumberOfLikes ,NumberOfdisLikes ;
    private boolean LikeCheker=false;
    private  int countLikes,countDisLikes;
    private  ImageButton LikeBtn,DislikeBtn ;
    private String LikeState,disLikeState;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        myDialog = new Dialog(this);
        imageDialog = new Dialog(this);

        CommentsList= (RecyclerView) findViewById(R.id.comments_list);//h

        PostKey = getIntent().getExtras().get("PostKey").toString();
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey).child("Comments");
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        currentuserid=mAuth.getCurrentUser().getUid();
        commentRef= FirebaseDatabase.getInstance().getReference().child("Comments");


// Noura

        NumberOfLikes=(TextView)findViewById(R.id.numOfLikes);
        NumberOfdisLikes=(TextView)findViewById(R.id.numOfDisLikes);
        PostReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        disLikeRef =FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey).child("disLike");
        LikeRef=FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey).child("Like");
        PostRef2 = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
        LikeBtn= (ImageButton)findViewById(R.id.Likebtn);
        DislikeBtn= (ImageButton)findViewById(R.id.DisLikebtn);
        LikeMaintainStatus();
        DisLikeMaintainStatus();


        LikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikeCheker = true;
                LikeRef.addValueEventListener(new ValueEventListener() {
                    @Override

                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (LikeCheker) {
                            if (dataSnapshot.hasChild(currentuserid)) {
                                // olrady liked
                                LikeRef.child(currentuserid).setValue(null);
                                LikeCheker = false;
                                LikeBtn.setImageResource(R.drawable.grayheart1);
                                DislikeBtn.setEnabled(true);
                            } else {
                                // not liked yet
                                HashMap like = new HashMap();
                                like.put(currentuserid, true);
                                LikeRef.updateChildren(like);
                                LikeCheker = false;
                                LikeBtn.setImageResource(R.drawable.orangeheart);
                                DislikeBtn.setEnabled(false);
                            }

                        }
                        countDisLikes = (int)dataSnapshot.getChildrenCount();
                        NumberOfLikes.setText(((countDisLikes) + "Likes"));
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });
            }

        });

        DislikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikeCheker = true;
                disLikeRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (LikeCheker) {
                            if (dataSnapshot.hasChild(currentuserid)) {
                                // olrady disliked
                                disLikeRef.child(currentuserid).setValue(null);
                                DislikeBtn.setImageResource(R.drawable.graybrokenheart);
                                LikeBtn.setEnabled(true);
                                LikeCheker = false;
                            } else {
                                // not disliked yet
                                HashMap disLike = new HashMap();
                                disLike.put(currentuserid, true);
                                disLikeRef.updateChildren(disLike);
                                LikeCheker = false;
                                DislikeBtn.setImageResource(R.drawable.orangebrokenheart);
                                LikeBtn.setEnabled(false);
                            }

                        }
                        countDisLikes = (int) dataSnapshot.getChildrenCount();
                        NumberOfdisLikes.setText(((countDisLikes) + " dislikes"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        });



        HiveRef =FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
        HiveRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String Hname;
                if (dataSnapshot.exists()) {
                    Hname = dataSnapshot.child("hivename").getValue().toString();
                    Hivenamee = FirebaseDatabase.getInstance().getReference().child("Hives").child(Hname).getKey();
                }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        iniRvComment();










        currentuserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ViewPostImage = (ImageView) findViewById(R.id.click_post_image);
        ViewUserImage = (CircleImageView) findViewById(R.id.post_profile_image);//////////id
        ViewPostDisc = (TextView) findViewById(R.id.click_post_description);
        ViewPostUsername = (TextView) findViewById(R.id.click_user_name);
        ViewPostDate = (TextView) findViewById(R.id.click_date);
        ViewPostTime = (TextView) findViewById(R.id.click_time);
        ViewPHiveName = (TextView) findViewById(R.id.posthivename);
        Deletepostbutton = findViewById(R.id.delpostbtn);
        commentinput=(EditText)findViewById(R.id.commentinput);
        sendcomment=(ImageView)findViewById(R.id.commentsendbtn);





        // Open Image
        ViewPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickPostRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("postimage")) {
                            String pimg = dataSnapshot.child("postimage").getValue().toString();
                            openImage(pimg);
                        } }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        // Opne the hive Activity
        ViewUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profielintent=new Intent(ViewPost.this,ViewHive.class);
                profielintent.putExtra("HiveName",Hivenamee);
                startActivity(profielintent);
            }
        });

        // Open User Dialog
        ViewPostUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
                PostRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String Uid =  dataSnapshot.child("uid").getValue().toString();
                        openDialog(Uid);}
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });


        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (currentuserID.equals(dataSnapshot.child("uid").getValue().toString())) {
                        // or is admin
                        System.out.println("true");
                        Deletepostbutton.setVisibility(View.VISIBLE);
                    }



                    //retrieve
                    String Description = dataSnapshot.child("description").getValue().toString();
                    String uname = dataSnapshot.child("username").getValue().toString();
                    String pdate = dataSnapshot.child("date").getValue().toString();
                    String ptime = dataSnapshot.child("time").getValue().toString();
                     hname = dataSnapshot.child("hivename").getValue().toString();
                    creatorid=dataSnapshot.child("uid").getValue().toString();




                    uid = dataSnapshot.child("uid").getValue().toString();

                    //post image
                    if (dataSnapshot.hasChild("postimage")) {
                        String pimg = dataSnapshot.child("postimage").getValue().toString();
                        Picasso.get().load(pimg).into(ViewPostImage);
                        Glide.with(getApplicationContext()).load(pimg).into(ViewPostImage);
                    }

                    DatabaseReference hve=FirebaseDatabase.getInstance().getReference().child("HIVES").child(hname);
                    hve.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String himg = dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(himg).into(ViewUserImage);
                            Glide.with(getApplicationContext()).load(himg).into(ViewUserImage);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    DatabaseReference usr=FirebaseDatabase.getInstance().getReference().child("Users").child(creatorid);
                    usr.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ViewPostUsername.setText(dataSnapshot.child("username").getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });





/*
                    if (dataSnapshot.hasChild("hiveimage")) {
                        String himg = dataSnapshot.child("hiveimage").getValue().toString();
                        Picasso.get().load(himg).into(ViewUserImage);
                        Glide.with(getApplicationContext()).load(himg).into(ViewUserImage);
                    }

*/
                    ViewPostDisc.setText(Description);
  //                 ViewPostUsername.setText(uname);
                    ViewPostDate.setText(pdate);
                    ViewPostTime.setText(ptime);
                    ViewPHiveName.setText(hname);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        System.out.println(currentuserID);
        System.out.println(uid);


        Deletepostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();

                //Deletepostbutton.setVisibility(View.INVISIBLE);
            }
        });



        ////////////////Raghad
        sendcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRef.child(currentuserid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            username=dataSnapshot.child("username").getValue().toString();
                            if(dataSnapshot.hasChild("profileimg")){
                                profileimage=dataSnapshot.child("profileimg").getValue().toString();
                            }
                            ValidateComment(username,profileimage);
                            commentinput.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });



    }


    private void iniRvComment() {

        CommentsList.setLayoutManager(new LinearLayoutManager(this));

        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey).child("Comments");
        PostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment = new ArrayList<>();
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment);
                }

                //Query query = PostRef.orderByChild()
                //listComment.sort();
                commentsAdapter = new CommentsAdapter(getApplicationContext(),listComment, PostKey);
                CommentsList.setAdapter(commentsAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
    private void ValidateComment(String username, String profileimage) {
        String commentText=commentinput.getText().toString();
        if(TextUtils.isEmpty(commentText)){
            Toast.makeText(this, "الرجاء إدخال تعليق...", Toast.LENGTH_SHORT).show();
        }
        else {

            Calendar calfordate=Calendar.getInstance();
            SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
            savecurrentdate=currentDate.format(calfordate.getTime());

            Calendar calfortime=Calendar.getInstance();
            SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
            savecurrenttime=currentTime.format(calfortime.getTime());




            long time=System.currentTimeMillis();
            final String randomKey=savecurrentdate+savecurrenttime+time+currentuserid;

            HashMap commentsMap=new HashMap();
            commentsMap.put("uid",currentuserid);
            commentsMap.put("comment",commentText);
            commentsMap.put("date",savecurrentdate);
            commentsMap.put("time",savecurrenttime);
            commentsMap.put("username",username);
            commentsMap.put("key",randomKey);/////hhhhh

            if(!TextUtils.isEmpty(profileimage)){
                commentsMap.put("profileimage",profileimage);
            }

            PostRef.child(randomKey).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){

                        Toast.makeText(ViewPost.this, "تم إرسال تعليقك...", Toast.LENGTH_SHORT).show();
                        commentsAdapter.notifyDataSetChanged();//////////

                        if(!(currentuserid.equals(creatorid)))
                        sendNotification();
                    }
                    else {
                        Toast.makeText(ViewPost.this, "حدث خطأ...", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //////comments outside
            if(!(currentuserid.equals(creatorid))) {
                HashMap commentmap = new HashMap();
                commentmap.put("date", savecurrentdate);
                commentmap.put("postkey", PostKey);
                commentmap.put("comment", commentText);
                commentmap.put("username", username);
                commentmap.put("creatorid", creatorid);
                commentmap.put("uid", currentuserid);
                commentmap.put("time", savecurrenttime);

                if (!TextUtils.isEmpty(profileimage)) {
                    commentmap.put("profileimage", profileimage);
                }
                long timee = System.currentTimeMillis();
                //commentRef.child(savecurrentdate+savecurrenttime+ creatorid+PostKey+timee).updateChildren(commentmap);
                commentRef.child(randomKey).updateChildren(commentmap);
            }

        }
    }

    private void sendNotification() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String send_email;


                    //This is a Simple Logic to Send Notification different Device Programmatically....

                    send_email =uid ;


                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic ZWM4NTUyODktMDFkMC00MjY1LWI4MmQtNzM1ZTdkMDljYzQy");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"3b001883-5fd6-4313-b582-c35c30c7a397\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"New comment\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
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
                    Toast.makeText(ViewPost.this,"لا يوجد مستخدم",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }


    public void openDialog() {
        PopUpDelPost dialog = new PopUpDelPost();
        dialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void onYesClicked() {
        ClickPostRef.removeValue();
        DeleteComments(PostKey);
        finish();
    }
    public void DeleteComments(String key){
        final DatabaseReference comref = FirebaseDatabase.getInstance().getReference().child("Comments");
        comref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String key1 = postsnapshot.getKey();
                    if((dataSnapshot.child(key1).child("postkey").getValue().equals(PostKey)))
                        comref.child(key1).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void LikeMaintainStatus() {
        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if ((dataSnapshot.exists())&&(!mAuth.getCurrentUser().getUid().equals(dataSnapshot.child("uid").getValue().toString()))){
                   // countLikes = (int) dataSnapshot.child("Like").getChildrenCount();
                  //  NumberOfLikes.setText(((countLikes) + "Likes"));
                    if(dataSnapshot.child("Like").hasChild(currentuserid)){
                        LikeState ="Liked";
                        LikeBtn.setImageResource(R.drawable.orangeheart);
                        DislikeBtn.setEnabled(false);
                        ///make sure not disLiked
                    }else {
                        LikeState="NotLiked";
                    }
                }
                countLikes = (int) dataSnapshot.child("Like").getChildrenCount();
                NumberOfLikes.setText(((countLikes) + " Likes"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void DisLikeMaintainStatus() {
        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()&&(!mAuth.getCurrentUser().getUid().equals(dataSnapshot.child("uid").getValue().toString()))){

                    //countDisLikes = (int) dataSnapshot.child("disLike").getChildrenCount();
                   // NumberOfdisLikes.setText(((countDisLikes) + " Dislikes"));

                    if(dataSnapshot.child("disLike").hasChild(currentuserid)){
                        disLikeState ="DisLiked";
                        DislikeBtn.setImageResource(R.drawable.orangebrokenheart);
                        ///make sure not disLiked
                        LikeBtn.setEnabled(false);
                    }else {
                        disLikeState="NotDisLiked";

                } }
                if(mAuth.getCurrentUser().getUid().equals(dataSnapshot.child("uid").getValue().toString())) {
                    LikeBtn.setEnabled(false);
                    DislikeBtn.setEnabled(false);
                }
                countDisLikes = (int) dataSnapshot.child("disLike").getChildrenCount();
                NumberOfdisLikes.setText(((countDisLikes) + " Dislikes"));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}