package com.example.gleb.first;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Gleb on 01.09.2016.
 */
public class MenuItemsList {
    public static final int MAX_ITEMS_COUNT = 20;

    private Queue<String> queue;

    public MenuItemsList(){
        queue = new ArrayBlockingQueue<String>(MAX_ITEMS_COUNT);
    }

    public void add(String item){
        if(queue.size() == MAX_ITEMS_COUNT)
            queue.remove();
        queue.add(item);
    }

    public Collection<String> getItems(){
        ArrayList<String> list = new ArrayList<String>(MAX_ITEMS_COUNT);
        int size = queue.size();
        String[] arr = queue.toArray(new String[size]);
        for (int i = 0 ; i < size ; i++) {
            list.add(arr[size - 1 - i]);
        }
        return list;
    }
}
