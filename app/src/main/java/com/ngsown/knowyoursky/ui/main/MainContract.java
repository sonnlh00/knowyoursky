package com.ngsown.knowyoursky.ui.main;

import com.ngsown.knowyoursky.domain.UserLocationManager;
import com.ngsown.knowyoursky.domain.forecast.CurrentForecast;
import com.ngsown.knowyoursky.domain.forecast.HourlyForecast;
import com.ngsown.knowyoursky.ui.base.BasePresenter;
import com.ngsown.knowyoursky.ui.base.BaseView;

import java.util.List;

public interface MainContract {
    interface Presenter extends BasePresenter<View> {
        void loadForecast();
        void reloadForecast();
        void onLocationPermissionGranted();
        void onLocationPermissionDenied();
    }
    interface View extends BaseView {
        void showCurrentForecast(CurrentForecast currentForecast);
        void showHourlyForecast(List<HourlyForecast> hourlyForecasts);
        void showNoLocationPermissionDialog();

        void hideNoInternetText();
        void showNoInternetText();
        void showLoadingLocationToast();
        void hideNoLocationServiceText();
        void showNoLocationServiceText();

        void showNoInternetIcon();
        void hideNoInternetIcon();
        void showLocationOffIcon();
        void showLocationOnIcon();

        void showShortLoadingAnimation();
        void showLoadingAnimation();
        void stopLoadingAnimation();
    }
}
