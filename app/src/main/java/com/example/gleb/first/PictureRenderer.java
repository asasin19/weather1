package com.example.gleb.first;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.gleb.first.cache.Cacher;

import java.net.URL;

/**
 * Created by Gleb on 21.08.2016.
 */
public class PictureRenderer extends AsyncTask<String, Void, Bitmap> {

    public static final String IMAGE_SOURCE_URI = "http://openweathermap.org/img/w/";
    public static final String IMAGE_SOURCE_TYPE = ".png";

    private float image_scale = 5;

    private ImageView image;

    PictureRenderer(ImageView image){
        this.image = image;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        final String imageName = strings[0];
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

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null) {
            image.setImageBitmap(bitmap);
            image.setScaleX(image_scale);
            image.setScaleY(image_scale);
        }

    }

    public float getImage_scale() {
        return image_scale;
    }

    public void setImage_scale(float image_scale) {
        this.image_scale = image_scale;
    }
}
