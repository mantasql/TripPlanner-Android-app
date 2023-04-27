package com.example.tripplanner.utils;

import com.example.tripplanner.models.Itinerary;

import java.util.Comparator;

public class ItineraryComparator implements Comparator<Itinerary> {
    @Override
    public int compare(Itinerary itinerary, Itinerary t1) {
        return itinerary.getPosition() - t1.getPosition();
    }
}
