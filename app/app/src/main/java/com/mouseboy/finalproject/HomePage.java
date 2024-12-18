package com.mouseboy.finalproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.mouseboy.finalproject.server.ServerApi;
import com.mouseboy.finalproject.util.Util;
import com.mouseboy.finalproject.weather.LocationTracker;
import com.mouseboy.finalproject.weather.WeatherApi;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePage extends Fragment {


    TextView textView2;

    public HomePage() {
        // Required empty public constructor
    }

    public static HomePage newInstance() {
        return new HomePage();
    }

    private volatile boolean granted = false;
    private ActivityResultLauncher<String[]> requester;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Runnable reqPerms = () -> requester.launch(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION});

        requester = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), g -> {
            boolean g2 = true;
            for (boolean entry : g.values()) {
                g2 &= entry;
            }
            if (!g2) {
                new AlertDialog.Builder(requireContext())
                    .setTitle("Permission Needed")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> reqPerms.run()).create().show();
            } else {
                permsGranted();
            }
        });
        reqPerms.run();
    }

    private void permsGranted(){
        granted=true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        Button button = view.findViewById(R.id.startWalkButton);
        button.setOnClickListener(e -> {
            if(granted){
                LocationTracker.startWithPerms(requireContext());
                requireContext().startService(new Intent(requireContext(), WalkTrackingService.class));
            }else{
                Util.toast(requireContext(), "No Location Permissions");
            }
        });
        return view;
    }
}
