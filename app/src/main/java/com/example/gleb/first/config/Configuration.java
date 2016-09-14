package com.example.gleb.first.config;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gleb.first.MainActivity;
import com.example.gleb.first.main.MainActivityNav;
import com.example.gleb.first.R;
import com.example.gleb.first.cache.Cacher;
import com.example.gleb.first.config.context.ConfigItem;
import com.example.gleb.first.config.context.ConfigItemAdapter;
import com.example.gleb.first.config.context.ConfigItemCheckBox;
import com.example.gleb.first.config.context.ConfigItemIcon;
import com.example.gleb.first.config.context.ConfigItemSwitch;
import com.example.gleb.first.language.Language;

import java.util.ArrayList;
import java.util.List;

public class Configuration extends AppCompatActivity {

    private ListView listView;
    private String CONFIGURATION_NOTIFICATIONS = "Notifications";
    private String CONFIGURATION_LANGUAGE = "Language";
    private String CONFIGURATION_DATA = "Data";
    private String CONFIGURATION_EXIT = "Exit";

    private boolean notificationState;
    private boolean byLocationState;
    private Bundle toReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        String tmp = Cacher.readConfig(MainActivity.FOLDER_CONFIG, MainActivity.CONFIG_NOTIFICATION_STATE);
        Bundle data = getIntent().getExtras();
        notificationState = data.getBoolean(MainActivityNav.CONFIG_NOTIFICATION_STATE);
        byLocationState = data.getBoolean(MainActivityNav.CONFIG_BY_LOCATION_STATE);


        toReturn = new Bundle();
        listView = (ListView) findViewById(R.id.configurationList);

    }

    private void initLanguageMenu(){
        CONFIGURATION_NOTIFICATIONS = getString(R.string.configuration_notifications);
        CONFIGURATION_LANGUAGE = getString(R.string.configuration_language);
        CONFIGURATION_DATA = getString(R.string.configuration_data);
        CONFIGURATION_EXIT = getString(R.string.configuration_exit);
    }


    private List<ConfigItem> initConfigList(){
        List<ConfigItem> list = new ArrayList<ConfigItem>();

        list.add(new ConfigItemSwitch(getString(R.string.configuration_notifications), notificationState?getString(R.string.desription_setting_notifications_on):getString(R.string.desription_setting_notifications_off) , notificationState));
        list.add(new ConfigItem(getString(R.string.configuration_language), getBaseContext().getResources().getConfiguration().locale.getDisplayName()));
        list.add(new ConfigItemCheckBox(getString(R.string.configuration_data), "Data Parametrs", false));
        list.add(new ConfigItemIcon(getString(R.string.configuration_exit), "Exit from application"));
        return list;
    }

    @Override
    protected void onStart() {

        //initLanguageMenu();
        final ConfigItemAdapter adapter = new ConfigItemAdapter(Configuration.this, initConfigList());
        listView.setAdapter(adapter);
        adapter.setOnCheckedListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch (compoundButton.getId()){
                    case R.id.config_switch:
                        notificationState = b;
                        ((TextView)((View)compoundButton.getParent()).findViewById(R.id.smallConfigText)).setText(b ? getString(R.string.desription_setting_notifications_on) : getString(R.string.desription_setting_notifications_off));
                        toReturn.putBoolean(MainActivity.CONFIG_NOTIFICATION_STATE, notificationState);
                        break;

                    case R.id.checkBox:
                        CheckBox box = (CheckBox)compoundButton;
                        byLocationState = box.isChecked();
                        toReturn.putBoolean(MainActivityNav.CONFIG_BY_LOCATION_STATE, byLocationState);
                        break;
                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item_name = adapter.getItemById(i).getLargeText();


                if(item_name.equals(getString(R.string.configuration_notifications))){

                }
                else if(item_name.equals(getString(R.string.configuration_language))){
                    startActivityForResult(new Intent(getApplicationContext(), Language.class), RESULT_OK);
                }
                else if(item_name.equals(getString(R.string.configuration_data))){

                }
                else if(item_name.equals(getString(R.string.configuration_exit))){
                    Intent intent = new Intent();
                    intent.putExtras(toReturn);
                    setResult(MainActivity.RESULT_APPLICATION_EXIT, intent);
                    finish();
                }
            }
        });


        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtras(toReturn);
        setResult(MainActivity.RESULT_CONFIGURATIONS_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
