package com.example.gleb.first.config.context;

/**
 * Created by Gleb on 25.08.2016.
 */
public class ConfigItemIcon extends ConfigItem {

    private int iconId;

    public ConfigItemIcon(String largeText, String smallText) {
        super(largeText, smallText);
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}
