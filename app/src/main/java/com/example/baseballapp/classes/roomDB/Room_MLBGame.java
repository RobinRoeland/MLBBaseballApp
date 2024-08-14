package com.example.baseballapp.classes.roomDB;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.baseballapp.classes.MLB.MLBDate;
import com.example.baseballapp.classes.MLB.MLBGame;
import com.example.baseballapp.classes.MLB.MLBStatus;
import com.example.baseballapp.classes.MLB.MLBTicket;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.data.MLBDataLayer;

import java.util.Objects;

@Entity(tableName = "mlbgame_table")
public class Room_MLBGame {
    // members that will be saved to DB flat structure, from MLBGame class
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "key")
    public String key;
    @ColumnInfo(name = "mlbdate")
    public String mlbdate;
    @ColumnInfo(name = "gamepk")
    public int gamePk;
    @ColumnInfo(name = "gameguid")
    public String gameGuid;
    @ColumnInfo(name = "link")
    public String link;
    @ColumnInfo(name = "gametype")
    public String gameType;
    @ColumnInfo(name = "season")
    public String season;
    @ColumnInfo(name = "gameDate")
    public String gameDate;
    @ColumnInfo(name = "gamenumber")
    public int gameNumber;
    @ColumnInfo(name = "description")
    public String description;
    @ColumnInfo(name = "gamesinseries")
    public int gamesInSeries;
    @ColumnInfo(name = "seriesgamenumber")
    public int seriesGameNumber;
    @ColumnInfo(name = "seriesdescription")
    public String seriesDescription;
    //Teams
    @ColumnInfo(name = "hometeam_id")
    public int hometeam_id;
    @ColumnInfo(name = "home_teamname")
    public String home_teamname;
    @ColumnInfo(name = "awayteam_id")
    public int awayteam_id;
    @ColumnInfo(name = "away_teamname")
    public String away_teamname;
    //Venue
    @ColumnInfo(name = "venue_id")
    public int venue_id;
    @ColumnInfo(name = "venue_name")
    public String venue_name;
    @ColumnInfo(name = "venue_link")
    public String venue_link;
    //Content
    @ColumnInfo(name = "content_link")
    public String content_link;

    public Room_MLBGame() {
    }

    public boolean initialiseFrom(MLBGame fromGame, MLBDate mlbGamedate, MLBDataLayer repo) {
        key = fromGame.gamePk + "_" + fromGame.gameGuid;
        mlbdate = mlbGamedate.date;
        gameGuid = fromGame.gameGuid;
        gamePk = fromGame.gamePk;
        String s = String.valueOf(gamePk);
        Log.d("jur", String.valueOf(fromGame.gamePk));
        link = fromGame.link;
        gameType = fromGame.gameType;
        season = fromGame.season;
        gameDate = fromGame.gameDate;
        gameNumber = fromGame.gameNumber;
        description = fromGame.description;
        gamesInSeries = fromGame.gamesInSeries;
        seriesGameNumber = fromGame.seriesGameNumber;
        seriesDescription = fromGame.seriesDescription;
        //Teams
        Team hometeam = fromGame.getHomeTeam(repo);
        if(hometeam == null) {
            return false;
        }
        Team awayteam = fromGame.getAwayTeam(repo);
        if(awayteam == null) {
            return false;
        }

        hometeam_id = Integer.parseInt(hometeam.mlb_org_id);
        home_teamname = hometeam.name_display_long;
        awayteam_id = Integer.parseInt(awayteam.mlb_org_id);
        away_teamname = awayteam.name_display_long;
        venue_id = fromGame.venue.id;
        venue_name = fromGame.venue.name;
        venue_link = fromGame.venue.link;
        content_link = fromGame.content.link;
        return true;
    }
};