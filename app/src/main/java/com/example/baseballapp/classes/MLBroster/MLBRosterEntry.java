package com.example.baseballapp.classes.MLBroster;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MLBRosterEntry {
    @JsonProperty("person")
    public MLBPerson MLBPerson;
    @JsonProperty("jerseyNumber")
    public String jerseyNumber;
    @JsonProperty("position")
    public MLBPosition MLBPosition;
    @JsonProperty("status")
    private MLBPersonStatus MLBPersonStatus;
    @JsonProperty("parentTeamId")
    public int parentTeamId;

    // getters and setters
}

