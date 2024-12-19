package com.mouseboy.finalproject;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.mouseboy.finalproject.server.Local;
import com.mouseboy.finalproject.weather.LocationTracker;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WalkTrackingService extends Service {

    public synchronized static boolean start(Context context){
        if(isRunning(context)){
            return false;
        }else{
            Intent intent = new Intent(context, WalkTrackingService.class);
            context.startService(intent);
            return true;
        }
    }

    public synchronized static void stop(Context context){
        context.stopService(new Intent(context, WalkTrackingService.class));
    }

    public static boolean isRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (WalkTrackingService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        LocationTracker.startWithPerms(this);
        Local.load(this);
        Logger.getGlobal().log(Level.INFO, "onCreate: " + this);
    }

    private Notification createNotification() {
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
            stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new Notification.Builder(this)
            .setContentTitle("My Background Task")
            .setContentText("Task is running")
            .setSmallIcon(android.R.drawable.star_big_on)
            .setContentIntent(resultPendingIntent)
            .setPriority(Notification.PRIORITY_HIGH)
            .build();
    }

    private void locationUpdate(Location loc){
        Local.addThing(this, loc);
    }


    LocationTracker. LocationListeners listener;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.getGlobal().log(Level.INFO, "Start: " + this);

        Local.startWalk();

        startForeground(1, createNotification());
        listener = this::locationUpdate;
        LocationTracker.addListener(listener);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Logger.getGlobal().log(Level.INFO, "Destroy: " + this);
        Local.endWalk(this);
        LocationTracker.removeListener(listener);
    }
}
