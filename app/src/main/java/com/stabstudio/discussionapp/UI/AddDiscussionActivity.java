package com.stabstudio.discussionapp.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stabstudio.discussionapp.Models.Discussion;
import com.stabstudio.discussionapp.R;

import org.joda.time.DateTime;

public class AddDiscussionActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference dRef;
    private StorageReference sRef;

    private EditText topic;
    private EditText content;
    private ImageView image;
    private Button publish;
    private ProgressDialog progressDialog;
    private Uri imageFile;
    private String userId;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_discussion);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        dRef = FirebaseDatabase.getInstance().getReference();
        sRef = FirebaseStorage.getInstance().getReference();

        preferences = getSharedPreferences("MetaData", Context.MODE_PRIVATE);
        editor = preferences.edit();

        topic = (EditText) findViewById(R.id.dis_topic);
        content = (EditText) findViewById(R.id.dis_content);
        image = (ImageView) findViewById(R.id.dis_image);
        publish = (Button) findViewById(R.id.post_discussion);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Publishing");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishDiscussion();
            }
        });

    }

    private void publishDiscussion(){
        String topicStr = topic.getText().toString();
        String contentStr = content.getText().toString();
        if(!TextUtils.isEmpty(topicStr) && !TextUtils.isEmpty(contentStr) && imageFile != null){
            progressDialog.show();
            String imagePath = uploadImage();

            DatabaseReference disRef = dRef.child("Discussions");
            DatabaseReference placeDisRef = dRef.child("place-discussion");

            String disId = disRef.push().getKey();
            String placeId = preferences.getString("user_place", "no_place");
            String userId = preferences.getString("user_id", "null");

            String timeStamp = DateTime.now().getSecondOfMinute() + "/" +
                               DateTime.now().getMinuteOfHour() + "/" +
                               DateTime.now().getHourOfDay() + "/" +
                               DateTime.now().getDayOfMonth() + "/" +
                               DateTime.now().getMonthOfYear() + "/" +
                               DateTime.now().getYear();

            Discussion newDiscussion = new Discussion(disId, placeId, userId, topicStr, imagePath, contentStr, timeStamp, 0, 0);

            disRef.child(disId).setValue(newDiscussion);
            placeDisRef.child(placeId).child(disId).setValue(newDiscussion);

            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Discussion Published", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "Please fill all the values", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImageChooser(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        startActivityForResult(chooserIntent, 100);
    }

    private String uploadImage(){
        if(imageFile != null){
            final StorageReference riversRef = sRef.child(userId + "/" + imageFile.getLastPathSegment() + ".jpg");
            riversRef.putFile(imageFile)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
            return riversRef.getDownloadUrl().toString();
        }
        else{
            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK && data != null){
            imageFile = data.getData();
            image.setImageURI(imageFile);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.still, R.anim.slide_out_down);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
