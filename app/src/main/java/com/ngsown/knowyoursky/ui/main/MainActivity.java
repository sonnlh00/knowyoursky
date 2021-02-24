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

import com.ngsown.knowyoursky.di.component.ActivityComponent;
import com.ngsown.knowyoursky.di.component.DaggerActivityComponent;
import com.ngsown.knowyoursky.di.module.ActivityModule;
import com.ngsown.knowyoursky.ui.custom.CustomAlertDialog;
import com.ngsown.knowyoursky.domain.UserLocationManager;
import com.ngsown.knowyoursky.utils.NetworkChecking;
import com.ngsown.knowyoursky.R;
import com.ngsown.knowyoursky.adapters.HourlyAdapter;
import com.ngsown.knowyoursky.domain.forecast.CurrentForecast;
import com.ngsown.knowyoursky.domain.forecast.HourlyForecast;
import com.ngsown.knowyoursky.utils.executor.ThreadExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    TextView txtCity, txtTemperature, txtDescription, txtTimeUpdated, txtFeelsLike;
    ImageView imgWeather;
    ConstraintLayout layout;

    @Inject
    MainContract.Presenter presenter;

    private RecyclerView hourlyRecyclerView;
    private HourlyAdapter hourlyAdapter;

    private ActivityComponent activityComponent;

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

        activityComponent = DaggerActivityComponent.builder().activityModule(new ActivityModule(this)).build();
        activityComponent.inject(this);
        presenter.setView(this);
        presenter.initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.pause();
    }

    public void onClickRefresh(View view) {
        Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
        new Thread(() -> presenter.reloadForecast()).start();
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
        runOnUiThread(() -> Toast.makeText(MainActivity.this,"Detecting your location....", Toast.LENGTH_LONG).show());
    }

    @Override
    public void showLocationDetectedAlert() {
        runOnUiThread(() -> {
            CustomAlertDialog locationError = new CustomAlertDialog("Location detected, please refresh");
            locationError.show(getSupportFragmentManager(), "Success");
        });
    }
}