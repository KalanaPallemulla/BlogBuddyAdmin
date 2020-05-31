package com.kalz.blogbuddyadminpannel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {


    private CircleImageView setupImage;
    private Uri mainImageURI = null;

    private String user_id;

    private boolean isChanged = false;

    private EditText setupName;
    private EditText setupAbout;
    private EditText setupMobile;
    private Button setupBtn;
    private ProgressBar setupProgress;
    private Button setupCancel;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        firebaseAuth = FirebaseAuth.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        Toolbar setupToolbar = findViewById(R.id.setup_toolbar);

        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setup");

        setupName = findViewById(R.id.setup_name);
        setupAbout =findViewById(R.id.setup_about);
        setupMobile = findViewById(R.id.setup_mobile);
        setupBtn = (Button) findViewById(R.id.setup_button);
        setupProgress = findViewById(R.id.setup_progress);
        setupCancel = findViewById(R.id.setup_cancel_btn);

        setupImage = (CircleImageView) findViewById(R.id.setup_image);

        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    if(task.getResult().exists()){

                        String name = task.getResult().getString("name");
                        String about = task.getResult().getString("about");
                        String number = task.getResult().getString("mobileNumber");
                        String image = task.getResult().getString("image");

                        setupName.setText(name);
                        setupAbout.setText(about);
                        setupMobile.setText(number);

                        RequestOptions placeHolderRequest = new RequestOptions();
                        placeHolderRequest.placeholder(R.drawable.default_image);

                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeHolderRequest).load(image).into(setupImage);

                    }


                }else{

                    String e = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "Firestore retrive Error :"+ e,Toast.LENGTH_LONG).show();

                }

                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);
            }
        });

        setupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(setupName != null && setupImage != null && setupAbout != null){

                    Intent setupMain = new Intent(SetupActivity.this, AllBlogsActivity.class);
                    startActivity(setupMain);
                    finish();

                } else {

                    Toast.makeText(SetupActivity.this, "You can't goto main page without completing your profile",Toast.LENGTH_LONG).show();

                }

            }
        });

        try {

            setupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String name = setupName.getText().toString();
                    final String about = setupAbout.getText().toString();
                    final String number = setupMobile.getText().toString();




                    setupProgress.setVisibility(View.VISIBLE);




                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(about) && mainImageURI != null ) {



                        user_id = firebaseAuth.getCurrentUser().getUid();


                        StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {

                                    storeFirestore(task, name, about, number);

                                } else {

                                    String Error = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this, "Error :" + Error, Toast.LENGTH_LONG).show();
                                    setupProgress.setVisibility(View.INVISIBLE);

                                }


                            }
                        });

                    } else {

                        Toast.makeText(SetupActivity.this, "Some field is Empty", Toast.LENGTH_LONG).show();
                        setupProgress.setVisibility(View.INVISIBLE);

                    }
                }



            });
        }catch (Exception e){

            Toast.makeText(SetupActivity.this,"Error :"+ e.getMessage(),Toast.LENGTH_LONG).show();

        }
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(SetupActivity.this,"Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                    }else{

                        bringImagePicker();

                    }

                }else {

                    bringImagePicker();

                }
            }


        });

    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String name,String about,String number) {

        Uri download_uri;

        if (task != null) {
            download_uri = task.getResult().getDownloadUrl();
        }else {
            download_uri = mainImageURI;
        }

        Map<String,String> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("about", about);
        userMap.put("mobileNumber",number);
        userMap.put("image", download_uri   .toString());

        Uri downloadUri = task.getResult().getDownloadUrl();
        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(SetupActivity.this, "User settings are updated",Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(SetupActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else {

                    String e = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "Firestore Error :"+ e,Toast.LENGTH_LONG).show();

                }

                setupProgress.setVisibility(View.INVISIBLE);

            }
        });

    }

    private void bringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
