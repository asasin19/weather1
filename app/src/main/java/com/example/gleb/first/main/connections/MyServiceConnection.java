package com.example.gleb.first.main.connections;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.gleb.first.service.NotificationService;

/**
 * Created by Gleb on 14.09.2016.
 */
public class MyServiceConnection implements ServiceConnection {
    private NotificationService service;
    private Context context;

    public MyServiceConnection(Context context){
        this.context = context;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = ((NotificationService.MyBinder) iBinder).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }

    public NotificationService getService(){
        if(service == null){
            context.startService(new Intent(context, NotificationService.class));
            context.bindService(new Intent(context, NotificationService.class), this, context.BIND_AUTO_CREATE);
        }
        return service;
    }

    public void unbindService(){
        context.unbindService(this);
    }
}
