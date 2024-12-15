package com.mouseboy.finalproject.server;

import android.content.Context;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mouseboy.finalproject.util.Volly;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerApi {

    public static final String SERVER = "http://192.168.1.106:8080/";

    public static void root(Context context, Response.Listener<String> response, Response.ErrorListener error){
        Volly.getString(context, SERVER, response, error);
    }

    public static void meow(Context context, String username, Response.Listener<String> response, Response.ErrorListener error){
        JSONObject obj = new JSONObject();
        try {
            obj.put("username", username);
        } catch (JSONException e) {
            error.onErrorResponse(new VolleyError(e));
            return;
        }
        Volly.postJson(context, SERVER+"meow", obj, json -> {
            try {
                response.onResponse(json.getString("meows"));
            } catch (JSONException e) {
                error.onErrorResponse(new VolleyError(e));
            }
        }, error);
    }

}
