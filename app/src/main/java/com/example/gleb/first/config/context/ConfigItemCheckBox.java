package com.example.gleb.first.config.context;

/**
 * Created by Gleb on 24.08.2016.
 */
public class ConfigItemCheckBox extends ConfigItem {

    boolean checkBox;

    public ConfigItemCheckBox(String largeText, String smallText, boolean state) {
        super(largeText, smallText);
        checkBox = state;
    }

    public boolean isCheckBox() {
        return checkBox;
    }

    public void setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
    }
}
