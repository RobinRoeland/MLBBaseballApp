package com.example.baseballapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.baseballapp.classes.MLBroster.MLBRosterEntry;
import com.example.baseballapp.classes.MLBroster.MLBRosterResponse;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.data.MLBDataLayer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetRosterTask extends AsyncTask<Team, Integer, MLBRosterResponse> {
    private Team m_team;
    private final MLBDataLayer m_repo;

    public GetRosterTask() {
        m_repo = MLBDataLayer.getInstance();
    }

    @Override
    protected MLBRosterResponse doInBackground(Team... teams) {
        m_team = teams[0];
        OkHttpClient client = new OkHttpClient();
        String dataUrl = "https://statsapi.mlb.com/api/v1/teams/" + m_team.mlb_org_id + "/roster";

        String s = "";
        Request request = new Request.Builder()
                .url(dataUrl)
                .build();
        MLBRosterResponse rosterResponse = null;
        try{
            Response urlResponse = client.newCall(request).execute();

            if (!urlResponse.isSuccessful()){
                return null;
            }

            s = urlResponse.body().string();

            try{
                ObjectMapper m = new ObjectMapper();
                m.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
                rosterResponse = m.readValue(s, MLBRosterResponse.class);
            }
            catch (JsonProcessingException e){
                Log.d("Error", e.getMessage());
            }
        }
        catch (Exception e) {
            s = e.getMessage();
        }

        return rosterResponse;
    }

    @Override
    protected void onPostExecute(MLBRosterResponse rosterResponse) {
        super.onPostExecute(rosterResponse);

        if(rosterResponse == null){
            Log.e("load team roster error", "rosterresponse is null");
            return;
        }

        MLBDataLayer dataPool = MLBDataLayer.getInstance();
        for (MLBRosterEntry entry : rosterResponse.roster) {
            String imgUrl = "https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:silo:current.png/r_max/w_180,q_auto:best/v1/people/" + entry.MLBPerson.id + "/headshot/silo/current";
            entry.MLBPerson.m_fullImageURL = imgUrl;
            Team t = m_repo.getTeamWithMLBID(entry.parentTeamId);
            entry.MLBPerson.m_imageName = t.team_code + "_" + entry.MLBPerson.id + ".jpg";
            entry.MLBPerson.m_localFileSubFolder = "/images/people";
            WebFetchImageTask webTask = new WebFetchImageTask(dataPool.baseContext);
            webTask.m_image =  null;
            webTask.execute(entry.MLBPerson);

        }
        dataPool.m_teamRosterMap.put(m_team.mlb_org_id, rosterResponse);
        dataPool.addCompletedTask("TeamRosterReady");
    }
}
