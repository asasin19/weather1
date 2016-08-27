package com.example.gleb.first.config;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gleb.first.MainActivity;
import com.example.gleb.first.R;
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
    private String CONFIGURATION_VOLUME = "Volume";
    private String CONFIGURATION_LANGUAGE = "Language";
    private String CONFIGURATION_DATA = "Data";
    private String CONFIGURATION_EXIT = "Exit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);



        listView = (ListView) findViewById(R.id.configurationList);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(Configuration.this, android.R.layout.simple_list_item_1, params);

    }

    public void initLanguageMenu(){
        CONFIGURATION_VOLUME = getString(R.string.configuration_volume);
        CONFIGURATION_LANGUAGE = getString(R.string.configuration_language);
        CONFIGURATION_DATA = getString(R.string.configuration_data);
        CONFIGURATION_EXIT = getString(R.string.configuration_exit);
    }

    public void configSelected(View view){
        Toast toast = Toast.makeText(getApplicationContext(), view.getId() + "", Toast.LENGTH_LONG);
        toast.show();
    }

    private List<ConfigItem> initConfigList(){
        List<ConfigItem> list = new ArrayList<ConfigItem>();

        list.add(new ConfigItemSwitch(CONFIGURATION_VOLUME, ((AudioManager)getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_ALARM) + "", true));
        list.add(new ConfigItem(CONFIGURATION_LANGUAGE, getBaseContext().getResources().getConfiguration().locale.getDisplayName()));
        list.add(new ConfigItemCheckBox(CONFIGURATION_DATA, "Data Parametrs", false));
        list.add(new ConfigItemIcon(CONFIGURATION_EXIT, "Exit from application"));
        return list;
    }

    @Override
    protected void onStart() {

        initLanguageMenu();
        final ConfigItemAdapter adapter = new ConfigItemAdapter(Configuration.this, initConfigList());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item_name = adapter.getItemById(i).getLargeText();

                if(item_name.equals(getString(R.string.configuration_volume))){

                }
                else if(item_name.equals(getString(R.string.configuration_language))){
                    startActivityForResult(new Intent(getApplicationContext(), Language.class), RESULT_OK);
                }
                else if(item_name.equals(getString(R.string.configuration_data))){

                }
                else if(item_name.equals(getString(R.string.configuration_exit))){
                    setResult(9999);
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
        setResult(MainActivity.RESULT_CONFIGURATIONS_OK);
        finish();
    }
}
