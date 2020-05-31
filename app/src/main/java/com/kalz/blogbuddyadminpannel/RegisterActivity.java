package com.kalz.blogbuddyadminpannel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText regEmail;
    private EditText regPass;
    private EditText regConfirmPass;
    private Button regBtn;
    private TextView backToMain;
    private ProgressBar regProgress;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        regEmail = (EditText) findViewById(R.id.reg_email);
        regPass = (EditText) findViewById(R.id.reg_password);
        regConfirmPass = (EditText) findViewById(R.id.reg_confirm_pass);
        regBtn = (Button) findViewById(R.id.reg_btn);
        regProgress = (ProgressBar) findViewById(R.id.reg_progress);
        backToMain = findViewById(R.id.back_to_main_reg);


        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent regFirstIntent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(regFirstIntent);
                finish();

            }
        });


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = regEmail.getText().toString();
                String pass = regPass.getText().toString();
                String passCon = regConfirmPass.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(passCon)){

                    if(pass.length() >= 8) {

                        if((pass.equals("Password")) || (pass.equals("password"))) {

                            if (pass.equals(passCon)) {

                                regProgress.setVisibility(View.VISIBLE);

                                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {

                                            Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                            startActivity(setupIntent);
                                            finish();

                                        } else {

                                            String errorMessage = task.getException().getMessage();
                                            Toast.makeText(RegisterActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                                        }


                                        regProgress.setVisibility(View.INVISIBLE);

                                    }
                                });

                            } else {

                                Toast.makeText(RegisterActivity.this, "Password and Confirm Password doesn't match", Toast.LENGTH_LONG).show();

                            }
                        }else{
                            Toast.makeText(RegisterActivity.this,"Your can not use 'password' or 'Password' as your password",Toast.LENGTH_LONG).show();
                        }
                    }else{

                        Toast.makeText(RegisterActivity.this, "Password should have more than 8 characters", Toast.LENGTH_LONG).show();

                    }



                }else {


                    if(TextUtils.isEmpty(email)){

                        Toast.makeText(RegisterActivity.this,"Error :Your Email is Empty", Toast.LENGTH_LONG).show();

                    }else if(TextUtils.isEmpty(pass)){

                        Toast.makeText(RegisterActivity.this,"Error :Your Password is Empty", Toast.LENGTH_LONG).show();

                    }else if(TextUtils.isEmpty(passCon)){

                        Toast.makeText(RegisterActivity.this,"Error :Your Confirm Password is Empty", Toast.LENGTH_LONG).show();

                    }else {

                        Toast.makeText(RegisterActivity.this, "Error : Something goes wrong", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//        if(currentUser != null){
//
//            sendToMain();
//
//        }
//    }

    private void sendToMain() {

        Intent regMainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(regMainIntent);
        finish();

    }
}
