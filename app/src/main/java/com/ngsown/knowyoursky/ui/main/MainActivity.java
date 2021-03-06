package com.ngsown.knowyoursky.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.ngsown.knowyoursky.R;
import com.ngsown.knowyoursky.adapters.HourlyAdapter;
import com.ngsown.knowyoursky.domain.forecast.CurrentForecast;
import com.ngsown.knowyoursky.domain.forecast.HourlyForecast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    @BindView(R.id.txtCity) TextView txtCity;
    @BindView(R.id.txtTemperature) TextView txtTemperature;
    @BindView(R.id.txtDescription) TextView txtDescription;
    @BindView(R.id.txtTimeUpdated) TextView txtTimeUpdated;
    @BindView(R.id.txtFeelsLike) TextView txtFeelsLike;
    @BindView(R.id.txtNoInternet) TextView txtNoInternet;
    @BindView(R.id.txtNoLocationService) TextView txtNoLocationService;
    @BindView(R.id.imgWeather) ImageView imgWeather;
    @BindView(R.id.imgInternet) ImageView imgInternet;
    @BindView(R.id.imgLocation) ImageView imgLocation;
    @BindView(R.id.layoutMain) ConstraintLayout layout;
    @BindView(R.id.listHourly) RecyclerView hourlyRecyclerView;

    @Inject
    MainContract.Presenter presenter;

    private HourlyAdapter hourlyAdapter;
    private ActivityComponent activityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
        presenter.reloadForecast();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == UserLocationManager.CODE_LOCATION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
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
    public void showNoLocationPermissionDialog() {
        CustomAlertDialog locationError = new CustomAlertDialog("Location permission denied!");
        locationError.show(getSupportFragmentManager(), "Error");
    }


    @Override
    public void hideNoInternetIcon() {
        imgInternet.setColorFilter(Color.WHITE);
    }

    @Override
    public void showNoInternetIcon() {
        imgInternet.clearColorFilter();
    }

    @Override
    public void showLocationOnIcon() {
        imgLocation.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_location_on_24));
    }

    @Override
    public void showLocationOffIcon() {
        imgLocation.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_location_off_24));
    }

    @Override
    public void showLoadingLocationToast() {
        runOnUiThread(() -> Toast.makeText(MainActivity.this,"Detecting your location....", Toast.LENGTH_LONG).show());
    }

    @Override
    public void hideNoLocationServiceText() {
        txtNoLocationService.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showNoLocationServiceText() {
        runOnUiThread(() -> txtNoLocationService.setVisibility(View.VISIBLE));

    }

    @Override
    public void hideNoInternetText() {
        txtNoLocationService.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showNoInternetText() {
        txtNoInternet.setVisibility(View.VISIBLE);
    }

}