package com.filutkie.gmmhelper.ui;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.filutkie.gmmhelper.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class GoogleMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener {

    private static final String TAG = "GoogleMapFragment";

    private SupportMapFragment mapFragment;
    private GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        Log.d(TAG, "onCreateView");
        setHasOptionsMenu(true);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, mapFragment)
                    .commit();
        }
        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submenu_mode_map:
                Log.d(TAG, "Map selected");
                item.setChecked(true);
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.submenu_mode_satellite:
                Log.d(TAG, "Satellite selected");
                item.setChecked(true);
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.submenu_mode_terrain:
                Log.d(TAG, "Terrain selected");
                item.setChecked(true);
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        map = googleMap;
        googleMap.setOnMyLocationChangeListener(this);
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onMyLocationChange(Location location) {
        Log.d(TAG, "onMyLocationChange");
        map.setOnMyLocationChangeListener(null);
    }
}
