package com.kalz.blogbuddyadminpannel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list;
    public List<User> user_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;








    public BlogRecyclerAdapter(List<BlogPost> blog_list, List<User> user_list) {

        this.blog_list = blog_list;
        this.user_list = user_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);

        context = parent.getContext();

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

            final String blogPostId = blog_list.get(position).BlogPostId;
        final String currentUser = mAuth.getCurrentUser().getUid();

        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);

        final String topic_data = blog_list.get(position).getTopic();
        holder.setTopicText(topic_data);

        String image_uri = blog_list.get(position).getImage_url();
        holder.setBlogImage(image_uri);


        final String blog_user_id = blog_list.get(position).getUser_id();



            holder.blogDeleteBtn.setEnabled(true);




        String userName = user_list.get(position).getName();
        String userImage = user_list.get(position).getImage();

        holder.setUserData(userName,userImage);






        long millisecond = blog_list.get(position).getTimestamp().getTime();
        String dateString = new SimpleDateFormat("MM.dd.yyyy").format(new Date(millisecond));
        holder.setTime(dateString);

        //Get Likes Count

        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty()){

                    int count = queryDocumentSnapshots.size();
                    holder.updateLikeCount(count);


                }else {

                    holder.updateLikeCount(0);

                }

            }
        });

        //Get Comment Count

        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty()){

                    int cCount = queryDocumentSnapshots.size();
                    holder.updateCommentCount(cCount);

                }else {
                    holder.updateCommentCount(0);
                }

            }
        });

        //Comment Feature

        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentActivity.class);
                commentIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentIntent);

            }
        });

        holder.blogDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.deleteProgressBar.setVisibility(View.VISIBLE);

                Toast.makeText(context,"Deleting, This take some time.. Be Patint",Toast.LENGTH_LONG).show();

                firebaseFirestore.collection("Posts").document(blogPostId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        blog_list.remove(position);
//                        user_list.remove(position);


                    }
                });

                holder.deleteProgressBar.setVisibility(View.INVISIBLE);

            }
        });




        //Get Likes

        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){

                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.fav_red));

                } else {

                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.fav_gray));
                }

            }
        });

        //Like feature

        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){

                            Map<String, Object> likesMap = new HashMap<>();

                            likesMap.put("timestamp", FieldValue.serverTimestamp());


                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUser).set(likesMap);

                        } else {

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUser).delete();


                        }

                    }
                });

            }
        });







    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView descView;
        private TextView topicView;
        private ImageView blogImageView;
        private TextView userNameView;
        private CircleImageView blogUserImage;
        private TextView blogDate;
        private ImageView blogLikeBtn;
        private TextView blogLikeCount;
        private ImageView commentBtn;
        private TextView commentCount;
        private Button blogDeleteBtn;
        private ProgressBar deleteProgressBar;





        public ViewHolder (@NonNull final View itemView) {
            super(itemView);

            mView = itemView;

            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogLikeCount = mView.findViewById(R.id.blog_like_count);

            commentBtn = mView.findViewById(R.id.comment_btn);
            commentCount = mView.findViewById(R.id.comment_count);



            blogDeleteBtn = mView.findViewById(R.id.blog_delete_btn);

            deleteProgressBar = mView.findViewById(R.id.delete_progress);







        }
        public void setDescText (String descText){

            descView = mView.findViewById(R.id.blog_post_desc);
            descView.setText(descText);

        }

        public  void setTopicText (String topicText){

            topicView = mView.findViewById(R.id.blog_post_topic);
            topicView.setText(topicText);

        }

        public void setBlogImage(String downlaodUri){

            blogImageView = mView.findViewById(R.id.blog_post_image);


            RequestOptions placeHolder = new RequestOptions();
            placeHolder.placeholder(R.drawable.blog_image);

            Glide.with(context).applyDefaultRequestOptions(placeHolder).load(downlaodUri).into(blogImageView);



        }

        public void setUserData (String userName, String image){

            userNameView = mView.findViewById(R.id.blog_user_name);
            blogUserImage = mView.findViewById(R.id.blog_user_image);

            userNameView.setText(userName);

            RequestOptions placeHolderOption = new RequestOptions();
            placeHolderOption.placeholder(R.drawable.blog_user_image);

            Glide.with(context).applyDefaultRequestOptions(placeHolderOption).load(image).into(blogUserImage);



        }

        public void updateLikeCount(int count){

            blogLikeCount = mView.findViewById(R.id.blog_like_count);
            blogLikeCount.setText(count + " Likes");

        }

        public void updateCommentCount(int cCount){

            commentCount = mView.findViewById(R.id.comment_count);
            commentCount.setText(cCount + "Comments");

        }


        public void setTime(String Date){

            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(Date);



        }

    }


}
