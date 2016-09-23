package com.example.gleb.first.place;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import com.example.gleb.first.cache.Cacher;
import com.example.gleb.first.place.dummy.DummyContent;
import com.example.gleb.first.weatherpack.Weather;
import com.example.gleb.first.weatherpack.context.OpenWeatherLight;
import com.example.gleb.first.weatherpack.models.WeatherCityModel;
import com.example.gleb.first.weatherpack.models.WeatherSimpleModel;

import java.util.*;

/**
 * Created by Gleb on 23.09.2016.
 */
public class SavedPlace implements Iterable<DummyContent.DummyItem>{
    public static final String LIST_NAME = "SavedPlacesList";
    public static final long UPDATE_FREQ_MS = 60000;
    public static final long MICRO_DELAY_MS = 10;
    public static final long NO_DELAY_MS = 0;

    private static SavedPlace savedPlace = new SavedPlace();
    private List<String> citiesList;
    private ArrayList<DummyContent.DummyItem> items;

    private Timer timer;
    private Handler handler;

    private SavedPlace(){
        citiesList = Cacher.readList(LIST_NAME);
        timer = new Timer();
        timer.scheduleAtFixedRate(task , UPDATE_FREQ_MS, UPDATE_FREQ_MS);
    }

    public void addPlace(String city){
        citiesList.add(city);
        Cacher.cacheList(LIST_NAME, citiesList, false);
        new Thread(task).start();
    }

    public void removePlace(String city){
        citiesList.remove(city);
        Cacher.cacheList(LIST_NAME, citiesList, false);
        new Thread(task).start();
    }

    public void removePlace(int i){
        citiesList.remove(i);
        Cacher.cacheList(LIST_NAME, citiesList, false);
        new Thread(task).start();
    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }


    public static SavedPlace init(){
        return savedPlace;
    }

    @Override
    public Iterator<DummyContent.DummyItem> iterator() {
        return items.iterator();
    }

    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            items = new ArrayList<DummyContent.DummyItem>(citiesList.size());
            int id = 0;
            for (String city : citiesList) {
                OpenWeatherLight openWeatherLight = new OpenWeatherLight(city);
                WeatherSimpleModel model = (WeatherSimpleModel) openWeatherLight.calculate();
                items.add(new DummyContent.DummyItem(String.valueOf(id++), new DummyContent.ItemContent(city,
                        model.getIconName(), model.getTemperature(), model.getTemperature_min(),
                        model.getTemperature_max()), "Item"));
            }
            if(id == 0)
                return;
            Parcelable[] parcelables = new Parcelable[items.size()];
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putParcelableArray("Items", items.toArray(parcelables));
            message.setData(bundle);
            handler.sendMessage(message);
        }
    };
}
