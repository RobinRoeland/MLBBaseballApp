package com.example.baseballapp.classes.MLBroster;

import com.example.baseballapp.classes.BitMapItem;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MLBPerson extends BitMapItem {
    @JsonProperty("id")
    public int id;
    @JsonProperty("fullName")
    public String fullName;
    @JsonProperty("link")
    public String link;

    // getters and setters
}
