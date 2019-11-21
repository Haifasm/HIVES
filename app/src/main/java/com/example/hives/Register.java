package com.example.hives;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {



    private EditText userEmail, userPassword, userPassword2,userName;
    private Button regBtn;

    private Button Gosignin;

    private FirebaseAuth mAuth;
    Toolbar mtoolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Toolbar

        mtoolbar=(Toolbar)findViewById(R.id.createhivetoolbar);
        setSupportActionBar(mtoolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setTitle("التسجيل");


        userEmail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPass);
        userPassword2 = findViewById(R.id.regPass2);


        regBtn = findViewById(R.id.regBtn);
        Gosignin =findViewById(R.id.gosignup);
        Gosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, SigninActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String Email =userEmail.getText().toString();
                final String Password= userPassword.getText().toString();
                final String Password2= userPassword2.getText().toString();
                final String Name =userName.getText().toString();

                if( Email.isEmpty() || Password.isEmpty() || Password2.isEmpty() || Name.isEmpty() || !Password.equals(Password2)){
                    showMessage("Please Verify All Fields");

                }
                else {

                    CreateUserAccount(Email,Name,Password);
                }
            }
        });
    }




    private void CreateUserAccount(String email, String name, String password) {

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.sendEmailVerification();
                    showMessage("تم التسجيل، تفقد البريد الالكتروني لتسجيل الدخول...");
                    sendUserToLoginActivity();
                }
                else
                   showMessage("حدث خطأ...");
            }
        });



    }

    // method to show a message
    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_LONG).show();
    }
    private void sendUserToLoginActivity(){
        Intent setupintent=new Intent(Register.this,SigninActivity.class);
        setupintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupintent);
        finish();
    }




}


