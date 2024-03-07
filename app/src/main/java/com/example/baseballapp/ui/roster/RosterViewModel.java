package com.example.baseballapp.ui.roster;

import androidx.lifecycle.ViewModel;

import com.example.baseballapp.classes.league.LeagueList;

public class RosterViewModel extends ViewModel {

    public LeagueList m_displayLeagueList;

    public RosterViewModel() {
        m_displayLeagueList = null;
    }
}