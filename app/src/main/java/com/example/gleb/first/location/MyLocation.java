package com.example.gleb.first.location;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.gleb.first.R;
import com.example.gleb.first.weatherpack.Weather;

import java.util.Date;

/**
 * Created by Gleb on 14.09.2016.
 */
public class MyLocation {

    public static final int LOCATION_ENABLED_NETWORK = 1;
    public static final int LOCATION_ENABLED_GPS = 2;
    public static final int LOCATION_ENABLED_ALL = 3;
    public static final int LOCATION_DISABLED = 4;

    private boolean status;

    private Context context;
    private Weather weather;

    private LocationManager locationManager;

    public MyLocation(Context context, Weather weather){
        this.context = context;
        this.weather = weather;
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void resumeLocationData(){
        switch (checkEnabled()){
            case LOCATION_ENABLED_ALL:
            case LOCATION_ENABLED_GPS:
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1000 * 10, 10, locationListener);
                break;

            case LOCATION_ENABLED_NETWORK:
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                        locationListener);
                break;

            case LOCATION_DISABLED:
                break;
        }
    }

    public void pauseLocationData(){
        locationManager.removeUpdates(locationListener);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            pauseLocationData();
            resumeLocationData();
        }

        @Override
        public void onProviderEnabled(String s) {
            pauseLocationData();
            resumeLocationData();
        }

        @Override
        public void onProviderDisabled(String s) {
            pauseLocationData();
            resumeLocationData();
        }
    };

    private void showLocation(android.location.Location location) {
        if (location == null)
            return;

        weather.setLocation(location);
    }

    private String formatLocation(android.location.Location location) {
        if (location == null)
            return "";
        return String.format(
                context.getString(R.string.location_string),
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    private int checkEnabled() {
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                return LOCATION_ENABLED_ALL;
            else
                return LOCATION_ENABLED_GPS;
        else
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            return LOCATION_ENABLED_NETWORK;
        else
            return LOCATION_DISABLED;

    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        if(this.status == status)
            return;
        this.status = status;
        if(status)
            resumeLocationData();
        else
            pauseLocationData();

    }
}
