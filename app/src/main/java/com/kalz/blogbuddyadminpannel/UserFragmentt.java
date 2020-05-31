package com.kalz.blogbuddyadminpannel;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragmentt extends Fragment {

    private RecyclerView userListView;
    private List<User> user_list;

    private FirebaseFirestore firebaseFirestore;
    private UsersRecyclerAdapter usersRecyclerAdapter;


    public UserFragmentt() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =inflater.inflate(R.layout.fragment_home, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        user_list = new ArrayList<>();
        userListView = view.findViewById(R.id.user_list_view);
        usersRecyclerAdapter = new UsersRecyclerAdapter(user_list);


        try {

            userListView.setLayoutManager(new LinearLayoutManager(getActivity()));
            userListView.setAdapter(usersRecyclerAdapter);
        }catch (Exception e){
            Toast.makeText(getActivity(),"Error : " + e,Toast.LENGTH_LONG).show();
        }


        firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                    if(doc.getType() == DocumentChange.Type.ADDED){

                        User user = doc.getDocument().toObject(User.class);
                        user_list.add(user);

                        usersRecyclerAdapter.notifyDataSetChanged();

                    }

                }

            }
        });



        return view;
    }

}
