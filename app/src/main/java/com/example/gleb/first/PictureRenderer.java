package com.example.gleb.first;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.widget.ImageView;

import com.example.gleb.first.cache.Cacher;

import java.net.URL;

/**
 * Created by Gleb on 21.08.2016.
 */
public class PictureRenderer extends AsyncTask<String, Void, Bitmap> {

    private ImageLoader loader;

    private float image_scale = 5;

    private ImageView image;

    PictureRenderer(ImageView image){
        this.loader = new ImageLoader();
        this.image = image;
    }


    @Override
    protected Bitmap doInBackground(String... strings) {
        return loader.getBitmap(strings[0]);
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
