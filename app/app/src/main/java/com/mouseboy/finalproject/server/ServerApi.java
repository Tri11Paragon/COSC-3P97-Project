package com.mouseboy.finalproject.server;

import android.content.Context;

import com.google.gson.annotations.JsonAdapter;
import com.mouseboy.finalproject.util.Gson;
import com.mouseboy.finalproject.util.OkHttp;
import com.mouseboy.finalproject.weather.WeatherApi;

import java.util.Date;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ServerApi {

    public static final String SERVER = "http://10.100.135.154:8080/";
    public static final String SERVER_API_DB = SERVER+"api/db/";

    public static final String HEADER = "x-meow";
    public static final String TOTALLY_MAGIC_MEOW_TOKEN = "this is a really absolutely secure token that will prevent all spam in user creation";

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
        public float rating;
        public String name;
        public String comment;
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
}
