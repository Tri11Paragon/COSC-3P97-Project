package com.mouseboy.finalproject;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.mouseboy.finalproject.server.Local;
import com.mouseboy.finalproject.weather.LocationTracker;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WalkTrackingService extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    public static class Close{}

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            if(msg.obj instanceof Location){
                Location l = (Location) msg.obj;
                Logger.getGlobal().log(Level.SEVERE, "meow" + l.getLongitude());
            }
            if(msg.obj instanceof Close){
                stopSelf(msg.arg1);
            }
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                // Restore interrupt status.
//                Thread.currentThread().interrupt();
//            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
        }
    }

    @Override
    public void onCreate() {
        Local.load(this);
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        HandlerThread thread = new HandlerThread("WalkTrackingService", HandlerThread.NORM_PRIORITY);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);

        LocationTracker.addListener(l -> {
            Message message = new Message();
            message.obj = l;
            serviceHandler.handleMessage(message);
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Local.save(this);
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
