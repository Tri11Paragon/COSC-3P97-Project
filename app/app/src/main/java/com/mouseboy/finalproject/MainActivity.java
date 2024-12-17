package com.mouseboy.finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LocationTracker.start(this);
        LocationTracker.addListener(loc -> {
            Logger.getGlobal().log(Level.INFO, "Lat/Long " + loc.getLatitude() + " " + loc.getLongitude());
        });

        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean isRegistered = prefs.getBoolean("is_registered", false);

        if (!isRegistered)
            switch_to_register(this);
        else
            switch_to_main(this);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }

    static void user_registered(Activity context, String username, String token) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("is_registered", true).putString("username", username).putString("token", token).apply();
    }

    static void user_logout(Activity context){
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().remove("is_registered").remove("username").remove("token").apply();
    }

    static void switch_to_main(FragmentActivity activity){
        activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, new MainFragment())
            .commit();
    }

    static void switch_to_register(FragmentActivity activity){
        activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, new AuthHandlerFragment())
            .commit();
    }
}
