package com.filutkie.gmmhelper.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public  class MyMarker {

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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyMarker myMarker = (MyMarker) o;
        return Objects.equals(id, myMarker.id) &&
                Objects.equals(latitude, myMarker.latitude) &&
                Objects.equals(longitude, myMarker.longitude) &&
                Objects.equals(address, myMarker.address) &&
                Objects.equals(title, myMarker.title) &&
                Objects.equals(description, myMarker.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, title, description, latitude, longitude);
    }

    @Override
    public String toString() {
        return "MyMarker{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
