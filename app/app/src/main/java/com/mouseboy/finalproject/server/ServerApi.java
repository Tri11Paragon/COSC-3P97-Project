package com.mouseboy.finalproject.server;

import android.content.Context;

import com.mouseboy.finalproject.util.OkHttp;

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

}
