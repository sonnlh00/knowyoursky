package com.ngsown.knowyoursky.ui.main;

import com.ngsown.knowyoursky.domain.UserLocationManager;
import com.ngsown.knowyoursky.domain.forecast.CurrentForecast;
import com.ngsown.knowyoursky.domain.forecast.HourlyForecast;
import com.ngsown.knowyoursky.ui.base.BasePresenter;
import com.ngsown.knowyoursky.ui.base.BaseView;

import java.util.List;

public interface MainContract {
    interface Presenter extends BasePresenter<View> {
        void loadCurrentForecast();
        void loadHourlyForecast();
        void loadForecast();
        void reloadForecast();
        void setLocationManager(UserLocationManager userLocationManager);
        void onLocationPermissionGranted();
        void onLocationPermissionDenied();
    }
    interface View extends BaseView {
        void showCurrentForecast(CurrentForecast currentForecast);
        void showHourlyForecast(List<HourlyForecast> hourlyForecasts);
        void showNoLocationPermissionError();
        void showNoInternetError();
        void showLoadingLocationToast();
        void showLocationDetectedAlert();
    }
}
