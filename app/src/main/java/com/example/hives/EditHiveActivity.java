package com.example.hives;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import de.hdodenhof.circleimageview.CircleImageView;


public class EditHiveActivity extends AppCompatActivity implements DeletHiveDialog.ExampleDialogListener{

    private EditText editHiveInfo;
    private Button editcancelbutton,editbutton,DeleteHiveBtn,OpenPopUpBtn;
    private CircleImageView edthivepic;
    private DatabaseReference editHiveRef;
    private FirebaseAuth mAuth;
    String VHiveName;
    private static final int Gallerypick=1;
    private StorageReference hiveimageref;
    private Uri imageuri;
    private String savecurrentdate,savecurrenttime,hiverandomname,downloadurl;
    private String currentUser;
    private DatabaseReference userRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hive);

        VHiveName=getIntent().getExtras().get("HiveName").toString();

        editHiveInfo=(EditText)findViewById(R.id.edithiveInfo);
        editcancelbutton=(Button)findViewById(R.id.editcancelbtn);
        editbutton=(Button)findViewById(R.id.editbtn);
        edthivepic=(CircleImageView)findViewById(R.id.editHivepic);

        mAuth=FirebaseAuth.getInstance();
        editHiveRef= FirebaseDatabase.getInstance().getReference().child("HIVES").child(VHiveName);

        hiveimageref= FirebaseStorage.getInstance().getReference().child("hive images");
        currentUser=mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        OpenPopUpBtn=(Button)findViewById(R.id.OpenPopUpBtn);

        OpenPopUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenPopUp();
            }
        });


        editHiveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("image")){
                        String Hivepic=dataSnapshot.child("image").getValue().toString();
                        Glide.with(getApplicationContext()).load(Hivepic).into(edthivepic);}
                    if(dataSnapshot.hasChild("hiveinfo")){
                        String HiveInfo=dataSnapshot.child("hiveinfo").getValue().toString();
                        editHiveInfo.setText(HiveInfo);}

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editcancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageuri!=null&&!imageuri.equals(Uri.EMPTY)){
                    storeimagetostorage();}
                else{
                    savingHiveInformation("");
                }
            }
        });


        edthivepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent= new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallerypick);


            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Gallerypick&&resultCode==RESULT_OK&&data!=null){
            imageuri=data.getData();
            edthivepic.setImageURI(imageuri);

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
                        Toast.makeText(EditHiveActivity.this, "تم حفظ الصورة...", Toast.LENGTH_SHORT).show();
                        savingHiveInformation(url);

                    }
                });
            }
        });



        OpenPopUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent popUpIntent = new Intent(EditHiveActivity.this,DeletHiveDialog.class);
                startActivity(popUpIntent);
            }
        });
    }

    private void Deletehive() {
        if(mAuth!=null && currentUser!=null) {
            editHiveRef.setValue(null);
            //editHiveRef.removeValue();
            Toast.makeText(EditHiveActivity.this, "تم حذف القفير بنجاح ..", Toast.LENGTH_SHORT).show();
            finish();
            sendUserToStartActivity();
        }
    }
    private void sendUserToStartActivity() {
        Intent MainIntent = new Intent(EditHiveActivity.this,MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }

    private void OpenPopUp() {
        DeletHiveDialog pop = new DeletHiveDialog();
        pop.show(getSupportFragmentManager(), "example dialog");
    }
    @Override
    public void onYesClicked() {
        Deletehive();
    }

    private void savingHiveInformation(final String url){
        userRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String Hiveinf=editHiveInfo.getText().toString();
                    HashMap hivesmap = new HashMap();
                    hivesmap.put("hiveinfo",Hiveinf);
                    if (!(url.isEmpty())) {
                        hivesmap.put("image", url);
                    }

                    editHiveRef.updateChildren(hivesmap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditHiveActivity.this, "تم تعديل القفير بنجاح...", Toast.LENGTH_SHORT).show();
                                //Intent intent = new Intent(Intent.ACTION_MAIN);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                //startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(EditHiveActivity.this, "حدث خطأ...", Toast.LENGTH_SHORT).show();

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





}

