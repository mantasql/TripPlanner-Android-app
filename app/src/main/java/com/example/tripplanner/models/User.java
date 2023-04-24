package com.example.tripplanner.models;

import android.os.Build;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class User {
    private String id;
    private String email;
    private String displayName;
    private HashMap<String, TripPlan> plans;

    public User(String id, String email, String displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            plans.add(new TripPlan("very nice trip", new Date() , new Date(), "my trip", null));
        }*/
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ArrayList<TripPlan> getPlans() {
        if (plans == null)
        {
            return new ArrayList<TripPlan>();
        }

        return new ArrayList<TripPlan>(plans.values());
    }
}
