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
import com.mouseboy.finalproject.weather.LocationTracker;
import com.mouseboy.finalproject.weather.WeatherApi;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HomePage extends Fragment implements Runnable {

    private Handler handler = new Handler();
    private Handler handler2 = new Handler();
    private Runnable timer;

    public HomePage() {
        // Required empty public constructor
    }

    public static HomePage newInstance() {
        return new HomePage();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationTracker.setupPermReq(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView status = view.findViewById(R.id.current_conditions_status);
        status.setText("Unknown");
        status.setTextColor(Color.GRAY);
        startTimer();

        timer = () -> {
            TextView test = requireView().findViewById(R.id.elapsed_time);
            if (WalkTrackingService.isRunning(requireContext())){
                Date start = Local.startOfCurrentWalk();
                if (start != null) {
                    Calendar ca = Calendar.getInstance();
                    long diffInMillis = Math.abs(ca.getTimeInMillis() - start.getTime());
                    long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
                    long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24;
                    long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60;
                    long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis) % 60;

                    String str = "Time Elapsed ";
                    if (diffInDays > 0)
                        str += diffInDays + " Days ";
                    if (diffInHours > 0)
                        str += diffInHours + " Hours ";
                    if (diffInMinutes > 0)
                        str += diffInMinutes + " Minutes ";
                    str += diffInSeconds + " Seconds";
                    test.setText(str);
                }
            } else {
                test.setText("");
            }
            handler2.postDelayed(timer, 1000);
        };

        handler2.post(timer);
    }

    Button button;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        button = view.findViewById(R.id.startWalkButton);
        if(WalkTrackingService.isRunning(requireContext())){
            button.setText("STOP WALK");
            button.setBackgroundResource(R.drawable.stop_button_background);
        }else{
            button.setText("START WALK");
            button.setBackgroundResource(R.drawable.start_button_background);
        }
        button.setOnClickListener(e -> {
            if(WalkTrackingService.isRunning(requireContext())){
                WalkTrackingService.stop(requireContext());
                button.setText("START WALK");
                button.setBackgroundResource(R.drawable.start_button_background);
                MainActivity.switchToUserHome(requireActivity());
            }else{
                LocationTracker.ensurePermissions(this, () -> {
                    WalkTrackingService.start(requireContext());
                    button.setText("STOP WALK");
                    button.setBackgroundResource(R.drawable.stop_button_background);
                });
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(this);
        handler2.removeCallbacks(timer);
    }

    public void startTimer(){
        handler.post(this);
    }

    public void update_fav() {
        TextView status = requireView().findViewById(R.id.current_conditions_status);
        if (Local.isUserLoggedIn()) {
            LocationTracker.ensurePermissions(this, () -> {
                WeatherApi.request(requireContext(), new WeatherApi.WeatherRequest(), weather -> {
                    System.out.println(weather);
                    ServerApi.analyzeWalkConditions(requireContext(), new ServerApi.Meow(Local.getCurrentUser().id, weather.current), parker -> {
                        if (parker > 0.5) {
                            status.setText(String.format(
                                "Favourable (%d%%)", Math.round(parker*100)
                            ));
                            status.setTextColor(Color.rgb(25, 200, 25));
                        } else {
                            status.setText(String.format(
                                "Unfavourable (%d%%)", Math.round(parker*100)
                            ));
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
