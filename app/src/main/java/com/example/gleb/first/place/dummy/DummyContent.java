package com.example.gleb.first.place.dummy;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.TtsSpan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyContent {


    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;


    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static void removeItem(String id){
        ITEMS.remove(Integer.valueOf(id).intValue());
        ITEM_MAP.remove(id);
    }

    private static DummyItem createDummyItem(int position, ItemContent content) {
        return new DummyItem(String.valueOf(position), content, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public static class DummyItem implements Parcelable {
        public final String id;
        public final ItemContent content;
        public final String details;

        public DummyItem(String id, ItemContent content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content.city.toString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeStringArray(new String[]{id, content.city,content.time, content.date, content.icon_name, content.temp, content.min_temp, content.max_temp, details});
        }

        public static final Parcelable.Creator<DummyItem> CREATOR = new Parcelable.Creator<DummyItem>(){

            public DummyItem createFromParcel(Parcel parcel){
                return new DummyItem(parcel);
            }

            public DummyItem[] newArray(int size){
                return new DummyItem[size];
            }
        };

        private DummyItem(Parcel parcel){
            String[] s_arr = new String[9];
            parcel.readStringArray(s_arr);
            id = s_arr[0];
            content = new ItemContent(s_arr[1], s_arr[2], s_arr[3], s_arr[4], s_arr[5], s_arr[6], s_arr[7]);
            details = s_arr[8];
        }
    }

    public static class ItemContent{
        public final String city;
        public final String time;
        public final String date;
        public final String icon_name;
        public final String temp;
        public final String min_temp;
        public final String max_temp;

        private static final DateFormat time_format = DateFormat.getTimeInstance(DateFormat.SHORT);
        private static final DateFormat date_format = DateFormat.getDateInstance(DateFormat.DATE_FIELD);

        public ItemContent(String city, String icon_name, String temp, String min_temp, String max_temp) {
            this.city = city;
            this.icon_name = icon_name;
            this.temp = temp;
            this.min_temp = min_temp;
            this.max_temp = max_temp;

            Date l_date = new Date(System.currentTimeMillis());
            time = time_format.format(l_date);
            date = date_format.format(l_date);
        }

        ItemContent(String city, String time, String date, String icon_name, String temp, String min_temp, String max_temp){
            this.city = city;
            this.icon_name = icon_name;
            this.temp = temp;
            this.min_temp = min_temp;
            this.max_temp = max_temp;
            this.time = time;
            this.date = date;
        }
    }
}
