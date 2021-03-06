package com.ngsown.knowyoursky.ui.main;

import android.Manifest;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.ngsown.knowyoursky.di.ApiKey;
import com.ngsown.knowyoursky.domain.GetWeatherForecast;
import com.ngsown.knowyoursky.domain.forecast.CurrentForecast;
import com.ngsown.knowyoursky.domain.forecast.HourlyForecast;
import com.ngsown.knowyoursky.domain.location.Location;
import com.ngsown.knowyoursky.domain.UserLocationManager;
import com.ngsown.knowyoursky.domain.prefs.PrefsHelper;
import com.ngsown.knowyoursky.utils.NetworkChecking;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainPresenter implements MainContract.Presenter {
    private final String TAG = MainPresenter.class.getSimpleName();
    private final PrefsHelper prefsHelper;
    private final String apiKey;
    private Location location = null;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final GetWeatherForecast getWeatherForecast; // model
    private MainContract.View view; // view
    private final UserLocationManager userLocationManager;
    private final NetworkChecking networkChecking;


    @Inject
    public MainPresenter(GetWeatherForecast getWeatherForecast,
                         NetworkChecking networkChecking,
                         UserLocationManager locationManager,
                         PrefsHelper prefsHelper,
                         @ApiKey String apiKey) {
        this.getWeatherForecast = getWeatherForecast;
        this.networkChecking = networkChecking;
        this.userLocationManager = locationManager;
        this.prefsHelper = prefsHelper;
        this.apiKey = apiKey;
    }
        @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    public void loadForecast() {
        if (prefsHelper.hasCachedData()){
            Log.d(TAG, "Has cached data");
            if (userLocationManager.isPermissionGranted() && networkChecking.isNetworkAvailable() && userLocationManager.isLocationServiceOn()){
                loadCurrentForecast();
                loadHourlyForecast();
            }
            else if (networkChecking.isNetworkAvailable()){
                getCachedLocation();
                loadCurrentForecast();
                loadHourlyForecast();
            }
            else {
                loadCachedCurrentForecast();
                loadCachedHourlyForecast();
            }
        }
        else {
            Log.d(TAG, "Doesn't have cached data");
            if (!userLocationManager.isPermissionGranted()) {
                Log.d(TAG, "Error: No Permission!");
                view.showNoLocationPermissionDialog();
            }
            else if (!networkChecking.isNetworkAvailable()) {
                Log.d(TAG, "Error: No Internet");
            }
            else if (!userLocationManager.isLocationServiceOn()) {
                Log.d(TAG, "Error: No Location Service");
            }
            else{
                loadCurrentForecast();
                loadHourlyForecast();
            }
        }
    }
    private void loadCachedCurrentForecast() {
        getWeatherForecast.getCachedCurrentForecast(new Observer<CurrentForecast>(){
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@NonNull CurrentForecast currentForecast) {
                showCurrentForecast(currentForecast);
                Log.d("MainPresenter", "Current forecast observer: onNext");
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d("MainPresenter", "Current forecast observer: onComplete");
            }
        });
    }
    private void loadCachedHourlyForecast(){
        getWeatherForecast.getCachedHourlyForecast(new Observer<List<HourlyForecast>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@NonNull List<HourlyForecast> hourlyForecasts) {
                showHourlyForecast(hourlyForecasts);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private void loadCurrentForecast(){
        if (networkChecking.isNetworkAvailable()) {
            getWeatherForecast.getCurrentForecast(location.getLatitude(), location.getLongitude(), apiKey,
                    new Observer<CurrentForecast>(){
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(@NonNull CurrentForecast currentForecast) {
                            showCurrentForecast(currentForecast);
                            Log.d("MainPresenter", "Current forecast observer: onNext");
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            Log.d("MainPresenter", "Current forecast observer: onComplete");
                        }
                    });
        }
        else {
            loadCachedCurrentForecast();
        }
    }
    
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private void loadHourlyForecast() {
        if (networkChecking.isNetworkAvailable()) {
            getWeatherForecast.getHourlyForecast(location.getLatitude(), location.getLongitude(), apiKey, new Observer<List<HourlyForecast>>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    compositeDisposable.add(d);
                }

                @Override
                public void onNext(@NonNull List<HourlyForecast> hourlyForecasts) {
                    showHourlyForecast(hourlyForecasts);
                }

                @Override
                public void onError(@NonNull Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
        }
        else {
            loadCachedHourlyForecast();
        }
    }
    
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void reloadForecast(){
        Log.d("MAIN_PRESENTER", "Reloading forecast");
        onLocationPermissionGranted();
    }
    
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    public void onLocationPermissionGranted() {
        if (prefsHelper.hasCachedData()){
            loadCachedCurrentForecast();
            loadCachedHourlyForecast();
        }
        if (userLocationManager.isLocationServiceOn()){
            view.showLocationOnIcon();
            userLocationManager.registerLocationObserver(new Observer<Location>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    Log.d(TAG, "OnSubscribe");
                    compositeDisposable.add(d);
                }
                    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                @Override
                public void onNext(@NonNull Location loc) {
                    Log.d(TAG, "OnNext: Receive location update");
                    if (location == null) {
                        location = loc;
                        loadForecast();
                    }
                    else
                        location = loc;

                }

                @Override
                public void onError(@NonNull Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
        }
        else {
            Log.d(TAG, "No location service");
            view.showLocationOffIcon();
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    public void onLocationPermissionDenied() {
        loadForecast();
    }

    private void showCurrentForecast(CurrentForecast currentForecast){
        view.showCurrentForecast(currentForecast);
    }
    
    private void showHourlyForecast(List<HourlyForecast> hourlyForecasts){
        view.showHourlyForecast(hourlyForecasts);
    }

    @Override
    public void setView(MainContract.View view) {
        this.view = view;
    }

        @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    public void initialize() {
        if (userLocationManager.isPermissionGranted()){
            onLocationPermissionGranted();
        }
        else {
            userLocationManager.requestPermission();
        }
        networkChecking.registerNetworkObserver(new Observer<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@NonNull Boolean isInternetAvailable) {
                Log.d(TAG, "Network: " + isInternetAvailable);
                if (isInternetAvailable) {
                    view.hideNoInternetText();
                    view.hideNoInternetIcon();
                }
                else {
                    view.showNoInternetIcon();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void resume() {

    }
    
        @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    public void pause() {
        Log.d(TAG, "OnPause");
        if (location != null) {
            Log.d(TAG, String.format("OnPause: Saving lat: %f lon: %f", location.getLatitude(), location.getLongitude()));
            prefsHelper.setLatitude(location.getLatitude());
            prefsHelper.setLongitude(location.getLongitude());
        }
    }

    private void getCachedLocation(){
        location.setLatitude(prefsHelper.getLatitude());
        location.setLongitude(prefsHelper.getLongitude());
    }
}
