package com.example.baseballapp.classes.team;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TeamAllSeason {
    public String copyRight;
    @JsonProperty("queryResults")
    public TeamQueryResults teamQueryResults;
}
