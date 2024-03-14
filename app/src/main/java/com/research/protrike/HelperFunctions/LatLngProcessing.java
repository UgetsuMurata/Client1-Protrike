package com.research.protrike.HelperFunctions;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.CountDownLatch;

public class LatLngProcessing {

    public static Integer LATLNG_REQCODE = 1001100;

    public static String getLocationAddress(Context context, LatLng latLng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address obj = addresses.get(0);
            return obj.getAddressLine(0);
        } catch (IOException e) {
            Toast.makeText(context, "Can't retrieve location name now.", Toast.LENGTH_SHORT).show();
            return "Search this location: ";
        }
    }

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

    public static boolean getCurrentLocation(Context context, LocationCallback locationCallback) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        boolean isOn = askForGPS(context, new PermissionReturn() {
            @Override
            public void permissionResponse(boolean response) {

            }
        });
        if (!isOn) {
            Toast.makeText(context, "GPS Location is required to do this action.", Toast.LENGTH_SHORT).show();
            return false;
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
        return true;
    }

    public interface PermissionReturn {
        void permissionResponse(boolean response);
    }

    public static boolean askForGPS(Context context, PermissionReturn permissionReturn) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            SettingsClient settingsClient = LocationServices.getSettingsClient(context);
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10 * 1000);
            locationRequest.setFastestInterval(2 * 1000);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            builder.setAlwaysShow(true);


            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                settingsClient.checkLocationSettings(locationSettingsRequest)
                        .addOnSuccessListener((Activity) context, new OnSuccessListener<LocationSettingsResponse>() {
                            @Override
                            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                                permissionReturn.permissionResponse(true);
                            }
                        })
                        .addOnFailureListener((Activity) context, e -> {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult((Activity) context, 1111);
                                        permissionReturn.permissionResponse(true);
                                    } catch (IntentSender.SendIntentException ignored) {
                                        permissionReturn.permissionResponse(false);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    permissionReturn.permissionResponse(true);
                                    break;
                            }
                        });
            }
            return false;
        }
        return true;
    }

    public interface LocationReturn {
        void LatLngReturn(LatLng latLng);
    }

    public static void getCurrentLocation(Context context, LocationReturn locationReturn) {

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Please turn your GPS on.", Toast.LENGTH_SHORT).show();
            locationReturn.LatLngReturn(null);
            return;
        }
        boolean isOn = askForGPS(context, new PermissionReturn() {
            @Override
            public void permissionResponse(boolean response) {
                if (!response) {
                    Toast.makeText(context, "GPS Location is required to do this action.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (!isOn) {
            Toast.makeText(context, "GPS is off, try again later.", Toast.LENGTH_SHORT).show();
            return;
        }
        ;
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(locationRequest, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                locationReturn.LatLngReturn(latLng);
                fusedLocationClient.removeLocationUpdates(this);
            }
        }, Looper.getMainLooper());
    }
}