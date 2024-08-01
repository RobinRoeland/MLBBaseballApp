package com.example.baseballapp.ui.roster;

import androidx.lifecycle.ViewModel;

import com.example.baseballapp.classes.MLBroster.MLBRosterEntry;
import com.example.baseballapp.data.MLBDataLayer;

import java.util.ArrayList;
import java.util.List;

public class RosterViewModel extends ViewModel {

    public List<MLBRosterEntry> m_displayPersonsList;


    public RosterViewModel() {
        m_displayPersonsList = new ArrayList<MLBRosterEntry>();
    }

    public void FilterListOnEnteredSearchString(String s) {
        m_displayPersonsList.clear();
        List<MLBRosterEntry> roster =  MLBDataLayer.getInstance().getRosterForTeam(MLBDataLayer.getInstance().m_selectedTeam.getValue());
        for (MLBRosterEntry rosterentry: roster)
        {
            if (rosterentry.compliesToFilter(s))
                m_displayPersonsList.add(rosterentry);
        }
    }

    public void initialisePersonsListFromRepo() {
        List<MLBRosterEntry> roster =  MLBDataLayer.getInstance().getRosterForTeam(MLBDataLayer.getInstance().m_selectedTeam.getValue());
        m_displayPersonsList.addAll(roster);
    }
}