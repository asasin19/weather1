package com.example.gleb.first.place;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import com.example.gleb.first.cache.Cacher;
import com.example.gleb.first.place.dummy.DummyContent;
import com.example.gleb.first.weatherpack.Weather;
import com.example.gleb.first.weatherpack.context.OpenWeatherLight;
import com.example.gleb.first.weatherpack.models.WeatherCityModel;
import com.example.gleb.first.weatherpack.models.WeatherSimpleModel;

import java.util.*;


public class SavedPlace implements Iterable<DummyContent.DummyItem>{
    public static final String SAVED_PLACE_DEBUG_TAG = "SAVEDPLACEDEBUG";

    public static final String LIST_NAME = "SavedPlacesList";
    public static final long UPDATE_FREQ_MS = 60000;
    public static final long MICRO_DELAY_MS = 10;
    public static final long NO_DELAY_MS = 0;

    public static final String OPERATION_ID = "OperationID";

    private static int operation_id;

    private static SavedPlace savedPlace = new SavedPlace();
    private Set<String> citiesList;
    private ArrayList<DummyContent.DummyItem> items;

    private Map<Integer, List<DummyContent.DummyItem>> contentContainer;

    private Timer timer;
    private Handler handler;

    private boolean listChanged;

    private SavedPlace(){
        operation_id = 0;
        listChanged = false;
        citiesList = Cacher.readList(LIST_NAME);
        if(citiesList == null)
            citiesList = new LinkedHashSet<>();
        contentContainer = new HashMap<Integer, List<DummyContent.DummyItem>>();
        timer = new Timer();
        timer.scheduleAtFixedRate(task , UPDATE_FREQ_MS, UPDATE_FREQ_MS);
    }

    public void addPlace(String city){
        citiesList.add(city);
        listChanged = true;
        new Thread(task).start();
    }

    public void removePlace(String city){
        citiesList.remove(city);
        listChanged = true;
        new Thread(task).start();
    }

    public void removePlace(int i){
        citiesList.remove(i);
        listChanged = true;
        new Thread(task).start();
    }

    public void setHandler(Handler handler){
        this.handler = handler;
        new Thread(task).start();
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
            Log.d(SAVED_PLACE_DEBUG_TAG, "!!!START TASK!!!");
            if(handler == null)
                return;
            items = new ArrayList<DummyContent.DummyItem>(citiesList.size());
            int id = 0;
            for (String city : citiesList) {
                OpenWeatherLight openWeatherLight = new OpenWeatherLight(city);
                WeatherSimpleModel model = (WeatherSimpleModel) openWeatherLight.calculate();
                if(model == null) {
                    citiesList.remove(city);
                    continue;
                }
                items.add(new DummyContent.DummyItem(String.valueOf(id++), new DummyContent.ItemContent(city,
                        model.getIconName(), model.getTemperature(), model.getTemperature_min(),
                        model.getTemperature_max()), "Item"));
            }
            contentContainer.put(operation_id, items);
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt(OPERATION_ID, operation_id++);
            message.setData(bundle);
            /*
            Parcelable[] parcelables = new Parcelable[items.size()];
            bundle.putParcelableArray("Items", items.toArray(parcelables));
            message.setData(bundle);
            */

            Log.d(SAVED_PLACE_DEBUG_TAG, "!!!END TASK!!!");
            if(listChanged) {
                Cacher.cacheList(LIST_NAME, citiesList, false);
                listChanged = false;
            }
            handler.sendMessage(message);
        }
    };

    public List<DummyContent.DummyItem> getContentItem(int operation_id){
        List<DummyContent.DummyItem> items = contentContainer.get(operation_id);
        contentContainer.remove(operation_id);
        return items;
    }
}
