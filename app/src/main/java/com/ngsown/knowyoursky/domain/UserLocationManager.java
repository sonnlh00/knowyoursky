package com.ngsown.knowyoursky.domain;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ngsown.knowyoursky.di.CompatActivity;
import com.ngsown.knowyoursky.domain.location.Location;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class UserLocationManager {
    public static final String TAG = UserLocationManager.class.getSimpleName();
    public static final int CODE_LOCATION = 0;
    private final AppCompatActivity context;
    private final LocationManager locationManager;
    private boolean isPermissionGranted = false;
    private final PublishSubject<Location> observable = PublishSubject.create();
    @Inject
    public UserLocationManager(@CompatActivity AppCompatActivity context, LocationManager locationManager) {
        this.context = context;
        this.locationManager = locationManager;
    }
    public void requestPermission(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, CODE_LOCATION);
        }
    }
    public boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    public boolean isLocationServiceOn(){
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void registerLocationObserver(Observer<Location> observer){
        observable.subscribe(observer);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull android.location.Location location) {
                observable.onNext(new Location(location.getLatitude(), location.getLongitude()));
                observable.onComplete();
                locationManager.removeUpdates(this);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                Log.d(TAG, "Location service is on");
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Log.d(TAG, "Location service is off");
                locationManager.removeUpdates(this);
            }
        });

    }

    public void permissionDenied(){
        isPermissionGranted = false;
    }


}
