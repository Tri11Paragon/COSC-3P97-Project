package com.mouseboy.finalproject.weather;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.mouseboy.finalproject.HomePage;
import com.mouseboy.finalproject.MainActivity;
import com.mouseboy.finalproject.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LocationTracker {
    private static boolean hasGps;
    private static boolean hasNetwork;

    private static Location curr;
    private static final ArrayList<LocationListeners> listeners = new ArrayList<>();


    public static Location bestLocation() {
        return curr;
    }

    public static synchronized void removeListener(LocationListeners listener) {
        listeners.remove(listener);
    }

    public interface LocationListeners {
        void update(Location loc);
    }

    public static synchronized void addListener(LocationListeners listener) {
        listeners.add(listener);
        if (curr != null) {
            listener.update(curr);
        }
    }

    @SuppressLint("MissingPermission")//checked before
    public static synchronized void startWithPerms(Context context) {
        LocationManager lm = getSystemService(context, LocationManager.class);

        assert lm != null;
        hasGps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        hasNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (hasGps) {
            lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F,
                    create(true)
            );
        }
        if (hasNetwork) {
            lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    create(false)
            );
        }
        gps(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        network(lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
    }

    @SuppressLint("MissingPermission")//checked before
    public static synchronized void hasSomething(Context context) {
        LocationManager lm = getSystemService(context, LocationManager.class);
        assert lm != null;
        hasGps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        hasNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        gps(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        network(lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
    }

    private static LocationListener create(boolean g) {
        return new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (g) gps(location);
                else network(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onFlushComplete(int requestCode) {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                if (g) hasGps = false;
                else hasNetwork = false;
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                if (g) hasGps = true;
                else hasNetwork = true;
            }
        };
    }

    private static void network(Location loc) {
        if (!hasGps && loc != null) {
            curr = loc;
            for (LocationListeners item : listeners) item.update(curr);
        }
    }

    private static void gps(Location loc) {
        if (loc != null) {
            curr = loc;
            for (LocationListeners item : listeners) item.update(curr);
        }
    }



    private static HashMap<HomePage, ActivityResultLauncher<String[]>> requesters = new HashMap();
    private static final ArrayList<Runnable> requestedPerms = new ArrayList<>();

    public static void setupPermReq(HomePage context){
        ActivityResultLauncher<String[]> requester = context.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), g -> {
            boolean g2 = true;
            for (boolean entry : g.values()) {
                g2 &= entry;
            }
            if (g2) {
                hasSomething(context.requireContext());
                synchronized (requestedPerms){
                    for(int i = 0; i < requestedPerms.size(); i ++) requestedPerms.get(i).run();
                    requestedPerms.clear();
                }
            } else {
                Objects.requireNonNull(requesters.get(context)).launch(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION});
            }
        });
        requesters.put(context, requester);
    }
    public static void ensurePermissions(HomePage context, Runnable run){
        synchronized (requestedPerms){
            requestedPerms.add(run);
        }
        Objects.requireNonNull(requesters.get(context)).launch(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION});
    }
}
