package com.example.baseballapp.classes.MLBroster;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MLBPersonStatus {
    @JsonProperty("code")
    private String code;
    @JsonProperty("description")
    private String description;

    // getters and setters
}