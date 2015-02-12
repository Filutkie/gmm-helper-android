package com.filutkie.gmmhelper.model;

import com.google.android.gms.maps.model.LatLng;

public class MyMarker {

    private String title;

    private String description;
    private String address;
    private double latitude;
    private double longitude;

    public MyMarker() {
    }

    public MyMarker(String title, String description, String address, double latitude, double longitude) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public MyMarker(String title, String description, double latitude, double longitude) {
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public MyMarker(String title, double latitude, double longitude) {

        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public MyMarker(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng getPosition() {
        return new LatLng(this.latitude, this.longitude);
    }

    @Override
    public String toString() {
        return "MyMarker{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

