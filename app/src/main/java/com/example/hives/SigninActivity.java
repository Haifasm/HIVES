package com.example.hives;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity {

    private EditText userMail, userPassword;
    private Button BtnSignin;
    private FirebaseAuth mAuth;
    private TextView Forgotpass;
    private Intent Mainact;
    private Button Gosignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        userMail = findViewById(R.id.signin_Mail);
        userPassword = findViewById(R.id.signin_Password);
        BtnSignin = findViewById(R.id.signinBtn);
        Forgotpass = findViewById(R.id.forgotpass);

        Gosignup = findViewById(R.id.gosignup);
        Gosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninActivity.this, Register.class));
            }
        });

        Forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //forgot password page
               startActivity(new Intent(SigninActivity.this, ForgotPasswordActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        Mainact =new Intent(SigninActivity.this, MainActivity.class);

        BtnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String Email =userMail.getText().toString();
                final String Password = userPassword.getText().toString();

                if( Email.isEmpty() || Password.isEmpty()){
                    showMessage("Please Verify All Field");
                }
                else{
                    signin(Email,Password);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    // boolean emailVerified = user.isEmailVerified();
                    //if(!(user.isEmailVerified())){
                        //FirebaseAuth.getInstance().signOut();
                       // Intent logoIntent = new Intent(SigninActivity.this, SigninActivity.class);
                      //  logoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       // startActivity(logoIntent);
                       // showMessage("الرجاء التحقق من البريد الالكتروني");
                    //}
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser=mAuth.getCurrentUser();
       if (currentUser!=null&&currentUser.isEmailVerified())
        {

            SendUserToMainActivity();


        }
    }

    private void signin(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    if (mAuth.getCurrentUser().isEmailVerified()) {

                        SendUserToMainActivity();
                    }
                    else{
                        showMessage("الرجاء التحقق من البريد الالكتروني وتأكيد الحساب...");
                    }
                }
                else {
                    showMessage("الرجاء التأكد من البريد الالكتروني وكلمة المرور...");
                   // showMessage(task.getException().getMessage());
                }

            }
        });

    }

    private void SendUserToMainActivity(){
        Intent mainintent =new Intent(SigninActivity.this,MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();

    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text,Toast.LENGTH_LONG).show();
    }
}
