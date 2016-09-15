package com.example.gleb.first.weatherpack;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.gleb.first.MainActivity;
import com.example.gleb.first.weatherpack.context.WeatherCalculatorInterface;
import com.example.gleb.first.weatherpack.context.WeatherInternetAccessInterface;
import com.example.gleb.first.weatherpack.context.WeatherInternetAccessInterfaceByCoord;
import com.example.gleb.first.weatherpack.models.WeatherCityModel;
import com.example.gleb.first.weatherpack.models.WeatherModel;
import com.example.gleb.first.weatherpack.models.WeatherSimpleModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

/**
 * Created by Gleb on 20.08.2016.
 */
public class Weather extends TimerTask {
    public static final String WEATHER_DEBUG_TAG = "WEATHERDEBUGTAG";
    public static final String WEATHER_INTERNET_BY_COORD = WeatherInternetAccessInterfaceByCoord.class.getSimpleName();
    public static final String WEATHER_INTERNET_BY_CITY = WeatherInternetAccessInterface.class.getSimpleName();

    private Context context;
    private Handler handle;

    private LinkedList<String> order;

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

    Map<String, List<WeatherCalculatorInterface>> weatherAPIs;

    public Weather(Context context, Handler handle, WeatherCalculatorInterface weather){
        this(context, handle);
        addWeathersApi(weather);
    }

    public Weather(Context context , Handler handle, WeatherCalculatorInterface weather, String city){
        this(context, handle, weather);
        setCity(city);
    }

    public Weather(Context context , Handler handle){
        weatherAPIs = new HashMap<String, List<WeatherCalculatorInterface>>();
        order = new LinkedList<String>();
        this.context = context;
        this.handle = handle;
    }

    @Override
    public void run() {
        for(String key : order)
            for(WeatherCalculatorInterface wiface : weatherAPIs.get(key)) {
                Log.d(WEATHER_DEBUG_TAG, "IN CYCLE! " + wiface.getClass().getSimpleName());
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
        for(WeatherCalculatorInterface weather : weatherAPIs.get(WeatherInternetAccessInterface.class.getSimpleName())){
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

    private void setInWeather(Units units){
        for (List<WeatherCalculatorInterface> list : weatherAPIs.values())
            for(WeatherCalculatorInterface weather : list) {
                if(WeatherInternetAccessInterface.class.isInstance(weather)){
                    WeatherInternetAccessInterface w = (WeatherInternetAccessInterface) weather;
                    w.setUnits(units);
                }
            }
    }

    private void setInWeather(Location location){
        for (List<WeatherCalculatorInterface> list : weatherAPIs.values())
            for(WeatherCalculatorInterface weather : list){
                if(WeatherInternetAccessInterfaceByCoord.class.isInstance(weather)){
                    WeatherInternetAccessInterfaceByCoord w = (WeatherInternetAccessInterfaceByCoord)weather;
                    w.setLocation(location);
                }
            }
    }

    public void setByFirst(WeatherTypes types){

        switch (types){
            case ByLocation: {
                int index = order.indexOf(WeatherInternetAccessInterfaceByCoord.class.getSimpleName());
                if (index > 0) {
                    order.remove(index);
                    order.addFirst(WeatherInternetAccessInterfaceByCoord.class.getSimpleName());
                }
            }
                break;

            case ByCity: {
                int index = order.indexOf(WeatherInternetAccessInterface.class.getSimpleName());
                if (index > 0) {
                    order.remove(index);
                    order.addFirst(WeatherInternetAccessInterface.class.getSimpleName());
                }
            }

                break;
        }

    }

    public void addWeathersApi(WeatherCalculatorInterface weather){
        if(WeatherInternetAccessInterfaceByCoord.class.isInstance(weather)) {
            if (weatherAPIs.containsKey(WeatherInternetAccessInterfaceByCoord.class.getSimpleName())) {
                weatherAPIs.get(WeatherInternetAccessInterfaceByCoord.class.getSimpleName()).add(weather);
            } else {
                List<WeatherCalculatorInterface> list = new LinkedList<WeatherCalculatorInterface>();
                list.add(weather);
                weatherAPIs.put(WeatherInternetAccessInterfaceByCoord.class.getSimpleName(), list);
                order.add(WeatherInternetAccessInterfaceByCoord.class.getSimpleName());
            }
        }
        else if(WeatherInternetAccessInterface.class.isInstance(weather)){
            if (weatherAPIs.containsKey(WeatherInternetAccessInterface.class.getSimpleName())) {
                weatherAPIs.get(WeatherInternetAccessInterface.class.getSimpleName()).add(weather);
            } else {
                List<WeatherCalculatorInterface> list = new LinkedList<WeatherCalculatorInterface>();
                list.add(weather);
                weatherAPIs.put(WeatherInternetAccessInterface.class.getSimpleName(), list);
                order.add(WeatherInternetAccessInterface.class.getSimpleName());
            }
        }
    }

    public void deleteWeathersApi(WeatherCalculatorInterface weather){

        //list.remove(weather);
    }

    public void deleteWeathersApi(int i){

        //list.remove(i);
    }

    public enum WeatherTypes{
        ByLocation,
        ByCity
    }
}


