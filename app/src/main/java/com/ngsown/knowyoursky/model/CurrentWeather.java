package com.ngsown.knowyoursky.model;

public class CurrentWeather {
    private int temperature;
    private int tempFeel;
    private double humidity;
    private String cityName;
    private String description;
    private int weatherType;
    private int iconId;
    private int backgroundId;
    private String dateTime;

    public CurrentWeather() {
        this.temperature = 0;
        this.tempFeel = 0;
        this.humidity = 0;
        this.cityName = "";
        this.description = "";
        this.weatherType = 0;
        this.backgroundId = -1;
        this.iconId = -1;
        this.dateTime = "";
    }
    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getTempFeel() {
        return tempFeel;
    }

    public void setTempFeel(int tempFeel) {
        this.tempFeel = tempFeel;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(int weatherType) {
        this.weatherType = weatherType;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(int backgroundId) {
        this.backgroundId = backgroundId;
    }
}
