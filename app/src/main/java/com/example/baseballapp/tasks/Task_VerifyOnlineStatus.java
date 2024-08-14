package com.example.baseballapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.baseballapp.classes.team.TeamAllSeasonResponse;
import com.example.baseballapp.data.MLBDataLayer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Task_VerifyOnlineStatus extends AsyncTask<MLBDataLayer, Object, Boolean> {
    private String TAG = "Task_OnlineCheck";
    private MLBDataLayer m_data;
    @Override
    protected Boolean doInBackground(MLBDataLayer... repo) {
        m_data = repo[0];

        OkHttpClient client = new OkHttpClient();

        //make a call to a small api function, if succeeds, we are online, if not offline
        String dataUrl = "http://statsapi.mlb.com/api/v1/conferences";;

        String s = "";
        Request request = new Request.Builder()
                .url(dataUrl)
                .build();
        TeamAllSeasonResponse teamListResponse = null;
        try{
            Response urlResponse = client.newCall(request).execute();

            if (!urlResponse.isSuccessful()){
                return false;
            }
            s = urlResponse.body().string();
            //got a response , we are online
        }
        catch (Exception e) {
            s = e.getMessage();
            Log.d("OnlineCheck", e.getMessage().toString());
            return false;
        }
        return true;
    }


    @Override
    protected void onPostExecute(Boolean r){
        super.onPostExecute(r);
        m_data.m_VerifyIfOnline = r;
        m_data.addCompletedTask("CheckedIfOnline");
    }
}