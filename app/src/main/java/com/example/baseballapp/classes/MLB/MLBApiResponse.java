package com.example.baseballapp.classes.MLB;

import com.example.baseballapp.classes.MLB.MLBDate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MLBApiResponse {
    @JsonIgnore
    public boolean m_isReady;
    @JsonProperty("copyright")
    private String copyright;

    @JsonProperty("totalItems")
    private int totalItems;

    @JsonProperty("totalEvents")
    private int totalEvents;

    @JsonProperty("totalGames")
    private int totalGames;

    @JsonProperty("totalGamesInProgress")
    private int totalGamesInProgress;

    @JsonProperty("dates")
    public List<MLBDate> dates;

    public MLBApiResponse() {
        m_isReady = false;
    }
}
