package com.mouseboy.finalproject;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mouseboy.finalproject.util.Util;

public class HomePage extends Fragment {

    public HomePage() {
        // Required empty public constructor
    }

    public static HomePage newInstance() {
        return new HomePage();
    }

    private ActivityResultLauncher<String[]> requester;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requester = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), g -> {
            boolean g2 = true;
            for (boolean entry : g.values()) {
                g2 &= entry;
            }
            if (g2) {
                permsGranted();
            } else {
                Util.toast(requireContext(), "Location Permission Needed");
            }
        });
    }

    private void reqPerms(){
        requester.launch(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION});
    }

    Button button;
    private void permsGranted(){
        WalkTrackingService.start(requireContext());
        button.setText("STOP WALK");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        button = view.findViewById(R.id.startWalkButton);
        if(WalkTrackingService.isRunning(requireContext())){
            button.setText("STOP WALK");
        }else{
            button.setText("START WALK");
        }
        button.setOnClickListener(e -> {
            if(WalkTrackingService.isRunning(requireContext())){
                WalkTrackingService.stop(requireContext());
                button.setText("START WALK");
            }else{
                reqPerms();
            }
        });
        return view;
    }
}
