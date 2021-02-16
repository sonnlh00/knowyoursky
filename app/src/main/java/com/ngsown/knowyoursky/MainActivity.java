package com.ngsown.knowyoursky;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ngsown.knowyoursky.databinding.ActivityMainBinding;
import com.ngsown.knowyoursky.model.WeatherInfo;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    TextView txtCity, txtTemperature, txtDescription, txtTimeUpdated;
    ImageView imgWeather;
    ConstraintLayout layout;
    WeatherInfo weatherInfo;
    ActivityMainBinding binding;

    String apiKey = "fe2cae6dc99f16488b3bf799d3b6330c";
    double longitude = 106.147942;
    double latitude = 11.518422;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MainActivity.this,
                R.layout.activity_main);
//        txtCity = findViewById(R.id.txtCity);
//        txtTemperature = findViewById(R.id.txtTemperature);
//        txtDescription = findViewById(R.id.txtDescription);
//        txtTimeUpdated = findViewById(R.id.txtTimeUpdated);
        imgWeather = findViewById(R.id.imgWeather);
        layout = findViewById(R.id.layoutMain);
        isNetworkAvailable = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (networkChecking != null && networkChecking.isNetworkAvailable()){
                    Toast.makeText(MainActivity.this, "Fetching data...", Toast.LENGTH_SHORT).show();
                    getForecast(latitude, longitude, apiKey);
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

    public void getHourlyForecast(double latitude, double longitude, String apiKey, int numHours){

    }

    public void getForecast(double latitude, double longitude, String apiKey) {
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
                        weatherInfo = processCurrentData(res);
                        if (weatherInfo != null) {
                            binding.setWeather(weatherInfo);
                            runOnUiThread(() -> updateView());
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

    WeatherInfo processCurrentData(String weatherData) throws JSONException{
        JSONObject resObject = new JSONObject(weatherData);
        if (resObject.getInt("cod") == 200) {
            JSONObject mainObject = resObject.getJSONObject("main");
            JSONObject weatherArray = resObject.getJSONArray("weather").getJSONObject(0);
            WeatherInfo weatherInfo = new WeatherInfo();

            weatherInfo.setCityName(resObject.getString("name"));
            weatherInfo.setTemperature((int)(mainObject.getDouble("temp")));
            weatherInfo.setTempFeel((int)(mainObject.getDouble("feels_like")));
            weatherInfo.setHumidity(mainObject.getDouble("humidity"));
            String des = weatherArray.getString("description");
            des = (des != "") ? des.substring(0,1).toUpperCase() + des.substring(1) : "";
            weatherInfo.setDescription(des);
            weatherInfo.setWeatherType(weatherArray.getString("main"));

            int timezone = resObject.getInt("timezone");
            Date currentDate = new Date();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
            dateFormatter.setTimeZone(
                    TimeZone.getTimeZone((timezone / 3600) > 0 ? String.format("GMT+%d", timezone/3600) : String.format("GMT%d", timezone / 3600))
            );
            weatherInfo.setDateTime(dateFormatter.format(currentDate));
            int currentHour = Integer.parseInt(dateFormatter.format(currentDate).substring(0,2));
            if (currentHour > 6 && currentHour < 18) {
                weatherInfo.setBackgroundId(R.drawable.day_background);
                switch (weatherInfo.getWeatherType()) {
                    case "Clear":
                        weatherInfo.setIconId(R.drawable.clearsky_day);
                        break;
                    case "Clouds":
                        weatherInfo.setIconId(R.drawable.cloudy_day);
                        break;
                    case "Rain":
                        weatherInfo.setIconId(R.drawable.rainy_day);
                        break;
                    case "Snow":
                        weatherInfo.setIconId(R.drawable.snowy_day);
                        break;
                    default:
                        weatherInfo.setIconId(R.drawable.ic_baseline_refresh_24);
                        break;
                }
            }
            else {
                weatherInfo.setBackgroundId(R.drawable.night_background);
                switch (weatherInfo.getWeatherType()) {
                    case "Clear":
                        weatherInfo.setIconId(R.drawable.clearsky_night);
                        break;
                    case "Clouds":
                        weatherInfo.setIconId(R.drawable.cloudy_night);
                        break;
                    case "Rain":
                        weatherInfo.setIconId(R.drawable.rainy_night);
                        break;
                    case "Snow":
                        weatherInfo.setIconId(R.drawable.snowy_night);
                        break;
                    default:
                        weatherInfo.setIconId(R.drawable.ic_baseline_refresh_24);
                        break;
                }
            }
            return weatherInfo;
        }

        else {
            return null;
        }
    }
    ArrayList<WeatherInfo> processHourlyData(String weatherData) throws JSONException {
        ArrayList<WeatherInfo> hourlyWeather = new ArrayList<>();

        return hourlyWeather;
    }
    void updateView(){
//        Log.d("DEBUG", (weatherForecast.getWeatherInfo() != null) ? "Not null" : "Null");
//        txtCity.setText(weatherForecast.getWeatherInfo().getCityName());
//        txtDescription.setText(weatherForecast.getWeatherInfo().getDescription());
//        txtTemperature.setText(Double.toString(weatherForecast.getWeatherInfo().getTemperature()) + "\u2103");
//        txtTimeUpdated.setText(weatherForecast.getWeatherInfo().getDateTime());
//        Log.d("ICON ID", Integer.toString(weatherForecast.getWeatherInfo().getIconId()));

        imgWeather.setImageDrawable(ContextCompat.getDrawable(this, weatherInfo.getIconId()));
        layout.setBackground(ContextCompat.getDrawable(this, weatherInfo.getBackgroundId()));

//        for (int i = 0; i < layout.getChildCount(); i++){
//            View view = layout.getChildAt(i);
//            if (view instanceof TextView){
//                TextView tmp = (TextView)(view);
//                tmp.setTextColor(Color.WHITE);
//            }
//        }
    }

    void alertRequestError() {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog("Fail to retrieve data!");
        customAlertDialog.show(getSupportFragmentManager(), "Error");
    }

    public void onClickRefresh(View view) {
        Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
        getForecast(latitude, longitude, apiKey);
    }
}