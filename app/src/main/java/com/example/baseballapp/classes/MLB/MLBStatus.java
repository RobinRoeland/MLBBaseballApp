package com.example.baseballapp.classes.MLB;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MLBStatus {
    @JsonProperty("abstractGameState")
    private String abstractGameState;

    @JsonProperty("codedGameState")
    private String codedGameState;

    @JsonProperty("detailedState")
    private String detailedState;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("startTimeTBD")
    private boolean startTimeTBD;

    @JsonProperty("abstractGameCode")
    private String abstractGameCode;

    // Getter and Setter methods

    // You can add constructor and other methods as needed
}
