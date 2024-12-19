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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        PagerAdaptor adapter = new PagerAdaptor(requireActivity());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1, false);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Set custom icons
            switch (position) {
                case 0:
                    tab.setIcon(android.R.drawable.ic_dialog_info);
                    break;
                case 1:
                    tab.setIcon(android.R.drawable.star_big_on);
                    break;
                case 2:
                    tab.setIcon(android.R.drawable.ic_menu_myplaces);
                    break;
            }
        }).attach();
    }
}
