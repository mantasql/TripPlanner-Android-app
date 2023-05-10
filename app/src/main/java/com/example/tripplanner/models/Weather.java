package com.example.tripplanner.models;

public class Weather {
    private String city;
    private String weatherIcon;
    private String temperature;
    private String humidity;
    private String wind;
    private String description;
    private String region;
    private String country;

    public Weather(String city, String weatherIcon, String temperature, String humidity, String wind, String description, String region, String country) {
        this.city = city;
        this.weatherIcon = weatherIcon;
        this.temperature = temperature;
        this.humidity = humidity;
        this.wind = wind;
        this.description = description;
        this.region = region;
        this.country = country;
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

    public String getDescription() {
        return description;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }
}
