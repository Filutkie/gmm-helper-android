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

    public static String getLocationAddress(Context context, LatLng latlng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
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
            Toast.makeText(context, "IOException raised", Toast.LENGTH_SHORT).show();
        }
        return resultString;
    }

}
