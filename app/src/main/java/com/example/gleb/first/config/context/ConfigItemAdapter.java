package com.example.gleb.first.config.context;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.gleb.first.R;
import com.example.gleb.first.config.context.Items;
import com.example.gleb.first.config.Configuration;
import com.example.gleb.first.language.Language;

import java.util.List;
import java.util.Objects;

/**
 * Created by Gleb on 22.08.2016.
 */
public class ConfigItemAdapter extends BaseAdapter{


    private List<ConfigItem> items;
    private LayoutInflater context;

    private Switch switch_item_switch;
    private CompoundButton.OnCheckedChangeListener switch_listener;


    public ConfigItemAdapter(@NonNull Context context , List<ConfigItem> items) {
        for (Object obj : items)
            Log.d("TestClassConf", obj.getClass().getSimpleName());
        this.items = items;
        this.context = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setOnCheckedListener(CompoundButton.OnCheckedChangeListener listener){
        //if(switch_item_switch == null) {
            switch_listener = listener;
            return;
        //}
        //switch_item_switch.setOnCheckedChangeListener(listener);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ConfigItem item = getItemById(i);
        switch (item.getClass().getSimpleName()){
            case Items.ITEM: {
                //view = view == null ? context.inflate(R.layout.config_items_layout, viewGroup, false) : view;

                if (view == null)
                    view = context.inflate(R.layout.config_items_layout, viewGroup, false);

                Log.d("CONFIG", "IN CONFIG ITEM. ID = " + i);
                break;
            }

            case Items.ITEM_CHECK_BOX: {
                ConfigItemCheckBox item1 = (ConfigItemCheckBox) item;
                if (view == null)
                    view = context.inflate(R.layout.config_item_checkbox, viewGroup, false);
                ((CheckBox) view.findViewById(R.id.checkBox)).setOnCheckedChangeListener(switch_listener);
                Log.d("CONFIG", "IN CONFIG CHECK BOX ITEM. ID = " + i);
                break;
            }

            case Items.ITEM_SWITCH:{
                ConfigItemSwitch item1 = (ConfigItemSwitch) item;
                if (view == null)
                    view = context.inflate(R.layout.config_item_switch, viewGroup, false);

                switch_item_switch = (Switch)view.findViewById(R.id.config_switch);
                switch_item_switch.setChecked(item1.getSwitch());

                if(switch_listener != null)
                    switch_item_switch.setOnCheckedChangeListener(switch_listener);

                Log.d("CONFIG", "IN CONFIG SWITCH ITEM. ID = " + i);
                break;
            }

            case Items.ITEM_ICON:{
                ConfigItemIcon item1 = (ConfigItemIcon) item;
                if (view == null)
                    view = context.inflate(R.layout.config_item_icon, viewGroup, false);

                if(item1.getIconId() != 0){
                    //((ImageView)view.findViewById(R.id.imageView)); add image and to do;
                }
                //if not then use default

                Log.d("CONFIG", "IN CONFIG ICON ITEM. ID = " + i);
                break;
            }

            default:
                Log.e("SWITCH", "SWITCH ERROR");
                break;
        }


        ((TextView) view.findViewById(R.id.largeConfigText)).setText(item.getLargeText());
        ((TextView) view.findViewById(R.id.smallConfigText)).setText(item.getSmallText());





        return view;
    }

    public ConfigItem getItemById(int id){
        return items.get(id);
    }
}
