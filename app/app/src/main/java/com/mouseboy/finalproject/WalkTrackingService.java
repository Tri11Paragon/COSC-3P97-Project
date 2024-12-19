package com.mouseboy.finalproject;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import com.mouseboy.finalproject.server.Local;
import com.mouseboy.finalproject.util.Util;
import com.mouseboy.finalproject.weather.LocationTracker;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WalkTrackingService extends Service {

    public static final String CHANNEL_ID = "WalkTrackingService";
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private Messenger messenger;;


    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        int startid;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.obj.toString()){
                case "Start":
                    startid=msg.arg1;
                    return;
                case "Close":
                    stopSelf(startid);
                    return;
            }
            if(msg.obj instanceof Location){
                Location l = (Location) msg.obj;
                Logger.getGlobal().log(Level.SEVERE, ""+this);
            }
        }
    }


    private static Messenger serviceMessenger;
    private static final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            serviceMessenger = new Messenger(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceMessenger = null;
        }
    };
    public synchronized static boolean start(Context context){
        if(isRunning(context)){
            Intent intent = new Intent(context, WalkTrackingService.class);
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
            context.unbindService(connection);
            return false;
        }else{
            Intent intent = new Intent(context, WalkTrackingService.class);
            context.startService(intent);
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
            context.unbindService(connection);
//            context.sendBroadcast();
            return true;
        }
    }

    public synchronized static void stop(Context context){
        Message msg = Message.obtain();
        msg.obj = "Close";
        try{
            context.bindService(new Intent(context, WalkTrackingService.class), connection, Context.BIND_AUTO_CREATE);

        }catch (Exception e){
            Util.logThrowable(e);
        }
        try {
            serviceMessenger.send(msg);
            context.unbindService(connection);
        } catch (RemoteException e) {
            Util.logThrowable(e);
        }
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

        HandlerThread thread = new HandlerThread("WalkTrackingService", HandlerThread.NORM_PRIORITY);
        thread.setDaemon(true);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
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


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.getGlobal().log(Level.INFO, "Start: " + this);
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = "Start";
        serviceHandler.sendMessage(msg);


        messenger = new Messenger(serviceHandler);

        startForeground(1, createNotification());
        LocationTracker.addListener(l -> {
            Message message = new Message();
            message.obj = l;
//            serviceHandler.handleMessage(message);
        });
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void onDestroy() {
        Logger.getGlobal().log(Level.INFO, "Destroy: " + this);
        Local.save(this);
        LocationTracker.startWithPerms(this);
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
