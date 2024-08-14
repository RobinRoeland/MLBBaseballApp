package com.example.baseballapp.classes.MLB;

import com.example.baseballapp.classes.MLB.MLBDate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MLBApiResponse {
    @JsonProperty("copyright")
    private String copyright;

    @JsonProperty("totalItems")
    private int totalItems;

    @JsonProperty("totalEvents")
    private int totalEvents;

    @JsonProperty("totalGames")
    private int totalGames;

    @JsonProperty("totalGamesInProgress")
    private int totalGamesInProgress;

    @JsonProperty("dates")
    public List<MLBDate> dates;

    public MLBApiResponse() {
    }

    public void removeGame(MLBGame game) {
        for (MLBDate agamedate: dates) {
            for(int i = agamedate.games.size()-1;i>=0;i--) {
                if (agamedate.games.get(i).gamePk == game.gamePk)
                    agamedate.games.remove(i);
            }
        }
    }

    public void RemoveAllGames() {
        if(dates==null)
            return;
        for (int i= dates.size()-1;i>=0;i--) {
            dates.remove(i);
        }
    }

    public MLBDate getMLBDate(String mlbgamedate) {
        //returns the mlbdate object that is same as given date string
        if(dates == null)
            return null;
        for (MLBDate agamedate: dates) {
            if(agamedate.date.equalsIgnoreCase(mlbgamedate))
                return agamedate;
        }
        return null;
    }
}
