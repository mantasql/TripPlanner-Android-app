package com.example.tripplanner.models;

public class Weather {
    private String city;
    private String weatherIcon;
    private String temperature;
    private String humidity;
    private String wind;

    public Weather(String city, String weatherIcon, String temperature, String humidity, String wind) {
        this.city = city;
        this.weatherIcon = weatherIcon;
        this.temperature = temperature;
        this.humidity = humidity;
        this.wind = wind;
        //this.weatherDescription = weatherDescription;
    }

    public Weather() {
    }

    public String getCity() {
        return city;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getWind() {
        return wind;
    }
}
