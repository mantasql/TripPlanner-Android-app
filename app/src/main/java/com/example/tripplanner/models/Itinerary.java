package com.example.tripplanner.models;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class Itinerary {
    private Integer position;
    private PlaceInfo place;
    private String duration;
    private Integer durationValue;
    private Integer extraDurationValue;

    public Itinerary(Integer position, PlaceInfo place) {
        this.position = position;
        this.place = place;
    }

    public Itinerary() {
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Itinerary itinerary = (Itinerary) obj;
        return position.equals(itinerary.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setPlace(PlaceInfo place) {
        this.place = place;
    }

    public PlaceInfo getPlace() {
        return place;
    }

    public String getDuration() {
        return duration;
    }

    public Integer getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(Integer durationValue) {
        this.durationValue = durationValue;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getExtraDurationValue() {
        if (extraDurationValue == null)
        {
            return 0;
        }

        return extraDurationValue;
    }

    public void setExtraDurationValue(Integer extraDuration) {
        this.extraDurationValue = extraDuration;
    }

    @Exclude
    public String getExtraDuration()
    {
        int hours = getExtraDurationValue() / 3600;
        int minutes = (getExtraDurationValue() % 3600) / 60;

        return String.format("%02d:%02d", hours, minutes);
    }

    @Exclude
    public String getOverallDuration()
    {

        int value = getDurationValue() + getExtraDurationValue();

        int hours = value / 3600;
        int minutes = (value % 3600) / 60;

        String dur = "X";

        if (hours < 1 && minutes < 2)
        {
            dur = minutes + " min";
        }
        else if (hours < 1)
        {
            dur = minutes + " mins";
        }
        else if (minutes < 2)
        {
            dur = hours + " h " + minutes + " min";
        }
        else
        {
            dur = hours + " h " + minutes + " mins";
        }

        return dur;
    }
}
