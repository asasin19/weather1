package com.example.gleb.first.Weather.models;

/**
 * Created by Gleb on 05.09.2016.
 */
public interface WeatherSimpleModel extends WeatherModel {
    String getTemperature();
    String getTemperature_min();
    String getTemperature_max();
    String getPressure();
    String getIconName();
}
