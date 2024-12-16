package com.mouseboy.finalproject.server;

import android.content.Context;

import com.google.gson.annotations.JsonAdapter;
import com.mouseboy.finalproject.util.Gson;
import com.mouseboy.finalproject.util.OkHttp;
import com.mouseboy.finalproject.weather.WeatherApi;

import java.util.Date;

public class ServerApi {

    public static final String SERVER = "http://10.100.132.227:8080/";
    public static final String SERVER_API_DB = SERVER+"api/db/";

    public static void root(Context context, OkHttp.OnResponse<String> response, OkHttp.OnFailure error) {
        OkHttp.getString(context, SERVER, response, error);
    }


    public static class User{
        public String id;
        public String name;

        public User(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static class AllWalkInfo{
        public String user_id;
        public WalkInfo walk_info;
        public WalkInstanceInfo[] conditions;
    }

    public static class WalkInstanceInfo{
        @JsonAdapter(Gson.UnixTimestampAdapter.class)
        public Date time;
        public float lon;
        public float lat;
        public WeatherApi.WeatherResult.CurrentWeather current;
    }

    public static class WalkInfo{
        public Integer id;
        @JsonAdapter(Gson.UnixTimestampAdapter.class)
        public Date start;
        @JsonAdapter(Gson.UnixTimestampAdapter.class)
        public Date end;
        public String name;
        public String comment;
        public float rating;
    }

    public static class WalkInfoId{
        public String user_id;
        public int walk_id;
    }


    public static class WalkInfoUpdate {
        public String user_id;
        public int walk_id;
        public float rating;
        public String name;
        public String comment;
    }

    public static void createUser(Context context, User user, OkHttp.OnResponse<Void> response, OkHttp.OnFailure error){
                OkHttp.postJson(
                context,
                SERVER_API_DB + "create_user",
                user,
                response,
                error
        );
    }

    public static void updateUser(Context context, User user, OkHttp.OnResponse<Void> response, OkHttp.OnFailure error){
        OkHttp.postJson(
                context,
                SERVER_API_DB + "update_user",
                user,
                response,
                error
        );
    }

    public static void deleteUser(Context context, String user_id, OkHttp.OnResponse<Void> response, OkHttp.OnFailure error){
        OkHttp.postJson(
                context,
                SERVER_API_DB + "delete_user",
                user_id,
                response,
                error
        );
    }

    public static void getUser(Context context, String user_id, OkHttp.OnResponse<User> response, OkHttp.OnFailure error){
        OkHttp.postJson(
                context,
                SERVER_API_DB + "get_user",
                user_id,
                User.class,
                response,
                error
        );
    }

    public static void createWalk(Context context, AllWalkInfo info, OkHttp.OnResponse<Integer> response, OkHttp.OnFailure error){
        OkHttp.postJson(
                context,
                SERVER_API_DB + "create_walk",
                info,
                Integer.class,
                response,
                error
        );
    }

    public static void listWalks(Context context, String user_id, OkHttp.OnResponse<WalkInfo[]> response, OkHttp.OnFailure error){
        OkHttp.postJson(
                context,
                SERVER_API_DB + "list_walks",
                user_id,
                WalkInfo[].class,
                response,
                error
        );
    }

    public static void listWalkInfo(Context context, WalkInfoId id, OkHttp.OnResponse<WalkInstanceInfo[]> response, OkHttp.OnFailure error){
        OkHttp.postJson(
                context,
                SERVER_API_DB + "list_walk_info",
                id,
                WalkInstanceInfo[].class,
                response,
                error
        );
    }

    public static void updateWalk(Context context, WalkInfoUpdate update, OkHttp.OnResponse<Void> response, OkHttp.OnFailure error){
        OkHttp.postJson(
                context,
                SERVER_API_DB + "update_walk",
                update,
                response,
                error
        );
    }

    public static void deleteWalk(Context context, WalkInfoId id, OkHttp.OnResponse<Void> response, OkHttp.OnFailure error){
        OkHttp.postJson(
                context,
                SERVER_API_DB + "delete_walk",
                id,
                response,
                error
        );
    }
}
