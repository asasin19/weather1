package com.example.gleb.first.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Created by Gleb on 25.08.2016.
 */
public class Cacher {
    public static final String CACHE_LOG_TAG = "CACHEDEBUG";
    public static final String CACHE_PATH  = "/.weathercache";
    public static final String IMAGE_FOLDER_PATH = CACHE_PATH + "/image";
    public static final String CACHE_TYPE = ".cache";
    public static final String CONFIG_TYPE = ".txt";
    public static final String CONFIG_FOLDER_PATH = CACHE_PATH + "/config";

    private static Map<String, Properties> config_cache;

    public static boolean cacheImage(Bitmap bitmap, String name){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d(CACHE_LOG_TAG, "can't found SD card.");
            return false;
        }
        if(bitmap == null)
            return false;
        File path = Environment.getExternalStorageDirectory();

        path = new File(path.getAbsolutePath() + IMAGE_FOLDER_PATH + "/" + name + CACHE_TYPE);



        FileOutputStream fos = null;

        try {
            createFolders();
            if(!path.exists())
                path.createNewFile();

            fos = new FileOutputStream(path);

            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fos);

            fos.flush();

            return true;

        }catch (IOException ex){
            Log.d(CACHE_LOG_TAG, "Image not saved, have some error: " + ex.getMessage());
            return false;
        }finally {
            if(fos != null) {
                try {
                    fos.close();
                }catch (IOException ex){
                    Log.d(CACHE_LOG_TAG, "Close stream error : " + ex.getMessage());
                }
            }
        }
    }

    public static boolean cacheConfig(String name, Properties property){
        return cacheConfig(name, property, false);
    }

    public static boolean cacheConfig(String name, String propertyName, String propertyValue){
        Properties property = new Properties();
        property.setProperty(propertyName, propertyValue);
        return cacheConfig(name, property, false);
    }

    public synchronized static boolean cacheConfig(String name, Properties property , boolean addToFile){
        if(config_cache == null)
            config_cache = new HashMap<String, Properties>();
        if(!config_cache.containsKey(name)) {
            config_cache.put(name, property);
        }else {
            Properties mp = config_cache.get(name);
            for (String prop : property.stringPropertyNames()) {
            mp.put(prop, property.getProperty(prop));
            }
        }
        if (!addToFile)
            return true;
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d(CACHE_LOG_TAG, "can't found SD card.");
            return false;
        }
        createFolders();
        File file = new File(Environment.getExternalStorageDirectory() + CONFIG_FOLDER_PATH + "/" + name + CONFIG_TYPE);

        BufferedWriter writer = null;
        Properties properties = readConfig(name);
        if(properties == null)
            properties = new Properties();


        try {
            if(!file.exists())
                file.createNewFile();
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));


            for(String prop : properties.stringPropertyNames()){
                writer.write(prop + "=" + properties.getProperty(prop));
                writer.newLine();
            }


            writer.flush();
            return true;
        }catch (IOException ex){
            Log.d(CACHE_LOG_TAG, "Can write config, error = " + ex.getMessage());
        }finally {
            closeStream(writer);
        }
        return false;
    }

    public static boolean saveAllConfigs(){

        for(String name : config_cache.keySet()) {
            BufferedWriter writer = null;
            try {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + CONFIG_FOLDER_PATH + "/" + name + CONFIG_TYPE);
                Properties properties = readConfig(name);
                if (!file.exists())
                    file.createNewFile();
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));


                for (String prop : properties.stringPropertyNames()) {
                    writer.write(prop + "=" + properties.getProperty(prop));
                    writer.newLine();
                }


                writer.flush();

            } catch (IOException ex) {
                Log.d(CACHE_LOG_TAG, "Can write config, error = " + ex.getMessage());
            } finally {
                closeStream(writer);
            }
            config_cache.put(name, null);
        }
        config_cache = null;
        return true;
    }

    public static String readConfig(String name, String propherty){
        Properties properties = readConfig(name);
        return properties.getProperty(propherty);
    }

    public static Properties readConfig(String name){
        if(config_cache == null)
            config_cache = new HashMap<String, Properties>();
        else if(config_cache.get(name) != null)
            return config_cache.get(name);
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d(CACHE_LOG_TAG, "can't found SD card.");
            return null;
        }
        Properties properties = new Properties();
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + CONFIG_FOLDER_PATH + "/" + name + CONFIG_TYPE);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = reader.readLine()) != null){
                if(line.indexOf("=") != -1) {
                    String[] tmp =  line.split("=", 2);
                    properties.setProperty(tmp[0], tmp[1]);
                }
            }
        }catch (IOException ex){
            Log.d(CACHE_LOG_TAG, "Can't read property, error = " + ex.getMessage());
        }finally {
            closeStream(reader);
        }

        config_cache.put(name, properties);
        return properties;

    }

    public static boolean createFolders(){
        List<Boolean> statusList = new ArrayList<Boolean>(2);

        File file = new File(Environment.getExternalStorageDirectory() + IMAGE_FOLDER_PATH);
        if(!file.exists())
            statusList.add(file.mkdirs());
        file = new File(Environment.getExternalStorageDirectory() + CONFIG_FOLDER_PATH);
        if(!file.exists())
            statusList.add(file.mkdirs());

        for(boolean stat : statusList){
            if(!stat)
                return false;
        }
        return true;
    }

    public static Bitmap readImage(String name){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d(CACHE_LOG_TAG, "can't found SD card.");
            return null;
        }
        if(name == null)
            return null;
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + IMAGE_FOLDER_PATH + "/" + name + CACHE_TYPE);

        FileInputStream fis = null;

        try{
            fis = new FileInputStream(path);

            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            return bitmap;
        }catch (IOException | NullPointerException ex){
            Log.d(CACHE_LOG_TAG, "Image not readed, have some error: " + ex.getMessage());
            return null;
        }finally {
            closeStream(fis);
        }
    }

    private static void closeStream(InputStream stream){
        if(stream != null) {
            try {
                stream.close();
            }catch (IOException ex){
                Log.d(CACHE_LOG_TAG, "Close stream error : " + ex.getMessage());
            }

        }
    }

    private static void closeStream(OutputStream stream){
        if(stream != null) {
            try {
                stream.close();
            }catch (IOException ex){
                Log.d(CACHE_LOG_TAG, "Close stream error : " + ex.getMessage());
            }

        }
    }

    private static void closeStream(Reader stream){
        if(stream != null) {
            try {
                stream.close();
            }catch (IOException ex){
                Log.d(CACHE_LOG_TAG, "Close stream error : " + ex.getMessage());
            }

        }
    }

    private static void closeStream(Writer stream){
        if(stream != null) {
            try {
                stream.close();
            }catch (IOException ex){
                Log.d(CACHE_LOG_TAG, "Close stream error : " + ex.getMessage());
            }

        }
    }
}
