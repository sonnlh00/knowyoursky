package com.ngsown.knowyoursky.ui.main;

import android.util.Log;

import com.ngsown.knowyoursky.domain.GetWeatherForecastImpl;
import com.ngsown.knowyoursky.domain.forecast.CurrentForecast;
import com.ngsown.knowyoursky.domain.forecast.HourlyForecast;

import java.util.List;
import java.util.Observer;
import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
public class MainPresenter implements MainContract.Presenter {

    String apiKey = "fe2cae6dc99f16488b3bf799d3b6330c";
    double longitude = 106.147942;
    double latitude = 11.518422;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private GetWeatherForecastImpl getWeatherForecast; // model
    private MainContract.View view; // view
    private DisposableObserver<CurrentForecast> currentForecastObserver = new DisposableObserver<CurrentForecast>() {
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
    };
    private DisposableObserver<List<HourlyForecast>> hourlyForecastObserver = new DisposableObserver<List<HourlyForecast>>() {
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
    };
    public MainPresenter(MainContract.View view, GetWeatherForecastImpl getWeatherForecast) {
        this.view = view;
        this.getWeatherForecast = getWeatherForecast;

    }
    public void loadCurrentForecast(){
        compositeDisposable.add(getWeatherForecast.getCurrentForecast(latitude, longitude, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(currentForecastObserver));
    }

    @Override
    public void loadHourlyForecast() {
        compositeDisposable.add(getWeatherForecast.getHourlyForecast(latitude,longitude,apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(hourlyForecastObserver));
    }
    public void reloadForecast(){
        Log.d("MAIN_PRESENTER", "Reloading forecast");
        latitude = new Random().nextDouble()*90;
        // Dispose old observers
        currentForecastObserver.dispose();
        hourlyForecastObserver.dispose();

        currentForecastObserver = new DisposableObserver<CurrentForecast>() {
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
        };
        hourlyForecastObserver = new DisposableObserver<List<HourlyForecast>>() {
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
        };
        loadCurrentForecast();
        loadHourlyForecast();
    }
    private void showCurrentForecast(CurrentForecast currentForecast){
        view.showCurrentForecast(currentForecast);
    }
    private void showHourlyForecast(List<HourlyForecast> hourlyForecasts){
        view.showHourlyForecast(hourlyForecasts);
    }
    @Override
    public void initialize() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }
}
