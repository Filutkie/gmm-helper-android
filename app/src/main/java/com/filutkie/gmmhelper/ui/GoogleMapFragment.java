package com.filutkie.gmmhelper.ui;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.filutkie.gmmhelper.R;
import com.filutkie.gmmhelper.data.FeatureContract;
import com.filutkie.gmmhelper.model.MarkerManager;
import com.filutkie.gmmhelper.model.MyMarker;
import com.filutkie.gmmhelper.utils.PreferenceHelper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GoogleMapFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        SlidingUpPanelLayout.PanelSlideListener,
        View.OnClickListener {

    public static final String TAG = "GoogleMapFragment";

    private GoogleMap map;

    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout panelLayout;
    @Bind(R.id.framelayout_detail_header)
    FrameLayout panelHeader;
    @Bind(R.id.textview_panel_title)
    TextView titleTextView;
    @Bind(R.id.textview_panel_description)
    TextView descriptionTextView;
    @Bind(R.id.fab_marker_add)
    FloatingActionButton addMarkerButton;
    @Bind(R.id.fab_mylocation)
    FloatingActionButton mylocationButton;
    @Bind(R.id.button_details)
    Button details;

    private MarkerManager markerManager;

    private int mapType = GoogleMap.MAP_TYPE_NORMAL;
    private boolean isSatellite; // because map type is restored on orientation change

    private static final String KEY_MAP_TYPE = "key_map_type";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);

        panelLayout.setPanelSlideListener(this);
        addMarkerButton.setOnClickListener(this);
        mylocationButton.setOnClickListener(this);
        details.setOnClickListener(this);

        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, mapFragment)
                    .commit();
        }

        mapFragment.getMapAsync(this);

        if (savedInstanceState == null) {
            isSatellite = false;
        } else {
            mapType = savedInstanceState.getInt(KEY_MAP_TYPE);
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
    public void onPause() {
        super.onPause();
        map.setMyLocationEnabled(false);
        PreferenceHelper.saveLastCameraLatLngZoom(
                getActivity(),
                map.getCameraPosition().target.latitude,
                map.getCameraPosition().target.longitude,
                map.getCameraPosition().zoom);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        map = googleMap;
        googleMap.setMapType(mapType);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        markerManager = new MarkerManager(googleMap);
        markerManager.setOnMarkerClickListener(this);

        CameraUpdate lastCameraPosition =
                CameraUpdateFactory.newLatLngZoom(
                        PreferenceHelper.getLastCameraLatLng(getActivity()),
                        PreferenceHelper.getLastCameraZoom(getActivity()));
        googleMap.moveCamera(lastCameraPosition);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_maptype_toggle:
                toggleMapType();
                break;
            case R.id.menu_maptype_terrain:
                // prevent blinking on repetitive terrain item selecting
                if (map.getMapType() != GoogleMap.MAP_TYPE_TERRAIN) {
                    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }
                break;
            case R.id.menu_list:
                // TODO: if tablet then:
                DialogFragment newFragment = MarkersDialogFragment.newInstance();
                newFragment.show(getFragmentManager(), "list_dialog");
                // TODO: if phone then:
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                DialogFragment newFragment = MarkersDialogFragment.newInstance();
//                ft.update(R.id.container, newFragment);
//                ft.commit();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_MAP_TYPE, mapType);
    }

    private void toggleMapType() {
        if (map.getMapType() == GoogleMap.MAP_TYPE_TERRAIN) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else {
            isSatellite = !isSatellite;
            map.setMapType(isSatellite ? GoogleMap.MAP_TYPE_HYBRID : GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        MyMarker myMarker = markerManager.getMyMarker(marker);

        titleTextView.setText(myMarker.getTitle());
        descriptionTextView.setText(myMarker.getDescription());

        markerManager.onMarkerClick(marker);
        panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_marker_add:
                LatLng mapCenter = map.getCameraPosition().target;
                String title = "Marker " + PreferenceHelper.nextMarkerNumber(getActivity());
                ContentValues values = new ContentValues();
                values.put(FeatureContract.MarkerEntry.COLUMN_LATITUDE, mapCenter.latitude);
                values.put(FeatureContract.MarkerEntry.COLUMN_LONGITUDE, mapCenter.longitude);
                values.put(FeatureContract.MarkerEntry.COLUMN_TITLE, title);
                values.put(FeatureContract.MarkerEntry.COLUMN_TIME_ADDED, System.currentTimeMillis());
                Uri uri = getActivity().getContentResolver().insert(FeatureContract.MarkerEntry.CONTENT_URI, values);
                Log.d(TAG, "inserted row: " + uri);
                MyMarker newMarker = new MyMarker()
                        .id(Integer.parseInt(uri.getLastPathSegment()))
                        .latitude(mapCenter.latitude)
                        .longitude(mapCenter.longitude)
                        .title(title);
                markerManager.addMarker(newMarker);
                break;
            case R.id.fab_mylocation:
                if (map.getMyLocation() != null) {
                    LatLng mylocation = new LatLng(
                            map.getMyLocation().getLatitude(),
                            map.getMyLocation().getLongitude());
                    if (map.getCameraPosition().zoom < 8) {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 14));
                    } else {
                        map.animateCamera(CameraUpdateFactory.newLatLng(mylocation));
                    }
                }
                break;
            case R.id.button_details:
                int markerId = markerManager.getMyMarker(
                        markerManager.getCurrentSelection()).getId();
                DialogFragment dialog = FeatureDetailDialogFragment.newInstance(markerId);
                dialog.show(getFragmentManager(), "feature_dialog");

                break;
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
    }

    @Override
    public void onPanelExpanded(View panel) {
    }

    @Override
    public void onPanelCollapsed(View panel) {
    }

    @Override
    public void onPanelAnchored(View panel) {
    }

    @Override
    public void onPanelHidden(View panel) {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = FeatureContract.MarkerEntry.CONTENT_URI;
        String[] projection = null;
        return new CursorLoader(getActivity(),
                uri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<MyMarker> myMarkerList = new ArrayList<>();
        MyMarker myMarker;
        Log.d(TAG, data.getCount()+"");
        for(data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            myMarker = new MyMarker()
                    .id(data.getInt(data.getColumnIndex(FeatureContract.MarkerEntry._ID)))
                    .title(data.getString(data.getColumnIndex(FeatureContract.MarkerEntry.COLUMN_TITLE)))
                    .description(data.getString(data.getColumnIndex(FeatureContract.MarkerEntry.COLUMN_DESCRIPTION)))
                    .latitude(data.getDouble(data.getColumnIndex(FeatureContract.MarkerEntry.COLUMN_LATITUDE)))
                    .longitude(data.getDouble(data.getColumnIndex(FeatureContract.MarkerEntry.COLUMN_LONGITUDE)));
            myMarkerList.add(myMarker);
        }
        markerManager.addAllMarkers(myMarkerList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
