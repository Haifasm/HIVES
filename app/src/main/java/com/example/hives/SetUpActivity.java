package com.example.hives;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity {

    //manifest change



    private EditText UserName,Bio;
    private Button SaveInformation;
    private CircleImageView ProfileImg;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;

    private ProgressDialog loadingBar;
    final static int Gallerypick=1;
    String currentUserId;
    Uri resultUri;
    FirebaseStorage firebaseStorage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UserProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile images");

        UserName=(EditText) findViewById(R.id.setup_username);
        Bio=(EditText) findViewById(R.id.setup_bio);
        SaveInformation=(Button) findViewById(R.id.setup_button);
        ProfileImg=(CircleImageView) findViewById(R.id.setup_profileimg);
        loadingBar=new ProgressDialog(this);

        SaveInformation.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view){
                SaveAccountSetupInformation();

            }
        });


        ProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent= new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallerypick);
            }
        });
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    if (dataSnapshot.hasChild("profileimg")){
                        String image=dataSnapshot.child("profileimg").getValue().toString();
                        ProfileImg.setImageURI(resultUri);
                        
                    }
                    else{
                       // Toast.makeText(SetUpActivity.this, "الرجاء اختيار صورة العرض", Toast.LENGTH_SHORT).show();
                    }
                    
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Gallerypick&&resultCode==RESULT_OK&&data!=null){
            Uri ImageUri=data.getData();
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);

        }
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){

                loadingBar.setTitle("صورة العرض");
                loadingBar.setMessage("الرجاء الانتظار حتى يتم الانتهاء من تحديث صورة العرض...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                resultUri=result.getUri();

                final StorageReference filepath=UserProfileImageRef.child(currentUserId + ".jpg");

                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url=String.valueOf(uri);


                                UsersRef.child("profileimg").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            //Intent selfIntent = new Intent(SetUpActivity.this,SetUpActivity.class);
                                            // startActivity(selfIntent);

                                            Toast.makeText(SetUpActivity.this, "تم حفظ صورة العرض بنجاح...", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                        else{
                                            Toast.makeText(SetUpActivity.this, "حدث خطأ...", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                });
                            }
                        });

                    }
                });

            }
            else {
                Toast.makeText(this, "لا يمكن اقتصاص صورة العرض حاول مرة اخرى...", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }

        }
    }



    private void SaveAccountSetupInformation(){
        String username=UserName.getText().toString();
        String bio=Bio.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "الرجاء ادخال اسم المستخدم", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(bio)){
            Toast.makeText(this, "الرجاء ادخال النبذة", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("جاري إنشاء حسابك");
            loadingBar.setMessage("الرجاء الانتظار حتى يتم الانتهاء من انشاء الحساب");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);



            HashMap userMap=new HashMap();
            userMap.put("username",username);
            userMap.put("bio",bio);

            //bio maybe null********* make sure that it works..........................................

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful()){
                        sendUserToMainActivity();
                        Toast.makeText(SetUpActivity.this, "تم إنشاء الحساب بنجاح", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }

                    else{
                        Toast.makeText(SetUpActivity.this, "حدث خطأ", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }
            });

        }

    }
    private void sendUserToMainActivity(){
        Intent mainIntent=new Intent(SetUpActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }



}
