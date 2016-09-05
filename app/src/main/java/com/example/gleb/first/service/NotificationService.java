package com.example.gleb.first.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.gleb.first.ImageLoader;
import com.example.gleb.first.MainActivity;
import com.example.gleb.first.R;
import com.example.gleb.first.Weather.Weather;
import com.example.gleb.first.Weather.context.OpenWeatherLight;

import java.util.Timer;

/**
 * Created by Gleb on 25.08.2016.
 */
public class NotificationService extends Service{

    public static final int NOTIFICATION_WEATHER_ID = 9999001;
    public static final String SERVICE_TAG = "SERVICEDEBUGTAG";

    private long refresh_time = 120000;

    private NotificationManager manager;
    private Timer timer;
    private ImageLoader loader;
    private Weather weather;


    private MyBinder binder = new MyBinder();

    private String city;

    private boolean serviceState;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        offNotations();
        Log.d(SERVICE_TAG, "onBind");
        return binder;

    }

    private void offNotations(){
        timer.cancel();
        timer = null;
    }

    private void onNotations(){
        try {
            getTimer().scheduleAtFixedRate(getWeather(), refresh_time, refresh_time);
        }catch (IllegalStateException ex){

        }
    }

    public void activeService(boolean state){
        serviceState = state;
    }

    public void setCity(String city){
        this.city = city;
        weather.setCity(city);
    }

    public Timer getTimer(){
        if(timer != null)
            return timer;
        return (timer = new Timer());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        weather = getWeather();
        loader = new ImageLoader();

        Log.d(SERVICE_TAG, "onCreate");
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Toast.makeText(this, "Service started", Toast.LENGTH_LONG);

        getTimer().scheduleAtFixedRate(getWeather(), refresh_time, refresh_time);
    }

    private Handler getHandler() {
        return new Handler() {
            private String last_icon = "";

            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String icon_name = bundle.getString(MainActivity.CONFIG_ICON_NAME);

                if(last_icon.equals(icon_name))
                    return;
                Bitmap image = loader.getBitmap(icon_name);

                Notification.Builder builder = new Notification.Builder(getApplicationContext());
                builder.setContentTitle(getString(R.string.service_notification_title))
                        .setAutoCancel(true)
                        .setTicker(getString(R.string.service_notification_ticker))
                        .setContentText(getString(R.string.service_notification_contentText) + " " + bundle.getString(MainActivity.CONFIG_TEMPERATURE))
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_notify_weather_2)
                        .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT));

                image = Bitmap.createScaledBitmap(image, 300, 300 ,true);
                builder.setLargeIcon(image);


                Notification nf = builder.getNotification();
                manager.notify(NOTIFICATION_WEATHER_ID, nf);
                last_icon = icon_name;


            }
        };
    }

    private Weather getWeather(){
        if(weather != null)
            return weather;
        return (weather = new Weather(getApplicationContext(),getHandler(),new OpenWeatherLight(), "Kyiv"));
    }

    @Override
    public void onDestroy() {
        Log.d(SERVICE_TAG, "onDestroy");
        offNotations();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(SERVICE_TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(!serviceState)
            stopSelf(NOTIFICATION_WEATHER_ID);
        else
            onNotations();
        Log.d(SERVICE_TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    public class MyBinder extends Binder{
        public NotificationService getService(){
            return NotificationService.this;
        }
    }
}
