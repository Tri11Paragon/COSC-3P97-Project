package com.mouseboy.finalproject;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Build;

import com.mouseboy.finalproject.server.Local;
import com.mouseboy.finalproject.weather.LocationTracker;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WalkTrackingService extends Service {
    static final String NOTIFICATION_CHANNEL = "service";

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
        Notification.Builder builder;
        // api 26 requires a notification channel for new notifications
        if (Build.VERSION.SDK_INT >= 26) {
            var channel = new NotificationChannel(
                NOTIFICATION_CHANNEL, "Walk Tracking", NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications to indicate that a walk is active");
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setBypassDnd(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE))
                .createNotificationChannel(channel);

            builder = new Notification.Builder(this, NOTIFICATION_CHANNEL);
        } else {
            builder = new Notification.Builder(this);
        }

        var resultIntent = new Intent(this, MainActivity.class);
        var stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        var resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return builder.setContentTitle("My Background Task")
            .setContentText("Task is running")
            .setSmallIcon(android.R.drawable.star_big_on)
            .setContentIntent(resultPendingIntent)
            // setting HIGH plays a sound
            .setPriority(Notification.PRIORITY_DEFAULT)
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
