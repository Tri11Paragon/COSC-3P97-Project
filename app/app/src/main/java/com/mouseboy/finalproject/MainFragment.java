package com.mouseboy.finalproject;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.GsonBuilder;
import com.mouseboy.finalproject.server.ServerApi;
import com.mouseboy.finalproject.util.Util;
import com.mouseboy.finalproject.weather.LocationTracker;
import com.mouseboy.finalproject.weather.WeatherApi;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFragment extends Fragment {

    TextView textView2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textView = view.findViewById(R.id.textView);
        view.findViewById(R.id.button).setOnClickListener(e -> {
                Location current = LocationTracker.bestLocation();
                ServerApi.AllWalkInfo walk = new ServerApi.AllWalkInfo("hewwow", new ServerApi.WalkInfo(), new ServerApi.WalkInstanceInfo[]{new ServerApi.WalkInstanceInfo()});
                walk.walk.rating = 1.0;
                walk.walk.start = new Date();
                walk.walk.end = new Date();
                walk.conditions[0].lat = current.getLatitude();
                walk.conditions[0].lon = current.getLongitude();
                walk.conditions[0].time = new Date();

                WeatherApi.request(requireContext(), new WeatherApi.WeatherRequest(), conditions -> {
                    walk.conditions[0].conditions = conditions.current;
                    ServerApi.createWalk(requireContext(), walk, id -> {
                        walk.walk.id = id;

                        ServerApi.listWalkInfo(requireContext(), new ServerApi.WalkInfoId("hewwow", walk.walk.id), ok -> {
                            textView.setText(new GsonBuilder().setPrettyPrinting().create().toJson(ok));
                        }, Util::logThrowable);
                    }, Util::logThrowable);
                }, Util::logThrowable);

            }
        );

        textView2 = view.findViewById(R.id.textView2);
        view.findViewById(R.id.button2).setOnClickListener(e -> WeatherApi.request(requireContext(),
            new WeatherApi.WeatherRequest(),
            this::receiveReport,
            Util::logThrowable
        ));

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        PagerAdaptor adapter = new PagerAdaptor(requireActivity());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1, false);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Set custom icons
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.ic_launcher_background);
                    break;
                case 1:
                    tab.setIcon(android.R.drawable.star_big_on);
                    break;
                case 2:
                    tab.setIcon(android.R.drawable.star_on);
                    break;
            }
        }).attach();
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