package com.stabstudio.discussionapp.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stabstudio.discussionapp.Adapters.DiscussionsAdapter;
import com.stabstudio.discussionapp.Models.Discussion;
import com.stabstudio.discussionapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DiscussionFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private DatabaseReference usersRef;
    private DatabaseReference placesRef;
    private DatabaseReference commentsRef;
    private DatabaseReference discussionsRef;
    private DatabaseReference placeDisRef;

    private LinearLayoutManager layoutManager;
    private DiscussionsAdapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView rv;
    private SwipeRefreshLayout refreshLayout;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static ArrayList<Discussion> discussionList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.fragment_discussion, container, false);

        preferences = getActivity().getSharedPreferences("MetaData", Context.MODE_PRIVATE);
        editor = preferences.edit();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading Discussions");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        rv = (RecyclerView) vi.findViewById(R.id.discussion_recycler_view);
        layoutManager = new LinearLayoutManager(rv.getContext());
        rv.setHasFixedSize(true);
        rv.setLayoutManager(layoutManager);

        refreshLayout = (SwipeRefreshLayout) vi.findViewById(R.id.dis_refreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDiscussions();
            }
        });

        return vi;
    }

    @Override
    public void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        loadDiscussions();
    }

    private void loadDiscussions(){
        usersRef = databaseRef.child("Users");
        placesRef = databaseRef.child("Places");
        commentsRef = databaseRef.child("Comments");
        discussionsRef = databaseRef.child("Discussions");
        placeDisRef = databaseRef.child("place-discussion");

        final String placeId = preferences.getString("user_place", "null");

        discussionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                discussionList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Discussion discussion = snapshot.getValue(Discussion.class);
                    discussionList.add(discussion);
                }
                adapter = new DiscussionsAdapter(getActivity());
                rv.setAdapter(adapter);
                progressDialog.dismiss();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Second Approach - Display discussions of a particular place

        /*placeDisRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                discussionList.clear();
                for(DataSnapshot snapshot : dataSnapshot.child(placeId).getChildren()){
                    Discussion discussion = snapshot.getValue(Discussion.class)
                    discussionList.add(discussion);
                }
                adapter = new DiscussionsAdapter(getActivity());
                rv.setAdapter(adapter);
                progressDialog.dismiss();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    private List<String> getRandomSublist(String[] array, int amount) {
        ArrayList<String> list = new ArrayList<>(amount);
        Random random = new Random();
        while (list.size() < amount) {
            list.add(array[random.nextInt(array.length)]);
        }
        return list;
    }

}
