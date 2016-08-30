package com.example.gleb.first.config.preference;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.example.gleb.first.MainActivity;
import com.example.gleb.first.R;

import java.util.Locale;

/**
 * Created by gleb on 30.08.16.
 */
public class PreferenceActivity extends android.preference.PreferenceActivity  {

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_settings_main_activity);

        //getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceMainSettingsFragment()).commit();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        setResult(MainActivity.RESULT_CONFIGURATIONS_OK);
        finish();
    }


}
