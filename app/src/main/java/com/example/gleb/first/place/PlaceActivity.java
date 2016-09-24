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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gleb.first.R;
import com.example.gleb.first.main.MainActivityNav;
import com.example.gleb.first.place.dummy.DummyContent;
import com.example.gleb.first.weatherpack.Weather;
import com.example.gleb.first.weatherpack.context.OpenWeatherLight;


public class PlaceActivity extends FragmentActivity implements PlaceList.OnListFragmentInteractionListener, PlaceList.OnListFragmentLongInteractionListener {
    public static final int DIALOG_BUTTON_OK = -1;
    public static final int DIALOG_BUTTON_CANCEL = -2;

    private Fragment fragment;
    private AlertDialog dialog;
    private AlertDialog dialogMenu;
    private SavedPlace savedPlace;
    private EditText input;

    private DummyContent.DummyItem choisedItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_place_main_layout);
        fragment = new PlaceList();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(linearLayout.getId(), fragment).commit();
        savedPlace = SavedPlace.init();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        returnByItem(item);
    }

    @Override
    public void onListFragmentLongInteraction(DummyContent.DummyItem item) {
        createMenuDialog();
        choisedItem = item;
        dialogMenu.show();
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

        View content = getLayoutInflater().inflate(R.layout.dialog_place_findbycity, null);
        input = (EditText) content.findViewById(R.id.dialog_place_city);
        input.setOnKeyListener(onKeyListener);

        builder.setMessage(R.string.dialog_place_find_message)
                .setView(content)
                .setPositiveButton(R.string.dialog_place_find_positive,listener)
                .setNegativeButton(R.string.dialog_place_find_negative, listener);

        dialog = builder.create();
    }

    private void createMenuDialog(){
        if(dialogMenu != null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View content = getLayoutInflater().inflate(R.layout.dialog_place_menu, null);
        content.findViewById(R.id.dialog_place_menu_cancel).setOnClickListener(onClickListener);
        content.findViewById(R.id.dialog_place_menu_choise).setOnClickListener(onClickListener);
        content.findViewById(R.id.dialog_place_menu_delete).setOnClickListener(onClickListener);

        builder.setTitle(R.string.dialog_place_menu_title)
                .setView(content);

        dialogMenu = builder.create();
    }

    private void returnByItem(DummyContent.DummyItem item){
        Bundle bundle = new Bundle();
        bundle.putString(MainActivityNav.CONFIG_CITY, item.content.city);
        setResult(MainActivityNav.RESULT_PLACE_CHOISE_OK, new Intent().putExtras(bundle));
        finish();
    }

    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                if(input.getText().length() < 2)
                    return false;
                final String text = input.getText().toString();
                input.setText("");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        savedPlace.addPlace(text);
                    }
                }).start();
                dialog.hide();
            }
            return false;
        }
    };

    private DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i){
                case DIALOG_BUTTON_OK:
                    if(input.getText().length() < 2) {
                        Toast.makeText(getApplicationContext(), R.string.dialog_place_find_city_dis, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final String text = input.getText().toString();
                    input.setText("");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            savedPlace.addPlace(text);
                        }
                    }).start();
                    break;
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.dialog_place_menu_cancel:
                    dialogMenu.hide();
                    break;

                case R.id.dialog_place_menu_choise:
                    returnByItem(choisedItem);
                    dialogMenu.hide();
                    break;

                case R.id.dialog_place_menu_delete:
                    savedPlace.removePlace(choisedItem.content.city);
                    dialogMenu.hide();
                    break;
            }
        }
    };

}
