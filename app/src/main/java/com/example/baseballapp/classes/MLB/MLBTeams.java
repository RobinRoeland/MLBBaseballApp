package com.example.baseballapp.classes.MLB;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MLBTeams {
    @JsonProperty("away")
    public MLBAwayTeam away;

    @JsonProperty("home")
    public MLBHomeTeam home;

    // Getter and Setter methods

    // Inner class for the "away" team
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MLBAwayTeam {
        @JsonProperty("leagueRecord")
        private MLBLeagueRecord leagueRecord;

        @JsonProperty("team")
        public MLBTeamInfo team;

        @JsonProperty("splitSquad")
        private boolean splitSquad;

        @JsonProperty("seriesNumber")
        private int seriesNumber;

        // Getter and Setter methods
    }

    // Inner class for the "home" team
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MLBHomeTeam {
        @JsonProperty("leagueRecord")
        private MLBLeagueRecord leagueRecord;

        @JsonProperty("team")
        public MLBTeamInfo team;

        @JsonProperty("splitSquad")
        private boolean splitSquad;

        @JsonProperty("seriesNumber")
        private int seriesNumber;

        // Getter and Setter methods
    }
}
