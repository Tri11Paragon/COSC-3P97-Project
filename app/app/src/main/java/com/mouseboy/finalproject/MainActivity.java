package com.mouseboy.finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.mouseboy.finalproject.server.Local;
import com.mouseboy.finalproject.server.ServerApi;
import com.mouseboy.finalproject.util.Util;
import com.mouseboy.finalproject.weather.LocationTracker;

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
                user -> {
                    Local.login(user);
                    switchToUserHome(this);
                },
                error -> switchToNotLoggedIn(this)
            );
        }else{
            switchToNotLoggedIn(this);
        }
    }

    public static void switchToFragment(FragmentActivity activity, Fragment fragment) {
        activity.getSupportFragmentManager().popBackStack("fragment.switch", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }

    public static void pushFragment(FragmentActivity activity, Fragment fragment) {
        activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("fragment.switch")
            .commit();
    }

    public static void popFragment(FragmentActivity activity){
        activity.getSupportFragmentManager().popBackStack();
    }

    public static void switchToUserHome(FragmentActivity activity){
        switchToFragment(activity, new MainFragment());
    }

    public static void switchToNotLoggedIn(FragmentActivity activity){
        switchToFragment(activity, new UserAuthHandlerFragment());
    }

    public static void user_registered(Activity context, String username, String password) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString("username", username).putString("password", password).apply();
    }

    public static void user_logout(Activity context){
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().remove("username").remove("token").apply();
        Local.logout();
    }
}
