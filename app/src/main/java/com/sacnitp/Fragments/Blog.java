package com.sacnitp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sacnitp.Model;
import com.sacnitp.Model_Adapter;
import com.sacnitp.R;

import java.util.ArrayList;

public class Blog extends Fragment {
private RecyclerView recyclerView;
private ArrayList<Model> list;
private Model_Adapter adapter;
private ProgressDialog prgd;
private ChildEventListener mlistener;
DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Blogs");
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_blog, container, false);

        recyclerView=(RecyclerView)v.findViewById(R.id.recyclerview_blog);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        list=new ArrayList<Model>();
        prgd=new ProgressDialog(getActivity());
        prgd.show();
        prgd.setMessage("Loading.....");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    try {
                        String name = dataSnapshot1.child("name").getValue().toString();
                        String post = dataSnapshot1.child("description").getValue().toString();
                        if (dataSnapshot1.hasChild("image")) {
                            String image=dataSnapshot1.child("image").getValue().toString();
                            list.add(new Model(name, post,image));
                        }
                        else
                            list.add(new Model(name,post));
                    }
                    catch (Exception e)
                    {
                        Log.e("error",e.getMessage());
                        Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT).show();
                    }
                    prgd.dismiss();
                }
                adapter=new Model_Adapter(getActivity(),list);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    mlistener=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot1, @Nullable String s) {
            String name = dataSnapshot1.child("name").getValue().toString();
            String post = dataSnapshot1.child("description").getValue().toString();
            if (dataSnapshot1.hasChild("image")) {
                String image=dataSnapshot1.child("image").getValue().toString();
                list.add(new Model(name, post,image));
            }
            else
                list.add(new Model(name,post));
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
        return v;
    }

}
