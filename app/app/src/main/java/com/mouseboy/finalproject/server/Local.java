package com.mouseboy.finalproject.server;

import android.content.Context;

import com.google.gson.Gson;
import com.mouseboy.finalproject.util.OkHttp;
import com.mouseboy.finalproject.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Stream;

public class Local {
    private static ServerApi.User currentUser = null;
    private static Information state = new Information();


    private static class Information{
        final ArrayList<ServerApi.AllWalkInfo> walks = new ArrayList<>();
        ServerApi.AllWalkInfo currentWalk;
        int curr_walk_id = -1;
    }

    public static synchronized void listWalks(Context context, Date start, Date end, OkHttp.OnResponse<ServerApi.WalkInfo[]> response, OkHttp.OnFailure error){
        java.util.stream.Stream<ServerApi.WalkInfo> localStream = state.walks.stream()
            .map(v -> v.walk)
            .filter(walk -> walk.start.compareTo(start) < 0)
            .filter(walk -> walk.start.compareTo(end) > 0)
            .sorted(Comparator.comparingLong(walkInfo -> walkInfo.start.getTime()));
        if(isUserLoggedIn())
            ServerApi.listWalks(context, new ServerApi.ListWalks(getCurrentUser().id, start, end), nl -> {
                response.onResponse(Stream.concat(Arrays.stream(nl), localStream)
                    .toArray(ServerApi.WalkInfo[]::new));
            }, error);
        else{
            response.onResponse(localStream
                .toArray(ServerApi.WalkInfo[]::new));
        }
    }

    public static synchronized void listWalkInfo(Context context, int walk_id, OkHttp.OnResponse<ServerApi.WalkInstanceInfo[]> response, OkHttp.OnFailure error){
        for(ServerApi.AllWalkInfo walk:state.walks){
            if(walk.walk.id == walk_id){
                response.onResponse(walk.conditions);
                return;
            }
        }
        if(isUserLoggedIn())
            ServerApi.listWalkInfo(context, new ServerApi.WalkInfoId(getCurrentUser().id, walk_id), response, error);
    }

    public static synchronized void updateWalk(Context context, ServerApi.WalkInfoUpdate update, OkHttp.OnResponse<Void> response, OkHttp.OnFailure error){
        for(ServerApi.AllWalkInfo walk:state.walks){
            if(walk.walk.id == update.walk_id){
                walk.walk.rating = update.rating;
                walk.walk.comment = update.comment;
                walk.walk.name = update.name;
                save(context);
                response.onResponse(null);
                return;
            }
        }
        if(isUserLoggedIn()) {
            update.user_id = getCurrentUser().id;
            ServerApi.updateWalk(context, update, response, error);
        }
    }

    public static synchronized void deleteWalk(Context context, int walk_id, OkHttp.OnResponse<Void> response, OkHttp.OnFailure error){
        if(state.walks.removeIf(v -> v.walk.id == walk_id)){
            save(context);
            response.onResponse(null);
            return;
        }

        if(isUserLoggedIn()){
            ServerApi.deleteWalk(context, new ServerApi.WalkInfoId(getCurrentUser().id, walk_id), response, error);
        }
    }

    private static synchronized void createWalk(Context context, ServerApi.AllWalkInfo info, OkHttp.OnResponse<Integer> response, OkHttp.OnFailure error){
        if(isUserLoggedIn()){
            ServerApi.createWalk(context, info, response, error);
        }else{
            info.walk.id = state.curr_walk_id--;
            state.walks.add(new Gson().fromJson(new Gson().toJson(info), ServerApi.AllWalkInfo.class));
            save(context);
            response.onResponse(info.walk.id);
        }
    }

    private static File nya;
    private static File getOut(Context context) {
        if(nya == null){
            String path = context.getFilesDir().getAbsolutePath();
            path += File.separator;
            path += "local.json";
            nya = new File(path);
        }
        return nya;
    }

    public static void save(Context context){
        try(FileOutputStream out = new FileOutputStream(getOut(context))) {
            out.write(new Gson().toJson(state).getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            Util.logThrowable(e);
        }
    }

    public static void load(Context context){
        if (!getOut(context).exists()) return;
        try(FileReader reader = new FileReader(getOut(context))) {
            state = new Gson().fromJson(reader, Information.class);
        } catch (Exception e) {
            Util.logThrowable(e);
        }
    }

    public static boolean isUserLoggedIn(){
        return currentUser != null;
    }
    public static ServerApi.User getCurrentUser(){
        return currentUser;
    }
    public static void setUserDisplayName(String name){
        currentUser.name = name;
    }

    public static void login(ServerApi.User user){
        currentUser = user;
    }

    public static void logout(){
        currentUser = null;
    }
}
