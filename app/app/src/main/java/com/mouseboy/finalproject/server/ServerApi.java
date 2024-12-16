package com.mouseboy.finalproject.server;

import android.content.Context;

import com.google.gson.annotations.JsonAdapter;
import com.mouseboy.finalproject.util.Gson;
import com.mouseboy.finalproject.util.OkHttp;
import com.mouseboy.finalproject.weather.WeatherApi;

import java.util.Date;

public class ServerApi {

    public static final String SERVER = "http://192.168.1.106:8080/";

    public static void root(Context context, OkHttp.OnResponse<String> response, OkHttp.OnFailure error) {
        OkHttp.getString(context, SERVER, response, error);
    }

    private static class Info {
        String username;

        Info(String username) {
            this.username = username;
        }
    }

    private static class Response {
        String meow;
    }

    public static void meow(Context context, String username, OkHttp.OnResponse<String> response, OkHttp.OnFailure error) {
        OkHttp.postJson(
                context,
                SERVER + "meow",
                new Info(username),
                Response.class,
                r -> response.onResponse(r.meow),
                error
        );
    }


    public static class User{
        public String id;
        public String name;
    }

    public static class WalkInfo{
        public Long id;
        @JsonAdapter(Gson.UnixTimestampAdapter.class)
        public Date start;
        @JsonAdapter(Gson.UnixTimestampAdapter.class)
        public Date end;
        public String name;
        public String comment;
        public float rating;
    }

    public static class WalkInstanceInfo{
        @JsonAdapter(Gson.UnixTimestampAdapter.class)
        public Date time;
        public float lon;
        public float lat;
        public WeatherApi.WeatherResult.CurrentWeather current;
    }

}
