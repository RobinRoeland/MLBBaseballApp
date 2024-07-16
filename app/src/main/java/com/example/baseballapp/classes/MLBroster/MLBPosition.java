package com.example.baseballapp.classes.MLBroster;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MLBPosition {
    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private String type;
    @JsonProperty("abbreviation")
    public String abbreviation;

    // getters and setters
}
