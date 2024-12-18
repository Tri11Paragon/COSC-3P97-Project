package com.mouseboy.finalproject;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

//        TextView textView = view.findViewById(R.id.textView);
//        view.findViewById(R.id.button).setOnClickListener(e -> {
//                Location current = LocationTracker.bestLocation();
//                ServerApi.AllWalkInfo walk = new ServerApi.AllWalkInfo("hewwow", new ServerApi.WalkInfo(), new ServerApi.WalkInstanceInfo[]{new ServerApi.WalkInstanceInfo()});
//                walk.walk.rating = 1.0;
//                walk.walk.start = new Date();
//                walk.walk.end = new Date();
//                walk.conditions[0].lat = current.getLatitude();
//                walk.conditions[0].lon = current.getLongitude();
//                walk.conditions[0].time = new Date();
//
//                WeatherApi.request(requireContext(), new WeatherApi.WeatherRequest(), conditions -> {
//                    walk.conditions[0].conditions = conditions.current;
//                    ServerApi.createWalk(requireContext(), walk, id -> {
//                        walk.walk.id = id;
//
//                        ServerApi.listWalkInfo(requireContext(), new ServerApi.WalkInfoId("hewwow", walk.walk.id), ok -> {
//                            textView.setText(new GsonBuilder().setPrettyPrinting().create().toJson(ok));
//                        }, Util::logThrowable);
//                    }, Util::logThrowable);
//                }, Util::logThrowable);
//
//            }
//        );
//
//        textView2 = view.findViewById(R.id.textView2);
//        view.findViewById(R.id.button2).setOnClickListener(e -> WeatherApi.request(requireContext(),
//            new WeatherApi.WeatherRequest(),
//            this::receiveReport,
//            Util::logThrowable
//        ));
        // Inflate the layout for this fragment
        return view;
    }

    private void receiveReport(WeatherApi.WeatherResult report) {
        Field[] fields = WeatherApi.WeatherResult.CurrentWeather.class.getFields();
        StringBuilder blah = new StringBuilder();
        for (Field field : fields) {
            try {
                blah
                    .append(field.getName())
                    .append(": ")
                    .append(field.get(report.current))
                    .append("\n");
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        textView2.setText(blah.toString());
        System.out.println(report);
    }
}
