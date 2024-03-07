package com.example.baseballapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.classes.team.TeamAllSeasonResponse;
import com.example.baseballapp.data.MLBDataLayer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetTeamsTask extends AsyncTask<Integer, Integer, TeamAllSeasonResponse> {
    @Override
    protected TeamAllSeasonResponse doInBackground(Integer... ints) {
        int jaar = ints[0];
        OkHttpClient client = new OkHttpClient();
        String dataUrl = "https://lookup-service-prod.mlb.com/json/named.team_all_season.bam?sport_code=%27mlb%27&all_star_sw=%27N%27&sort_order=name_asc&season=%27"+ String.valueOf(jaar) + "%27";

        String s = "";
        Request request = new Request.Builder()
                .url(dataUrl)
                .build();
        TeamAllSeasonResponse teamListResponse = null;
        try{
            Response urlResponse = client.newCall(request).execute();

            if (!urlResponse.isSuccessful()){
                return null;
            }

            s = urlResponse.body().string();

            try{
                ObjectMapper m = new ObjectMapper();
                m.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
                teamListResponse = m.readValue(s, TeamAllSeasonResponse.class);
            }
            catch (JsonProcessingException e){
                Log.d("Error", e.getMessage());
            }
        }
        catch (Exception e) {
            s = e.getMessage();
        }

        return teamListResponse;
    }

    @Override
    protected void onPostExecute(TeamAllSeasonResponse teamResponse) {
        super.onPostExecute(teamResponse);

        if(teamResponse == null){
            Log.e("league load teams error", "teamresponse is null");
            return;
        }

        MLBDataLayer dataPool = MLBDataLayer.getInstance();
        for (Team t: teamResponse.teamAllSeason.teamQueryResults.row) {
            t.m_imageName = "team/"+t.name_abbrev+".png";
            dataPool.teamList.add(t);
        }
        dataPool.initLeaguesFromTeams();
    }
}
