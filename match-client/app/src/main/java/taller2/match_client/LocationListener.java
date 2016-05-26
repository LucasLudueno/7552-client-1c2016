package taller2.match_client;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by lucas on 22/05/16.
 */
public class LocationListener implements android.location.LocationListener{

    /* Atributes */
    Double latitude = 0.0;
    Double longitude = 0.0;

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
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
};

