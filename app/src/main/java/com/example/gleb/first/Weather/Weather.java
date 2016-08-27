package com.example.gleb.first.Weather;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.example.gleb.first.MainActivity;
import com.example.gleb.first.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.TimerTask;

/**
 * Created by Gleb on 20.08.2016.
 */
public class Weather extends TimerTask {

    public static final String API_JSON = "http://api.openweathermap.org/data/2.5/weather?";
    private final static String CITY_TEMPLATE = "q=";
    private final static String APIKEY_TEMPLATE = "appid=";
    private final static String UNITS_TEMPLATE = "&units=";

    private Context context;

    enum Units{
        Celsius,
        Kelvin,
        Fahrenheit
    }

    private final static String API_KEY = "5b5375e5f95b02ad0553a181b2dd9857";

    private String city;
    private String api_key;
    private String api_string;
    private String units;

    private String prev_wright_city;

    private Handler handle;

    public Weather(Context context , Handler handle, String city){
        this(context, handle);
        setCity(city);
    }

    public Weather(Context context , Handler handle){
        setUnits(Units.Celsius);

        city = context.getString(R.string.saved_city);
        api_key = API_KEY;
        createRequest();

        this.context = context;
        this.handle = handle;
    }

    @Override
    public void run() {
        if(city == "" || city == null)
            return;
        Bundle data = new Bundle();
        try {
            String tmp;
            URL url = new URL(api_string);
            HttpURLConnection con =  (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent" , "Mozilla/5.0");
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
            String pressure = main_obj.get("pressure").toString();

            int maxTrim = countMaxTrim(new String[]{temp,max_tmp,min_tmp,pressure});
            int trim = 4 < maxTrim ? 4 : maxTrim;


            data.putString(MainActivity.CONFIG_ICON_NAME, jobj.get("icon").toString());
            data.putString(MainActivity.CONFIG_TEMPERATURE ,temp.substring(0,trim) + " °C");
            data.putString(MainActivity.CONFIG_PRESSURE,pressure.substring(0,trim));
            data.putString(MainActivity.CONFIG_MAX_TEMPERATURE, max_tmp.substring(0,trim) + " °C");
            data.putString(MainActivity.CONFIG_MIN_TEMPERATURE, min_tmp.substring(0,trim) + " °C");
            prev_wright_city = new String(city);

        }catch (SecurityException | ParseException | IOException | NullPointerException pex){
            data.putString("Error", pex.getMessage());
            data.putString("prev_city", prev_wright_city);
        } finally {
            Message h_msg = handle.obtainMessage();
            h_msg.setData(data);
            handle.sendMessage(h_msg);
        }
    }

    private int countMaxTrim(String[] strings){
        int min_lenght = strings[0].length();
        for (String str : strings){
            min_lenght = min_lenght > str.length() ? str.length() : min_lenght;
        }
        return min_lenght;
    }

    private void createRequest(){
        api_string = API_JSON + CITY_TEMPLATE + city  + "&" + APIKEY_TEMPLATE + api_key + "&" + UNITS_TEMPLATE + units;
    }

    public void setCity(String city){
        this.city = city;
        createRequest();
    }

    public void setUnits(Units units){
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
    }

    public void setApiKey(String api_k){
        api_key = api_k;
        createRequest();
    }
}
