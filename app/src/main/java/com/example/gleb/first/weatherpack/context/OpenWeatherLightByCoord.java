package com.example.gleb.first.weatherpack.context;

import android.location.Location;
import android.util.Log;

import com.example.gleb.first.weatherpack.Weather;
import com.example.gleb.first.weatherpack.context.helpful.StringCalc;
import com.example.gleb.first.weatherpack.models.WeatherCityModel;
import com.example.gleb.first.weatherpack.models.WeatherModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gleb on 07.09.16.
 */
public class OpenWeatherLightByCoord implements WeatherCityModel, WeatherInternetAccessInterfaceByCoord{
    public static final String API_JSON = "http://api.openweathermap.org/data/2.5/weather?";
    private final static String LOCATION_LATITUDE_TEMPLATE = "lat=";
    private final static String LOCATION_LONGTITUDE_TEMPLATE = "lon=";
    private final static String APIKEY_TEMPLATE = "appid=";
    private final static String UNITS_TEMPLATE = "&units=";

    public static final String WEATHER_ERROR_CODE = "Error";

    //<<Private constants!>>
    private static final String USER_AGENT = "User-Agent";
    private static final String BROWSER_TYPE = "Mozilla/5.0";
    private static final String REQUEST_METHOD = "GET";


    private final static String API_KEY = "5b5375e5f95b02ad0553a181b2dd9857";
    public static final String DEFAULT_CITY = "Kyiv";
    public static final Weather.Units DEFAULT_UNITS = Weather.Units.Celsius;
    //<< >>

    private String location;
    private String api_key;
    private String api_string;
    private String units;



    private String temperature;
    private String temperature_max;
    private String temperature_min;
    private String icon_name;
    private String pressure;
    private String city;

    public OpenWeatherLightByCoord(){
        setUnits(DEFAULT_UNITS);
        setApiKey(API_KEY);
    }

    @Override
    public WeatherModel calculate() {
        if(location == null || location.equals(""))
            return null;
        try {
            String tmp;
            URL url = new URL(api_string);
            HttpURLConnection con =  (HttpURLConnection) url.openConnection();
            con.setRequestMethod(REQUEST_METHOD);
            con.setRequestProperty(USER_AGENT , BROWSER_TYPE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer page = new StringBuffer();
            String inputLine;
            while ((inputLine = reader.readLine()) != null){
                page.append(inputLine);
            }
            tmp = page.toString();

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(tmp);

            JSONObject jobj  = ((JSONObject)((JSONArray) json.get("weather")).get(0));
            JSONObject main_obj = ((JSONObject) json.get("main"));
            JSONObject sys_obj = ((JSONObject) json.get("sys"));

            String temp = main_obj.get("temp").toString();
            String max_tmp = main_obj.get("temp_max").toString();
            String min_tmp = main_obj.get("temp_min").toString();
            pressure = main_obj.get("pressure").toString();
            city = json.get("name").toString();

            int maxTrim = StringCalc.countMaxTrim(new String[]{temp,max_tmp,min_tmp,pressure});
            int trim = 4 < maxTrim ? 4 : maxTrim;


            icon_name = jobj.get("icon").toString();
            temperature = temp.substring(0,trim) + " °C";
            temperature_max = max_tmp.substring(0,trim) + " °C";
            temperature_min = min_tmp.substring(0,trim) + " °C";

            return this;
        }catch (SecurityException | ParseException | IOException | NullPointerException pex){
            for (StackTraceElement el : pex.getStackTrace())
                Log.e(WEATHER_ERROR_CODE, el.toString());
            Log.e(WEATHER_ERROR_CODE, api_string);
            return null;
        }
    }

    @Override
    public String getTemperature() {
        return temperature;
    }

    @Override
    public String getTemperature_min() {
        return temperature_max;
    }

    @Override
    public String getTemperature_max() {
        return temperature_min;
    }

    @Override
    public String getPressure() {
        return pressure;
    }

    @Override
    public String getIconName() {
        return icon_name;
    }

    @Override
    public void setLocation(Location location) {
        this.location = new StringBuilder(LOCATION_LATITUDE_TEMPLATE).append(location.getLatitude()).append("&").append(LOCATION_LONGTITUDE_TEMPLATE).append(location.getLongitude()).toString();
        createRequest();
    }

    @Override
    public void setUnits(Weather.Units units) {
        switch (units){
            case Celsius:
                this.units = "Metric";
                break;

            case Kelvin:
                this.units = "Default";
                break;

            case Fahrenheit:
                this.units = "Imperial";
                break;
        }
        createRequest();
    }

    @Override
    public void setApiKey(String api_k) {
        api_key = api_k;
        createRequest();
    }

    private void createRequest(){
        api_string = new StringBuilder(API_JSON).append(location).append("&").append(APIKEY_TEMPLATE).append(api_key).append("&").append(UNITS_TEMPLATE).append(units).toString();
    }

    @Override
    public String getCity() {
        return city;
    }
}
