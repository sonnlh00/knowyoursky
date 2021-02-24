package com.ngsown.knowyoursky.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ngsown.knowyoursky.domain.GetWeatherForecastImpl;
import com.ngsown.knowyoursky.domain.prefs.PrefsHelper;
import com.ngsown.knowyoursky.ui.custom.CustomAlertDialog;
import com.ngsown.knowyoursky.domain.UserLocationManager;
import com.ngsown.knowyoursky.utils.NetworkChecking;
import com.ngsown.knowyoursky.R;
import com.ngsown.knowyoursky.adapters.HourlyAdapter;
import com.ngsown.knowyoursky.domain.forecast.CurrentForecast;
import com.ngsown.knowyoursky.domain.forecast.HourlyForecast;
import com.ngsown.knowyoursky.utils.SchedulerProvider;
import com.ngsown.knowyoursky.utils.SchedulerProviderImpl;
import com.ngsown.knowyoursky.utils.ThreadExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    NetworkChecking networkChecking;
    Observer isNetworkAvailable; // Observer for network changes
    TextView txtCity, txtTemperature, txtDescription, txtTimeUpdated, txtFeelsLike;
    ImageView imgWeather;
    ConstraintLayout layout;
    ThreadExecutor threadExecutor = new ThreadExecutor();
    MainContract.Presenter presenter;

    private RecyclerView hourlyRecyclerView;
    private HourlyAdapter hourlyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region Initialize view
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
        //endregion
        GetWeatherForecastImpl getWeatherForecast = new GetWeatherForecastImpl();
        NetworkChecking networkChecking = new NetworkChecking(this);
        //networkChecking.registerNetworkCallback();
        UserLocationManager userLocationManager = new UserLocationManager(this);
        SchedulerProvider schedulerProvider = new SchedulerProviderImpl();
        MainPresenter mPresenter = new MainPresenter(this, getWeatherForecast, networkChecking, userLocationManager, new PrefsHelper(this, "location"), schedulerProvider);
        setPresenter(mPresenter);
        presenter.initialize();
//        threadExecutor.run(new Interactor() {
//            @Override
//            public void run() {
//
//            }
//        });
    }


    void alertInternetError() {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog("No internet connection!");
        customAlertDialog.show(getSupportFragmentManager(), "Error");
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.pause();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == UserLocationManager.CODE_LOCATION){
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                presenter.onLocationPermissionGranted();
            }
            else
                presenter.onLocationPermissionDenied();
        }
        else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void showCurrentForecast(CurrentForecast currentForecast) {
        txtCity.setText(currentForecast.getCityName());
        txtDescription.setText(currentForecast.getDescription());
        txtTemperature.setText(String.format(getResources().getString(R.string.temperature), currentForecast.getTemperature()));
        txtTimeUpdated.setText(String.format(getResources().getString(R.string.last_update_time), currentForecast.getDateTime()));
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

    @Override
    public void showNoLocationPermissionError() {
        CustomAlertDialog locationError = new CustomAlertDialog("Location permission denied!");
        locationError.show(getSupportFragmentManager(), "Error");
    }

    @Override
    public void showNoInternetError() {
        CustomAlertDialog locationError = new CustomAlertDialog("No internet connection!");
        locationError.show(getSupportFragmentManager(), "Error");
    }

    @Override
    public void showLoadingLocationToast() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,"Detecting your location....", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void showLocationDetectedAlert() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomAlertDialog locationError = new CustomAlertDialog("Location detected, please refresh");
                locationError.show(getSupportFragmentManager(), "Success");
            }
        });
    }
}