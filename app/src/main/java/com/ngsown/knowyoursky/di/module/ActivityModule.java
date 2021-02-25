package com.ngsown.knowyoursky.di.module;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.ngsown.knowyoursky.di.CompatActivity;
import com.ngsown.knowyoursky.di.ActivityContext;
import com.ngsown.knowyoursky.di.ApiKey;
import com.ngsown.knowyoursky.di.PrefsFile;
import com.ngsown.knowyoursky.domain.GetWeatherForecast;
import com.ngsown.knowyoursky.domain.GetWeatherForecastImpl;
import com.ngsown.knowyoursky.ui.main.MainContract;
import com.ngsown.knowyoursky.ui.main.MainPresenter;
import com.ngsown.knowyoursky.utils.NetworkChecking;
import com.ngsown.knowyoursky.utils.rxjava.SchedulerProvider;
import com.ngsown.knowyoursky.utils.rxjava.SchedulerProviderImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private final AppCompatActivity activity;

    public ActivityModule(AppCompatActivity activity) { this.activity = activity; }

    @Provides
    public MainContract.Presenter provideMainPresenter(MainPresenter presenter){
        return presenter;
    }

    @Provides
    public SchedulerProvider provideSchedulerProvider(){
        return new SchedulerProviderImpl();
    }

    @Provides
    public GetWeatherForecast provideGetWeatherForecast(SchedulerProvider scheduler){
        return new GetWeatherForecastImpl(scheduler);
    }

    @Provides
    public NetworkChecking provideNetworkChecking(){
        return new NetworkChecking(activity);
    }

    @Provides
    @ActivityContext
    public Context provideActivityContext(){
        return activity;
    }
    @Provides
    @CompatActivity
    public AppCompatActivity provideActivityCompat() {return activity;}

    @Provides
    @PrefsFile
    public String providePrefsFileName(){
        return "location";
    }

    @Provides
    @ApiKey
    public String provideApiKey(){
        return "fe2cae6dc99f16488b3bf799d3b6330c";
    }
}
