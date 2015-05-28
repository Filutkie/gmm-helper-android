package com.filutkie.gmmhelper.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.filutkie.gmmhelper.R;
import com.filutkie.gmmhelper.database.MyMarkerDatasource;
import com.filutkie.gmmhelper.model.MyMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GoogleMapFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        OnStreetViewPanoramaReadyCallback,
        StreetViewPanorama.OnStreetViewPanoramaClickListener,
        SlidingUpPanelLayout.PanelSlideListener,
        View.OnClickListener {

    private static final String TAG = "GoogleMapFragment";

    private MapFragment mapFragment;
    private GoogleMap map;
    private Button streetViewButton;
    private StreetViewPanorama streetViewPanorama;
    private ActionBar actionbar;
    private MyMarkerDatasource markerDatasource;
    private SlidingUpPanelLayout mLayout;
    private TextView titleTextView;
    private TextView descriptionTextView;
    //    private TextView myTV;
//    private ViewSwitcher switcher;
    private FrameLayout panelHeader;
//    private Button deleteButton;

    private List<MyMarker> myMarkerList;
    private HashMap<Marker, MyMarker> myMarkerHashMap;
    private static final String KEY_CHECKED_MENU = "key_checked_menu";
    private static final String KEY_MAP_TYPE = "key_map_type";
    private int valueCheckedMenu = R.id.submenu_maptype_map;
    private int valueMapType = GoogleMap.MAP_TYPE_NORMAL;

    private boolean isScreenRotated;
    private int actionbarHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        Log.d(TAG, "onCreateView");
        setHasOptionsMenu(true);

//        switcher = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcher);
//        myTV = (TextView) switcher.findViewById(R.id.clickable_text_view);
        mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        panelHeader = (FrameLayout) rootView.findViewById(R.id.framelayout_detail_header);
        titleTextView = (TextView) rootView.findViewById(R.id.textview_panel_title);
        descriptionTextView = (TextView) rootView.findViewById(R.id.textview_panel_description);
        actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        deleteButton = (Button) rootView.findViewById(R.id.button_marker_delete);

        mLayout.setPanelSlideListener(this);
        mLayout.setAnchorPoint(0.6f);
        mLayout.setCoveredFadeColor(0);

//        myTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switcher.showNext(); //or switcher.showPrevious();
//                myTV.setText("value");
//            }
//        });
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (MapFragment) fm.findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, mapFragment)
                    .commit();
        }

        markerDatasource = new MyMarkerDatasource(getActivity());
        mapFragment.getMapAsync(this);

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.fragment_sv);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);


        if (savedInstanceState != null) {
            valueCheckedMenu = savedInstanceState.getInt(KEY_CHECKED_MENU);
            valueMapType = savedInstanceState.getInt(KEY_MAP_TYPE);
            isScreenRotated = true;
        } else {
            isScreenRotated = false;
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        map = ((MapFragment) getChildFragmentManager().findFragmentById(R.id.map_container)).getMap();
        map.setMyLocationEnabled(true);
        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionbarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
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
        googleMap.setMapType(valueMapType);
        googleMap.setOnMyLocationChangeListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setMyLocationEnabled(true);
        googleMap.setPadding(0, actionbarHeight, 0, 0);
        myMarkerList = markerDatasource.getAllMarkers();
        myMarkerHashMap = new HashMap<>();
        for (MyMarker m : myMarkerList) {
            Marker marker = placeMarker(m);
            myMarkerHashMap.put(marker, m);
        }
    }

    private Marker placeMarker(MyMarker marker) {
        return map.addMarker(new MarkerOptions()
                .position(marker.getPosition())
                .title(marker.getTitle())
                .snippet(marker.getDescription())
                .draggable(true));
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
//        MenuItem item = menu.findItem(valueCheckedMenu);
//        if (!item.isChecked()) {
//            item.setChecked(true);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submenu_maptype_map:
//                if (!item.isChecked()) {
//                    item.setChecked(true);
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                getActivity().invalidateOptionsMenu();

//                }
                break;
            case R.id.submenu_maptype_satellite:
//                if (!item.isChecked()) {
//                    item.setChecked(true);
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                getActivity().invalidateOptionsMenu();

//                }
                break;
            case R.id.submenu_maptype_terrain:
//                if (!item.isChecked()) {
//                    item.setChecked(true);
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                getActivity().invalidateOptionsMenu();
//                }
                break;
        }
//        if (item.isCheckable() && item.isChecked()) {
//            valueCheckedMenu = item.getItemId();
//            valueMapType = map.getMapType();
//        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CHECKED_MENU, valueCheckedMenu);
        outState.putInt(KEY_MAP_TYPE, valueMapType);
    }

    @Override
    public void onMyLocationChange(Location location) {
        Log.d(TAG, "onMyLocationChange");
        if (!isScreenRotated) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
        map.setOnMyLocationChangeListener(null);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        MyMarker myMarker = new MyMarker("title", latLng.latitude, latLng.longitude);
        String address = getLocationAddress(myMarker.getPosition());
        myMarker.setAddress(address);
        int rowId = markerDatasource.addMarker(myMarker);
        myMarker.setId(rowId);
        Log.d(TAG, "added: " + myMarker.toString());
        myMarkerHashMap.put(placeMarker(myMarker), myMarker);
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

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
//        Log.i(TAG, "onPanelSlide, offset " + slideOffset);
        if (slideOffset > 0) {
//            myTV.setText("sliding");
            panelHeader.setBackgroundResource(R.color.color_primary);
            titleTextView.setTextColor(Color.WHITE);
            descriptionTextView.setTextColor(Color.WHITE);
            // TODO optimize method calls
            if (slideOffset > .8) {
                if (actionbar.isShowing()) {
                    actionbar.hide();
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                }
            } else {
                if (!actionbar.isShowing()) {
                    actionbar.show();
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                }
            }
        } else {
//            myTV.setText("ended");
            panelHeader.setBackgroundResource(R.color.color_background);
            titleTextView.setTextColor(Color.BLACK);
            descriptionTextView.setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onPanelExpanded(View panel) {
        Log.i(TAG, "onPanelExpanded");
    }

    @Override
    public void onPanelCollapsed(View panel) {
        Log.i(TAG, "onPanelCollapsed");
    }

    @Override
    public void onPanelAnchored(View panel) {
        Log.i(TAG, "onPanelAnchored");
    }

    @Override
    public void onPanelHidden(View panel) {
        Log.i(TAG, "onPanelHidden");
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        MyMarker myMarker = myMarkerHashMap.get(marker);
        titleTextView.setText(myMarker.getTitle());
        descriptionTextView.setText(myMarker.getAddress());
        streetViewPanorama.setPosition(marker.getPosition(), 100);
//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                marker.remove();
//            }
//        });
        return true;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setOnStreetViewPanoramaClickListener(this);
        this.streetViewPanorama = streetViewPanorama;
    }

    @Override
    public void onStreetViewPanoramaClick(StreetViewPanoramaOrientation streetViewPanoramaOrientation) {
        Toast.makeText(getActivity(), "Pano clicked.", Toast.LENGTH_SHORT).show();
    }
}
