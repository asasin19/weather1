package com.example.gleb.first.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.gleb.first.MainActivity;
import com.example.gleb.first.R;
import com.example.gleb.first.Weather.Weather;
import com.example.gleb.first.cache.Cacher;

import java.util.Timer;

/**
 * Created by Gleb on 25.08.2016.
 */
public class NotificationService extends Service {

    public static final int NOTIFICATION_WEATHER_ID = 9999001;
    public static final String SERVICE_TAG = "SERVICEDEBUGTAG";

    private long refresh_time = 120000;

    private NotificationManager manager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(SERVICE_TAG, "onCreate");
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Toast.makeText(this, "Service started", Toast.LENGTH_LONG);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Weather(this, new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String icon_name = bundle.getString(MainActivity.CONFIG_ICON_NAME);
                Log.d(SERVICE_TAG, "MessageHandled!");
                Notification.Builder builder = new Notification.Builder(getApplicationContext());
                builder.setContentTitle("Weather change state!")
                        .setAutoCancel(true)
                        .setTicker("Hello, are u see weather today?")
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_notify_weather)
                        .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT));

                Bitmap image = null;
                if((image = Cacher.readImage(icon_name)) == null)
                    return;
                builder.setLargeIcon(image);

                Notification nf = builder.getNotification();
                manager.notify(NOTIFICATION_WEATHER_ID, nf);


            }
        }, "Kyiv"), refresh_time, refresh_time);
    }

    @Override
    public void onDestroy() {
        Log.d(SERVICE_TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(SERVICE_TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }
}
