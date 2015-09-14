package com.filutkie.gmmhelper.ui;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.filutkie.gmmhelper.R;
import com.filutkie.gmmhelper.data.FeatureContract;
import com.filutkie.gmmhelper.data.MyMarkerDatasource;
import com.filutkie.gmmhelper.model.MyMarker;
import com.filutkie.gmmhelper.utils.GeoUtils;
import com.filutkie.gmmhelper.utils.PreferenceHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.google.maps.android.MarkerManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

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

    public static final String TAG = "GoogleMapFragment";

    private MapFragment mapFragment;
    private GoogleMap map;
    private StreetViewPanorama streetViewPanorama;
    private MarkerManager markerManager;

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

    private MyMarkerDatasource markerDatasource;
    private List<MyMarker> myMarkerList;
    private HashMap<Marker, MyMarker> myMarkerHashMap;
    private Marker tempMarker;

    private int mapType = GoogleMap.MAP_TYPE_NORMAL;
    private boolean isScreenRotated;
    private boolean isSatellite;

    private static final String KEY_MAP_TYPE = "key_map_type";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);

        panelLayout.setPanelSlideListener(this);
        addMarkerButton.setOnClickListener(this);
        mylocationButton.setOnClickListener(this);

        FragmentManager fm = getChildFragmentManager();
        mapFragment = (MapFragment) fm.findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, mapFragment)
                    .commit();
        }

        markerDatasource = new MyMarkerDatasource(getActivity());
        mapFragment.getMapAsync(this);

//        StreetViewPanoramaFragment streetViewPanoramaFragment =
//                (StreetViewPanoramaFragment) getFragmentManager()
//                        .findFragmentById(R.id.fragment_sv);
//        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        if (savedInstanceState == null) {
            isSatellite = false;
            isScreenRotated = false;
        } else {
            mapType = savedInstanceState.getInt(KEY_MAP_TYPE);
            isScreenRotated = true;
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        map = ((MapFragment) getChildFragmentManager().findFragmentById(R.id.map_container)).getMap();
        map.setMyLocationEnabled(true);
        markerManager = new MarkerManager(map);
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
        googleMap.setMapType(mapType);
        googleMap.setOnMyLocationChangeListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        myMarkerList = markerDatasource.getAllMarkers();
        myMarkerHashMap = new HashMap<>();

        MarkerManager.Collection co = markerManager.newCollection("default");
        for (MyMarker my : myMarkerList) {
            Marker marker = co.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    .position(my.getPosition())
                    .title(my.getTitle())
                    .snippet(my.getDescription()));
            myMarkerHashMap.put(marker, my);
        }
    }

    // TODO implement structure to manage custom markers
    private Marker placeMarker(MyMarker myMarker) {
        Marker marker = map.addMarker(
                new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                        .position(myMarker.getPosition())
                        .title(myMarker.getTitle())
                        .snippet(myMarker.getDescription())
        );
        myMarkerHashMap.put(marker, myMarker);
        return marker;
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
                if (map.getMapType() == GoogleMap.MAP_TYPE_TERRAIN) {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else {
                    map.setMapType(isSatellite ? GoogleMap.MAP_TYPE_HYBRID : GoogleMap.MAP_TYPE_NORMAL);
                    isSatellite = !isSatellite;
                }
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
//                ft.add(R.id.container, newFragment);
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

    @Override
    public void onMapClick(LatLng latLng) {
        panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        if (tempMarker != null) {
            tempMarker.setVisible(false);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        String address = GeoUtils.getStringFromAddress(GeoUtils.getLocationAddress(getActivity(), latLng));
        if (tempMarker != null) {
            tempMarker.setVisible(false);
        } else {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(drawCircleMarkerIcon(Color.DKGRAY)))
                    .anchor(0.5f, 0.5f)
                    .title(address);
            tempMarker = map.addMarker(markerOptions);
        }
        tempMarker.setPosition(latLng);
        tempMarker.setTitle(address);
        tempMarker.setSnippet("");
        tempMarker.setVisible(true);
        onMarkerClick(tempMarker);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        MyMarker myMarker = myMarkerHashMap.get(marker);
        map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        if (myMarker != null) {
            panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            titleTextView.setText(myMarker.getTitle());
            descriptionTextView.setText(myMarker.getAddress());
//            streetViewPanorama.setPosition(marker.getPosition(), 100);
        } else if (marker.getTitle() != null) {
            panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            titleTextView.setText(marker.getTitle());
            descriptionTextView.setText(marker.getSnippet());
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        LatLng mapCenter = map.getCameraPosition().target;
        switch (v.getId()) {
            case R.id.fab_marker_add:
                String title = "Marker " + PreferenceHelper.nextMarkerNumber(getActivity());
                ContentValues values = new ContentValues();
                values.put(FeatureContract.MarkerEntry.COLUMN_LATITUDE, mapCenter.latitude);
                values.put(FeatureContract.MarkerEntry.COLUMN_LONGITUDE, mapCenter.longitude);
                values.put(FeatureContract.MarkerEntry.COLUMN_TITLE, title);
//                values.put(FeatureContract.MarkerEntry.COLUMN_TIME_ADDED, System.currentTimeMillis());
                Uri uri = getActivity().getContentResolver().insert(FeatureContract.MarkerEntry.CONTENT_URI, values);
                Log.d(TAG, "inserted row: " + uri);
                MyMarker newMarker = new MyMarker()
                        .id(Integer.parseInt(uri.getLastPathSegment()))
                        .latitude(mapCenter.latitude)
                        .longitude(mapCenter.longitude)
                        .title(title);
                placeMarker(newMarker);
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
        }
    }

    private Bitmap drawCircleMarkerIcon(int color) {
        int wheight = 25;
        final Bitmap output = Bitmap.createBitmap(wheight, wheight, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        int col = Color.RED;
        if (color != 0) {
            col = color;
        }
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, wheight, wheight);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(col);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        return output;
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
    public void onPanelSlide(View panel, float slideOffset) {
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



}
