package com.example.gleb.first.config.context;

/**
 * Created by Gleb on 24.08.2016.
 */
public class ConfigItemSwitch extends ConfigItem {

    private boolean switchState;

    public ConfigItemSwitch(String largeText, String smallText, boolean state){
        super(largeText, smallText);
        switchState = state;
    }

    public void setSwitch(boolean state){
        switchState = state;
    }

    public boolean getSwitch(){
        return switchState;
    }
}
