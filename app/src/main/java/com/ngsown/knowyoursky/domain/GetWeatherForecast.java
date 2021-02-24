package com.ngsown.knowyoursky.domain;

import com.ngsown.knowyoursky.domain.forecast.CurrentForecast;
import com.ngsown.knowyoursky.domain.forecast.HourlyForecast;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public interface GetWeatherForecast {
    public Observable<CurrentForecast> getCurrentForecast(double latitude, double longitude, String apiKey);
    public Observable<List<HourlyForecast>> getHourlyForecast(double latitude, double longitude, String apiKey);
}
