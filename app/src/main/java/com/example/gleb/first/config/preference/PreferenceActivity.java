package com.example.gleb.first.config.preference;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
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
    private PreferenceMainSettingsFragment preferenceMainSettingsFragment;
    private int backResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_settings_main_activity);


        preferenceMainSettingsFragment = (PreferenceMainSettingsFragment) getFragmentManager().findFragmentById(R.id.fragment);
        //getFragmentManager().beginTransaction().replace(android.R.id.content, preferenceMainSettingsFragment).commit();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean(MainActivity.CONFIG_NOTIFICATION_STATE, preferenceMainSettingsFragment.getNotificationState());
        intent.putExtras(bundle);
        setResult(MainActivity.RESULT_CONFIGURATIONS_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean(MainActivity.CONFIG_NOTIFICATION_STATE, preferenceMainSettingsFragment.getNotificationState());
        intent.putExtras(bundle);
        setResult(backResult, intent);
        finish();

        super.onDestroy();
    }
}
