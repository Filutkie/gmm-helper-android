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
import android.widget.Button;

import com.filutkie.gmmhelper.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener,
        GoogleMap.OnMapLongClickListener {

    private SupportMapFragment mapFragment;

    private GoogleMap map;
    private Button invalidateButton;

    private static final String TAG = "GoogleMapFragment";
    private String KEY_CHECKED_MENU = "key_checked_menu";
    private String KEY_MAP_TYPE = "key_map_type";
    private int VALUE_CHECKED_MENU = R.id.submenu_maptype_map;
    private int VALUE_MAP_TYPE = GoogleMap.MAP_TYPE_NORMAL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        invalidateButton = (Button) rootView.findViewById(R.id.button_invalidate);
        invalidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().invalidateOptionsMenu();
            }
        });
        if (savedInstanceState != null) {
            VALUE_CHECKED_MENU = savedInstanceState.getInt(KEY_CHECKED_MENU);
            VALUE_MAP_TYPE = savedInstanceState.getInt(KEY_MAP_TYPE);
        }

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        map.setMyLocationEnabled(false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        map = googleMap;
        googleMap.setMapType(VALUE_MAP_TYPE);
        googleMap.setOnMyLocationChangeListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionsMenu");
        getActivity().getMenuInflater().inflate(R.menu.menu_map, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(VALUE_CHECKED_MENU).setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submenu_maptype_map:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                break;
            case R.id.submenu_maptype_satellite:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
                break;
            case R.id.submenu_maptype_terrain:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }
                break;
        }
        if (item.isCheckable() && item.isChecked()) {
            VALUE_CHECKED_MENU = item.getItemId();
            VALUE_MAP_TYPE = map.getMapType();
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CHECKED_MENU, VALUE_CHECKED_MENU);
        outState.putInt(KEY_MAP_TYPE, VALUE_MAP_TYPE);
    }


    @Override
    public void onMyLocationChange(Location location) {
        Log.d(TAG, "onMyLocationChange");
        map.setOnMyLocationChangeListener(null);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        map.addMarker(new MarkerOptions().draggable(true).position(latLng));
    }
}
