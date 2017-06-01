package com.stabstudio.discussionapp.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stabstudio.discussionapp.Adapters.CommentsAdapter;
import com.stabstudio.discussionapp.Models.Comment;
import com.stabstudio.discussionapp.Models.Discussion;
import com.stabstudio.discussionapp.R;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscussionActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "cheese_name";
    private static final Random RANDOM = new Random();

    @BindView(R.id.topic) TextView topic;
    @BindView(R.id.dis_value) TextView content;
    @BindView(R.id.comments_recyclerview) RecyclerView recyclerView;
    @BindView(R.id.comment_text) EditText commentText;
    @BindView(R.id.button_post_comment) Button postButton;

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private DatabaseReference dRef;
    private String discussionId;
    private SharedPreferences preferences;
    private ArrayList<Comment> commentsList = new ArrayList<Comment>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        preferences = getSharedPreferences("MetaData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        ButterKnife.bind(this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        final String cheeseName = intent.getStringExtra(EXTRA_NAME);
        discussionId = intent.getStringExtra("dissId");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(cheeseName);

        loadBackdrop();

        loadContents();

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
    }

    private void loadContents(){
        dRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dissRef = dRef.child("Discussions").child(discussionId);
        dissRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Discussion temp = dataSnapshot.getValue(Discussion.class);
                topic.setText(temp.getSubject());
                content.setText(temp.getContent());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void postComment(){                                         //Commenting System
        if(!TextUtils.isEmpty(commentText.getText().toString())){       //The most complicated part

            dRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference commentRef = dRef.child("Comments");
            DatabaseReference dissCommRef = dRef.child("discussion-comments");

            String timeStamp = DateTime.now().getSecondOfMinute() + "/" +
                    DateTime.now().getMinuteOfHour() + "/" +
                    DateTime.now().getHourOfDay() + "/" +
                    DateTime.now().getDayOfMonth() + "/" +
                    DateTime.now().getMonthOfYear() + "/" +
                    DateTime.now().getYear();

            String commentStr = commentText.getText().toString();
            String author = preferences.getString("first_name", "Anonymous");

            String commId = dissCommRef.child(discussionId).push().getKey();
            Comment comment = new Comment(commId, discussionId, author, commentStr, timeStamp);
            dissCommRef.child(discussionId).child(commId).setValue(comment);
            commentRef.child(commId).setValue(comment);
            commentsList.add(comment);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadComments();
    }

    private void loadComments(){
        dRef = FirebaseDatabase.getInstance().getReference().child("discussion-comments");
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentsList.clear();
                for(DataSnapshot snapshot : dataSnapshot.child(discussionId).getChildren()){
                    Comment temp = snapshot.getValue(Comment.class);
                    commentsList.add(temp);
                }
                adapter = new CommentsAdapter(DiscussionActivity.this, commentsList);
                recyclerView.setAdapter(adapter);
                //Toast.makeText(DiscussionActivity.this, "Comments Loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DiscussionActivity.this, "Failed to load comments, Please Refresh", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(getRandomCheeseDrawable()).centerCrop().into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static int getRandomCheeseDrawable() {
        switch (RANDOM.nextInt(5)) {
            default:
            case 0:
                return R.drawable.cheese_1;
            case 1:
                return R.drawable.cheese_2;
            case 2:
                return R.drawable.cheese_3;
            case 3:
                return R.drawable.cheese_4;
            case 4:
                return R.drawable.cheese_5;
        }
    }

}
