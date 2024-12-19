package com.mouseboy.finalproject;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mouseboy.finalproject.server.Local;
import com.mouseboy.finalproject.server.ServerApi;
import com.mouseboy.finalproject.util.Util;
import com.mouseboy.finalproject.weather.WeatherApi;

public class HomePage extends Fragment implements Runnable {

    private Handler handler = new Handler();

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView status = view.findViewById(R.id.current_conditions_status);
        status.setText("Unknown");
        status.setTextColor(Color.GRAY);
        startTimer();
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

    public void startTimer(){
        handler.post(this);
    }

    public void update_fav() {
        TextView status = requireView().findViewById(R.id.current_conditions_status);
        if (Local.isUserLoggedIn()) {
            WeatherApi.request(requireContext(), new WeatherApi.WeatherRequest(), weather -> {
                System.out.println(weather);
                ServerApi.analyzeWalkConditions(requireContext(), new ServerApi.Meow(Local.getCurrentUser().id, weather.current), parker -> {
                    if (parker > 0.5) {
                        status.setText("Favourable");
                        status.setTextColor(Color.rgb(25, 200, 25));
                    } else {
                        status.setText("Unfavourable");
                        status.setTextColor(Color.rgb(200, 25, 25));
                    }
                }, error -> {
                    System.out.println("Parker is also bad at java 2");
                    Util.logThrowable(error);
                });
            }, error -> {
                System.out.println("Parker writes bad java");
                Util.logThrowable(error);
            });
        } else {
            status.setText("Unknown");
            status.setTextColor(Color.GRAY);
        }
    }

    @Override
    public void run() {
        update_fav();
        handler.postDelayed(this, 5 * 60 * 1000);
    }
}
