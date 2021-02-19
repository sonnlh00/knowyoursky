package com.ngsown.knowyoursky.ui.main;

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

import com.ngsown.knowyoursky.domain.GetWeatherForecastImpl;
import com.ngsown.knowyoursky.ui.custom.CustomAlertDialog;
import com.ngsown.knowyoursky.utils.NetworkChecking;
import com.ngsown.knowyoursky.R;
import com.ngsown.knowyoursky.adapters.HourlyAdapter;
import com.ngsown.knowyoursky.domain.forecast.CurrentForecast;
import com.ngsown.knowyoursky.domain.forecast.HourlyForecast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    NetworkChecking networkChecking;
    Observer isNetworkAvailable; // Observer for network changes
    TextView txtCity, txtTemperature, txtDescription, txtTimeUpdated, txtFeelsLike;
    ImageView imgWeather;
    ConstraintLayout layout;

    MainContract.Presenter presenter;

    private RecyclerView hourlyRecyclerView;
    private HourlyAdapter hourlyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        binding = DataBindingUtil.setContentView(MainActivity.this,
//                R.layout.activity_main);

        setPresenter(new MainPresenter(this, new GetWeatherForecastImpl()));

        txtCity = findViewById(R.id.txtCity);
        txtTemperature = findViewById(R.id.txtTemperature);
        txtDescription = findViewById(R.id.txtDescription);
        txtTimeUpdated = findViewById(R.id.txtTimeUpdated);
        txtFeelsLike = findViewById(R.id.txtFeelsLike);
        imgWeather = findViewById(R.id.imgWeather);
        layout = findViewById(R.id.layoutMain);

        hourlyRecyclerView = findViewById(R.id.listHourly);
        hourlyAdapter = new HourlyAdapter(new ArrayList<HourlyForecast>(), this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        hourlyRecyclerView.setLayoutManager(layoutManager);
        hourlyRecyclerView.setAdapter(hourlyAdapter);

        isNetworkAvailable = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (networkChecking != null && networkChecking.isNetworkAvailable()){
                    presenter.loadCurrentForecast();
                    presenter.loadHourlyForecast();
                    Toast.makeText(MainActivity.this, "Fetching data...", Toast.LENGTH_SHORT).show();
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

    void alertRequestError() {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog("Fail to retrieve data!");
        customAlertDialog.show(getSupportFragmentManager(), "Error");
    }

    public void onClickRefresh(View view) {
        Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                presenter.reloadForecast();
            }
        }).start();
    }

    @Override
    public void showCurrentForecast(CurrentForecast currentForecast) {
        txtCity.setText(currentForecast.getCityName());
        txtDescription.setText(currentForecast.getDescription());
        txtTemperature.setText(currentForecast.getTemperature() + "\u2103");
        //txtTimeUpdated.setText("Last updated at " + currentWeather.getDateTime());
        txtTimeUpdated.setText(String.format(getResources().getString(R.string.last_update_time), currentForecast.getDateTime()));
        //txtFeelsLike.setText("Feels like " + currentWeather.getTempFeel() + "\u2103");
        txtFeelsLike.setText(String.format(getResources().getString(R.string.feel_like_temp), currentForecast.getTempFeel()));
        imgWeather.setImageDrawable(ContextCompat.getDrawable(this, currentForecast.getIconId()));
        layout.setBackground(ContextCompat.getDrawable(this, currentForecast.getBackgroundId()));
    }

    @Override
    public void showHourlyForecast(List<HourlyForecast> hourlyForecasts) {
        hourlyAdapter.setHourlyForecastList(hourlyForecasts);
        hourlyAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.presenter = presenter;
    }
}