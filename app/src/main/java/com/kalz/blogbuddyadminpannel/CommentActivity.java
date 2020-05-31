package com.kalz.blogbuddyadminpannel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private Toolbar commentToolbar;

    private EditText commentField;
    private ImageView commentPostBtn;

    private RecyclerView commentsList;
    private List<Comments> comments_list;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private CommentsRecyclerAdapter commentsRecyclerAdapter;

    private String blogPostId;
    private String currentUserId;

    private ProgressBar commentProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentToolbar = findViewById(R.id.comment_toolbar);
        setSupportActionBar(commentToolbar);
        getSupportActionBar().setTitle("    Comments");

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        currentUserId = firebaseAuth.getCurrentUser().getUid();

        blogPostId = getIntent().getStringExtra("blog_post_id");

        commentField = findViewById(R.id.comment_field);
        commentPostBtn = findViewById(R.id.comment_post_btn);

        commentsList = findViewById(R.id.comment_list);

        commentProgress = findViewById(R.id.comment_progressbar);

        //RecyclerView firebase list

        comments_list = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(comments_list);
        commentsList.setHasFixedSize(true);
        commentsList.setLayoutManager(new LinearLayoutManager(this));
        commentsList.setAdapter(commentsRecyclerAdapter);


        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener(CommentActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {



                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String commentId = doc.getDocument().getId();
                        Comments comments = doc.getDocument().toObject(Comments.class);
                        comments_list.add(comments);
                        commentsRecyclerAdapter.notifyDataSetChanged();


                    }

                }

            }

        });



        commentPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                commentProgress.setVisibility(View.VISIBLE);

                String comment_message = commentField.getText().toString();

                if(!comment_message.isEmpty()){

                    Map<String,Object> commentMap = new HashMap<>();
                    commentMap.put("message", comment_message);
                    commentMap.put("user_id",currentUserId);
                    commentMap.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {



                            if(!task.isSuccessful()){
                                commentProgress.setVisibility(View.INVISIBLE);

                                Toast.makeText(CommentActivity.this,"Something you did wrong"+ task.getException().getMessage() ,Toast.LENGTH_LONG).show();

                            }else {

                                commentField.setText("");
                                Toast.makeText(CommentActivity.this,"Your Comment is added",Toast.LENGTH_LONG).show();
                            }
                            commentProgress.setVisibility(View.INVISIBLE);

                        }
                    });

                }

            }
        });

    }
}
