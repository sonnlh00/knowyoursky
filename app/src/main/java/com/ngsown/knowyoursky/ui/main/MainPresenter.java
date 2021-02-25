package com.ngsown.knowyoursky.ui.main;

import android.Manifest;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.ngsown.knowyoursky.di.ApiKey;
import com.ngsown.knowyoursky.domain.GetWeatherForecast;
import com.ngsown.knowyoursky.domain.forecast.CurrentForecast;
import com.ngsown.knowyoursky.domain.forecast.HourlyForecast;
import com.ngsown.knowyoursky.domain.location.Location;
import com.ngsown.knowyoursky.domain.UserLocationManager;
import com.ngsown.knowyoursky.domain.prefs.PrefsHelper;
import com.ngsown.knowyoursky.utils.executor.Interactor;
import com.ngsown.knowyoursky.utils.NetworkChecking;
import com.ngsown.knowyoursky.utils.rxjava.SchedulerProvider;
import com.ngsown.knowyoursky.utils.executor.ThreadExecutor;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class MainPresenter implements MainContract.Presenter {
    private final PrefsHelper prefsHelper;
    private final SchedulerProvider schedulerProvider;
    private String apiKey;
    private Location location;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final GetWeatherForecast getWeatherForecast; // model
    private MainContract.View view; // view
    private UserLocationManager userLocationManager;
    private NetworkChecking networkChecking;
    // TODO: implement network checking
    private DisposableObserver<Boolean> networkObserver = new DisposableObserver<Boolean>() {
        @Override
        public void onNext(@NonNull Boolean aBoolean) {
            if (aBoolean){}
                //loadForecast();
            else
                view.showNoInternetError();
        }

        @Override
        public void onError(@NonNull Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    };

    @Inject
    public MainPresenter(GetWeatherForecast getWeatherForecast,
                         NetworkChecking networkChecking,
                         UserLocationManager locationManager,
                         PrefsHelper prefsHelper,
                         SchedulerProvider schedulerProvider,
                         @ApiKey String apiKey) {
        this.getWeatherForecast = getWeatherForecast;
        this.networkChecking = networkChecking;
        this.userLocationManager = locationManager;
        this.prefsHelper = prefsHelper;
        this.schedulerProvider = schedulerProvider;
        this.apiKey = apiKey;
    }
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    @Override
    public void loadForecast() {
        location = userLocationManager.getLocation();
        if (location != null && userLocationManager.isLocationUpdated()){
            prefsHelper.setLatitude(location.getLatitude());
            prefsHelper.setLongitude(location.getLongitude());
            Log.d("LOCATION", String.format("%f %f", location.getLatitude(), location.getLongitude()));
            loadCurrentForecast();
            loadHourlyForecast();
        }
        else if (location != null && !userLocationManager.isLocationUpdated() && prefsHelper.hasSavedLocation()){
            Log.d("LOCATION", "Load old location");
            location.setLatitude(prefsHelper.getLatitude());
            location.setLongitude(prefsHelper.getLongitude());
            Log.d("LOCATION", String.format("%f %f", location.getLatitude(), location.getLongitude()));
            loadCurrentForecast();
            loadHourlyForecast();
        }
        else if (location != null && !userLocationManager.isLocationUpdated() && !prefsHelper.hasSavedLocation()) {
            Log.d("LOCATION","Wait for new location");
            // Loading dialog here
            view.showLoadingLocationToast();
            while (!userLocationManager.isLocationUpdated()){}
            //view.showLocationDetectedAlert();
            location = userLocationManager.getLocation();
            prefsHelper.setLatitude(location.getLatitude());
            prefsHelper.setLongitude(location.getLongitude());
            Log.d("LOCATION", String.format("%f %f", location.getLatitude(), location.getLongitude()));
            loadCurrentForecast();
            loadHourlyForecast();
        }
        else {
            view.showNoLocationPermissionError();
        }
    }
    
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void loadCurrentForecast(){
        if (location != null) {
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
        else
            view.showNoLocationPermissionError();
    }
    
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void loadHourlyForecast() {
        if (location != null) {
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
    }
    
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void reloadForecast(){
        Log.d("MAIN_PRESENTER", "Reloading forecast");
        loadForecast();
    }
    
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    @Override
    public void onLocationPermissionGranted() {
        userLocationManager.permissionGranted();
        loadForecast();
//        threadExecutor.run(new Interactor() {
//            @RequiresPermission(anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
//            @Override
//            public void run() {
//                Log.d("PER_GRANTED","After calling per granted");
//
//            }
//        });

    }

    @Override
    public void onLocationPermissionDenied() {
        userLocationManager.permissionDenied();
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

    @Override
    public void initialize() {
//        if (Looper.getMainLooper().getThread() == Thread.currentThread()){
//            Log.d("THREAD", "Running on UI thread");
//        }
//        else
//            Log.d("THREAD", "Running on worker thread");
        Log.d("INITIALIZE", "Load forecast");
        userLocationManager.checkPermission();
        if (userLocationManager.isPermissionGranted()) {
            loadForecast();
        }
        new CompositeDisposable().add(networkChecking.getNetworkObservable()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.io())
                .subscribeWith(networkObserver));
        new Thread(() -> networkChecking.registerNetworkCallback()).start();

    }

    @Override
    public void resume() {

    }
    
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    @Override
    public void pause() {
        Log.d("LIFE_CYCLE", "OnPause");
        Location temp = userLocationManager.getLocation();
        if (temp != null) {
            Log.d("NEW_PREFS", String.format("lat: %f lon: %f", temp.getLatitude(), temp.getLongitude()));
            prefsHelper.setLatitude(temp.getLatitude());
            prefsHelper.setLongitude(temp.getLongitude());
        }
    }
}
