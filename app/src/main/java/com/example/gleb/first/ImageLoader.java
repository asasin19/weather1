package com.example.gleb.first;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.gleb.first.cache.Cacher;

import java.io.FileOutputStream;
import java.net.URL;

/**
 * Created by Gleb on 28.08.2016.
 */
public class ImageLoader {
    public static final String IMAGE_SOURCE_URI = "http://openweathermap.org/img/w/";
    public static final String IMAGE_SOURCE_TYPE = ".png";

    public Bitmap getBitmap(String imageName){
        Bitmap bitmap = Cacher.readImage(imageName);
        if(bitmap == null){
            String s_url =  IMAGE_SOURCE_URI + imageName + IMAGE_SOURCE_TYPE;
            try {

                bitmap = BitmapFactory.decodeStream(new URL(s_url).openStream());
                Cacher.cacheImage(bitmap, imageName);

            }catch (Exception ex){

            }

        }
        return bitmap;
    }

}
