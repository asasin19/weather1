package com.example.gleb.first.weatherpack.context;

import android.location.Location;

import com.example.gleb.first.weatherpack.Weather;


/**
 * Created by gleb on 07.09.16.
 */
public interface WeatherInternetAccessInterfaceByCoord extends WeatherCalculatorInterface {
    void setLocation(Location location);
    void setUnits(Weather.Units units);
    void setApiKey(String api_k);
}
