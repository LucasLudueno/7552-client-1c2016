package taller2.match_client;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/* Location Listener Getter can get Location as Latitude and Longitude */
public class ActivityLocationListener implements LocationListener {

    /* Attributes */
    Double latitude = 0.0;
    Double longitude = 0.0;
    private static final String TAG = "ActivityLocationListener";

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    public Double getLatitude() {
        Log.d(TAG, "Get Latitude ");
        return latitude;
    }

    public Double getLongitude() {
        Log.d(TAG, "Get Longitude");
        return longitude;
    }
};

