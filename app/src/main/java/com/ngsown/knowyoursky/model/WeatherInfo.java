package com.ngsown.knowyoursky.model;

public class WeatherInfo {
    private double temperature;
    private double tempFeel;
    private double humidity;
    private String cityName;
    private String description;
    private String weatherType;
    private int iconId;
    private String dateTime;

    public WeatherInfo(double temperature, double tempFeel, double humidity, String cityName, String description) {
        this.temperature = temperature;
        this.tempFeel = tempFeel;
        this.humidity = humidity;
        this.cityName = cityName;
        this.description = description;
    }
    public WeatherInfo() {
        this.temperature = 0.0;
        this.tempFeel = tempFeel;
        this.humidity = 0.0;
        this.cityName = "";
        this.description = "";
        this.weatherType = "";
        this.iconId = -1;
    }
    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTempFeel() {
        return tempFeel;
    }

    public void setTempFeel(double tempFeel) {
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

    public String getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(String weatherType) {
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
}
