package com.ngsown.knowyoursky.ui.main;

import com.ngsown.knowyoursky.domain.forecast.CurrentForecast;
import com.ngsown.knowyoursky.domain.forecast.HourlyForecast;
import com.ngsown.knowyoursky.ui.base.BasePresenter;
import com.ngsown.knowyoursky.ui.base.BaseView;

import java.util.List;

public interface MainContract {
    interface Presenter extends BasePresenter {
        void loadCurrentForecast();
        void loadHourlyForecast();
        void reloadForecast();
    }
    interface View extends BaseView<Presenter> {
        void showCurrentForecast(CurrentForecast currentForecast);
        void showHourlyForecast(List<HourlyForecast> hourlyForecasts);
    }
}
