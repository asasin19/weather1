package com.example.gleb.first.main;

import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gleb.first.MenuItemsList;
import com.example.gleb.first.PictureRenderer;
import com.example.gleb.first.R;
import com.example.gleb.first.location.MyLocation;
import com.example.gleb.first.place.PlaceActivity;
import com.example.gleb.first.weatherpack.Weather;
import com.example.gleb.first.weatherpack.context.OpenWeatherLight;
import com.example.gleb.first.weatherpack.context.OpenWeatherLightByCoord;
import com.example.gleb.first.cache.Cacher;
import com.example.gleb.first.config.Configuration;
import com.example.gleb.first.config.preference.PreferenceActivity;
import com.example.gleb.first.google.map.MapsActivity;
import com.example.gleb.first.main.connections.MyServiceConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Timer;

/**
 * Created by Gleb on 01.09.2016.
 */
public class MainActivityNav extends AppCompatActivity {

    public static final String CONFIG_CITY = "City";
    public static final String CONFIG_TEMPERATURE = "Temp";
    public static final String CONFIG_MIN_TEMPERATURE = "MinTemp";
    public static final String CONFIG_MAX_TEMPERATURE = "MaxTemp";
    public static final String CONFIG_PRESSURE = "Pressure";
    public static final String CONFIG_ICON_NAME = "IconName";
    public static final String CONFIG_LOCALE = "Locale";
    public static final String CONFIG_NOTIFICATION_STATE = "Notification State";
    public static final String CONFIG_BY_LOCATION_STATE = "Bylocation state";

    public static final String FOLDER_CONFIG = "Main";

    public static final String STRING_ERROR = "Error";


    public static final int RESULT_CONFIGURATIONS_OK = 100;
    public static final int RESULT_APPLICATION_EXIT = 9999;
    public static final int RESULT_PLACE_CHOISE_OK = 101;

    public static final int REQEST_CODE_PLACE = 10;

    public static final long UPDATE_FREQ = 80000;
    public static final long UPDATE_DELAY = 10;

    //Dynamic views
    private TextView minTempView;
    private TextView maxTempView;
    private TextView tempView;
    private TextView pressureView;
    private ImageView image;
    private SubMenu subMenu;
    private NavigationView navigationView;
    private EditText cityLine;
    //

    //Static views
    private TextView minTempText;
    private TextView maxTempText;
    private TextView pressureText;
    private TextView navHeaderText;
    private Animation animation;
    //

    private Menu menu;
    private SharedPreferences sharedPreferences;


    private Timer timer;
    private Weather weather;

    private MyServiceConnection serviceConnection;
    private InputMethodManager imputManager;


    //Containers
    private List<MenuItem> items;
    private MenuItemsList citiesList;
    //end

    //Java native types
    private String prev_wright_city;
    private String icon_name_weather;

    private boolean notifiaction_active;
    private boolean old_menu_active;
    private boolean geolocationState;
    //end

    //Inner classes
    private MyLocation location;
    private Listeners listenersInitiator;
    private MainTask mainTasks;
    //end



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Native
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //end

        //Init all fields, view elements and set listeners
        initFields();
        //end

        //Init service connection and bind
        getServiceConnection();
        //end;


        //Read config informaton from cahce in external thread
        mainTasks.getCachTask().execute();
        //end

