package com.mouseboy.finalproject.util;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Volly {
    public static void getString(Context context, String url, Response.Listener<String> response, Response.ErrorListener error){
        RequestQueue queue = Volley.newRequestQueue(context);
        Request<?> request = new StringRequest(Request.Method.GET, url, response, error);
        queue.add(request);
    }

    public static void getJson(Context context, String url, Response.Listener<JSONObject> response, Response.ErrorListener error){
        getJson(context, url, null, response, error);
    }

    public static void getJson(Context context, String url, JSONObject data, Response.Listener<JSONObject> response, Response.ErrorListener error){
        RequestQueue queue = Volley.newRequestQueue(context);
        Request<?> request = new JsonObjectRequest(Request.Method.GET, url, data, response, error){
            @Override
            public Map<String,String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("content-type","application/json");
                return params;
            }
        };
        queue.add(request);
    }

    public static void postString(Context context, String url, Response.Listener<String> response, Response.ErrorListener error){
        RequestQueue queue = Volley.newRequestQueue(context);
        Request<?> request = new StringRequest(Request.Method.POST, url, response, error);
        queue.add(request);
    }

    public static void postJson(Context context, String url, Response.Listener<JSONObject> response, Response.ErrorListener error){
        getJson(context, url, null, response, error);
    }

    public static void postJson(Context context, String url, JSONObject data, Response.Listener<JSONObject> response, Response.ErrorListener error){
        RequestQueue queue = Volley.newRequestQueue(context);
        Request<?> request = new JsonObjectRequest(Request.Method.POST, url,data, response, error);
        queue.add(request);
    }
}
