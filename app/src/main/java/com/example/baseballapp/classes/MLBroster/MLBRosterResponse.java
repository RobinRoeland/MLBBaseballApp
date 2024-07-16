package com.example.baseballapp.classes.MLBroster;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MLBRosterResponse {
    @JsonProperty("copyright")
    private String copyright;
    @JsonProperty("roster")
    public List<MLBRosterEntry> roster;
    @JsonProperty("link")
    private String link;
    @JsonProperty("teamId")
    private Integer teamId;
    @JsonProperty("rosterType")
    private String rosterType;
    // getters and setters
}
