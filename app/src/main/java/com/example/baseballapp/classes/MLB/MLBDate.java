package com.example.baseballapp.classes.MLB;

import com.example.baseballapp.classes.team.Team;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MLBDate {
    @JsonProperty("date")
    public String date;

    @JsonProperty("totalItems")
    private int totalItems;

    @JsonProperty("totalEvents")
    private int totalEvents;

    @JsonProperty("totalGames")
    private int totalGames;

    @JsonProperty("totalGamesInProgress")
    private int totalGamesInProgress;

    @JsonProperty("games")
    private List<MLBGame> games;

    public List<MLBGame> getGamesForTeam(Team team){
        List<MLBGame> gamesWithTeam = new ArrayList<MLBGame>();
        for (MLBGame game: games) {
            if(game.hasTeam(team))
                gamesWithTeam.add(game);
        }
        return gamesWithTeam;
    }
}
