package com.ngsown.knowyoursky.domain;

import com.ngsown.knowyoursky.domain.forecast.CurrentForecast;
import com.ngsown.knowyoursky.domain.forecast.HourlyForecast;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;

public interface GetWeatherForecast {
    public void getHourlyForecast(double latitude, double longitude, String apiKey, Observer<List<HourlyForecast>> observer);
    public void getCurrentForecast(double latitude, double longitude, String apiKey, Observer<CurrentForecast> observer);
}
