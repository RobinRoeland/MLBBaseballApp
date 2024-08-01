package com.example.baseballapp.classes.MLBroster;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MLBPersonStatus {
    @JsonProperty("code")
    public String code;
    @JsonProperty("description")
    public String description;

    // getters and setters
}