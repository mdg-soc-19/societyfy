package com.example.societyfy.Activities.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.societyfy.Activities.FoodFragment;
import com.example.societyfy.Activities.HangoutFragment;
import com.example.societyfy.Activities.OtherFragment;
import com.example.societyfy.Activities.PlayFragment;
import com.example.societyfy.Activities.StudyFragment;
import com.example.societyfy.Activities.UserClient;
import com.example.societyfy.Activities.models.User;
import com.example.societyfy.Activities.models.UserLocation;
import com.example.societyfy.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

import static com.example.societyfy.Activities.Constants.ERROR_DIALOG_REQUEST;
import static com.example.societyfy.Activities.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.societyfy.Activities.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class HomeFragment extends Fragment implements View.OnClickListener {

    CardView study;
    CardView play;
    CardView food;
    CardView hangout;
    CardView other;
    FragmentTransaction fragmentTransaction;
    final static String TAG = "MainActivity";
    boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private UserLocation mUserLocation;
    private FirebaseFirestore mDb;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home2, container, false);

        mDb = FirebaseFirestore.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        study = v.findViewById(R.id.study);
        play = v.findViewById(R.id.play);
        food = v.findViewById(R.id.food);
        hangout = v.findViewById(R.id.hangout);
        other = v.findViewById(R.id.other);

        study.setOnClickListener(this);
        play.setOnClickListener(this);
        food.setOnClickListener(this);
        hangout.setOnClickListener(this);
        other.setOnClickListener(this);

        return v;


    }




    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.study:

                Toast.makeText(getContext(), "Let's Study", Toast.LENGTH_SHORT).show();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new StudyFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case R.id.play:
                Toast.makeText(getContext(), "Let's Play", Toast.LENGTH_SHORT).show();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new PlayFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case R.id.food:
                Toast.makeText(getContext(), "Let's Eat", Toast.LENGTH_SHORT).show();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new FoodFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case R.id.hangout:
                Toast.makeText(getContext(), "Let's Hangout", Toast.LENGTH_SHORT).show();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new HangoutFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case R.id.other:
                Toast.makeText(getContext(), "Let's Try Out Something New", Toast.LENGTH_SHORT).show();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new OtherFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            default:
                Toast.makeText(getContext(), "Invalid Choice", Toast.LENGTH_SHORT).show();

        }
    }



    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }


    private void getUserDetails(){

        if(mUserLocation == null){
            mUserLocation = new UserLocation();

            DocumentReference userRef = mDb.collection("Users").document(FirebaseAuth.getInstance().getUid());
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                        mUserLocation.setUser(user);
                        ((UserClient) Objects.requireNonNull(getActivity().getApplicationContext())).setUser(user);
                        getLastKnownLocation();

                    }
                }
            });
        }else {
            getLastKnownLocation();
        }
    }

    private void saveUserLocation(){

        if(mUserLocation != null){
            DocumentReference locationRef = mDb.collection("Users' Locations").document(FirebaseAuth.getInstance().getUid());
            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "latitude: " + mUserLocation.getGeoPoint().getLatitude());
                        Log.d(TAG, "longitude: " + mUserLocation.getGeoPoint().getLongitude());
                    }
                }
            });
        }

    }

    private void getLastKnownLocation(){
        Log.d(TAG,"getLastKnownLocation : called");
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location = task.getResult();
                    if(location!=null) {
                        Log.d(TAG, "OnComplete latitude: " + location.getLatitude());
                        Log.d(TAG, "OnComplete latitude: " + location.getLongitude());
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
                        mUserLocation.setGeoPoint(geoPoint);
                        mUserLocation.setTimestamp(null);
                        saveUserLocation();

                    }
                }

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (!mLocationPermissionGranted)
                getLocationPermission();
        }else{
            getUserDetails();
        }

    }


    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: checking google services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getUserDetails();
                }
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
            return true;

    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            showMessage("Welcome to Societyfy!!!");
            getUserDetails();

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    showMessage("Enjoy the app");
                    getUserDetails();
                } else {
                    getLocationPermission();
                }
            }
        }


    }

    private void showMessage(String message) {

        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

    }


}