package com.example.baseballapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.baseballapp.classes.MLB.MLBApiResponse;
import com.example.baseballapp.data.MLBDataLayer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetGamesTask extends AsyncTask<String, Integer, MLBApiResponse> {
    @Override
    protected void onPostExecute(MLBApiResponse games) {
        super.onPostExecute(games);

        MLBDataLayer.getInstance().MLBGamesList = games;
        MLBDataLayer.getInstance().MLBGamesList.m_isReady = true;
    }

    @Override
    protected MLBApiResponse doInBackground(String... params) {
        String jaar = params[0];
        String mlbOrigTeamId = params[1];
        String endDate = String.format("%s-12-31", jaar);
        String startDate = String.format("%s-01-01", jaar);
        OkHttpClient client = new OkHttpClient();
        String dataUrl = "https://statsapi.mlb.com/api/v1/schedule/games/?sportId=1&startDate="+ startDate +"&endDate="+ endDate;
        dataUrl = "https://statsapi.mlb.com/api/v1/schedule/games/?teamId="+mlbOrigTeamId+"&sportId=1&startDate="+ startDate +"&endDate="+ endDate;

        String s = "";
        Request request = new Request.Builder()
                .url(dataUrl)
                .build();
        MLBApiResponse gameListResponse = null;
        try{
            Response urlResponse = client.newCall(request).execute();

            if (!urlResponse.isSuccessful()){
                return null;
            }

            s = urlResponse.body().string();

            try{
                ObjectMapper m = new ObjectMapper();
                m.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
                gameListResponse = m.readValue(s, MLBApiResponse.class);
            }
            catch (JsonProcessingException e){
                Log.d("Error", e.getMessage());
            }
        }
        catch (Exception e) {
            s = e.getMessage();
        }
        return gameListResponse;
    }
}
