package com.mouseboy.finalproject.server;

import android.content.Context;

import com.google.gson.annotations.JsonAdapter;
import com.mouseboy.finalproject.util.Gson;
import com.mouseboy.finalproject.util.OkHttp;
import com.mouseboy.finalproject.weather.WeatherApi;

import java.io.Serializable;
import java.util.Date;

import okhttp3.Headers;

public class ServerApi {

    public static final String SERVER = "https://cosc3p97.tpgc.me/";
    public static final String SERVER_API_DB = SERVER+"api/db/";
    public static final String SERVER_API_ANALYSIS = SERVER+"api/analysis/";

    public static final String HEADER = "x-meow";
    public static final String TOTALLY_MAGIC_MEOW_TOKEN = "this is a really absolutely secure token that will prevent all spam in user creation";

    public static void root(Context context, OkHttp.OnResponse<String> response, OkHttp.OnFailure error) {
        OkHttp.getString(context, SERVER, response, error);
    }


    public static class User implements Serializable {
        public String id;
        public String name;

        public User(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static class AllWalkInfo implements Serializable {
        public String user_id;
        public WalkInfo walk;
        public WalkInstanceInfo[] conditions;

        public AllWalkInfo(){

        }

        public AllWalkInfo(String user_id, WalkInfo walk, WalkInstanceInfo[] conditions) {
            this.user_id = user_id;
            this.walk = walk;
            this.conditions = conditions;
        }
    }

    public static class WalkInstanceInfo implements Serializable {
        @JsonAdapter(Gson.UnixTimestampAdapter.class)
        public Date time;
        public double lon;
        public double lat;
        public WeatherApi.WeatherResult.CurrentWeather conditions;
    }

    public static class WalkInfo implements Serializable {
        public Integer id;
        @JsonAdapter(Gson.UnixTimestampAdapter.class)
        public Date start;
        @JsonAdapter(Gson.UnixTimestampAdapter.class)
        public Date end;
        public String name;
        public String comment;
        public double rating;
    }

    public static class WalkInfoId implements Serializable {
        public String user_id;
        public int walk_id;

        public WalkInfoId(String user_id, int walk_id) {
            this.user_id = user_id;
            this.walk_id = walk_id;
        }
    }

    public static class ListWalks{
        public String user_id;

        @JsonAdapter(Gson.UnixTimestampAdapter.class)
        public Date start;
        @JsonAdapter(Gson.UnixTimestampAdapter.class)
        public Date end;

        public ListWalks(String user_id, Date start, Date end) {
            this.user_id = user_id;
            this.start = start;
            this.end = end;
        }
    }

    public static class WalkInfoUpdate {
        public String user_id;
        public int walk_id;
        public double rating;
        public String name;
        public String comment;
        public WalkInfoUpdate(){}

        public WalkInfoUpdate(int walk_id, double rating, String name, String comment) {
            this.walk_id = walk_id;
            this.rating = rating;
            this.name = name;
            this.comment = comment;
        }
    }


    public static <S> void postJson(Context context, String url, S data, OkHttp.OnResponse<Void> onResponse, OkHttp.OnFailure onFailure) {
        OkHttp.postJson(context, url, data, Headers.of(HEADER, TOTALLY_MAGIC_MEOW_TOKEN), onResponse, onFailure);
    }

    public static <S, R> void postJson(Context context, String url, S data, Class<R> clazz, OkHttp.OnResponse<R> onResponse, OkHttp.OnFailure onFailure) {
        OkHttp.postJson(context, url, data, clazz, Headers.of(HEADER, TOTALLY_MAGIC_MEOW_TOKEN), onResponse, onFailure);
    }

    public static void createUser(Context context, User user, OkHttp.OnResponse<Void> response, OkHttp.OnFailure error){
        postJson(
            context,
            SERVER_API_DB + "create_user",
            user,
            response,
            error
        );
    }

    public static void updateUser(Context context, User user, OkHttp.OnResponse<Void> response, OkHttp.OnFailure error){
        postJson(
            context,
            SERVER_API_DB + "update_user",
            user,
            response,
            error
        );
    }

    public static void deleteUser(Context context, String user_id, OkHttp.OnResponse<Void> response, OkHttp.OnFailure error){
        postJson(
            context,
            SERVER_API_DB + "delete_user",
            user_id,
            response,
            error
        );
    }

    public static void getUser(Context context, String user_id, OkHttp.OnResponse<User> response, OkHttp.OnFailure error){
        postJson(
            context,
            SERVER_API_DB + "get_user",
            user_id,
            User.class,
            response,
            error
        );
    }

    public static void createWalk(Context context, AllWalkInfo info, OkHttp.OnResponse<Integer> response, OkHttp.OnFailure error){
        postJson(
            context,
            SERVER_API_DB + "create_walk",
            info,
            Integer.class,
            response,
            error
        );
    }

    public static void listWalks(Context context, ListWalks listWalks, OkHttp.OnResponse<WalkInfo[]> response, OkHttp.OnFailure error){
        postJson(
            context,
            SERVER_API_DB + "list_walks",
            listWalks,
            WalkInfo[].class,
            response,
            error
        );
    }

    public static void listWalkInfo(Context context, WalkInfoId id, OkHttp.OnResponse<WalkInstanceInfo[]> response, OkHttp.OnFailure error){
        postJson(
            context,
            SERVER_API_DB + "list_walk_info",
            id,
            WalkInstanceInfo[].class,
            response,
            error
        );
    }

    public static void updateWalk(Context context, WalkInfoUpdate update, OkHttp.OnResponse<Void> response, OkHttp.OnFailure error){
        postJson(
            context,
            SERVER_API_DB + "update_walk",
            update,
            response,
            error
        );
    }

    public static void deleteWalk(Context context, WalkInfoId id, OkHttp.OnResponse<Void> response, OkHttp.OnFailure error){
        postJson(
            context,
            SERVER_API_DB + "delete_walk",
            id,
            response,
            error
        );
    }

    public static class Meow{
        public String user_id;
        public WeatherApi.WeatherResult.CurrentWeather conditions;

        public Meow(String user_id, WeatherApi.WeatherResult.CurrentWeather conditions) {
            this.user_id = user_id;
            this.conditions = conditions;
        }
    }

    public static void analyzeWalkConditions(Context context, Meow meow,  OkHttp.OnResponse<Float> response, OkHttp.OnFailure error){
        postJson(
            context,
            SERVER_API_ANALYSIS + "analyze_walk_conditions",
            meow,
            Float.class,
            response,
            error
        );
    }
}
