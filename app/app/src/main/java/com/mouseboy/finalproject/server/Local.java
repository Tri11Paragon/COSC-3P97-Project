package com.mouseboy.finalproject.server;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mouseboy.finalproject.util.OkHttp;
import com.mouseboy.finalproject.util.Util;
import com.mouseboy.finalproject.weather.LocationTracker;
import com.mouseboy.finalproject.weather.WeatherApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Local {
    private static ServerApi.User currentUser = null;
    private static Information state = new Information();

    public static synchronized void startWalk(Context context) {
        state.currentWalk = new ServerApi.AllWalkInfo();
        state.currentWalk.walk = new ServerApi.WalkInfo();
        state.currentWalk.walk.rating = 0.5;
        state.currentWalk.conditions = new ServerApi.WalkInstanceInfo[0];
        state.currentWalk.walk.start = new Date();
        addThing(context, LocationTracker.bestLocation());
    }

    private static synchronized void addCondition(WeatherApi. WeatherResult weather){
        if(state.currentWalk == null)return;
        ServerApi. WalkInstanceInfo[] conditions = new ServerApi.WalkInstanceInfo[state.currentWalk.conditions.length+1];
        System.arraycopy(state.currentWalk.conditions, 0, conditions, 0, state.currentWalk.conditions.length);
        conditions[state.currentWalk.conditions.length] = new ServerApi.WalkInstanceInfo();
        conditions[state.currentWalk.conditions.length].time = weather.current.time;
        conditions[state.currentWalk.conditions.length].conditions = weather.current;
        conditions[state.currentWalk.conditions.length].lat = weather.latitude;
        conditions[state.currentWalk.conditions.length].lon = weather.longitude;
    }

    public static synchronized void addThing(Context context, Location location){
        WeatherApi.request(
            context,
            new WeatherApi.WeatherRequest(location),
            Local::addCondition,
            Util.toastFail(context, "Cannot weather")
        );
    }

    public static synchronized void endWalk(Context context){
        state.currentWalk.walk.end = new Date();
        createWalk(context, state.currentWalk, v -> {}, Util.toastFail(context, "Failed to end walk"));
        state.currentWalk = null;
    }

    public synchronized static Date startOfCurrentWalk(){
        if(state.currentWalk == null){
            return null;
        }else{
            return state.currentWalk.walk.start;
        }
    }


    private static class Information{
        final ArrayList<ServerApi.AllWalkInfo> walks = new ArrayList<>();
        ServerApi.AllWalkInfo currentWalk;
        int curr_walk_id = -1;
    }

    static <T> T[] sort(List<T> data, T[] p, Comparator<T> cmp){
        T[] array = data.toArray(p);
        Arrays.sort(array, cmp);
        return array;
    }

    public static synchronized void listWalks(Context context, Date start, Date end, OkHttp.OnResponse<ServerApi.WalkInfo[]> response, OkHttp.OnFailure error){

        ArrayList<ServerApi.WalkInfo> walks = new ArrayList<>();
        for(int i = 0; i < state.walks.size(); i ++){
            ServerApi.WalkInfo walk = state.walks.get(i).walk;
            if(walk.start.compareTo(start) >= 0 && walk.end.compareTo(end) <= 0){
                walks.add(new Gson().fromJson(new Gson().toJson(walk), ServerApi.WalkInfo.class));
            }
        }
        if(isUserLoggedIn())
            ServerApi.listWalks(context, new ServerApi.ListWalks(getCurrentUser().id, start, end), nl -> {
                walks.addAll(Arrays.asList(nl));
                response.onResponse(sort(walks, new ServerApi.WalkInfo[0], (t1, t2) -> t2.start.compareTo(t1.start)));
            }, error);
        else{
            response.onResponse(sort(walks, new ServerApi.WalkInfo[0], (t1, t2) -> t2.start.compareTo(t1.start)));
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

        for(int i = 0; i < state.walks.size(); i ++){
            if (state.walks.get(i).walk.id == walk_id){
                state.walks.remove(i);
                save(context);
                response.onResponse(null);
                return;
            }
        }

        if(isUserLoggedIn()){
            ServerApi.deleteWalk(context, new ServerApi.WalkInfoId(getCurrentUser().id, walk_id), response, error);
        }
    }

    private static synchronized void createWalk(Context context, ServerApi.AllWalkInfo info, OkHttp.OnResponse<Integer> response, OkHttp.OnFailure error){
        if(isUserLoggedIn()){
            info.user_id = Local.getCurrentUser().id;
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

    public static synchronized void load(Context context){
        if(state.currentWalk != null)return;
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
