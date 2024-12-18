package com.mouseboy.finalproject.server;

import java.util.ArrayList;

public class Local {
    private static ServerApi.User currentUser = null;
    private static final ArrayList<ServerApi.AllWalkInfo> walks = new ArrayList<>();
    private static ServerApi.AllWalkInfo currentWalk;


    public static void addWalk(){

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
