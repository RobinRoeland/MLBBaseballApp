package com.example.baseballapp.classes.league;

import com.example.baseballapp.classes.league.League;

import java.util.ArrayList;

public class LeagueList extends ArrayList<League> {
    public boolean LeagueIDExists(String id){
        for (League l: this) {
            if (l.id.equalsIgnoreCase(id))
                return true;
        }
        return false;
    }
}
