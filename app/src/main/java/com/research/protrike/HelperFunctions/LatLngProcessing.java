package com.research.protrike.HelperFunctions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.util.concurrent.Semaphore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.CountDownLatch;

public class LatLngProcessing {

    public static Integer LATLNG_REQCODE = 1001100;

    public static float getDistance(LatLng point1, LatLng point2) {
        Location location1 = new Location("point1");
        location1.setLatitude(point1.latitude);
        location1.setLongitude(point1.longitude);

        Location location2 = new Location("point2");
        location2.setLatitude(point2.latitude);
        location2.setLongitude(point2.longitude);

        return location1.distanceTo(location2);
    }

    public static boolean checkForPermissions(Context context) {
        try {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, LATLNG_REQCODE);
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public interface LocationCallback {
        void onLocationChanged(@NonNull LatLng latLng);
    }

    public static void getCurrentLocation(Context context, LocationCallback locationCallback) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Criteria criteria = new Criteria();
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_LOW);
        criteria.setBearingAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_MEDIUM);
        locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 5, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        Log.d("LOCATION_CHANGED", String.format("new location found: %s %s", location.getLatitude(), location.getLongitude()));
                        locationCallback.onLocationChanged(new LatLng(location.getLatitude(), location.getLongitude()));
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                });
    }

    public interface LocationReturn{
        void LatLngReturn(LatLng latLng);
    }

    public static void getCurrentLocation(Context context, LocationReturn locationReturn) {

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Please turn your GPS on.", Toast.LENGTH_SHORT).show();
            locationReturn.LatLngReturn(null);
        }
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        // Get the last known location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            LatLng latLng = new LatLng(latitude, longitude);
                            locationReturn.LatLngReturn(latLng);
                        }
                    }
                });
    }
}