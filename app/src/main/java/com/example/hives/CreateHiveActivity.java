package com.example.hives;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateHiveActivity extends AppCompatActivity
{
    private Toolbar mtoolbar;
    private ProgressDialog loadingBar;

    private Button hivebutton;
    private EditText hivename;
    private EditText hiveinfo;
    private CircleImageView hivepict;
    private Uri imageuri;
    private String hiven,hivedes;
    private String savecurrentdate,savecurrenttime,hiverandomname,downloaduri;


    private DatabaseReference userRef,hivesref;
    private StorageReference hiveimageref;
    private FirebaseAuth mAuth;

    private static final int Gallerypick=1;
    private String currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hive);

        hiveimageref= FirebaseStorage.getInstance().getReference();

        hivebutton= (Button)findViewById(R.id.createhivebtn);
        hivename=(EditText) findViewById(R.id.hiveName);
        hiveinfo=(EditText)findViewById(R.id.hiveInfo);
        hivepict=(CircleImageView) findViewById(R.id.hivepic);
        loadingBar=new ProgressDialog(this);

        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        hivesref= FirebaseDatabase.getInstance().getReference().child("HIVES");
        mAuth=FirebaseAuth.getInstance();

        currentUser=mAuth.getCurrentUser().getUid();






        //image code

        hivepict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        hivebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateHive();
            }
        });

    }

    private void OpenGallery() {
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
            hivepict.setImageURI(imageuri);

        }
    }



    private void validateHive(){
        hiven=hivename.getText().toString();
        hivedes=hiveinfo.getText().toString();
        if (imageuri==null){
            Toast.makeText(this, "الرجاء اختيار صورة للقفير", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(hiven)){

            Toast.makeText(this, "الرجاء ادخال اسم القفير", Toast.LENGTH_SHORT).show();
        }



        if (!TextUtils.isEmpty(hiven)&&imageuri!=null) {
            hivesref.child(hiven).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Toast.makeText(CreateHiveActivity.this, "الرجاء ادخال اسم آخر،اسم القفير موجود مسبقاً..", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        //if(imageuri!=null){
                        storeimagetostorage();
                        // }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
    }

    private void storeimagetostorage() {
        Calendar calfordate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        savecurrentdate=currentDate.format(calfordate.getTime());

        Calendar calfortime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        savecurrenttime=currentTime.format(calfortime.getTime());


        hiverandomname=savecurrentdate+savecurrenttime;


        final StorageReference filepath=hiveimageref.child("hive images").child(imageuri.getLastPathSegment()+hiverandomname+".jpg");




        filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url=String.valueOf(uri);
                        Toast.makeText(CreateHiveActivity.this, "تم حفظ الصورة...", Toast.LENGTH_SHORT).show();
                        savingHiveInformation(url);

                    }
                });
            }
        });


    }


    private void savingHiveInformation(final String url){
        userRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String username=dataSnapshot.child("username").getValue().toString();
                    HashMap hivesmap=new HashMap();
                    hivesmap.put("uid",currentUser);
                    hivesmap.put("title",hiven);
                    hivesmap.put("hiveinfo",hivedes);
                    hivesmap.put("username",username);
                    hivesmap.put("image",url);
                    hivesmap.put(currentUser,currentUser);


                    hivesref.child(hiven).updateChildren(hivesmap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){

                                // sendUserToMainActivity();
                                Toast.makeText(CreateHiveActivity.this, "تم إنشاء القفير", Toast.LENGTH_SHORT).show();
                                sendUserToMainActivity();
                            }
                            else {
                                Toast.makeText(CreateHiveActivity.this, "حدث خطأ", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if (id== android.R.id.home){
            sendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }
    private void sendUserToMainActivity(){
        Intent mainIntent=new Intent(CreateHiveActivity.this,searchActivity.class);
        startActivity(mainIntent);

    }
}