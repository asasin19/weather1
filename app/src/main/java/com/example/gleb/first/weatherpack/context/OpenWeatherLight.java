package com.example.gleb.first.weatherpack.context;

import android.util.Log;

import com.example.gleb.first.weatherpack.Weather;
import com.example.gleb.first.weatherpack.context.helpful.StringCalc;
import com.example.gleb.first.weatherpack.models.WeatherModel;
import com.example.gleb.first.weatherpack.models.WeatherSimpleModel;

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
 * Created by Gleb on 05.09.2016.
 */
public class OpenWeatherLight implements WeatherSimpleModel, WeatherInternetAccessInterface {
    public static final String API_JSON = "http://api.openweathermap.org/data/2.5/weather?";
    private final static String CITY_TEMPLATE = "q=";
    private final static String APIKEY_TEMPLATE = "appid=";
    private final static String UNITS_TEMPLATE = "&units=";

    public static final String WEATHER_ERROR_CODE = "Error";
    public static final String PREVIOUS_CITY = "prev_city";

    //<<Private constants!>>
    private static final String USER_AGENT = "User-Agent";
    private static final String BROWSER_TYPE = "Mozilla/5.0";
    private static final String REQUEST_METHOD = "GET";


    private final static String API_KEY = "5b5375e5f95b02ad0553a181b2dd9857";
    public static final String DEFAULT_CITY = "Kyiv";
    public static final Weather.Units DEFAULT_UNITS = Weather.Units.Celsius;
    //<< >>

    private String city;
    private String api_key;
    private String api_string;
    private String units;

    private String prev_wright_city;


    private String temperature;
    private String temperature_max;
    private String temperature_min;
    private String icon_name;
    private String pressure;


    public OpenWeatherLight(){
        this(DEFAULT_CITY, API_KEY, DEFAULT_UNITS);
    }

    public OpenWeatherLight(String city){
        this(city, API_KEY, DEFAULT_UNITS);
    }

    public OpenWeatherLight(String city, Weather.Units units){
        this(city, API_KEY, units);
    }

    public OpenWeatherLight(String city, String key, Weather.Units units){
        setCity(city);
        setApiKey(key);
        setUnits(units);
    }

    public void setCity(String city){
        this.city = city;
        createRequest();
    }

    public void setUnits(Weather.Units units){
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

    public void setApiKey(String api_k){
        api_key = api_k;
        createRequest();
    }



    private void createRequest(){
        api_string = API_JSON + CITY_TEMPLATE + city  + "&" + APIKEY_TEMPLATE + api_key + "&" + UNITS_TEMPLATE + units;
    }


    @Override
    public String getTemperature() {
        return temperature;
    }

    @Override
    public WeatherModel calculate() {
        if(city == "" || city == null)
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

            String temp = main_obj.get("temp").toString();
            String max_tmp = main_obj.get("temp_max").toString();
            String min_tmp = main_obj.get("temp_min").toString();
            pressure = main_obj.get("pressure").toString();

            int maxTrim = StringCalc.countMaxTrim(new String[]{temp,max_tmp,min_tmp,pressure});
            int trim = 4 < maxTrim ? 4 : maxTrim;


            icon_name = jobj.get("icon").toString();
            temperature = temp.substring(0,trim) + " °C";
            temperature_max = max_tmp.substring(0,trim) + " °C";
            temperature_min = min_tmp.substring(0,trim) + " °C";
            prev_wright_city = city;

            return this;
        }catch (SecurityException | ParseException | IOException | NullPointerException pex){
            for (StackTraceElement el : pex.getStackTrace())
                Log.e(WEATHER_ERROR_CODE, el.toString());
            return null;
        }
    }

    public String getPressure() {
        return pressure;
    }

    @Override
    public String getIconName() {
        return icon_name;
    }

    public String getIcon_name() {
        return icon_name;
    }

    public String getTemperature_min() {
        return temperature_min;
    }

    public String getTemperature_max() {
        return temperature_max;
    }
}
