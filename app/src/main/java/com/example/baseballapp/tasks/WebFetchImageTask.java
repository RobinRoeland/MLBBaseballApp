package com.example.baseballapp.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.example.baseballapp.classes.BitMapItem;
import com.example.baseballapp.classes.team.Team;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebFetchImageTask extends AsyncTask<BitMapItem, Integer, Bitmap> {
    public ImageView m_image;
    public BitMapItem m_forItem;

    @Override
    protected Bitmap doInBackground(BitMapItem... item) {
        m_forItem = item[0];
        String dataUrl = "http://www.jursairplanefactory.com/baseballimg/"+ m_forItem.m_imageName;

        try{
            URL urlConnection = new URL(dataUrl);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        }
        catch (Exception e) {
            Log.e("WebTask error" , e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null && m_image != null){
            m_forItem.m_image = bitmap;
            m_image.setImageBitmap(bitmap);
        }
    }
}
