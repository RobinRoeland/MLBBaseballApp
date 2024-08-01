package com.example.baseballapp.classes.MLB;

import com.example.baseballapp.classes.roomDB.Room_MLBGame;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.data.MLBDataLayer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MLBGame {
    @JsonProperty("gamePk")
    public int gamePk;

    @JsonProperty("gameGuid")
    public String gameGuid;

    @JsonProperty("link")
    public String link;

    @JsonProperty("gameType")
    public String gameType;

    @JsonProperty("season")
    public String season;

    @JsonProperty("gameDate")
    public String gameDate;

    @JsonProperty("officialDate")
    public String officialDate;

    @JsonProperty("status")
    public MLBStatus status;

    @JsonProperty("teams")
    public MLBTeams teams;

    @JsonProperty("venue")
    public MLBVenue venue;

    @JsonProperty("content")
    public MLBContent content;

    @JsonProperty("gameNumber")
    public int gameNumber;

    @JsonProperty("publicFacing")
    public boolean publicFacing;

    @JsonProperty("doubleHeader")
    public String doubleHeader;

    @JsonProperty("gamedayType")
    public String gamedayType;

    @JsonProperty("tiebreaker")
    public String tiebreaker;

    @JsonProperty("calendarEventID")
    public String calendarEventID;

    @JsonProperty("seasonDisplay")
    public String seasonDisplay;

    @JsonProperty("dayNight")
    public String dayNight;

    @JsonProperty("description")
    public String description;

    @JsonProperty("scheduledInnings")
    public int scheduledInnings;

    @JsonProperty("reverseHomeAwayStatus")
    public boolean reverseHomeAwayStatus;

    @JsonProperty("inningBreakLength")
    public int inningBreakLength;

    @JsonProperty("gamesInSeries")
    public int gamesInSeries;

    @JsonProperty("seriesGameNumber")
    public int seriesGameNumber;

    @JsonProperty("seriesDescription")
    public String seriesDescription;

    @JsonProperty("recordSource")
    public String recordSource;

    @JsonProperty("ifNecessary")
    public String ifNecessary;

    @JsonProperty("ifNecessaryDescription")
    public String ifNecessaryDescription;

    @JsonIgnore
    public String mlbgamedate;

    public boolean hasTeam(Team team){
        return (teams.away.team.id == Integer.parseInt(team.mlb_org_id) || teams.home.team.id == Integer.parseInt(team.mlb_org_id));
    }

    public boolean isHomeTeam(Team team){
        return teams.home.team.id == Integer.parseInt(team.mlb_org_id);
    }

    public MLBTeamInfo getOpponentInfo(Team team){
        if (isHomeTeam(team))
            return teams.away.team;
        else
            return teams.home.team;
    }

    public MLBTeamInfo getHomeTeamInfo(){
        return teams.home.team;
    }

    public Team getHomeTeam(MLBDataLayer repo){
        MLBTeamInfo info= getHomeTeamInfo();
        Team t = repo.getTeamWithMLBID(info.id);
        return t;
    }

    public Team getAwayTeam(MLBDataLayer repo){
        Team home = getHomeTeam(repo);
        if (home==null)
            return null;
        MLBTeamInfo awayinfo = getOpponentInfo(home);
        Team t = repo.getTeamWithMLBID(awayinfo.id);
        return t;
    }

    //function to recreate structure in mlbgame from roomdb flat record
    public void initialiseWith(Room_MLBGame dbGame, MLBDataLayer repo) {
        mlbgamedate = dbGame.mlbdate;
        gameGuid = dbGame.gameGuid;
        gamePk = dbGame.gamePk;
        link = dbGame.link;
        gameType = dbGame.gameType;
        season = dbGame.season;
        gameDate = dbGame.gameDate;
        gameNumber = dbGame.gameNumber;
        description = dbGame.description;
        gamesInSeries = dbGame.gamesInSeries;
        seriesGameNumber = dbGame.seriesGameNumber;
        seriesDescription = dbGame.seriesDescription;
        //Teams
        Team hometeam = repo.getTeamWithMLBID(dbGame.hometeam_id);
        Team awayteam = repo.getTeamWithMLBID(dbGame.awayteam_id);

        MLBTeamInfo newhometeaminfo = new MLBTeamInfo();
        newhometeaminfo.id = dbGame.hometeam_id;
        newhometeaminfo.name = hometeam.name_display_long;
        newhometeaminfo.link = hometeam.base_url;

        teams = new MLBTeams();
        teams.home = new MLBTeams.MLBHomeTeam();
        teams.home.team = newhometeaminfo;

        MLBTeamInfo newawayteaminfo = new MLBTeamInfo();
        newawayteaminfo.id = dbGame.awayteam_id;
        newawayteaminfo.name = awayteam.name_display_long;
        newawayteaminfo.link = awayteam.base_url;

        teams.away = new MLBTeams.MLBAwayTeam();
        teams.away.team = newawayteaminfo;

        venue = new MLBVenue();
        venue.id = dbGame.venue_id;
        venue.name = dbGame.venue_name;
        venue.link = dbGame.venue_link;

        content = new MLBContent();
        content.link = dbGame.content_link;
    }

    public ZonedDateTime getGameDate_ZonedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
        LocalDateTime utcDateTime = LocalDateTime.parse(gameDate, formatter);
        // Convert LocalDateTime to ZonedDateTime with UTC time zone
        ZonedDateTime zonedDateTimeUtc = ZonedDateTime.of(utcDateTime, ZoneId.of("UTC"));
        return zonedDateTimeUtc;
    }

    public boolean isInThePast() {
        ZonedDateTime zonedDateTimeUtc = getGameDate_ZonedDateTime();
        // Get the current time in UTC
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);
        // Compare the times
        boolean isInThePast = zonedDateTimeUtc.isBefore(nowUtc);

        return isInThePast;
    }
}
