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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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
        if(prefs.contains("username") && prefs.contains("password")){
            String username = prefs.getString("username", "");
            String password = prefs.getString("password", "");
            ServerApi.getUser(
                this, Util.mash(username, password),
                data -> {
                    receiveUserData(data);
                    switch_to_main(this);
                },
                error -> switch_to_register(this)
            );
        }else{
            switch_to_register(this);
        }
    }

    private static String displayName = "";

    public static void receiveUserData(ServerApi.User data){
        displayName = data.name;
    }

    public static void switchToFragment(FragmentActivity activity, Fragment fragment) {
        activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("fragment.swtich")
            .commit();
    }

    public static void user_registered(Activity context, String username, String token) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString("username", username).putString("token", token).apply();
    }

    public static void user_logout(Activity context){
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().remove("username").remove("token").apply();
    }

    public static void switch_to_main(FragmentActivity activity){
        activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, new MainFragment())
            .commit();
    }

    public static void switch_to_register(FragmentActivity activity){
        activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, new AuthHandlerFragment())
            .commit();
    }
}
