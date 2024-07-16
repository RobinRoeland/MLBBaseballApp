package com.example.baseballapp.data;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.baseballapp.classes.MLB.MLBApiResponse;
import com.example.baseballapp.classes.MLB.MLBDate;
import com.example.baseballapp.classes.MLB.MLBGame;
import com.example.baseballapp.classes.MLB.MLBTicket;
import com.example.baseballapp.classes.league.LeagueList;
import com.example.baseballapp.classes.league.League;
import com.example.baseballapp.classes.MLBroster.MLBRosterEntry;
import com.example.baseballapp.classes.MLBroster.MLBRosterResponse;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.classes.team.TeamList;
import com.example.baseballapp.tasks.GetGamesTask;
import com.example.baseballapp.tasks.GetRosterTask;
import com.example.baseballapp.tasks.GetTeamsTask;
import com.example.baseballapp.tasks.WebFetchImageTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MLBDataLayer {
    public static MLBDataLayer dataLayer = null;
    public TeamList teamList;
    public MLBApiResponse MLBGamesList;
    public MutableLiveData<LeagueList> m_leagueList;
    public MutableLiveData<Team> m_selectedTeam;
    public HashMap<String, MLBRosterResponse> m_teamRosterMap;
    public Context baseContext;
    public boolean m_VerifyIfOnline;
    public boolean m_CheckedIfOnline;
    public MLBTicket m_tempTicketDuringPayment;
    //paypal data
    public String m_PaymentSessionToken_Paypal;
    public String PAYPAL_CLIENT_ID;
    public String PAYPAL_SECRET_KEY1;

    MLBDataLayer(){
        teamList = new TeamList();
        LeagueList emptyList = new LeagueList();
        MLBGamesList = new MLBApiResponse();
        m_leagueList = new MutableLiveData<LeagueList>();
        m_leagueList.setValue(emptyList);
        m_selectedTeam = new MutableLiveData<Team>();
        m_selectedTeam.setValue(null);
        m_teamRosterMap = new HashMap<String, MLBRosterResponse>();
        baseContext = null;
        m_PaymentSessionToken_Paypal = "";
        m_CheckedIfOnline = false;
        m_VerifyIfOnline = false;
        m_tempTicketDuringPayment = null;
        PAYPAL_CLIENT_ID = "AdbT4aXxgxiqs6DZi4WlhbNL2FdAJIlIwleHJRpkqrxbe66fjn6XAZLtSC91IAiDyVNtu4HcfggP-Uu9";
        PAYPAL_SECRET_KEY1 = "EJxr5kQ8GO0Zf-MtJN9b-o3ILy1N_cUOH71I9s0hw4FtsPeL_UjcrDLi-SgxU0O5Qq3HOL_Wpd14M33g";
    }

    public static synchronized MLBDataLayer getInstance(){
        if (dataLayer == null){
            dataLayer = new MLBDataLayer();
        }

        return dataLayer;
    }

    public void initTeams() {
        GetTeamsTask task = new GetTeamsTask();
        task.execute(2024);
    }
    public void initGamesForSelectedTeam() {
        MLBGamesList.m_isReady = false;
        GetGamesTask task = new GetGamesTask();
        String[] params = { "2024", m_selectedTeam.getValue().mlb_org_id };
        task.execute(params);
    }
    public void initLeaguesFromTeams(){
        LeagueList leagueList = m_leagueList.getValue();
        leagueList.clear();
        for (Team t: teamList) {
            if (!leagueList.LeagueIDExists(t.league_id)){
                League l = new League();
                l.name = t.league_full;
                l.shortName = t.league;
                l.id = t.league_id;
                l.m_imageName = t.league+".png";
                l.m_fullImageURL = "http://www.jursairplanefactory.com/baseballimg/team/"+ l.m_imageName;
                l.m_localFileSubFolder = "/images/league";
                leagueList.add(l);

                // invoke load images
                WebFetchImageTask webTask = new WebFetchImageTask(baseContext);
                webTask.m_image = null;
                webTask.execute(l);
            }
        }
        m_leagueList.setValue(leagueList);
    }

    public List<MLBGame> getGames(LocalDateTime datum, Team forTeam){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = datum.format(formatter);
        List<MLBGame> games = new ArrayList<>();
        if (MLBGamesList.dates != null){
            for (MLBDate date: MLBGamesList.dates){
                if (date.date.equals(formattedDate)){
                    games = date.getGamesForTeam(forTeam);
                }
            }
        }
        return games;
    }

    public Team getTeamWithMLBID(int mlbID){
        for (Team t: teamList){
            if(Integer.parseInt(t.mlb_org_id) == mlbID)
                return t;
        }
        return null;
    }

    public void startupLoadPhaseOne(){
        initTeams();
    }

    public boolean isStartupPhaseOneReady(){
        if (teamList.size() == 0 || m_leagueList.getValue().size() == 0)
            return false;

        // team  images loaded ?
        for (Team t: teamList) {
            if (t.m_image == null)
                return false;
        }
        // league images loaded ?
        for (League l: m_leagueList.getValue()) {
            if (l.m_image == null)
                return false;
        }

        return true;
    }
    public boolean isStartupPhaseTwo_TeamSelectionReady() {
        if(MLBGamesList.m_isReady && m_teamRosterMap.containsKey(m_selectedTeam.getValue().mlb_org_id)) {
            MLBRosterResponse response = m_teamRosterMap.get(m_selectedTeam.getValue().mlb_org_id);
            for(MLBRosterEntry entr: response.roster) {
                if(entr.MLBPerson.m_image == null)
                    return false;
            }
            return true;
        }
        return false;
    }

    public void initRosterForTeam(Team team) {
        GetRosterTask task = new GetRosterTask();
        task.execute(team);
    }

    public List<MLBRosterEntry> getRosterForTeam(Team team) {
        if(team == null)
            return null;

        if(m_teamRosterMap.containsKey(team.mlb_org_id))
            return m_teamRosterMap.get(team.mlb_org_id).roster;
        else
            return null;
    }

    public void Get_PaymentSessionToken(){

          // een accesstoken is een soort van sessie
        String input = PAYPAL_CLIENT_ID + ":" + PAYPAL_SECRET_KEY1;
        String AUTH = Base64.getEncoder().encodeToString(input.getBytes());

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", "Basic "+ AUTH);
        String jsonString = "grant_type=client_credentials";
        HttpEntity entity = new StringEntity(jsonString, "utf-8");
        client.post(baseContext, "https://api-m.sandbox.paypal.com/v1/oauth2/token", entity, "application/x-www-form-urlencoded",new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.e("RESPONSE", response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                try {
                    JSONObject jobj = new JSONObject(response);
                    m_PaymentSessionToken_Paypal = jobj.getString("access_token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
