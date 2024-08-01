package com.example.baseballapp.classes.MLB;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MLBContent {
    @JsonProperty("link")
    public String link;

    // Getter and Setter methods

    // You can add constructor and other methods as needed

}
