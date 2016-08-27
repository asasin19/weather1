package com.example.gleb.first.language;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gleb.first.R;

import java.util.Locale;

public class Language extends AppCompatActivity {
    private ListView listView;

    public static final String RUSSIAN = "Русский";
    public static final String ENGLISH = "English";
    public static final String UKRAINIAN = "Українська";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{RUSSIAN, ENGLISH, UKRAINIAN});

        listView = (ListView) findViewById(R.id.configurationList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String locale_name = adapter.getItem(i);
                Locale locale = Locale.getDefault();

                switch (locale_name){
                    case RUSSIAN:
                        locale = new Locale("ru");
                        break;

                    case ENGLISH:
                        locale = new Locale("en");
                        break;

                    case UKRAINIAN:
                        locale = new Locale("uk");
                        break;
                }

                changeLocale(locale);
                setResult(RESULT_OK);
                finish();
            }

            private void changeLocale(Locale locale){
                Locale.setDefault(locale);
                Configuration conf = new Configuration();
                conf.locale = locale;
                getBaseContext().getResources().updateConfiguration(conf, getBaseContext().getResources().getDisplayMetrics());

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