        //Start timer
        timer.scheduleAtFixedRate(weather, UPDATE_DELAY, UPDATE_FREQ);
        //end

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.settings_item_settings:
                Bundle bundle = new Bundle();
                bundle.putBoolean(MainActivityNav.CONFIG_BY_LOCATION_STATE, geolocationState);
                bundle.putBoolean(MainActivityNav.CONFIG_NOTIFICATION_STATE, notifiaction_active);
                if(old_menu_active) {
                    Intent intent = new Intent(getApplicationContext(), Configuration.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, RESULT_CONFIGURATIONS_OK);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), PreferenceActivity.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, RESULT_CONFIGURATIONS_OK);
                }
                break;

            case R.id.settings_item_settings_style:
                item.setChecked(!item.isChecked());
                old_menu_active = item.isChecked();
                break;
            case R.id.settings_item_exit:
                finish();
                break;

            case R.id.settings_item_weather_list:
                startActivityForResult(new Intent(getApplicationContext(), PlaceActivity.class), REQEST_CODE_PLACE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case RESULT_APPLICATION_EXIT: {
                finish();
            }

            case RESULT_CONFIGURATIONS_OK: {
                Bundle bundle = data.getExtras();
                initMultiLanguage();
                notifiaction_active = bundle.getBoolean(CONFIG_NOTIFICATION_STATE);
                geolocationState = bundle.getBoolean(CONFIG_BY_LOCATION_STATE);
                serviceConnection.getService().activeService(notifiaction_active);
                Cacher.cacheConfig(FOLDER_CONFIG, CONFIG_NOTIFICATION_STATE, String.valueOf(notifiaction_active));

                /*
                if(bundle.getBoolean(CONFIG_BY_LOCATION_STATE) != geolocationState) {
                    geolocationState = bundle.getBoolean(CONFIG_BY_LOCATION_STATE);
                    if(geolocationState)
                        weather.setByFirst(Weather.WeatherTypes.ByLocation) ;
                    else
                        weather.setByFirst(Weather.WeatherTypes.ByCity);

                    new Thread(weather).start();
                }
                location.setStatus(geolocationState);
                */

                Cacher.cacheConfig(FOLDER_CONFIG, CONFIG_BY_LOCATION_STATE, String.valueOf(geolocationState));
                break;
            }

            case MapsActivity.RESULT_RETURNED_LOCATION:{
                Bundle bundle = data.getExtras();
                weather.setByFirst(Weather.WeatherTypes.ByLocation) ;
                Location tmp = new Location("");
                tmp.setLatitude(bundle.getDouble(MapsActivity.GOOGLEMAP_LATITUDE));
                tmp.setLongitude(bundle.getDouble(MapsActivity.GOOGLEMAP_LONGITUDE));
                Toast.makeText(getApplicationContext(), tmp.toString(), Toast.LENGTH_SHORT).show();
                weather.setLocation(tmp);
                new Thread(weather).start();
                break;
            }

            case RESULT_PLACE_CHOISE_OK:
                cityLine.setText(data.getExtras().getString(CONFIG_CITY));
                startWeather();
                break;
        }



        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Properties properties = new Properties();
        properties.setProperty(CONFIG_CITY, cityLine.getText().toString());
        properties.setProperty(CONFIG_TEMPERATURE, tempView.getText().toString());
        properties.setProperty(CONFIG_MIN_TEMPERATURE, minTempView.getText().toString());
        properties.setProperty(CONFIG_MAX_TEMPERATURE, maxTempView.getText().toString());
        properties.setProperty(CONFIG_PRESSURE, pressureView.getText().toString());
        if(icon_name_weather != null)
            properties.setProperty(CONFIG_ICON_NAME, icon_name_weather);
        properties.setProperty(CONFIG_LOCALE, Locale.getDefault().getLanguage());
        properties.setProperty(CONFIG_NOTIFICATION_STATE, String.valueOf(notifiaction_active));
        properties.setProperty(CONFIG_BY_LOCATION_STATE, String.valueOf(geolocationState));
        super.onDestroy();
        Cacher.cacheConfig(FOLDER_CONFIG, properties);
        Cacher.saveAllConfigs();

        serviceConnection.unbindService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        location.setStatus(geolocationState);
        if(geolocationState)
            weather.setByFirst(Weather.WeatherTypes.ByLocation) ;
        else
            weather.setByFirst(Weather.WeatherTypes.ByCity);

        new Thread(weather).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        location.setStatus(false);
    }

    /*
    ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    ++++++++++++++++++++++++++++++++++++++++PRIVATE METHODS+++++++++++++++++++++++++++++++++++++++++
    ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
     */
    private void initFields(){
        initGeneralFields();
        initViewFields();

        //Init Navigation Menu,drawer and toolbar
        initNavigationMenu();
        //end

        setListenersInViews();
    }

    private void initGeneralFields(){
        //Init inner classes
        listenersInitiator = new Listeners();
        mainTasks = new MainTask();
        citiesList = new MenuItemsList();
        //end

        //Init weather
        weather = new Weather(getApplicationContext() , handlerInit(), new OpenWeatherLight());
        weather.addWeathersApi(new OpenWeatherLightByCoord());
        //end

        //init geolocation
        location = new MyLocation(getApplicationContext(), weather);
        //end

        //Init android service
        imputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //

        //Init java classes
        timer = new Timer();
        //end
    }

    private void initViewFields(){
        //Dynamic views init
        minTempView = (TextView) findViewById(R.id.minTempView);
        maxTempView = (TextView) findViewById(R.id.maxTempView);
        tempView = (TextView) findViewById(R.id.tempView);
        pressureView = (TextView) findViewById(R.id.pressureView);
        image = (ImageView)findViewById(R.id.weatherImage);

        cityLine = (EditText) findViewById(R.id.cityLineEdit);
        //end

        //Static views init
        minTempText = (TextView) findViewById(R.id.minTempText);
        maxTempText = (TextView) findViewById(R.id.maxTempText);
        pressureText = (TextView) findViewById(R.id.pressureText);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.wrong_city_animation);
        //end

    }

    private void setListenersInViews(){
        //Set listeners
        cityLine.setOnKeyListener(listenersInitiator.getOnKeyListener());
        cityLine.setOnFocusChangeListener(listenersInitiator.getOnFocusChangeListener());
        animation.setAnimationListener(listenersInitiator.getAnimationListener());
        navigationView.setNavigationItemSelectedListener(listenersInitiator.getNavigationItemSelectedListener());
        navigationView.setOnClickListener(listenersInitiator.getOnClickListener());
        //end
    }

    private void initNavigationMenu(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navHeaderText = (TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_menu_header_textView);
        subMenu = navigationView.getMenu().addSubMenu(R.id.context_menu_menuitem_group_lc, Menu.NONE, 101, "LastCities");

    }


    private void initMultiLanguage(){
        minTempText.setText(R.string.Min_tmp);
        maxTempText.setText(R.string.Max_tmp);
        pressureText.setText(R.string.Pressure);
        navHeaderText.setText(R.string.nav_menu_header_text);
        initMultiLanguageNavigationMenu();
        initMultiLanguageContextMenu();
    }

    private void initMultiLanguageNavigationMenu(){
        navigationView.getMenu().findItem(R.id.context_menu_findOnMap).setTitle(getString(R.string.navigation_menu_item_findOnMap));
        navigationView.getMenu().findItem(R.id.context_menu_share).setTitle(getString(R.string.navigation_menu_item_share));
        subMenu.setHeaderTitle(R.string.navigation_category_name);
    }

    private void initMultiLanguageContextMenu(){
        menu.findItem(R.id.settings_item_exit).setTitle(R.string.menu_item_exit);
        menu.findItem(R.id.settings_item_settings).setTitle(R.string.menu_item_settings);
        menu.findItem(R.id.settings_item_settings_style).setTitle(R.string.menu_item_settings_style);
    }

    private void changeLocale(Locale locale){
        Locale.setDefault(locale);
        android.content.res.Configuration conf = new android.content.res.Configuration();
        conf.locale = locale;
        getBaseContext().getResources().updateConfiguration(conf, getBaseContext().getResources().getDisplayMetrics());

    }

    private void addToNavigationList(String item){
        citiesList.add(item);
        subMenu.clear();
        for (String item1 : citiesList.getItems())
            subMenu.add(R.id.context_menu_menuitem_group_lc, Menu.NONE, Menu.NONE, item1);
    }

    private void startWeather(){
        weather.setByFirst(Weather.WeatherTypes.ByCity);
        geolocationState = false;
        weather.setCity(cityLine.getText().toString());
        new Thread(weather).start();
    }

    private void startWeatherByLocation(){
        weather.setByFirst(Weather.WeatherTypes.ByLocation);
        geolocationState = true;
    }

    /*
    ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    ++++++++++++++++++++++++++++++++++++++++++++++END+++++++++++++++++++++++++++++++++++++++++++++++
    ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
     */

    /*
    ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    ++++++++++++++++++++++++++++++++++++++++ABSTRACT GETTERS++++++++++++++++++++++++++++++++++++++++
    ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
     */

    private ServiceConnection getServiceConnection(){
        if(serviceConnection == null) {
            serviceConnection = new MyServiceConnection(getApplicationContext());
            serviceConnection.getService();
        }
        return serviceConnection;
    }

    private Handler handlerInit(){
        return new Handler(){
            @Override
            public void handleMessage(Message message){
                Bundle data = message.getData();

                if(!data.containsKey(STRING_ERROR)) {

                    minTempView.setText(data.getString(CONFIG_MIN_TEMPERATURE));
                    maxTempView.setText(data.getString(CONFIG_MAX_TEMPERATURE));
                    tempView.setText(data.getString(CONFIG_TEMPERATURE));
                    pressureView.setText(data.getString(CONFIG_PRESSURE));

                    if(data.containsKey(CONFIG_CITY))
                        cityLine.setText(data.getString(CONFIG_CITY));
                    else
                        prev_wright_city = cityLine.getText().toString();

                }else {
                    //cityLine.startAnimation(animation);
                    Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                }
                String icon_name = data.getString(CONFIG_ICON_NAME);
                icon_name_weather = icon_name;
                new PictureRenderer(image).execute(icon_name);

            }

        };
    }
    /*
    ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    ++++++++++++++++++++++++++++++++++++++++++++++END+++++++++++++++++++++++++++++++++++++++++++++++
    ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
     */

    /*
    ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    ++++++++++++++++++++++++++++++++++++++++++INNER CLASSES+++++++++++++++++++++++++++++++++++++++++
    ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
     */

    /*
    *LISTENERS
    * Container for listeners.
     */
    private class Listeners{
        private NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener;
        private View.OnClickListener onClickListener;
        private View.OnKeyListener onKeyListener;
        private View.OnFocusChangeListener onFocusChangeListener;
        private Animation.AnimationListener animationListener;

        public Animation.AnimationListener getAnimationListener() {
            if(animationListener == null)
                animationListener = new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        cityLine.setText(prev_wright_city);
                        startWeather();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };
            return animationListener;
        }

        public NavigationView.OnNavigationItemSelectedListener getNavigationItemSelectedListener(){
            if(navigationItemSelectedListener == null)
                navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getGroupId()){
                            case R.id.context_menu_menuitem_group_lc:
                                cityLine.setText(item.getTitle());
                                startWeather();
                                break;

                            case R.id.context_menu_menuitem_group_helpful:
                                switch (item.getItemId()){
                                    case R.id.context_menu_findOnMap:
                                        startActivityForResult(new Intent(getApplicationContext() , MapsActivity.class), MapsActivity.RESULT_RETURNED_LOCATION);
                                        break;

                                    case R.id.context_menu_share: {
                                        String text = String.format(getString(R.string.share_text), tempView.getText(), cityLine.getText());
                                        startActivity(new Intent()
                                                .setAction(Intent.ACTION_SEND)
                                                .putExtra(Intent.EXTRA_TEXT, text)
                                                .setType("text/plain"));
                                    }
                                        break;
                                }
                                break;
                        }


                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }
                };
            return navigationItemSelectedListener;
        }

        public View.OnClickListener getOnClickListener(){
            if(onClickListener == null)
                onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cityLine.setText(((MenuItem)view).getTitle());
                    }
                };
            return onClickListener;
        }

        public View.OnKeyListener getOnKeyListener(){
            if(onKeyListener == null)
                onKeyListener = new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                            view.clearFocus();
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            return true;
                        }
                        return false;
                    }
                };
            return onKeyListener;
        }

        public View.OnFocusChangeListener getOnFocusChangeListener(){
            if(onFocusChangeListener == null)
                onFocusChangeListener = new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if(!b) {
                            //Toast.makeText(getApplicationContext(), "focus lose", Toast.LENGTH_SHORT).show();
                            if (cityLine.getText().length() < 1) {
                                return;
                            }
                            addToNavigationList(cityLine.getText().toString());
                            startWeather();
                            imputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                };
            return onFocusChangeListener;
        }

    }
    /*
    *END INNER CLASS
     */

    /*
    *MAINTASK
    * Container for asynctasks.
     */
    private class MainTask{

        public AsyncTask<Void, Void, Properties> getCachTask(){
            return new AsyncTask<Void, Void, Properties>(){
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
                    cityLine.setText(properties.getProperty(CONFIG_CITY) == null ? "Kyiv": properties.getProperty(CONFIG_CITY) );
                    prev_wright_city = cityLine.getText().toString();
                    tempView.setText(properties.getProperty(CONFIG_TEMPERATURE));
                    minTempView.setText(properties.getProperty(CONFIG_MIN_TEMPERATURE));
                    maxTempView.setText(properties.getProperty(CONFIG_MAX_TEMPERATURE));
                    pressureView.setText(properties.getProperty(CONFIG_PRESSURE));
                    icon_name_weather = properties.getProperty(CONFIG_ICON_NAME);
                    new PictureRenderer(image).execute(icon_name_weather);
                    String tmp = properties.getProperty(CONFIG_LOCALE);
                    if(!Locale.getDefault().getLanguage().equals(tmp) && tmp != null && !tmp.equals("") ){
                        changeLocale(new Locale(tmp));
                        initMultiLanguage();
                    }

                    tmp = properties.getProperty(CONFIG_NOTIFICATION_STATE);
                    if(tmp == null || tmp.equals("") || (notifiaction_active = Boolean.parseBoolean(tmp))){}

                    tmp = properties.getProperty(CONFIG_BY_LOCATION_STATE);
                    if(tmp == null || tmp.equals("") || (geolocationState = Boolean.parseBoolean(tmp))){}

                    weather.setCity(cityLine.getText().toString());

                    location.setStatus(geolocationState);
                    if(geolocationState)
                        startWeatherByLocation();
                    else
                        startWeather();
                }
            };
        }
    }
    /*
    *END INNER CLASS
     */


    /*
    ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    ++++++++++++++++++++++++++++++++++++++++++++++END+++++++++++++++++++++++++++++++++++++++++++++++
    ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
     */

}


