package com.example.tripplanner.models;

import com.example.tripplanner.utils.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TripPlan {

    private String id;
    private String title;
    private Date startDate = new Date();
    private Date endDate = new Date();
    private String description;
    private ArrayList<PlaceInfo> places = new ArrayList<>();

    public TripPlan(String id, String title, Date startDate, Date endDate, String description, ArrayList<PlaceInfo> places) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.places = places;
    }

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

    public String getStartOnlyDate() {
        return new DateFormat().getDateOnly(startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

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

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<PlaceInfo> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<PlaceInfo> places) {
        this.places = places;
    }
}
