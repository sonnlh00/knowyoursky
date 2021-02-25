package com.ngsown.knowyoursky.domain;

import com.ngsown.knowyoursky.R;
import com.ngsown.knowyoursky.domain.forecast.CurrentForecast;
import com.ngsown.knowyoursky.domain.forecast.HourlyForecast;
import com.ngsown.knowyoursky.utils.rxjava.SchedulerProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetWeatherForecastImpl implements GetWeatherForecast {
    SchedulerProvider schedulerProvider;
    @Inject
    public GetWeatherForecastImpl(SchedulerProvider schedulerProvider) {
        this.schedulerProvider = schedulerProvider;
    }
    @Override
    public void getCurrentForecast(double latitude, double longitude, String apiKey, Observer<CurrentForecast> observer){
        String requestURL = String.format(Locale.US, "https://api.openweathermap.org/data/2.5/weather?lat=%1$f&lon=%2$f&units=metric&appid=%3$s",
                latitude,
                longitude,
                apiKey);
        Observable<CurrentForecast> observable = Observable.create(new ObservableOnSubscribe<CurrentForecast>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<CurrentForecast> emitter) throws Throwable {
                emitter.onNext(doCurrentForecastApi(requestURL));
                emitter.onComplete();
            }
        })
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.mainThread());
        observable.subscribe(observer);
    }

    @Override
    public void getHourlyForecast(double latitude, double longitude, String apiKey, Observer<List<HourlyForecast>> observer){
        String requestURL = String.format(Locale.US, "https://api.openweathermap.org/data/2.5/onecall?lat=%1$f&lon=%2$f&exclude=current,minutely,daily&units=metric&appid=%3$s",
                latitude,
                longitude,
                apiKey);
        Observable<List<HourlyForecast>> observable = Observable.create(new ObservableOnSubscribe<List<HourlyForecast>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<HourlyForecast>> emitter) throws Throwable {
                emitter.onNext(doHourlyForecastApi(requestURL));
                emitter.onComplete();
            }
        })
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.mainThread());
        observable.subscribe(observer);
    }

    private CurrentForecast doCurrentForecastApi(String requestURL){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(requestURL).build();
        CurrentForecast forecast = new CurrentForecast();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            forecast = processCurrentData(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return forecast;
    }

    private List<HourlyForecast> doHourlyForecastApi(String requestURL){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(requestURL).build();
        List<HourlyForecast> forecast = new ArrayList<>();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            forecast = processHourlyData(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return forecast;
    }

    private CurrentForecast processCurrentData(String weatherData) throws JSONException{
        JSONObject resObject = new JSONObject(weatherData);
        if (resObject.getInt("cod") == 200) {
            JSONObject mainObject = resObject.getJSONObject("main");
            JSONObject weatherArray = resObject.getJSONArray("weather").getJSONObject(0);
            CurrentForecast currentForecast = new CurrentForecast();

            currentForecast.setCityName(resObject.getString("name"));
            currentForecast.setTemperature((int)(mainObject.getDouble("temp")));
            currentForecast.setTempFeel((int)(mainObject.getDouble("feels_like")));
            currentForecast.setHumidity(mainObject.getDouble("humidity"));
            String des = weatherArray.getString("description");
            des = (des != "") ? des.substring(0,1).toUpperCase() + des.substring(1) : "";
            currentForecast.setDescription(des);
            currentForecast.setWeatherType(weatherArray.getInt("id"));

            int timezone = resObject.getInt("timezone");
            Date currentDate = new Date();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
            dateFormatter.setTimeZone(
                    TimeZone.getTimeZone((timezone / 3600) > 0 ? String.format("GMT+%d", timezone/3600) : String.format("GMT%d", timezone / 3600))
            );
            currentForecast.setDateTime(dateFormatter.format(currentDate));
            int currentHour = Integer.parseInt(dateFormatter.format(currentDate).substring(0,2));
            currentForecast.setIconId(getIconId(currentForecast.getWeatherType(), currentHour));
            if (currentHour > 6 && currentHour < 18) {
                currentForecast.setBackgroundId(R.drawable.day_background);
            }
            else {
                currentForecast.setBackgroundId(R.drawable.night_background);
            }
            return currentForecast;
        }

        else {
            return null;
        }
    }

    private ArrayList<HourlyForecast> processHourlyData(String weatherData) throws JSONException {
        ArrayList<HourlyForecast> hourlyForecasts = new ArrayList<>();
        JSONObject resObj = new JSONObject(weatherData);
        if (!resObj.has("cod")){
            JSONArray hourlyArr = resObj.getJSONArray("hourly");
            Calendar calendar = Calendar.getInstance();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            for (int i = 1; i <= 24; i++){
                JSONObject mainObject = hourlyArr.getJSONObject(i);
                JSONObject weatherArray = mainObject.getJSONArray("weather").getJSONObject(0);

                HourlyForecast hourlyForecast = new HourlyForecast();

                hourlyForecast.setTemperature((int)(mainObject.getDouble("temp")));
                hourlyForecast.setWeatherType(weatherArray.getInt("id"));

                int timezone = resObj.getInt("timezone_offset");
                Date currentDate = new Date(mainObject.getLong("dt") * 1000);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
                dateFormatter.setTimeZone(
                        TimeZone.getTimeZone((timezone / 3600) > 0 ? String.format("GMT+%d", timezone/3600) : String.format("GMT%d", timezone / 3600))
                );
                hourlyForecast.setDateTime(dateFormatter.format(currentDate));
                int forecastHour = (currentHour + i) % 24;
                hourlyForecast.setIconId(getIconId(hourlyForecast.getWeatherType(), forecastHour));
                hourlyForecasts.add(hourlyForecast);
            }
        }
        return hourlyForecasts;
    }

    private int getIconId(int weatherType, int hourInDay){
        if (hourInDay >= 6 && hourInDay <= 18) {
            switch (weatherType / 100) {
                case 8:
                    if (weatherType == 800)
                        return R.drawable.clearsky_day;
                    else
                        return R.drawable.cloudy_day;
                case 7:
                    return R.drawable.mist;
                case 6:
                    return R.drawable.snowy_day;
                case 5:
                    return (R.drawable.rainy_day);
                case 3:
                    return (R.drawable.drizzle);
                case 2:
                    return (R.drawable.thunderstorm_day);
                default:
                    return (R.drawable.ic_baseline_refresh_24);
            }
        }
        else {
            switch (weatherType / 100) {
                case 8:
                    if (weatherType == 800)
                        return R.drawable.clearsky_night;
                    else
                        return R.drawable.cloudy_night;
                case 7:
                    return R.drawable.mist;
                case 6:
                    return R.drawable.snowy_night;
                case 5:
                    return (R.drawable.rainy_night);
                case 3:
                    return (R.drawable.drizzle);
                case 2:
                    return (R.drawable.thunderstorm_night);
                default:
                    return (R.drawable.ic_baseline_refresh_24);
            }
        }
    }
}
