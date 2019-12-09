package com.example.societyfy.Activities.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.societyfy.Activities.FoodFragment;
import com.example.societyfy.Activities.HangoutFragment;
import com.example.societyfy.Activities.LocationFragment;
import com.example.societyfy.Activities.OtherFragment;
import com.example.societyfy.Activities.PlayFragment;
import com.example.societyfy.Activities.StudyFragment;
import com.example.societyfy.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment implements View.OnClickListener {

    CardView study;
    CardView play;
    CardView food;
    CardView hangout;
    CardView other;
    FragmentTransaction fragmentTransaction;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home2, container, false);

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

}