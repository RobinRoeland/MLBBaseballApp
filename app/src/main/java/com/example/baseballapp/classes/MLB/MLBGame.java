package com.example.baseballapp.classes.MLB;

import com.example.baseballapp.classes.team.Team;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MLBGame {
    @JsonProperty("gamePk")
    private int gamePk;

    @JsonProperty("gameGuid")
    private String gameGuid;

    @JsonProperty("link")
    private String link;

    @JsonProperty("gameType")
    private String gameType;

    @JsonProperty("season")
    private String season;

    @JsonProperty("gameDate")
    public String gameDate;

    @JsonProperty("officialDate")
    private String officialDate;

    @JsonProperty("status")
    private MLBStatus status;

    @JsonProperty("teams")
    private MLBTeams teams;

    @JsonProperty("venue")
    private MLBVenue venue;

    @JsonProperty("content")
    private MLBContent content;

    @JsonProperty("gameNumber")
    private int gameNumber;

    @JsonProperty("publicFacing")
    private boolean publicFacing;

    @JsonProperty("doubleHeader")
    private String doubleHeader;

    @JsonProperty("gamedayType")
    private String gamedayType;

    @JsonProperty("tiebreaker")
    private String tiebreaker;

    @JsonProperty("calendarEventID")
    private String calendarEventID;

    @JsonProperty("seasonDisplay")
    private String seasonDisplay;

    @JsonProperty("dayNight")
    private String dayNight;

    @JsonProperty("description")
    private String description;

    @JsonProperty("scheduledInnings")
    private int scheduledInnings;

    @JsonProperty("reverseHomeAwayStatus")
    private boolean reverseHomeAwayStatus;

    @JsonProperty("inningBreakLength")
    private int inningBreakLength;

    @JsonProperty("gamesInSeries")
    private int gamesInSeries;

    @JsonProperty("seriesGameNumber")
    private int seriesGameNumber;

    @JsonProperty("seriesDescription")
    private String seriesDescription;

    @JsonProperty("recordSource")
    private String recordSource;

    @JsonProperty("ifNecessary")
    private String ifNecessary;

    @JsonProperty("ifNecessaryDescription")
    private String ifNecessaryDescription;

    public boolean hasTeam(Team team){
        return (teams.away.team.id == Integer.parseInt(team.mlb_org_id) || teams.home.team.id == Integer.parseInt(team.mlb_org_id));
    }

    public boolean isHomeTeam(Team team){
        return teams.home.team.id == Integer.parseInt(team.mlb_org_id);
    }

    public MLBTeamInfo getOpponent(Team team){
        if (isHomeTeam(team))
            return teams.away.team;
        else
            return teams.home.team;
    }

    public MLBTeamInfo getHomeTeam(){
        return teams.home.team;
    }
}
