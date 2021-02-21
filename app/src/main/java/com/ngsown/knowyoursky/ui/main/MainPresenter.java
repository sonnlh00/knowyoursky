package com.ngsown.knowyoursky.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.ngsown.knowyoursky.domain.GetWeatherForecastImpl;
import com.ngsown.knowyoursky.domain.forecast.CurrentForecast;
import com.ngsown.knowyoursky.domain.forecast.HourlyForecast;
import com.ngsown.knowyoursky.domain.location.Location;
import com.ngsown.knowyoursky.domain.UserLocationManager;
import com.ngsown.knowyoursky.domain.prefs.PrefsHelper;
import com.ngsown.knowyoursky.utils.Interactor;
import com.ngsown.knowyoursky.utils.NetworkChecking;
import com.ngsown.knowyoursky.utils.ThreadExecutor;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
public class MainPresenter implements MainContract.Presenter {

    private final PrefsHelper prefsHelper;
    String apiKey = "fe2cae6dc99f16488b3bf799d3b6330c";
    Location location;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final GetWeatherForecastImpl getWeatherForecast; // model
    private final MainContract.View view; // view
    private UserLocationManager userLocationManager;
    private NetworkChecking networkChecking;
    private ThreadExecutor threadExecutor = new ThreadExecutor();
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
//    private DisposableObserver<CurrentForecast> currentForecastObserver = new DisposableObserver<CurrentForecast>() {
//        @Override
//        public void onNext(@NonNull CurrentForecast currentForecast) {
//            showCurrentForecast(currentForecast);
//        }
//
//        @Override
//        public void onError(@NonNull Throwable e) {
//
//        }
//
//        @Override
//        public void onComplete() {
//
//        }
//    };
//    private DisposableObserver<List<HourlyForecast>> hourlyForecastObserver = new DisposableObserver<List<HourlyForecast>>() {
//        @Override
//        public void onNext(@NonNull List<HourlyForecast> hourlyForecasts) {
//            showHourlyForecast(hourlyForecasts);
//        }
//
//        @Override
//        public void onError(@NonNull Throwable e) {
//
//        }
//
//        @Override
//        public void onComplete() {
//
//        }
//    };

    public MainPresenter(MainContract.View view, GetWeatherForecastImpl getWeatherForecast, NetworkChecking networkChecking, UserLocationManager locationManager, PrefsHelper prefsHelper) {
        this.view = view;
        this.getWeatherForecast = getWeatherForecast;
        this.networkChecking = networkChecking;
        this.userLocationManager = locationManager;
        this.prefsHelper = prefsHelper;
    }
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
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
    public void loadCurrentForecast(){
        if (location != null) {
            compositeDisposable.add(getWeatherForecast.getCurrentForecast(location.getLatitude(), location.getLongitude(), apiKey)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<CurrentForecast>() {
                        @Override
                        public void onNext(@NonNull CurrentForecast currentForecast) {
                            showCurrentForecast(currentForecast);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    }));

        }
        else
            view.showNoLocationPermissionError();
    }
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void loadHourlyForecast() {
        if (location != null) {
            compositeDisposable.add(getWeatherForecast.getHourlyForecast(location.getLatitude(), location.getLongitude(), apiKey)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<List<HourlyForecast>>() {
                        @Override
                        public void onNext(@NonNull List<HourlyForecast> hourlyForecasts) {
                            view.showHourlyForecast(hourlyForecasts);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        }
    }
    public void reloadForecast(){
        Log.d("MAIN_PRESENTER", "Reloading forecast");
//        latitude = 10.644363;
//        longitude = 106.488701;
        // Dispose old observers
//        currentForecastObserver.dispose();
//        hourlyForecastObserver.dispose();

//        currentForecastObserver = new DisposableObserver<CurrentForecast>() {
//            @Override
//            public void onNext(@NonNull CurrentForecast currentForecast) {
//                showCurrentForecast(currentForecast);
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        };
//        hourlyForecastObserver = new DisposableObserver<List<HourlyForecast>>() {
//            @Override
//            public void onNext(@NonNull List<HourlyForecast> hourlyForecasts) {
//                showHourlyForecast(hourlyForecasts);
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        };
        loadForecast();
    }

    @Override
    public void setLocationManager(UserLocationManager userLocationManager) {
        this.userLocationManager = userLocationManager;
    }
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    @Override
    public void onLocationPermissionGranted() {
        userLocationManager.permissionGranted();
        threadExecutor.run(new Interactor() {
            @Override
            public void run() {
                Log.d("PER_GRANTED","After calling per granted");
                loadForecast();
            }
        });
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
    public void initialize() {
//        if (Looper.getMainLooper().getThread() == Thread.currentThread()){
//            Log.d("THREAD", "Running on UI thread");
//        }
//        else
//            Log.d("THREAD", "Running on worker thread");
        Log.d("INITIALIZE", "Load forecast");
        userLocationManager.checkPermission();
        if (userLocationManager.isPermissionGranted()) {
            Log.d("THREAD_EXECUTOR", "Before loading forecast");
            threadExecutor.run(new Interactor() {
                @Override
                public void run() {
                    Log.d("THREAD_EXECUTOR", "Load forecast");
                    loadForecast();
                }
            });
        }
        new CompositeDisposable().add(networkChecking.getNetworkObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(networkObserver));
        new Thread(new Runnable() {
            @Override
            public void run() {
                networkChecking.registerNetworkCallback();
            }
        }).start();

    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }
}
