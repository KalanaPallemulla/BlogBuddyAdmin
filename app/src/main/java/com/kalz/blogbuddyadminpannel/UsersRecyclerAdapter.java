package com.kalz.blogbuddyadminpannel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.ViewHolder> {


    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    public List<User> user_list;

    public UsersRecyclerAdapter(List<User> user_list){
        this.user_list = user_list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);

        context = parent.getContext();

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        return new UsersRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String user_name = user_list.get(position).getName();

        holder.setUserName(user_name);

    }

    @Override
    public int getItemCount() {
        return user_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private CircleImageView userImage;
        private TextView userName;
        private TextView userAbout;
        private Button userDeleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            userImage = mView.findViewById(R.id.user_user_image);
            userName = mView.findViewById(R.id.user_username);
            userAbout = mView.findViewById(R.id.user_user_about);
            userDeleteBtn = mView.findViewById(R.id.user_delete_btn);
        }


//        public void setBlogImage(String downlaodUri) {
//
//            userImage = mView.findViewById(R.id.blog_post_image);
//
//
//            RequestOptions placeHolder = new RequestOptions();
//            placeHolder.placeholder(R.drawable.blog_image);
//
//            Glide.with(context).applyDefaultRequestOptions(placeHolder).load(downlaodUri).into(userImage);
//
//
//        }

        public void setUserName(String UserName){

            userName = mView.findViewById(R.id.user_username);
            userName.setText(UserName);

        }
    }

}


