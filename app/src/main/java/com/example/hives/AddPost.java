package com.example.hives;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.BaseBundle;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class AddPost extends AppCompatActivity {

    private ImageView selectpostimage,selectedimage,deleteimage;
    private Button addpost,cancelpost;
    private EditText postdes;
    private static final int Gallerypick=1;
    private Uri imageuri;
    String postdesc;
    private StorageReference postsimageref;
    private String savecurrentdate,savecurrenttime,postrandomname,currentuser,Hivename,hivepic;
    String VHiveName;
    private DatabaseReference usersRef,HiveRef,postref;
    private FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mAuth=FirebaseAuth.getInstance();
        currentuser=mAuth.getCurrentUser().getUid();
        postsimageref= FirebaseStorage.getInstance().getReference();

        VHiveName=getIntent().getExtras().get("HiveName").toString();

        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        HiveRef= FirebaseDatabase.getInstance().getReference().child("HIVES").child(VHiveName);
        postref= FirebaseDatabase.getInstance().getReference().child("Posts");

        HiveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("image")){
                        hivepic=dataSnapshot.child("image").getValue().toString();}
                    if(dataSnapshot.hasChild("title")){
                        Hivename=dataSnapshot.child("title").getValue().toString();}

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Calendar calfordate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        savecurrentdate=currentDate.format(calfordate.getTime());

        Calendar calfortime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        savecurrenttime=currentTime.format(calfortime.getTime());

        postrandomname=savecurrentdate+savecurrenttime;

        selectedimage=(ImageView)findViewById(R.id.postimageview);
        selectpostimage=(ImageView)findViewById(R.id.addpostimage);
        deleteimage=(ImageView)findViewById(R.id.deletepostimage);
        addpost=(Button)findViewById(R.id.createpostbtn);
        cancelpost=(Button)findViewById(R.id.cancelcreatepostbtn);
        postdes=(EditText)findViewById(R.id.postdes);
        VHiveName=getIntent().getExtras().get("HiveName").toString();

        selectpostimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatepostinfo();
            }
        });

        deleteimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteimage.setVisibility(View.INVISIBLE);
                selectedimage.setVisibility(View.INVISIBLE);
                imageuri=null;
            }
        });
        cancelpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void validatepostinfo() {
        postdesc=postdes.getText().toString();
        if (imageuri==null&& TextUtils.isEmpty(postdesc)){
            Toast.makeText(this, "الرجاء ادخال صورة او محتوى لمشاركته...", Toast.LENGTH_SHORT).show();
        }
        else
        if(imageuri!=null&&!imageuri.equals(Uri.EMPTY)){
            Storeimagetostorage();}
        else{
            savepostinfo("");


        }
    }

    private void savepostinfo(final String url) {
        usersRef.child(currentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String username=dataSnapshot.child("username").getValue().toString();
                    HashMap postmap=new HashMap();
                    postmap.put("uid",currentuser);
                    postmap.put("username",username);
                    postmap.put("hivename",Hivename);
                    postmap.put("hiveimage",hivepic);
                    postmap.put("date",savecurrentdate);
                    postmap.put("time",savecurrenttime);
                    if(!TextUtils.isEmpty(postdesc)){
                        postmap.put("description",postdesc);}
                    if(!TextUtils.isEmpty(url)){
                        postmap.put("postimage",url);}
                    postref.child(currentuser+postrandomname).updateChildren(postmap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                finish();
                                Toast.makeText(AddPost.this, "تم نشر الرحيق...", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(AddPost.this, "حدث خطأ...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Storeimagetostorage() {






        final StorageReference filepath=postsimageref.child("post images").child(imageuri.getLastPathSegment()+postrandomname+".jpg");




        filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url=String.valueOf(uri);
                        Toast.makeText(AddPost.this, "تم حفظ الصورة...", Toast.LENGTH_SHORT).show();
                        savepostinfo(url);

                    }
                });
            }
        });


    }


    private void openGallery() {
        Intent galleryIntent= new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallerypick);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Gallerypick&&resultCode==RESULT_OK&&data!=null){
            imageuri=data.getData();
            selectedimage.setImageURI(imageuri);
            selectedimage.setVisibility(View.VISIBLE);
            deleteimage.setVisibility(View.VISIBLE);

        }
    }
}

