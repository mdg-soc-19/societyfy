package com.example.societyfy.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterFragment extends Fragment {

    CircleImageView ImgUserPhoto;
    static int PReqCode=1;
    static int RequesCode=1;
    public Uri pickedImgUri;

    private EditText userMail,userPassword,userName;
    private ProgressBar loadingProgress;
    private Button regBtn;

    private FirebaseAuth mAuth;
    public SharedPreferences preferences;
    public SharedPreferences.Editor  editor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_register, container, false);

        userMail=v.findViewById(R.id.regUid);
        userPassword=v.findViewById(R.id.regPwd);
        userName=v.findViewById(R.id.regName);
        loadingProgress=v.findViewById(R.id.login_progress);
        regBtn=v.findViewById(R.id.login_bton);

        loadingProgress.setVisibility(View.INVISIBLE);
        mAuth=  FirebaseAuth.getInstance();


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                regBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();
                final String name = userName.getText().toString();
                preferences = Objects.requireNonNull(getContext()).getSharedPreferences("User_pref",Context.MODE_PRIVATE);
                editor = preferences.edit();
                editor.putString("password", userPassword.getText().toString());
                editor.commit();



                if (mail.isEmpty() || name.isEmpty() || password.isEmpty()) {
                    showMessage("Please Verify all fields");
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }
                else {

                    CreateUserAccount(mail,password,name);

                }
            }
        });

        ImgUserPhoto=v.findViewById(R.id.regUserPic);
        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=28){
                    checkAndRequestForPermission();
                }
                else{
                    openGallery();
                }
            }
        });

        return v;
    }

    private void CreateUserAccount(String uid, String pwd, final String name) {

        mAuth.createUserWithEmailAndPassword(uid,pwd).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    showMessage("Account created. Please DON'T CLOSE THE APP");
                    update(name,pickedImgUri,mAuth.getCurrentUser());
                }
                else{
                    showMessage("Account creation failed");
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void update(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

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
                                    showMessage("Registration Complete");
                                    updateUI();
                                }


                            }
                        });
                    }
                });


            }
        });

    }

    private void updateUI() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment, new PermissionFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showMessage(String message) {

        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

    }

    private void openGallery() {

        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,RequesCode);
    }


    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){

                Toast.makeText(getContext(), "Please grant permission", Toast.LENGTH_SHORT).show();

            }
            else{
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode); }
        }
        else
            openGallery();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==-1 && requestCode==RequesCode && data!=null){
            pickedImgUri = data.getData();
            ImgUserPhoto.setImageURI(pickedImgUri);

        }
    }


}
