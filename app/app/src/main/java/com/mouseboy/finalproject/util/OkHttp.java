package com.mouseboy.finalproject.util;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttp {
    private static final OkHttpClient CLIENT;

    static {
        OkHttpClient client = null;
        try {
            TrustManager[] trustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
//            Security.insertProviderAt(Conscrypt.newProvider(), 1);
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init(keyStore);
//            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
//            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
//                throw new IllegalStateException("Unexpected default trust managers:"
//                        + Arrays.toString(trustManagers));
//            }
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            client = new OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, trustManager).followSslRedirects(true).build();

        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage());
        }
        CLIENT = client;
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public interface OnResponse<T> {
        void onResponse(T data);
    }

    public interface OnFailure {
        void onFailure(Throwable e);
    }


    public static void getString(Context context, String url, OnResponse<String> onResponse, OnFailure onFailure) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        enqueue(context, request, response -> {
            try {
                onResponse.onResponse(response.body() != null ? response.body().string() : "");
            } catch (IOException e) {
                onFailure.onFailure(e);
            }
        }, onFailure);
    }

    public static <T> void getJson(Context context, String url, Class<T> clazz, OnResponse<T> onResponse, OnFailure onFailure) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        enqueue(context, request, response -> {
            try {
                String body = response.body() != null ? response.body().string() : "";
                onResponse.onResponse(new Gson().fromJson(body, clazz));
            } catch (Exception e) {
                onFailure.onFailure(e);
            }
        }, onFailure);
    }

    public static <S, T> void postJson(Context context, String url, S data, Class<T> clazz, OnResponse<T> onResponse, OnFailure onFailure) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(new Gson().toJson(data), JSON))
                .build();
        enqueue(context, request, response -> {
            try {
                String body = response.body() != null ? response.body().string() : "";
                onResponse.onResponse(new Gson().fromJson(body, clazz));
            } catch (Exception e) {
                onFailure.onFailure(e);
            }
        }, onFailure);
    }

    public static <S, T> void postJson(Context context, String url, S data, Class<T> clazz, Headers headers, OnResponse<T> onResponse, OnFailure onFailure) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(new Gson().toJson(data), null))
                .headers(headers)
                .addHeader("content-type", "application/json")
                .build();
        enqueue(context, request, response -> {
            try {
                String body = response.body() != null ? response.body().string() : "";
                Logger.getGlobal().log(Level.INFO, body);
                onResponse.onResponse(new Gson().fromJson(body, clazz));
            } catch (Exception e) {
                onFailure.onFailure(e);
            }
        }, onFailure);
    }

    public static <S> void postJson(Context context, String url, S data, OnResponse<Void> onResponse, OnFailure onFailure) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(new Gson().toJson(data), JSON))
                .build();
        enqueue(context, request, response -> {
            try {
                if(response.body() == null)
                    onResponse.onResponse(null);
                else{
                    String body = response.body().string();
                    if(body.isEmpty())
                        onResponse.onResponse(null);
                    else
                        onFailure.onFailure(new RuntimeException(body));
                }
            } catch (Exception e) {
                onFailure.onFailure(e);
            }
        }, onFailure);
    }

    public static <S> void postJson(Context context, String url, S data, Headers headers, OnResponse<Void> onResponse, OnFailure onFailure) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(new Gson().toJson(data), null))
                .headers(headers)
                .addHeader("content-type", "application/json")
                .build();
        enqueue(context, request, response -> {
            try {
                if(response.body() == null)
                    onResponse.onResponse(null);
                else{
                    String body = response.body().string();
                    if(body.isEmpty())
                        onResponse.onResponse(null);
                    else
                        onFailure.onFailure(new RuntimeException(body));
                }
            } catch (Exception e) {
                onFailure.onFailure(e);
            }
        }, onFailure);
    }

    private static void enqueue(Context context, Request request, OnResponse<Response> onResponse, OnFailure onFailure) {
        CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(context.getMainLooper()).post(() -> onFailure.onFailure(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.code() == 200){
                    new Handler(context.getMainLooper()).post(() -> onResponse.onResponse(response));
                }else{
                    new Handler(context.getMainLooper()).post(() -> {
                        try {
                            if(response.body() != null || !response.body().string().isEmpty()){
                                onFailure.onFailure(new HttpException(response.code(), response.body().string()));
                            }else{
                                onFailure.onFailure(new HttpException(response.code(), ""));
                            }
                        } catch (IOException e) {
                            onFailure.onFailure(e);
                        }
                    });
                }
            }
        });
    }

    public static class HttpException extends Exception{
        public final int code;

        public HttpException(int code, String body) {
            super(body);
            this.code = code;
        }

        @Override
        public String toString() {
            return "HttpException{" +
                "code=" + code +
                ", message='" + getMessage() + '\'' +
                '}';
        }
    }
}
