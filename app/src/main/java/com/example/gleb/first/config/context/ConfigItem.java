package com.example.gleb.first.config.context;

/**
 * Created by Gleb on 22.08.2016.
 */
public class ConfigItem {
    private static int ID = 0;

    private int id;
    private String largeText;
    private String smallText;

    public ConfigItem(String largeText, String smallText) {
        this.id = ++ID;
        this.largeText = largeText;
        this.smallText = smallText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLargeText() {
        return largeText;
    }

    public void setLargeText(String largeText) {
        this.largeText = largeText;
    }

    public String getSmallText() {
        return smallText;
    }

    public void setSmallText(String smallText) {
        this.smallText = smallText;
    }


}
