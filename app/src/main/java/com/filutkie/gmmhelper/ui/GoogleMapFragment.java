package com.filutkie.gmmhelper.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filutkie.gmmhelper.R;
import com.google.android.gms.maps.SupportMapFragment;

public class GoogleMapFragment extends Fragment {

    private static final String TAG = "GoogleMapFragment";
    private SupportMapFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, fragment)
                    .commit();
        }
        return rootView;
    }
}
