package com.stabstudio.discussionapp.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
import com.stabstudio.discussionapp.Models.Discussion;
import com.stabstudio.discussionapp.Models.Places;
import com.stabstudio.discussionapp.Models.User;
import com.stabstudio.discussionapp.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseRef;
    private DatabaseReference usersRef;
    private DatabaseReference placesRef;
    private DatabaseReference discussionsRef;
    private StorageReference storageRef;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @BindView(R.id.first_name) TextView firstName;
    @BindView(R.id.last_name) TextView lastName;
    @BindView(R.id.user_email) TextView email;
    @BindView(R.id.user_phone) TextView phone;
    @BindView(R.id.profileimg) ImageView profilePic;
    @BindView(R.id.ll1) LinearLayout firstNameLl;
    @BindView(R.id.ll2) LinearLayout lastNameLl;
    @BindView(R.id.ll3) LinearLayout emailLl;
    @BindView(R.id.ll4) LinearLayout phoneLl;
    @BindView(R.id.ll5) LinearLayout notiToggle;
    @BindView(R.id.ll6) LinearLayout logoutLl;
    @BindView(R.id.noti) Switch aSwitch;

    private User snapshot;
    private String userId;
    private String userEmail;
    private String userName;
    private Uri imageFile;
    private Uri defaultUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        userEmail = user.getEmail();
        userName = user.getDisplayName();
        defaultUri = user.getPhotoUrl();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = storageRef.child(userId + "/" + "profile_image.jpg");
        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(getActivity()).load(uri).fitCenter().into(profilePic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Cannot load profile image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.bind(this, vi);

        preferences = getActivity().getSharedPreferences("MetaData", Context.MODE_PRIVATE);
        editor = preferences.edit();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        storageRef = FirebaseStorage.getInstance().getReference();

        notiToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aSwitch.isChecked()){
                    aSwitch.setChecked(false);
                }else{
                    aSwitch.setChecked(true);
                }
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        firstNameLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(1, firstName.getText().toString());
            }
        });

        lastNameLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(2, lastName.getText().toString());
            }
        });

        phoneLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(3, phone.getText().toString());
            }
        });

        logoutLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("Do you wish to sign out?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });

        return vi;
    }

    private void showImageChooser(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        startActivityForResult(chooserIntent, 500);
    }

    private void logout(){
        auth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        getActivity().finish();
                    }
                }
        );
    }

    private void updateData(){
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                snapshot = dataSnapshot.child(userId).getValue(User.class);
                firstName.setText(snapshot.getFirst_name());
                lastName.setText(snapshot.getLast_name());
                email.setText(userEmail);
                phone.setText(snapshot.getPhoneNo());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showAlertDialog(final int n, String str){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_layout, null);
        final EditText editText = (EditText) view.findViewById(R.id.et_change);
        editText.setText(str);
        dialog.setTitle("Edit Value");
        dialog.setView(view);
        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                User temp = snapshot;
                if(n == 1){
                    temp.setFirst_name(editText.getText().toString());
                }else if(n == 2){
                    temp.setLast_name(editText.getText().toString());
                }else if(n == 3){
                    temp.setPhoneNo(editText.getText().toString());
                }
                usersRef.child(userId).setValue(temp);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void createDatabaseEntries(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();

        usersRef = databaseRef.child("Users");
        placesRef = databaseRef.child("Places");
        discussionsRef = databaseRef.child("Discussions");

        String place1 = "449 Palo Verde Road, Gainesville, FL";
        String place2 = "6731 Thompson Street, Gainesville, FL";
        String place3 = "8771 Thomas Boulevard, Orlando, FL";
        String place4 = "1234 Verano Place, Orlando, FL";

        String place_id_1 = placesRef.push().getKey();
        Places p1 = new Places(place_id_1, place1);
        placesRef.child(place_id_1).setValue(p1);

        String place_id_2 = placesRef.push().getKey();
        Places p2 = new Places(place_id_2, place2);
        placesRef.child(place_id_2).setValue(p2);

        String place_id_3 = placesRef.push().getKey();
        Places p3 = new Places(place_id_3, place3);
        placesRef.child(place_id_3).setValue(p3);

        String place_id_4 = placesRef.push().getKey();
        Places p4 = new Places(place_id_4, place4);
        placesRef.child(place_id_4).setValue(p4);

        for(int i = 0; i < 30; i++){
            String id = discussionsRef.push().getKey();
            //Discussion temp = new Discussion(id, "Palo Alto, California", "Matt Franco", "Time to clean the streets", "", "", "11 minutes ago", 150, 50);
            //discussionsRef.child(id).setValue(temp);
        }
    }

    @Override
    public void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
        updateData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 500 && resultCode == getActivity().RESULT_OK && data != null){
            imageFile = data.getData();
            uploadImage();
            Glide.with(getActivity()).load(imageFile).into(profilePic);
            /*try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageFile);
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    private void uploadImage(){
        if(imageFile != null){
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            //progressDialog.setTitle("Updating");
            progressDialog.setMessage("Updating Profile Pic...");
            progressDialog.setCancelable(false);
            progressDialog.show();
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
            Toast.makeText(getActivity(), "File not Uploaded", Toast.LENGTH_SHORT).show();
        }
    }

}
