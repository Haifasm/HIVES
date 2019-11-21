package com.example.hives;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.widget.EditText;
import android.text.InputType;
import android.content.DialogInterface;
public class settingsActivity extends AppCompatActivity implements PopUpDelAcc.ExampleDialogListener {
    public static final String TAG = settingsActivity.class.getName();
    Button logout, edit, delete, reset;
    DatabaseReference posts, hives, user;
    FirebaseUser currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilesettings);
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        posts = FirebaseDatabase.getInstance().getReference().child("Posts");
        hives = FirebaseDatabase.getInstance().getReference().child("HIVES");
        user = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuser.getUid());
        logout = findViewById(R.id.loggout);
        edit = findViewById(R.id.edit);
        delete = findViewById(R.id.deleteacc);
        reset = findViewById(R.id.forgpas);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent logout = new Intent(settingsActivity.this, SigninActivity.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logout);
                finish();

            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reset = new Intent(settingsActivity.this, ForgotPasswordActivity.class);
                startActivity(reset);

            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(settingsActivity.this, EditProfileActivity.class);
                startActivity(edit);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });


    }

    public void openDialog() {
        PopUpDelAcc dialog = new PopUpDelAcc();
        dialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void onYesClicked() {
       DeleteHives();
       Deleteposts();
       DeleteComments(); //outside comments for notif
       DeleteComments2(); //inside posts
       DeleteAccount();
        }



    public void DeleteAccount(){
        final String[] pass = new String[1];
        //get password
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("فضلا ادخل رقمك السري لاتمام العملية");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
// Set up the buttons
        builder.setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user.removeValue();
                pass[0] = input.getText().toString();
                AuthCredential credential = EmailAuthProvider.getCredential(currentuser.getEmail(),pass[0]);
                currentuser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        currentuser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    System.out.println("deleted");
                                    Intent out = new Intent(settingsActivity.this, SigninActivity.class);
                                    startActivity(out);}
                                else{
                                    System.out.println("something went wrong");
                                }

                            }
                        });
                    }
                });
            }
        });
        builder.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();


    }
    //POSTS
    public void Deleteposts(){
        Query query = posts.orderByChild("uid").equalTo(currentuser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {

                    String key = postsnapshot.getKey();
                    posts.child(key).removeValue();
                    DeleteCommentWithinPost(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void DeleteHives() {
//HIVES

        Query query2 = hives.orderByChild("uid").equalTo(currentuser.getUid());//reference to posts here
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {

                    String key = postsnapshot.getKey();
                    hives.child(key).removeValue();//ignore this
                    Deletepostswithinhive(key);//this should be recycler
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void Deletepostswithinhive(String key) {
        Query query3 = posts.orderByChild("hivename").equalTo(key);//refernce to comments here
        //ignore the rests .. you should perform recycler view here of comments
        query3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {

                    String key1 = postsnapshot.getKey();
                    posts.child(key1).removeValue();
                    DeleteCommentWithinPost(key1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void DeleteComments() {
        final DatabaseReference comref = FirebaseDatabase.getInstance().getReference().child("Comments");
        comref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String key1 = postsnapshot.getKey();
                    if((dataSnapshot.child(key1).child("uid").getValue().equals(currentuser.getUid())))
                        comref.child(key1).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void DeleteComments2() {


        final DatabaseReference postref = FirebaseDatabase.getInstance().getReference().child("Posts");
        postref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()){
                    if(postsnapshot.hasChild("Comments")){
                        for ( DataSnapshot comsnapshot : postsnapshot.getChildren() ) {
                         for(DataSnapshot c : comsnapshot.getChildren()){
                             Comment com = c.getValue(Comment.class); //////////have to cast here!!! to be able to reach the uid inside the comment
                           //  Log.d(TAG,"uid is = "+com.getUid());
                             if(currentuser.getUid().equals(com.getUid()))
                                // Log.d(TAG,"trueeeee!!!!!!!!!");
                             postref.child(postsnapshot.getKey()).child("Comments").child(c.getKey()).removeValue(); ////// have to get the post key from prev snapshot
                         }

                        }
                    } else{
                        Log.d(TAG,"There is no comments");
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });




    }

    public void DeleteCommentWithinPost(final String key){
        final DatabaseReference comref = FirebaseDatabase.getInstance().getReference().child("Comments");
        comref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String key1 = postsnapshot.getKey();
                    if((dataSnapshot.child(key1).child("postkey").getValue().equals(key)))
                        comref.child(key1).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}