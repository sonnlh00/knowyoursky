package com.ngsown.knowyoursky.domain.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsHelper {
    public static final String PREF_KEY_LATITUDE = "PREF_KEY_LATITUDE";
    public static final String PREF_KEY_LONGITUDE = "PREF_KEY_LONGITUDE";
    public static final String PREF_KEY_CURRENT_WEATHER = "PREF_KEY_CURRENT_WEATHER";
    public static final String PREF_KEY_HOURLY_WEATHER = "PREF_KEY_HOURLY_WEATHER";

    private final SharedPreferences sharedPreferences;

    public PrefsHelper(Context context, String prefsFile){
        sharedPreferences = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
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
}
