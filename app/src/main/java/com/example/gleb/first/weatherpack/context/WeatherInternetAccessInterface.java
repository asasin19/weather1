package com.example.gleb.first.weatherpack.context;

import com.example.gleb.first.weatherpack.Weather;

/**
 * Created by Gleb on 05.09.2016.
 */
public interface WeatherInternetAccessInterface extends WeatherCalculatorInterface {
    void setCity(String city);
    void setUnits(Weather.Units units);
    void setApiKey(String api_k);

}
