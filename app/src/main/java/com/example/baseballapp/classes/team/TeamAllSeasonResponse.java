package com.example.baseballapp.classes.team;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TeamAllSeasonResponse {
    @JsonProperty("team_all_season")
    public TeamAllSeason teamAllSeason;
}
