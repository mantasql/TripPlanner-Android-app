package com.example.tripplanner;

public enum TravelMode {
    DRIVING("driving"),
    WALKING("walking");

    public final String mode;

    TravelMode(String mode)
    {
        this.mode = mode;
    }
}
