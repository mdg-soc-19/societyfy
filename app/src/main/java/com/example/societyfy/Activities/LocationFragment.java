package com.example.societyfy.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.societyfy.Activities.Fragments.HomeFragment;
import com.example.societyfy.R;


public class LocationFragment extends Fragment {

    private Button btn;
    private ProgressBar pro;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_location, container, false);

        btn = v.findViewById(R.id.btnNext);
        pro = v.findViewById(R.id.nextpro);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setVisibility(View.INVISIBLE);
                pro.setVisibility(View.VISIBLE);
                updateUI();
            }
        });

        return v;
    }

    private void updateUI() {

        final Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);

    }

}
