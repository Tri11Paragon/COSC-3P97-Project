package com.mouseboy.finalproject.weather;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.mouseboy.finalproject.MainActivity;

import java.util.ArrayList;

public class LocationTracker {
    private static boolean hasGps;
    private static boolean hasNetwork;

    private static Location curr;
    private static final ArrayList<LocationListeners> listeners = new ArrayList<>();
    private static ActivityResultLauncher<String[]> requester;

    public static Location bestLocation() {
        return curr;
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

    public static synchronized void start(MainActivity context) {
        listeners.clear();
        requester = context.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), g -> {
            boolean g2 = true;
            for (boolean entry : g.values()) {
                g2 &= entry;
            }
            if (!g2) {
                new AlertDialog.Builder(context)
                        .setTitle("Permission Needed")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> perms(context, false)).create().show();
            } else {
                perms(context, true);
            }
        });
        perms(context, false);
    }

    private static void perms(MainActivity context, boolean granted) {
        if (granted) {
            startWithPerms(context);
        } else {
            requester.launch(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION});
        }
    }

    @SuppressLint("MissingPermission")//checked before
    private static synchronized void startWithPerms(MainActivity context) {
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
}
