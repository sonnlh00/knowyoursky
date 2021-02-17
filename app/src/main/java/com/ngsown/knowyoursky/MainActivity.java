package com.ngsown.knowyoursky;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ngsown.knowyoursky.adapters.HourlyAdapter;
import com.ngsown.knowyoursky.model.CurrentWeather;
import com.ngsown.knowyoursky.model.HourlyWeather;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    NetworkChecking networkChecking;
    Observer isNetworkAvailable; // Observer for network changes
    TextView txtCity, txtTemperature, txtDescription, txtTimeUpdated, txtFeelsLike;
    ImageView imgWeather;
    ConstraintLayout layout;
    CurrentWeather currentWeather;
    ArrayList<HourlyWeather> hourlyWeathers;

    private RecyclerView hourlyRecyclerView;
    private HourlyAdapter hourlyAdapter;

    String apiKey = "fe2cae6dc99f16488b3bf799d3b6330c";
    double longitude = 106.147942;
    double latitude = 11.518422;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        binding = DataBindingUtil.setContentView(MainActivity.this,
//                R.layout.activity_main);

        txtCity = findViewById(R.id.txtCity);
        txtTemperature = findViewById(R.id.txtTemperature);
        txtDescription = findViewById(R.id.txtDescription);
        txtTimeUpdated = findViewById(R.id.txtTimeUpdated);
        txtFeelsLike = findViewById(R.id.txtFeelsLike);
        imgWeather = findViewById(R.id.imgWeather);
        layout = findViewById(R.id.layoutMain);
        hourlyWeathers = new ArrayList<>();

        hourlyRecyclerView = findViewById(R.id.listHourly);
        hourlyAdapter = new HourlyAdapter(hourlyWeathers, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        hourlyRecyclerView.setLayoutManager(layoutManager);
        hourlyRecyclerView.setAdapter(hourlyAdapter);

        isNetworkAvailable = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (networkChecking != null && networkChecking.isNetworkAvailable()){
                    Toast.makeText(MainActivity.this, "Fetching data...", Toast.LENGTH_SHORT).show();
                    getCurrentForecast(latitude, longitude, apiKey);
                    getHourlyForecast(latitude, longitude, apiKey);
                    Log.d("NETWORK", "Available");
                }
                else{
                    alertRequestError();
                    Log.d("NETWORK", "Unavailable");
                }
            }
        };
        networkChecking = new NetworkChecking(this);
        networkChecking.addObserver(isNetworkAvailable);
        networkChecking.registerNetworkCallback();

    }

    public void getHourlyForecast(double latitude, double longitude, String apiKey){
        String requestURL = String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%1$f&lon=%2$f&exclude=current,minutely,daily&units=metric&appid=%3$s",
                latitude,
                longitude,
                apiKey);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(requestURL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (response.isSuccessful()){
                        String res = response.body().string();
                        Log.d("RESPONSE", res);
                        hourlyWeathers = processHourlyData(res);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateHourlyView();
                            }
                        });
                    }
                } catch (IOException | JSONException e) {
                    Log.e("ERROR","Exception caught",e);
                }
            }
        });
    }

    public void getCurrentForecast(double latitude, double longitude, String apiKey) {
        String requestURL = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%1$f&lon=%2$f&units=metric&appid=%3$s",
                latitude,
                longitude,
                apiKey);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(requestURL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (response.isSuccessful()){
                        String res = response.body().string();
                        Log.d("RESPONSE", res);
                        currentWeather = processCurrentData(res);
                        if (currentWeather != null) {
                            runOnUiThread(() -> updateCurrentView());
                        }
                        else
                            alertRequestError();
                    }
                } catch (IOException | JSONException e) {
                    Log.e("ERROR","Exception caught",e);
                }
            }
        });
    }

    private CurrentWeather processCurrentData(String weatherData) throws JSONException{
        JSONObject resObject = new JSONObject(weatherData);
        if (resObject.getInt("cod") == 200) {
            JSONObject mainObject = resObject.getJSONObject("main");
            JSONObject weatherArray = resObject.getJSONArray("weather").getJSONObject(0);
            CurrentWeather currentWeather = new CurrentWeather();

            currentWeather.setCityName(resObject.getString("name"));
            currentWeather.setTemperature((int)(mainObject.getDouble("temp")));
            currentWeather.setTempFeel((int)(mainObject.getDouble("feels_like")));
            currentWeather.setHumidity(mainObject.getDouble("humidity"));
            String des = weatherArray.getString("description");
            des = (des != "") ? des.substring(0,1).toUpperCase() + des.substring(1) : "";
            currentWeather.setDescription(des);
            currentWeather.setWeatherType(weatherArray.getInt("id"));

            int timezone = resObject.getInt("timezone");
            Date currentDate = new Date();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
            dateFormatter.setTimeZone(
                    TimeZone.getTimeZone((timezone / 3600) > 0 ? String.format("GMT+%d", timezone/3600) : String.format("GMT%d", timezone / 3600))
            );
            currentWeather.setDateTime(dateFormatter.format(currentDate));
            int currentHour = Integer.parseInt(dateFormatter.format(currentDate).substring(0,2));
            currentWeather.setIconId(getIconId(currentWeather.getWeatherType(), currentHour));
            if (currentHour > 6 && currentHour < 18) {
                currentWeather.setBackgroundId(R.drawable.day_background);
            }
            else {
                currentWeather.setBackgroundId(R.drawable.night_background);
            }
            return currentWeather;
        }

        else {
            return null;
        }
    }

    private ArrayList<HourlyWeather> processHourlyData(String weatherData) throws JSONException {
        ArrayList<HourlyWeather> hourlyWeathers = new ArrayList<>();
        JSONObject resObj = new JSONObject(weatherData);
        if (!resObj.has("cod")){
            JSONArray hourlyArr = resObj.getJSONArray("hourly");
            Calendar calendar = Calendar.getInstance();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            for (int i = 1; i <= 24; i++){
                JSONObject mainObject = hourlyArr.getJSONObject(i);
                JSONObject weatherArray = mainObject.getJSONArray("weather").getJSONObject(0);

                HourlyWeather hourlyWeather = new HourlyWeather();

                hourlyWeather.setTemperature((int)(mainObject.getDouble("temp")));
                hourlyWeather.setWeatherType(weatherArray.getInt("id"));

                int timezone = resObj.getInt("timezone_offset");
                Date currentDate = new Date(mainObject.getLong("dt") * 1000);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
                dateFormatter.setTimeZone(
                        TimeZone.getTimeZone((timezone / 3600) > 0 ? String.format("GMT+%d", timezone/3600) : String.format("GMT%d", timezone / 3600))
                );
                hourlyWeather.setDateTime(dateFormatter.format(currentDate));
                int forecastHour = (currentHour + i) % 24;
                hourlyWeather.setIconId(getIconId(hourlyWeather.getWeatherType(), forecastHour));
                hourlyWeathers.add(hourlyWeather);
            }
        }
        return hourlyWeathers;
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
    void updateCurrentView(){
        txtCity.setText(currentWeather.getCityName());
        txtDescription.setText(currentWeather.getDescription());
        txtTemperature.setText(currentWeather.getTemperature() + "\u2103");
        //txtTimeUpdated.setText("Last updated at " + currentWeather.getDateTime());
        txtTimeUpdated.setText(String.format(getResources().getString(R.string.last_update_time), currentWeather.getDateTime()));
        //txtFeelsLike.setText("Feels like " + currentWeather.getTempFeel() + "\u2103");
        txtFeelsLike.setText(String.format(getResources().getString(R.string.feel_like_temp), currentWeather.getTempFeel()));
        imgWeather.setImageDrawable(ContextCompat.getDrawable(this, this.currentWeather.getIconId()));
        layout.setBackground(ContextCompat.getDrawable(this, this.currentWeather.getBackgroundId()));
    }
    void updateHourlyView(){
        hourlyAdapter.setHourlyWeatherList(hourlyWeathers);
        hourlyAdapter.notifyDataSetChanged();
    }
    void alertRequestError() {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog("Fail to retrieve data!");
        customAlertDialog.show(getSupportFragmentManager(), "Error");
    }

    public void onClickRefresh(View view) {
        Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
        getCurrentForecast(latitude, longitude, apiKey);
        getHourlyForecast(latitude, longitude, apiKey);
    }
}