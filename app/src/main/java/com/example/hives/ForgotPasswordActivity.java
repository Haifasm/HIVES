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
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText mail;
    private Button Reset;
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        //Toolbar
        mtoolbar=(Toolbar)findViewById(R.id.createhivetoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        mail = findViewById(R.id.FPemail);
        Reset = findViewById(R.id.FPbtn);

        mAuth = FirebaseAuth.getInstance();

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email =mail.getText().toString();

                if (Email.isEmpty()){
                    Toast.makeText(getApplicationContext(),"ادخل بريدك الالكتروني",Toast.LENGTH_LONG).show();
                }
                else{
                    mAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"لقد تم ارسال رسالة التفعيل الى بريدك الالكتروني",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ForgotPasswordActivity.this,SigninActivity.class));

                            }
                            else {
                                String Error = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(),Error,Toast.LENGTH_LONG).show();
                                
                            }
                        }
                    });
                }
            }
        });
    }
}
