package com.example.gleb.first.config.preference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.gleb.first.MainActivity;
import com.example.gleb.first.main.MainActivityNav;
import com.example.gleb.first.R;

/**
 * Created by gleb on 30.08.16.
 */
public class PreferenceActivity extends android.preference.PreferenceActivity  {
    public static final String DEBUG_PREFERENCE_ACTIVITY_TAG = "PREFERENCE_ACTIVITY";

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
        bundle.putBoolean(MainActivityNav.CONFIG_BY_LOCATION_STATE, preferenceMainSettingsFragment.getDataState());

        Log.d(DEBUG_PREFERENCE_ACTIVITY_TAG, new StringBuilder("NOTIFICATION STATE = ").
                append(preferenceMainSettingsFragment.getNotificationState()).
                append(" DATA STATE = ").
                append(preferenceMainSettingsFragment.getDataState()).
                toString());

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
