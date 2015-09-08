package com.filutkie.gmmhelper.model;

import com.google.android.gms.maps.model.LatLng;

public class MyMarker extends AbstractMarker{

    private int id;
    private String title;
    private String description;
    private String address;
    private double latitude;
    private double longitude;

    public MyMarker id(int id) {
        this.id = id;
        return this;
    }

    public MyMarker title(String title) {
        this.title = title;
        return this;
    }

    public MyMarker description(String description) {
        this.description = description;
        return this;
    }

    public MyMarker address(String address) {
        this.address = address;
        return this;
    }

    public MyMarker latitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public MyMarker longitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public String toString() {
        return "MyMarker{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public int getType() {
        return TYPE_MARKER_DEFAULT;
    }
}
