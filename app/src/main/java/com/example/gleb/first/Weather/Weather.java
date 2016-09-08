package com.example.gleb.first.Weather;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.gleb.first.MainActivity;
import com.example.gleb.first.Weather.context.WeatherCalculatorInterface;
import com.example.gleb.first.Weather.context.WeatherInternetAccessInterface;
import com.example.gleb.first.Weather.context.WeatherInternetAccessInterfaceByCoord;
import com.example.gleb.first.Weather.models.WeatherCityModel;
import com.example.gleb.first.Weather.models.WeatherModel;
import com.example.gleb.first.Weather.models.WeatherSimpleModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by Gleb on 20.08.2016.
 */
public class Weather extends TimerTask {
    public static final String WEATHER_DEBUG_TAG = "WEATHERDEBUGTAG";

    private Context context;
    private Handler handle;

    public enum Units{
        Celsius,
        Kelvin,
        Fahrenheit
    }

    enum Types{
        City,
        Key,
        Units
    }


    private String prev_wright_city;

    List<WeatherCalculatorInterface> list;

    public Weather(Context context, Handler handle, WeatherCalculatorInterface weather){
        this(context, handle);
        list.add(weather);
    }

    public Weather(Context context , Handler handle, WeatherCalculatorInterface weather, String city){
        this(context, handle, weather);
        setCity(city);
    }

    public Weather(Context context , Handler handle){
        list = new LinkedList<WeatherCalculatorInterface>();
        this.context = context;
        this.handle = handle;
    }

    @Override
    public void run() {
        for(WeatherCalculatorInterface wiface : list) {
            Log.d(WEATHER_DEBUG_TAG, "IN CYCLE!");
            WeatherModel weather = wiface.calculate();
            if (weather == null) {
                Log.d(WEATHER_DEBUG_TAG, " == NULL !!!");
                continue;
            }
            if(checkWeather(weather))
                return;

        }
        Bundle data = new Bundle();
        data.putString("Error", "Cant find any city!");
        createMsgWithData(data);
    }

    protected boolean checkWeather(WeatherModel weather){
        for (Class iface : weather.getClass().getInterfaces()) {
            if(checkInterface(iface, weather))
                return true;
        }
        return false;
    }

    protected boolean checkInterface(Class iface, WeatherModel weather){
        boolean ret_statm = false;
        Bundle data = new Bundle();
        String className = iface.getSimpleName();
        Log.d(WEATHER_DEBUG_TAG, "CHECK INTERFACES WITH NAME == " + className);
        if (className.equals(WeatherCalculatorInterface.class.getSimpleName())) {
            return ret_statm;
        }
        if (WeatherModel.class.isInstance(weather)){

        }
        if (WeatherSimpleModel.class.isInstance(weather)) {
            WeatherSimpleModel weatherSimpleModel = (WeatherSimpleModel) weather;
            data.putString(MainActivity.CONFIG_ICON_NAME, weatherSimpleModel.getIconName());
            data.putString(MainActivity.CONFIG_TEMPERATURE, weatherSimpleModel.getTemperature());
            data.putString(MainActivity.CONFIG_PRESSURE, weatherSimpleModel.getPressure());
            data.putString(MainActivity.CONFIG_MAX_TEMPERATURE, weatherSimpleModel.getTemperature_max());
            data.putString(MainActivity.CONFIG_MIN_TEMPERATURE, weatherSimpleModel.getTemperature_min());
            ret_statm = true;
        }
        if(WeatherCityModel.class.isInstance(weather)){
            WeatherCityModel cityModel = (WeatherCityModel)weather;
            data.putString(MainActivity.CONFIG_CITY, cityModel.getCity());
            ret_statm = true;
        }

        createMsgWithData(data);
        return ret_statm;
    }


    private void createMsgWithData(Bundle data){
        Message msg = handle.obtainMessage();
        msg.setData(data);
        handle.sendMessage(msg);
    }

    public void setCity(String city){
        //will support in future;
        setInWeather(city, Types.City);
    }

    public void setUnits(Units units){
        //will support in future;
        setInWeather(units);
    }

    public void setApiKey(String api_k){
        //will support in future;
        setInWeather(api_k, Types.Key);

    }

    public void setLocation(Location location){
        setInWeather(location);
    }

    private void setInWeather(String data, Types types){
        for(WeatherCalculatorInterface weather : list){
            if(WeatherInternetAccessInterface.class.isInstance(weather)){
                WeatherInternetAccessInterface w = (WeatherInternetAccessInterface) weather;
                switch (types){
                    case City:
                        w.setCity(data);
                        break;

                    case Key:
                        w.setApiKey(data);
                        break;
                }
            }
        }
    }

    private void setInWeather(Units units){
        for(WeatherCalculatorInterface weather : list) {
            if(WeatherInternetAccessInterface.class.isInstance(weather)){
                WeatherInternetAccessInterface w = (WeatherInternetAccessInterface) weather;
                w.setUnits(units);
            }
        }
    }

    private void setInWeather(Location location){
        for(WeatherCalculatorInterface weather : list){
            if(WeatherInternetAccessInterfaceByCoord.class.isInstance(weather)){
                WeatherInternetAccessInterfaceByCoord w = (WeatherInternetAccessInterfaceByCoord)weather;
                w.setLocation(location);
            }
        }
    }

    public void addWeathersApi(WeatherCalculatorInterface weather){
        list.add(weather);
    }

    public void deleteWeathersApi(WeatherCalculatorInterface weather){
        list.remove(weather);
    }

    public void deleteWeathersApi(int i){
        list.remove(i);
    }
}
