package com.kalz.blogbuddyadminpannel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button loginBtn;
    private Button regBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = findViewById(R.id.first_login_btn);
        regBtn = findViewById(R.id.first_join_btn);



        try {
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent logIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(logIntent);
                    finish();
                }
            });
        } catch (Exception e){
            Toast.makeText(MainActivity.this,"Error :" + e,Toast.LENGTH_LONG).show();
        }



        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent regIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(regIntent);
                finish();

            }
        });

    }
}
