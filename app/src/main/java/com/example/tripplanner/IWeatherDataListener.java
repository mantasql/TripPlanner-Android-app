package com.example.tripplanner;

import com.example.tripplanner.models.Weather;

public interface IWeatherDataListener {
    void onDataReceived(Weather weather);
}
