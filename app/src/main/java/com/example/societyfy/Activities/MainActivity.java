package com.example.societyfy.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.societyfy.Activities.Fragments.HomeFragment;
import com.example.societyfy.Activities.Fragments.ProfileFragment;
import com.example.societyfy.Activities.Fragments.SettingsFragment;
import com.example.societyfy.Activities.Fragments.UserListFragment;
import com.example.societyfy.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.societyfy.Activities.Constants.ERROR_DIALOG_REQUEST;
import static com.example.societyfy.Activities.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.societyfy.Activities.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CURRENT_USER_KEY = "CURRENT_USER_KEY";
    private static final String CURRENT_USER_NAME = "CURRENT_USER_NAME";
    private static final String CURRENT_USER_IMAGE = "CURRENT_USER_IMAGE";

    public FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction;
    Fragment fragment;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        drawer = findViewById(R.id.drawer_layout);


        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (firstStart) {

            login();
            getSupportActionBar().hide();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        } else if (currentUser == null) {
            login();
            getSupportActionBar().hide();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(CURRENT_USER_KEY, currentUser.getUid());
            editor.putString(CURRENT_USER_NAME,currentUser.getDisplayName());
            editor.putString(CURRENT_USER_IMAGE, String.valueOf(currentUser.getPhotoUrl()));
            editor.apply();


            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                    (this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            updateNavHeader();

            getSupportActionBar().setTitle("Let's Societyfy");
            fragment = new HomeFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment, fragment);
            fragmentTransaction.commit();


        }

    }



    private void login() {
        fragment = new LoginFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
            getSupportActionBar().setTitle("Let's Societyfy");
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportActionBar().setTitle("Let's Societyfy");
            fragment = new HomeFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("Profile");
            fragment = new ProfileFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_settings) {
            getSupportActionBar().setTitle("Settings");
            fragment = new SettingsFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();


        } else if (id == R.id.nav_user_list) {
            getSupportActionBar().setTitle("Users' List");
            fragment = new UserListFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();


        } else if (id == R.id.nav_sign_out) {

            FirebaseAuth.getInstance().signOut();
            currentUser = null;
            fragment = new LoginFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().hide();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }


    public void updateNavHeader() {

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_username);
        TextView navmail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        navUserName.setText(currentUser.getDisplayName());
        navmail.setText(currentUser.getEmail());
        Glide.with(getApplicationContext()).load(currentUser.getPhotoUrl()).into(navUserPhoto);

    }

}
