package com.kalz.blogbuddyadminpannel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AllBlogsActivity extends AppCompatActivity {

    private Toolbar mainToolBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

//    private FloatingActionButton addPostBtn;
    private String currentUserId;

    private BottomNavigationView mainBottomNav;

    private HomeFragment homeFragment;
    private UserFragmentt userFragmentt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_blogs);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();







        mainToolBar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolBar);

        getSupportActionBar().setTitle("Blog Buddy Admin Blogs");

        if(mAuth.getCurrentUser() != null) {

//            addPostBtn = findViewById(R.id.add_post_btn);
            mainBottomNav = findViewById(R.id.mainBottomNav);





            //Fragments

            homeFragment = new HomeFragment();
            userFragmentt = new UserFragmentt();


            replaceFragment(homeFragment);

            mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.bottum_action_Home:
                            replaceFragment(homeFragment);
                            return true;

                        



                        default:
                            return false;
                    }


                }
            });



//            addPostBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Intent postIntent = new Intent(AllBlogsActivity.this, NewPostActivity.class);
//                    startActivity(postIntent);
//                    finish();
//
//                }
//            });
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser == null){

            Intent firstIntent = new Intent(AllBlogsActivity.this, MainActivity.class);
            startActivity(firstIntent);
            finish();

        }else{

            currentUserId = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        if(!task.getResult().exists()){

                            Intent setupIntent = new Intent(AllBlogsActivity.this, SetupActivity.class);
                            startActivity(setupIntent);
                            finish();

                        }

                    }else {
                        String error = task.getException().getMessage();
                        Toast.makeText(AllBlogsActivity.this, "Error :" + error,Toast.LENGTH_LONG).show();

                    }

                }
            });

        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_logout_btn:

                logOut();
                return true;

            case  R.id.action_accunt_btn:
                Intent setupIntent = new Intent(AllBlogsActivity.this, SetupActivity.class);
                startActivity(setupIntent);
                finish();


            default:
                return false;
        }


    }

    private void logOut() {

        mAuth.signOut();
        sendToLogin();

    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(AllBlogsActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();


    }

    private  void  replaceFragment (Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }
}
