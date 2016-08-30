package com.example.gleb.first.config.preference;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.widget.Toast;

import com.example.gleb.first.MainActivity;
import com.example.gleb.first.R;

import java.util.List;
import java.util.Locale;

/**
 * Created by gleb on 30.08.16.
 */
public class PreferenceMainSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener{

    public static final String PREFERENCE_NOTIFICATION = "Notification";
    public static final String PREFERENCE_LANGUAGE = "Language";
    public static final String PREFERENCE_DATA = "Data";
    public static final String PREFERENCE_EXIT = "Exit";

    private SwitchPreference switchPreference;
    private ListPreference listPreference;
    private CheckBoxPreference checkBoxPreference;
    private Preference preference;

    private String currentLang;
    private Intent data;
    private boolean resultSeted;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);

        switchPreference = ((SwitchPreference) findPreference(PREFERENCE_NOTIFICATION));
        listPreference = ((ListPreference) findPreference(PREFERENCE_LANGUAGE));
        checkBoxPreference = ((CheckBoxPreference) findPreference(PREFERENCE_DATA));
        preference = ((Preference) findPreference(PREFERENCE_EXIT));

        initMultilanguage();

        switchPreference.setOnPreferenceClickListener(this);
        listPreference.setOnPreferenceChangeListener(this);
        checkBoxPreference.setOnPreferenceClickListener(this);
        preference.setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String name = preference.getTitle().toString();


        if(name.equals(getString(R.string.configuration_exit))){
            data = new Intent();
            getActivity().setResult(MainActivity.RESULT_APPLICATION_EXIT,data);
            resultSeted = true;
            getActivity().finish();
        }
        else if(name.equals(getString(R.string.configuration_notifications))){

        }
        else if(name.equals(getString(R.string.configuration_data))){

        }

        return true;
    }

    private void changeLocale(Locale locale){
        Locale.setDefault(locale);
        Configuration conf = new Configuration();
        conf.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(conf, getActivity().getBaseContext().getResources().getDisplayMetrics());

    }

    private void initMultilanguage(){
        switchPreference.setTitle(R.string.configuration_notifications);
        listPreference.setTitle(R.string.configuration_language);
        checkBoxPreference.setTitle(R.string.configuration_data);
        preference.setTitle(R.string.configuration_exit);

        switchPreference.setSummaryOff(R.string.desription_setting_notifications_off);
        switchPreference.setSummaryOn(R.string.desription_setting_notifications_on);

        switchPreference.setSwitchTextOff(R.string.off);
        switchPreference.setSwitchTextOn(R.string.on);

        listPreference.setSummary(R.string.language);

        checkBoxPreference.setSummary(R.string.configuration_data);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        String loca = o.toString();
        if(loca.equals(currentLang))
            return true;
        if(loca.equals("null"))
            return false;
        changeLocale(new Locale(loca));
        initMultilanguage();
        currentLang = loca;

        return true;
    }


    @Override
    public void onDestroy() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(MainActivity.CONFIG_NOTIFICATION_STATE, switchPreference.isChecked());
        if(!resultSeted) {
            data = new Intent();
            data.putExtras(bundle);
            getActivity().setResult(MainActivity.RESULT_CONFIGURATIONS_OK, data);
            getActivity().finish();
        }


        super.onDestroy();
    }
}
