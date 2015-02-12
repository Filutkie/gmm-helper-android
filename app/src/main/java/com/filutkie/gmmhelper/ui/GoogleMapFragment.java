package com.filutkie.gmmhelper.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.filutkie.gmmhelper.R;
import com.filutkie.gmmhelper.database.MyMarkerDatasource;
import com.filutkie.gmmhelper.model.MyMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GoogleMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener,
        GoogleMap.OnMapLongClickListener {

    private MapFragment mapFragment;

    private GoogleMap map;
    private Button button;
    private TextView geocoderTextView;

    private MyMarkerDatasource markerDatasource;
    private List<MyMarker> myMarkerList;

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
        mapFragment = (MapFragment) fm.findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, mapFragment)
                    .commit();
        }
        markerDatasource = new MyMarkerDatasource(getActivity());
        mapFragment.getMapAsync(this);
        button = (Button) rootView.findViewById(R.id.button_invalidate);
        geocoderTextView = (TextView) rootView.findViewById(R.id.textview_geocoder);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        if (savedInstanceState != null) {
            VALUE_CHECKED_MENU = savedInstanceState.getInt(KEY_CHECKED_MENU);
            VALUE_MAP_TYPE = savedInstanceState.getInt(KEY_MAP_TYPE);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        map = ((MapFragment) getChildFragmentManager().findFragmentById(R.id.map_container)).getMap();
        map.setMyLocationEnabled(true);
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

        myMarkerList = markerDatasource.getAllMarkers();
        for (MyMarker m : myMarkerList) {
            placeMarker(m);
        }
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
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        map.setOnMyLocationChangeListener(null);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        MyMarker marker = new MyMarker("title", latLng.latitude, latLng.longitude);
        String address = getLocationAddress(marker.getPosition());
        marker.setAddress(address);
        placeMarker(marker);
        markerDatasource.addMarker(marker);
        geocoderTextView.setText(address);
    }

    private void placeMarker(MyMarker marker) {
        map.addMarker(new MarkerOptions().draggable(true).position(marker.getPosition()).title(marker.getTitle()));
    }

    // TODO move from ui thread
    public String getLocationAddress(LatLng latlng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        String resultString = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
            if (addresses != null && addresses.size() != 0) {
                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();
                for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append(", ");
                }
                resultString = strAddress.toString();
            } else
                resultString = "Address not found";
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "IOException raised", Toast.LENGTH_SHORT).show();
        }
        return resultString;
    }
}
