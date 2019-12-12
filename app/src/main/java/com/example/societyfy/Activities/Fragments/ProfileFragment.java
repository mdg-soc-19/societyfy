package com.example.societyfy.Activities.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.societyfy.Activities.MainActivity;
import com.example.societyfy.Activities.PermissionFragment;
import com.example.societyfy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    CircleImageView profile_pic;
    TextView profile_name;
    TextView profile_mail;
    Button update;
    ProgressBar profile_pro;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    static int PReqCode=1;
    static int RequesCode=1;
    View v;
    Uri pickedImgUri;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        profile_pic = v.findViewById(R.id.profile_photo);
        profile_name = v.findViewById(R.id.profile_name);
        profile_mail = v.findViewById(R.id.profile_user_mail);
        update = v.findViewById(R.id.update);
        profile_pro = v.findViewById(R.id.profile_progress);
        profile_pro.setVisibility(View.INVISIBLE);
        update.setVisibility(View.INVISIBLE);



        profile_name.setText(currentUser.getDisplayName());
        profile_mail.setText(currentUser.getEmail());
        Glide.with(this).load(currentUser.getPhotoUrl()).into(profile_pic);

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openGallery();


            }
        });



        mAuth=  FirebaseAuth.getInstance();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                update.setVisibility(View.INVISIBLE);
                profile_pro.setVisibility(View.VISIBLE);

                final String name = currentUser.getDisplayName();
                update(name, pickedImgUri,mAuth.getCurrentUser());


                }
        });



        return v;

    }

    private void openGallery() {

        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,RequesCode);
    }

    private void showMessage(String message) {

        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

    }

    private void update( final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name)
                                .setPhotoUri(uri).build();

                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    showMessage("Profile Updated");
                                    updateImage();
                                    updateUI();
                                }


                            }
                        });
                    }
                });


            }
        });

    }
    private void updateImage() {
        CircleImageView profile_pic = v.findViewById(R.id.profile_photo);


        Glide.with(this).load(currentUser.getPhotoUrl()).into(profile_pic);
    }

    private void updateUI() {
        final Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==-1 && requestCode==RequesCode && data!=null){
            pickedImgUri = data.getData();
            profile_pic.setImageURI(pickedImgUri);
            update.setVisibility(View.VISIBLE);

        }
    }






}



