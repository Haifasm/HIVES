package com.example.hives;

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

public class EditProfileActivity extends AppCompatActivity {
    private EditText editprofileInfo, editprofilename;
    private Button editcancelbutton, editbutton;
    private CircleImageView edtprofilepic;
    private DatabaseReference editprofRef;
    private FirebaseAuth mAuth;
    String VHiveName;
    private static final int Gallerypick = 1;
    private StorageReference profimageref;
    private Uri imageuri;
    private String savecurrentdate, savecurrenttime, hiverandomname, downloadurl;
    private String currentUser;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        editprofileInfo = (EditText) findViewById(R.id.editProfInfo);
        editprofilename = (EditText) findViewById(R.id.editProfName);
        editcancelbutton = (Button) findViewById(R.id.editcancelbtn);
        editbutton = (Button) findViewById(R.id.editbtn);
        edtprofilepic = (CircleImageView) findViewById(R.id.editprofilepic);

        mAuth = FirebaseAuth.getInstance();
        profimageref = FirebaseStorage.getInstance().getReference().child("Profile images");
        currentUser = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        editprofRef = userRef.child(currentUser);

        editprofRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profileimg")) {
                        String pic = dataSnapshot.child("profileimg").getValue().toString();
                        Glide.with(getApplicationContext()).load(pic).into(edtprofilepic);
                    }
                    if (dataSnapshot.hasChild("bio")) {
                        String Info = dataSnapshot.child("bio").getValue().toString();
                        editprofileInfo.setText(Info);
                    }
                    if (dataSnapshot.hasChild("username")) {
                        String name = dataSnapshot.child("username").getValue().toString();
                        editprofilename.setText(name);
                    }

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
                if (imageuri != null && !imageuri.equals(Uri.EMPTY)) {
                    storeimagetostorage();
                } else {
                    savingHiveInformation("");
                }
            }
        });


        edtprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallerypick);


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallerypick && resultCode == RESULT_OK && data != null) {
            imageuri = data.getData();
            edtprofilepic.setImageURI(imageuri);

        }
    }

    private void storeimagetostorage() {
        Calendar calfordate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        savecurrentdate = currentDate.format(calfordate.getTime());

        Calendar calfortime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        savecurrenttime = currentTime.format(calfortime.getTime());


        hiverandomname = savecurrentdate + savecurrenttime;


        final StorageReference filepath = profimageref.child(imageuri.getLastPathSegment() + hiverandomname + ".jpg");


        filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = String.valueOf(uri);
                        Toast.makeText(EditProfileActivity.this, "تم حفظ الصورة...", Toast.LENGTH_SHORT).show();
                        savingHiveInformation(url);

                    }
                });
            }
        });


    }

    private void savingHiveInformation(final String url) {
        FirebaseDatabase.getInstance().getReference().child("HIVES").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String info = editprofileInfo.getText().toString();
                    String name = editprofilename.getText().toString();
                    HashMap hivesmap = new HashMap();
                    hivesmap.put("bio", info);
                    hivesmap.put("username", name);
                    if (!(url.isEmpty())) {
                        hivesmap.put("profileimg", url);
                    }

                    editprofRef.updateChildren(hivesmap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditProfileActivity.this, "تم تعديل معلومات الحساب بنجاح...", Toast.LENGTH_SHORT).show();
                                //Intent intent = new Intent(Intent.ACTION_MAIN);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                //startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(EditProfileActivity.this, "حدث خطأ...", Toast.LENGTH_SHORT).show();

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