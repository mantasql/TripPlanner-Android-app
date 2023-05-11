package com.example.tripplanner.models;

import android.util.Log;

import com.example.tripplanner.utils.DateFormat;
import com.google.firebase.database.Exclude;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TripPlan {

    private static final String TAG = "TripPlan";
    private String id;
    private String title;
    private Date startDate = new Date();
    private Date endDate = new Date();
    private String description;
    private ArrayList<Itinerary> itinerary = new ArrayList<>();
    private ArrayList<String> tripFriends = new ArrayList<>();

    public TripPlan(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public TripPlan() {
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Exclude
    public String getStartOnlyDate() {
        return new DateFormat().getDateOnly(startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Exclude
    public String getEndOnlyDate() {
        return new DateFormat().getDateOnly(endDate);
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Itinerary> getItinerary() {
        return itinerary;
    }

    public void setItinerary(ArrayList<Itinerary> itinerary) {
        this.itinerary = itinerary;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getTripFriends() {
        return tripFriends;
    }

    public void setTripFriends(ArrayList<String> tripFriends) {
        this.tripFriends = tripFriends;
    }

    public void removeAndRearrange(int position)
    {
        this.itinerary.remove(position);

        for (int i = 0; i < itinerary.size(); i++)
        {
            itinerary.get(i).setPosition(i);
        }
    }

    @Exclude
    public String getTripDuration()
    {
        int hours;
        int minutes;
        Integer v;
        int value = 0;

        for(int i = 0; i < itinerary.size(); i++)
        {
            v = itinerary.get(i).getDurationValue();
            if (v == null)
            {
                return "X";
            }

            value += v + itinerary.get(i).getExtraDurationValue();
        }

        hours = value / 3600;
        minutes = (value % 3600) / 60;

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

/*    public HashMap<String, Itinerary> getItineraryHash() {
        return itineraryHash;
    }

    public void setItineraryHash(HashMap<String, Itinerary> itineraryHash) {
        this.itineraryHash = itineraryHash;
    }

    @Exclude
    public ArrayList<Itinerary> getItinerary() {
        return itinerary;
    }

    public void setItinerary(ArrayList<Itinerary> itinerary) {
        this.itinerary = itinerary;
    }

    private ArrayList<Itinerary> getItinerariesFromHash() {
        if (itinerary == null) {
            return new ArrayList<Itinerary>();
        }

        List<Map.Entry<String, Itinerary>> sortedItinerary =
                new ArrayList<>(itineraryHash.entrySet());
        sortedItinerary.sort(Map.Entry.comparingByValue(Comparator.comparingInt(Itinerary::getPosition)));

        itinerary = new ArrayList<>();
        for (Map.Entry<String, Itinerary> entry : sortedItinerary) {
            itinerary.add(entry.getValue());
        }

        return itinerary;
    }

    public ArrayList<Itinerary> getItinerariesUnsorted() {
        if (itineraryHash == null) {
            return new ArrayList<Itinerary>();
        }

        ArrayList<Itinerary> places = new ArrayList<Itinerary>(itineraryHash.values());

        return places;
    }

    public void removeItinerary(int position)
    {
        for (Iterator<Map.Entry<String, Itinerary>> it = itineraryHash.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry<String, Itinerary> entry = it.next();
            if (entry.getValue().getPosition() == position)
            {
                it.remove();
            }
        }
        rearrangeItineraryPositions();
    }

    public void rearrangeItineraryPositions()
    {
        ArrayList<Itinerary> itineraries = new ArrayList<>(itineraryHash.values());
        itineraries.sort(Comparator.comparing(Itinerary::getPosition));

        for (int i = 0; i < itineraries.size(); i++) {
            Itinerary it = itineraries.get(i);
            int newPosition = i;

            if (newPosition > 0 && it.getPosition() <= itineraries.get(newPosition - 1).getPosition()) {
                newPosition = itineraries.get(newPosition - 1).getPosition() + 1;
            }

            it.setPosition(newPosition);
        }
    }*/
}
