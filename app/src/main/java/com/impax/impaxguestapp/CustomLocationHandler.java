package com.impax.impaxguestapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class CustomLocationHandler {
    private static final String TAG = "CustomLocationHandler";
    private static final int REQUEST_CHECK_SETTINGS = 1001;

    private final Context context;
    private final Activity activity;
    private final LocationCallback locationCallback;
    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationRequest locationRequest;
    private LocationListener locationListener;

    public interface LocationListener {
        void locationOn();
        void locationCancelled();
        void locationChanged(Location location);
    }

    public CustomLocationHandler(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        // Create location request
        locationRequest = new LocationRequest.Builder(10000)  // 10 seconds
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)  // 5 seconds
                .setMaxUpdateDelayMillis(15000)    // 15 seconds
                .build();

        // Create location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null && locationListener != null) {
                    locationListener.locationChanged(locationResult.getLastLocation());
                }
            }
        };
    }

    public void setLocationListener(LocationListener listener) {
        this.locationListener = listener;
    }

    public void startLocationUpdates() {
        if (!hasLocationPermissions()) {
            Log.e(TAG, "Location permissions not granted");
            return;
        }

        // Check if location settings are satisfied
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        try {
            SettingsClient settingsClient = LocationServices.getSettingsClient(context);
            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

            task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    // Location settings are satisfied, start location updates
                    if (locationListener != null) {
                        locationListener.locationOn();
                    }
                    requestLocationUpdates();
                }
            });

            task.addOnFailureListener(activity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, show dialog to enable location
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            Log.e(TAG, "Error showing location settings dialog", sendEx);
                            if (locationListener != null) {
                                locationListener.locationCancelled();
                            }
                        }
                    } else {
                        Log.e(TAG, "Error checking location settings", e);
                        if (locationListener != null) {
                            locationListener.locationCancelled();
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error initializing location settings check", e);
            if (locationListener != null) {
                locationListener.locationCancelled();
            }
        }
    }

    private void requestLocationUpdates() {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (Exception e) {
            Log.e(TAG, "Error requesting location updates", e);
        }
    }

    public void stopLocationUpdates() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        } catch (Exception e) {
            Log.e(TAG, "Error stopping location updates", e);
        }
    }

    public boolean hasLocationPermissions() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}