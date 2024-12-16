package com.mouseboy.finalproject;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mouseboy.finalproject.server.ServerApi;
import com.mouseboy.finalproject.weather.LocationTracker;
import com.mouseboy.finalproject.weather.WeatherApi;

import java.lang.reflect.Field;
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

//        getResources().openRawResource(R.raw.system);
//        KeyStore.get


//        findViewById(R.id.button).setOnClickListener(e ->
//                ServerApi.root(this,
//                        ((TextView)findViewById(R.id.textView))::setText,
//                        err -> ((TextView)findViewById(R.id.textView)).setText("Something went wrong: "+err.getMessage())));

        TextView textView = findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.textView2);
        findViewById(R.id.button).setOnClickListener(e -> {
            WeatherApi.request(this, new WeatherApi.WeatherRequest(), report -> {
                Field[] fields = WeatherApi.WeatherResult.CurrentWeather.class.getFields();
                String blah = "";
                for(Field field:fields){
                    try {
                        blah += field.getName() + ": " + field.get(report.current) + "\n";
                    } catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                textView2.setText(blah);
                System.out.println(report);
            }, err -> {
                Logger.getGlobal().log(Level.SEVERE, err.getMessage());
            });


            ServerApi.meow(this,
                    "username thing",
                    text -> textView.setText(text),
                    err -> textView.setText(err.getMessage())
            );
        });

    }
}