package com.mouseboy.finalproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.GsonBuilder;
import com.mouseboy.finalproject.server.ServerApi;
import com.mouseboy.finalproject.util.Gson;
import com.mouseboy.finalproject.util.Util;
import com.mouseboy.finalproject.weather.LocationTracker;
import com.mouseboy.finalproject.weather.WeatherApi;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    TextView textView2;

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

        TextView textView = findViewById(R.id.textView);
        findViewById(R.id.button).setOnClickListener(e -> ServerApi.listWalks(this,
                new ServerApi.ListWalks("hewwow", new Date(0), new Date(Long.MAX_VALUE)),
                walks -> {
                    textView.setText(new GsonBuilder().setPrettyPrinting().create().toJson(walks));
                },
                Util::logThrowable
        ));

        textView2 = findViewById(R.id.textView2);
        findViewById(R.id.button2).setOnClickListener(e -> WeatherApi.request(this,
                new WeatherApi.WeatherRequest(),
                this::receiveReport,
                Util::logThrowable
        ));
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