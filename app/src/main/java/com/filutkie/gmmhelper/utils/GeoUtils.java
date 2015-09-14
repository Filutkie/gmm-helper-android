package com.filutkie.gmmhelper.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoUtils {

    public static Address getLocationAddress(Context context, LatLng latlng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        Address address = new Address(Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
            if (addresses != null && addresses.size() != 0) {
                address = addresses.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "IOException raised", Toast.LENGTH_SHORT).show();
        }
        return address;
    }

    public static String getStringFromAddress(Address address) {
        String resultString = "";
        StringBuilder strAddress = new StringBuilder();
        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            strAddress.append(address.getAddressLine(i)).append(", ");
        }
        return strAddress.toString();
    }
}
