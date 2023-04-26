package com.example.tripplanner.models;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Itinerary {
    private Integer position;
    private PlaceInfo place;

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

    /*    public ArrayList<PlaceInfo> getPlaces() {
        if (places == null) {
            return new ArrayList<PlaceInfo>();
        }

        return new ArrayList<PlaceInfo>(places.values());
    }*/
}
