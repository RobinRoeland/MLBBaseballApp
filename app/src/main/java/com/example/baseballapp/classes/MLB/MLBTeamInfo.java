package com.example.baseballapp.classes.MLB;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MLBTeamInfo {
    @JsonProperty("id")
    public int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("link")
    private String link;

    // Getter and Setter methods
}