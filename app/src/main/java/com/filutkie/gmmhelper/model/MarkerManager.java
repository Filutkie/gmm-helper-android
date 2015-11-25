package com.filutkie.gmmhelper.model;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkerManager implements GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private Marker currentMarker;
    private Map<String, MyMarker> markerMap = new HashMap<>();
    private GoogleMap.OnMarkerClickListener listener;

    public MarkerManager(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void addMarker(MyMarker myMarker) {
        MarkerOptions options = new MarkerOptions()
                .position(myMarker.getPosition());
        Marker marker = googleMap.addMarker(options);
        markerMap.put(marker.getId(), myMarker);
    }

    public void addAllMarkers(List<MyMarker> list) {
        for (MyMarker myMarker : list) {
            MarkerOptions options = new MarkerOptions()
                    .position(myMarker.getPosition());
            Marker marker = googleMap.addMarker(options);
            markerMap.put(marker.getId(), myMarker);
        }
    }

    public void removeMarker(Marker marker) {
        if (markerMap.containsKey(marker.getId())) {
            marker.remove();
            markerMap.remove(marker.getId());
        }
    }

    public MyMarker getMyMarker(Marker marker) {
        return markerMap.get(marker.getId());
    }

    public Marker getCurrentSelection() {
        return currentMarker;
    }

    public void setOnMarkerClickListener(GoogleMap.OnMarkerClickListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        currentMarker = marker;
        return true;
    }
}
