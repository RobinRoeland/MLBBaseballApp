package com.example.baseballapp.data;

import androidx.lifecycle.MutableLiveData;

import com.example.baseballapp.classes.MLB.MLBApiResponse;
import com.example.baseballapp.classes.MLB.MLBDate;
import com.example.baseballapp.classes.MLB.MLBGame;
import com.example.baseballapp.classes.league.LeagueList;
import com.example.baseballapp.classes.league.League;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.classes.team.TeamList;
import com.example.baseballapp.tasks.GetGamesTask;
import com.example.baseballapp.tasks.GetTeamsTask;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MLBDataLayer {
    public static MLBDataLayer dataLayer = null;
    public TeamList teamList;
    public MLBApiResponse MLBGamesList;

    public MutableLiveData<LeagueList> m_leagueList;
    public MutableLiveData<Team> m_selectedTeam;

    MLBDataLayer(){
        teamList = new TeamList();
        LeagueList emptyList = new LeagueList();
        MLBGamesList = new MLBApiResponse();
        m_leagueList = new MutableLiveData<LeagueList>();
        m_leagueList.setValue(emptyList);
        m_selectedTeam = new MutableLiveData<Team>();
        m_selectedTeam.setValue(null);
    }

    public static synchronized MLBDataLayer getInstance(){
        if (dataLayer == null){
            dataLayer = new MLBDataLayer();
        }

        return dataLayer;
    }

    public void init(){
        initTeams();
    }

    public void initTeams() {
        GetTeamsTask task = new GetTeamsTask();
        task.execute(2024);
    }
    public void initGamesForSelectedTeam() {
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
                l.m_imageName = "team/"+t.league+".png";
                leagueList.add(l);
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
}
