package com.mouseboy.finalproject.server;

import android.content.Context;

import com.google.gson.Gson;
import com.mouseboy.finalproject.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Local {
    private static ServerApi.User currentUser = null;
    private static Information state = new Information();

    private static class Information{
        final ArrayList<ServerApi.AllWalkInfo> walks = new ArrayList<>();
        ServerApi.AllWalkInfo currentWalk;
    }

    public static synchronized void addWalk(){

    }

    public static void deleteWalk(ServerApi.WalkInfo walk){

    }

    public static void updateWalk(ServerApi.WalkInfo walk) {

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
