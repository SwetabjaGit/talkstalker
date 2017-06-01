package com.stabstudio.discussionapp.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stabstudio.discussionapp.Models.Places;
import com.stabstudio.discussionapp.Models.User;
import com.stabstudio.discussionapp.R;
import com.stabstudio.discussionapp.utils.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class PlaceAutocompleteActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference dRef;
    private DatabaseReference placeRef;
    private DatabaseReference usersRef;
    private StorageReference storageRef;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private List<Places> placesList = new ArrayList<Places>();
    private List<String> placesNameList = new ArrayList<String>();
    private AutoCompleteTextView autoTextView;
    private ArrayAdapter<String> arrayAdapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_autocomplete);

        storageRef = FirebaseStorage.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        dRef = FirebaseDatabase.getInstance().getReference();
        placeRef = dRef.child("Places");
        usersRef = dRef.child("Users");
        final String userId = firebaseUser.getUid();
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.child(userId).getValue(User.class);
                if(temp != null){
                    if(temp.getPlaceSet()){
                        Intent in = new Intent(getApplicationContext(), HomeScreenActivity.class);
                        startActivity(in);
                        finish();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        pref = getSharedPreferences("MetaData", Context.MODE_PRIVATE);
        editor = pref.edit();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void autoCompleteFeature(){
        autoTextView = (AutoCompleteTextView) findViewById(R.id.autocompleteEditTextView);
        arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, placesNameList);
        autoTextView.setThreshold(1);
        autoTextView.setAdapter(arrayAdapter);
        autoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String user_id = firebaseUser.getUid();
                String place_id = placesList.get(position).getId();
                String photoUrl = firebaseUser.getPhotoUrl().toString();
                String[] name = firebaseUser.getDisplayName().split(" ");
                String first_name = name[0];
                String last_name = name[1];
                String notificationToken = null;

                uploadImage(firebaseUser.getPhotoUrl(), user_id);

                User newUser = new User(user_id, place_id, photoUrl, first_name, last_name, "", notificationToken);
                newUser.setPlaceSet(true);
                usersRef.child(firebaseUser.getUid()).setValue(newUser);
                editor.putBoolean("LoggedIn", true);
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void uploadImage(Uri imageFile, String userId){
        if(imageFile != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            //progressDialog.setTitle("Updating");
            //progressDialog.setMessage("Updating Profile Pic...");
            //progressDialog.setCancelable(false);
            //progressDialog.show();
            storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference riversRef = storageRef.child(userId + "/" + "profile_image.jpg");
            riversRef.putFile(imageFile)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            //Toast.makeText(getActivity(), "File Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        else{
            Toast.makeText(this, "File not Uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        placeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                placesList.clear();
                placesNameList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Places place = snapshot.getValue(Places.class);
                    placesList.add(place);
                    placesNameList.add(place.getAddress());
                }
                autoCompleteFeature();
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
