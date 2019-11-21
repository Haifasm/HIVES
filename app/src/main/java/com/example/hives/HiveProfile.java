package com.example.hives;


    import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;


public class HiveProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button AddPostbtn = (Button)findViewById(R.id.button4);
        AddPostbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenNewPost();
            }
        });

    }
    private void OpenNewPost() {
        Intent intent=new Intent(HiveProfile.this,AddPost.class);
        startActivity(intent);
    }
}
