package com.example.baseballapp.classes.MLB;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MLBVenue {
    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("link")
    private String link;

    // Getter and Setter methods

    // You can add constructor and other methods as needed
}
