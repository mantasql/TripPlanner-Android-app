package com.example.tripplanner.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;

public class PlaceInfo {
    private String id;
    private String name;
    private String address;
    private String phoneNumber;
    private String websiteUri;
    private com.example.tripplanner.models.LatLng latLng;
    private Double rating;
    private List<String> attributions;

    public PlaceInfo(String id, String name, String address, String phoneNumber, String websiteUri, com.example.tripplanner.models.LatLng latLng, Double rating, List<String> attributions) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.websiteUri = websiteUri;
        this.latLng = latLng;
        this.rating = rating;
        this.attributions = attributions;
    }

    public PlaceInfo(Place place)
    {
        this.name = place.getName();
        this.address = place.getAddress();
        this.phoneNumber = place.getPhoneNumber();
        if (place.getWebsiteUri() != null)
        {
            this.websiteUri = place.getWebsiteUri().toString();
        }
        this.latLng = new com.example.tripplanner.models.LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
        this.attributions = place.getAttributions();
    }

    public PlaceInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(String websiteUri) {
        this.websiteUri = websiteUri;
    }

    public com.example.tripplanner.models.LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(com.example.tripplanner.models.LatLng latLng) {
        this.latLng = latLng;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<String> getAttributions() {
        return attributions;
    }

    public void setAttributions(List<String> attributions) {
        this.attributions = attributions;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", websiteUri=" + websiteUri +
                ", latLng=" + latLng +
                ", rating=" + rating +
                ", attributions='" + attributions + '\'' +
                '}';
    }
}
