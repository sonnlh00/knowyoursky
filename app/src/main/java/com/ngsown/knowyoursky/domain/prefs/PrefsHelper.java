package com.ngsown.knowyoursky.domain.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ngsown.knowyoursky.di.ActivityContext;
import com.ngsown.knowyoursky.di.PrefsFile;
import com.ngsown.knowyoursky.domain.forecast.CurrentForecast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import javax.inject.Inject;
import javax.inject.Singleton;

public class PrefsHelper {
    public static final String PREF_KEY_LATITUDE = "PREF_KEY_LATITUDE";
    public static final String PREF_KEY_LONGITUDE = "PREF_KEY_LONGITUDE";
    public static final String PREF_KEY_CURRENT_WEATHER = "PREF_KEY_CURRENT_WEATHER";
    public static final String PREF_KEY_HOURLY_WEATHER = "PREF_KEY_HOURLY_WEATHER";
    public static int numOfInstances = 0;
    private final SharedPreferences sharedPreferences;

    public PrefsHelper(@ActivityContext Context context,@PrefsFile String prefsFile){
        sharedPreferences = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        numOfInstances++;
        Log.d(this.getClass().getSimpleName(), "PrefsHelper: Number of instances is " + numOfInstances);
    }
    public double getLatitude(){
        return Double.longBitsToDouble(sharedPreferences.getLong(PREF_KEY_LATITUDE, 1000));
    }
    public void setLatitude(double latitude){
        sharedPreferences.edit().putLong(PREF_KEY_LATITUDE, Double.doubleToRawLongBits(latitude)).apply();
    }
    public double getLongitude(){
        return Double.longBitsToDouble(sharedPreferences.getLong(PREF_KEY_LONGITUDE, 1000));
    }
    public void setLongitude(double longitude){
        sharedPreferences.edit().putLong(PREF_KEY_LONGITUDE, Double.doubleToRawLongBits(longitude)).apply();
    }
    public boolean hasSavedLocation(){
        return sharedPreferences.contains(PREF_KEY_LATITUDE) && sharedPreferences.contains(PREF_KEY_LONGITUDE);
    }
    public void putCurrentForecast(String currentForecast){
        sharedPreferences.edit().putString(PREF_KEY_CURRENT_WEATHER, currentForecast).apply();
    }
    public String getCurrentForecast(){
        return sharedPreferences.getString(PREF_KEY_CURRENT_WEATHER, "");
    }
    public void putHourlyForecast(String hourly){
        sharedPreferences.edit().putString(PREF_KEY_HOURLY_WEATHER, hourly).apply();
    }
    public String getHourlyForecast(){
        return sharedPreferences.getString(PREF_KEY_HOURLY_WEATHER, "");
    }
    public boolean hasCachedData(){
        return !getCurrentForecast().equals("") && !getHourlyForecast().equals("");
    }

}
