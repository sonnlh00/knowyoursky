package com.ngsown.knowyoursky.domain;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ngsown.knowyoursky.domain.location.Location;

import io.reactivex.rxjava3.core.Observable;

public class UserLocationManager{
    public static final int CODE_LOCATION = 0;
    private Activity context;
    private boolean isPermissionGranted = false;
    private boolean isLocationUpdated = false;
    private double latitude = 0.0;
    private double longitude = 0.0;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public UserLocationManager(Activity context) {
        this.context = context;
//        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
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

    public boolean isPermissionGranted() {
        return isPermissionGranted;
    }
    public boolean isLocationUpdated(){
        return isLocationUpdated;
    }

    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION})
    public void permissionGranted(){
        isPermissionGranted = true;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        Task<android.location.Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull Task<android.location.Location> task) {
                android.location.Location temp = task.getResult();
                if (temp != null) {
                    setLocation(temp.getLatitude(), temp.getLongitude());
                    Log.d("FUSED", String.format("lat: %f lon: %f", temp.getLatitude(), temp.getLongitude()));
                }
                else
                    Log.d("FUSED", "temp is null");
            }
        });
        locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(10)
                .setNumUpdates(2);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Log.d("NEW_LOCATION",String.format("lat: %f lon: %f",
                        locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude()));
                isLocationUpdated = true;
                setLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                super.onLocationResult(locationResult);
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        }, Looper.myLooper());
//        Log.d("PERMISSION", "Location permission granted!");
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
//            @Override
//            public void onLocationChanged(@NonNull android.location.Location location) {
////                Log.d("LOCATION",String.format("lat: %f lon: %f",
////                        location.getLatitude(),
////                        location.getLongitude()));
////                //isLocationUpdated = true;
////                //setLocation(location.getLatitude(), location.getLongitude());
//            }
//        });
//        Log.d("PERMISSION","Finished permissionGranted");
    }

    public void permissionDenied(){
        isPermissionGranted = false;
    }
    Observable<Location> emitNewLocation(double latitude, double longitude){
        return Observable.just(new Location(latitude, longitude));
    }
    private void setLocation(double lat, double lon){
        this.latitude = lat;
        this.longitude = lon;
    }
}
