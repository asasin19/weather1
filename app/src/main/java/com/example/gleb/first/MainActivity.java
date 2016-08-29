package com.example.gleb.first;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gleb.first.Weather.Weather;
import com.example.gleb.first.Weather.WeatherInterface;
import com.example.gleb.first.cache.Cacher;
import com.example.gleb.first.config.Configuration;
import com.example.gleb.first.service.NotificationService;

import java.util.Locale;
import java.util.Properties;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements WeatherInterface {
    public static final String CONFIG_CITY = "City";
    public static final String CONFIG_TEMPERATURE = "Temp";
    public static final String CONFIG_MIN_TEMPERATURE = "MinTemp";
    public static final String CONFIG_MAX_TEMPERATURE = "MaxTemp";
    public static final String CONFIG_PRESSURE = "Pressure";
    public static final String CONFIG_ICON_NAME = "IconName";
    public static final String CONFIG_LOCALE = "Locale";
    public static final String CONFIG_NOTIFICATION_STATE = "Notification State";

    public static final String FOLDER_CONFIG = "Main";


    public static final int RESULT_CONFIGURATIONS_OK = 100;
    public static final int RESULT_APPLICATION_EXIT = 9999;

    //Dynamic views
    private TextView minTempView;
    private TextView maxTempView;
    private TextView tempView;
    private TextView pressureView;
    private ImageView image;

    private EditText cityLine;
    //

    //Static views
    private TextView minTempText;
    private TextView maxTempText;
    private TextView pressureText;
    //

    private Weather weather;
    private ServiceConnection serviceConnection;
    private Menu menu;

    private String prev_wright_city;
    private String icon_name_weather;
    private boolean notifiaction_active;
    private NotificationService service;


    public static final long UPDATE_FREQ = 80000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) { service = ((NotificationService.MyBinder) iBinder).getService();}
            @Override
            public void onServiceDisconnected(ComponentName componentName) {}};


        //Dynamic views init
        minTempView = (TextView) findViewById(R.id.minTempView);
        maxTempView = (TextView) findViewById(R.id.maxTempView);
        tempView = (TextView) findViewById(R.id.tempView);
        pressureView = (TextView) findViewById(R.id.pressureView);
        image = (ImageView)findViewById(R.id.weatherImage);

        cityLine = (EditText) findViewById(R.id.cityLineEdit);
        //

        //Static views init
        minTempText = (TextView) findViewById(R.id.minTempText);
        maxTempText = (TextView) findViewById(R.id.maxTempText);
        pressureText = (TextView) findViewById(R.id.pressureText);
        //

        weather = new Weather(getApplicationContext() , handlerInit());

        cityLine.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    view.clearFocus();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    return true;
                }
                return false;
            }
        });

        cityLine.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    Toast.makeText(getApplicationContext(), "focus lose", Toast.LENGTH_SHORT).show();
                    if (cityLine.getText().length() < 1) {
                        return;
                    }
                    weather.setCity(cityLine.getText().toString());
                    new Thread(weather).start();
                }
            }
        });

        new AsyncTask<Void, Void, Properties>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Properties doInBackground(Void... v) {
                return Cacher.readConfig(FOLDER_CONFIG);
            }

            @Override
            protected void onPostExecute(Properties properties) {
                super.onPostExecute(properties);
                cityLine.setText(properties.getProperty(CONFIG_CITY));
                tempView.setText(properties.getProperty(CONFIG_TEMPERATURE));
                minTempView.setText(properties.getProperty(CONFIG_MIN_TEMPERATURE));
                maxTempView.setText(properties.getProperty(CONFIG_MAX_TEMPERATURE));
                pressureView.setText(properties.getProperty(CONFIG_PRESSURE));
                icon_name_weather = properties.getProperty(CONFIG_ICON_NAME);
                new PictureRenderer(image).execute(icon_name_weather);
                String tmp = properties.getProperty(CONFIG_LOCALE);
                if(!Locale.getDefault().getLanguage().equals(tmp) && tmp != "" && tmp != null){
                    changeLocale(new Locale(tmp));
                    initMultiLanguage();
                }
                tmp = properties.getProperty(CONFIG_NOTIFICATION_STATE);
                if(tmp == null || tmp.equals(""))
                    notifiaction_active = true;
                else
                    notifiaction_active = Boolean.parseBoolean(properties.getProperty(CONFIG_NOTIFICATION_STATE));
                weather.setCity(cityLine.getText().toString());
            }
        }.execute();

        startService(new Intent(getApplicationContext(), NotificationService.class));
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(weather, 10, UPDATE_FREQ);

        bindService(new Intent(getApplicationContext(), NotificationService.class), serviceConnection, BIND_AUTO_CREATE);
    }

    private Handler handlerInit(){
        return new Handler(){
            @Override
            public void handleMessage(Message message){
                Bundle data = message.getData();


                if(!data.containsKey("Error")) {

                    minTempView.setText(data.getString(CONFIG_MIN_TEMPERATURE));
                    maxTempView.setText(data.getString(CONFIG_MAX_TEMPERATURE));
                    tempView.setText(data.getString(CONFIG_TEMPERATURE));
                    pressureView.setText(data.getString(CONFIG_PRESSURE));

                }else {
                    cityLine.setText(data.getString("prev_city"));
                    Toast.makeText(getApplicationContext(), data.getString("Error"), Toast.LENGTH_LONG);
                }
                String icon_name = data.getString(CONFIG_ICON_NAME);
                Log.d(Cacher.CACHE_LOG_TAG, "Icon name = " + icon_name);
                icon_name_weather = icon_name;
                new PictureRenderer(image).execute(icon_name);

            }

        };
    }

    private void initMultiLanguage(){
        minTempText.setText(getString(R.string.Min_tmp));
        maxTempText.setText(getString(R.string.Max_tmp));
        pressureText.setText(getString(R.string.Pressure));
        menu.getItem(0).setTitle(getString(R.string.menu_item_settings));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public void setWeather(String d_text, String w_text) {
        minTempView.setText(d_text);
        maxTempView.setText(w_text);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.Settings:
                Bundle bundle = new Bundle();
                Intent intent = new Intent(this, Configuration.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, RESULT_CONFIGURATIONS_OK);
                break;
            case R.id.Exit:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_CONFIGURATIONS_OK: {
                initMultiLanguage();
                Toast.makeText(this, "Setting closed", Toast.LENGTH_SHORT).show();
                break;
            }
            case RESULT_APPLICATION_EXIT: {
                finish();
                break;
            }
        }

        Bundle bundle = data.getExtras();
        Boolean state = bundle.getBoolean(CONFIG_NOTIFICATION_STATE);
        Log.d(CONFIG_NOTIFICATION_STATE, state.toString());
        if(state != null) {
            notifiaction_active = state;
            service.activeService(notifiaction_active);
            Cacher.cacheConfig(FOLDER_CONFIG, CONFIG_NOTIFICATION_STATE, notifiaction_active + "");
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        Properties properties = new Properties();
        properties.setProperty(CONFIG_CITY, cityLine.getText().toString());
        properties.setProperty(CONFIG_TEMPERATURE, tempView.getText().toString());
        properties.setProperty(CONFIG_MIN_TEMPERATURE, minTempView.getText().toString());
        properties.setProperty(CONFIG_MAX_TEMPERATURE, maxTempView.getText().toString());
        properties.setProperty(CONFIG_PRESSURE, pressureView.getText().toString());
        properties.setProperty(CONFIG_ICON_NAME, icon_name_weather);
        properties.setProperty(CONFIG_LOCALE, Locale.getDefault().getLanguage());
        properties.setProperty(CONFIG_NOTIFICATION_STATE, notifiaction_active + "");
        super.onDestroy();
        Cacher.cacheConfig(FOLDER_CONFIG, properties);
        Cacher.saveAllConfigs();

        unbindService(serviceConnection);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void changeLocale(Locale locale){
        Locale.setDefault(locale);
        android.content.res.Configuration conf = new android.content.res.Configuration();
        conf.locale = locale;
        getBaseContext().getResources().updateConfiguration(conf, getBaseContext().getResources().getDisplayMetrics());

    }
}
