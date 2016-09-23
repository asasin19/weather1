package com.example.gleb.first.place;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gleb.first.R;
import com.example.gleb.first.place.dummy.DummyContent;
import com.example.gleb.first.weatherpack.Weather;
import com.example.gleb.first.weatherpack.context.OpenWeatherLight;


public class PlaceActivity extends FragmentActivity implements PlaceList.OnListFragmentInteractionListener {

    private Fragment fragment;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_place_main_layout);
        fragment = new PlaceList();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(linearLayout.getId(), fragment).commit();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Toast.makeText(getApplicationContext(), item.content.city, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_place_add:
                createDialog();
                dialog.show();


                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createDialog(){
        if(dialog != null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input = new EditText(getApplicationContext());
        input.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        input.setOnKeyListener(onKeyListener);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        builder.setMessage("Please, write city")
                .setView(input)
                .setPositiveButton("Ok",listener)
                .setNegativeButton("Cancel", listener);

        dialog = builder.create();
    }

    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            EditText input = (EditText)view;
            if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                if(input.getText().length() < 2)
                    return false;
                SavedPlace.init().addPlace(input.getText().toString());
                dialog.hide();
            }
            return false;
        }
    };

    private DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
        }
    };
}
