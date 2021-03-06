package com.ngsown.knowyoursky;

import android.Manifest;
import android.content.pm.PackageManager;

import com.ngsown.knowyoursky.domain.GetWeatherForecast;
import com.ngsown.knowyoursky.domain.UserLocationManager;
import com.ngsown.knowyoursky.domain.prefs.PrefsHelper;
import com.ngsown.knowyoursky.ui.main.MainActivity;
import com.ngsown.knowyoursky.ui.main.MainContract;
import com.ngsown.knowyoursky.ui.main.MainPresenter;
import com.ngsown.knowyoursky.utils.NetworkChecking;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {
    MainActivity view = mock(MainActivity.class);
    GetWeatherForecast getWeatherForecast = mock(GetWeatherForecast.class);
    NetworkChecking networkChecking = mock(NetworkChecking.class);
    UserLocationManager userLocationManager = mock(UserLocationManager.class);
    PrefsHelper prefsHelper = mock(PrefsHelper.class);
    String apiKey = "test";


    @Test
    public void testPermissionDenied_NoCachedData(){
        when(prefsHelper.hasCachedData()).thenReturn(false);
        MainPresenter presenter = new MainPresenter(getWeatherForecast, networkChecking, userLocationManager, prefsHelper, apiKey);
        presenter.setView(view);
        presenter.onLocationPermissionDenied();
        verify(view, times(1)).showNoLocationPermissionDialog();
    }
    @Test
    public void testPermissionDenied_HasCachedData() {
        when(prefsHelper.hasCachedData()).thenReturn(true);
        MainPresenter presenter = Mockito.spy(new MainPresenter(getWeatherForecast, networkChecking, userLocationManager, prefsHelper, apiKey));
        presenter.setView(view);
        presenter.onLocationPermissionDenied();
        verify(presenter, times(1)).loadForecast();
    }
}
