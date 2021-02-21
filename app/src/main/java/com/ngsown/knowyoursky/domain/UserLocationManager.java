package com.ngsown.knowyoursky.domain;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import com.ngsown.knowyoursky.domain.location.Location;

public class UserLocationManager{
    public static final int CODE_LOCATION = 0;
    private Activity context;
    private LocationManager locationManager;
    private boolean isPermissionGranted = false;
    private boolean isGPSOn = false;
    private boolean isLocationUpdated = false;
    private double latitude = 0.0;
    private double longitude = 0.0;
    public UserLocationManager(Activity context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
    public void checkPermission(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, CODE_LOCATION);
        }
        else
            permissionGranted();
    }
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public Location getLocation() {
        if (isPermissionGranted){
            return new Location(latitude, longitude);
        }
        else
            return null;
    }
//    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//    @Override
//    public void onLocationChanged(@NonNull android.location.Location location) {
//        //locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        Log.d("LOCATION", "changed");
//        this.latitude = location.getLatitude();
//        this.longitude = location.getLongitude();
//    }
//
//    @Override
//    public void onProviderEnabled(@NonNull String provider) {
//        this.isGPSOn = true;
//    }
//
//    @Override
//    public void onProviderDisabled(@NonNull String provider) {
//        this.isGPSOn = false;
//    }

    public boolean isPermissionGranted() {
        return isPermissionGranted;
    }
    public boolean isLocationUpdated(){
        return isLocationUpdated;
    }
    public void setPermissionGranted(boolean permissionGranted) {
        isPermissionGranted = permissionGranted;
    }
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION})
    public void permissionGranted(){
        isPermissionGranted = true;
        Log.d("PERMISSION", "Location permission granted!");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull android.location.Location location) {
                Log.d("LOCATION","Location changes detected");
                isLocationUpdated = true;
                setLocation(location.getLatitude(), location.getLongitude());
            }
        });
        Log.d("PERMISSION","Finished permissionGranted");
    }

    public void permissionDenied(){
        isPermissionGranted = false;
    }

    private void setLocation(double lat, double lon){
        this.latitude = lat;
        this.longitude = lon;
    }
}
