package com.example.baseballapp.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.baseballapp.classes.BitMapItem;
import com.example.baseballapp.classes.functionlib.LocaleFileLib;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebFetchImageTask extends AsyncTask<BitMapItem, Integer, Bitmap> {
    public ImageView m_image;
    public BitMapItem m_forItem;
    private Context m_context;

    public WebFetchImageTask(Context context){
        m_context = context;
    }

    @Override
    protected Bitmap doInBackground(BitMapItem... item) {
        m_forItem = item[0];
        Bitmap returnBitmap = null;
        String dataUrl="";

        if(m_forItem.m_fullImageURL.equals("")) {
            Log.e("WebTask error", "FullUrl must be specified for image load task loading image: "+ m_forItem.m_imageName);
            return null;
        }

        dataUrl = m_forItem.m_fullImageURL;

        String localeDiskFileName = m_forItem.m_localFileSubFolder + "/" + m_forItem.m_imageName;

        if (!LocaleFileLib.FileExists(localeDiskFileName, m_context)){
            try{
                URL urlConnection = new URL(dataUrl);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                if (input != null){
                    LocaleFileLib.createFolderIfNotExists(m_forItem.m_localFileSubFolder, m_context);
                    LocaleFileLib.saveInputStreamToLocalFile(input, localeDiskFileName, m_context);
                }
            }
            catch (Exception e) {
                Log.e("WebTask error" , e.getMessage());
            }
        }

        if (LocaleFileLib.FileExists(localeDiskFileName, m_context)){
            InputStream input = LocaleFileLib.loadInputStreamFromLocalFile(localeDiskFileName, m_context);
            if (input != null){
                try {
                    returnBitmap = BitmapFactory.decodeStream(input);
                    input.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return returnBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null){
            m_forItem.m_image = bitmap;
            if(m_image != null)
                m_image.setImageBitmap(bitmap);
        }
    }
}
