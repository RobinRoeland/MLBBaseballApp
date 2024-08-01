package com.example.baseballapp.ui;

import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.lifecycle.MutableLiveData;

import com.example.baseballapp.R;
import com.example.baseballapp.classes.BitMapItem;
import com.example.baseballapp.classes.league.LeagueList;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.classes.team.TeamList;

public class TeamSelectionActViewModel {
    public LeagueList m_displayLeagueList;
    public MutableLiveData<TeamList> m_filteredTeamList;
    public MutableLiveData<String> m_currentLeagueID;
    public BitMapItem m_mlbLogo;


    public TeamSelectionActViewModel() {
        m_displayLeagueList = null;
        TeamList emptyList = new TeamList();
        m_filteredTeamList = new MutableLiveData<TeamList>();
        m_filteredTeamList.setValue(emptyList);
        m_currentLeagueID = new MutableLiveData<String>();
        m_currentLeagueID.setValue("");
        m_mlbLogo = null;
    }
    public void initImages(Context c) {
        m_mlbLogo = new BitMapItem();
        m_mlbLogo.m_image = BitmapFactory.decodeResource(c.getResources(), R.drawable.mlblogo);
        m_mlbLogo.m_imageName = "team/MLB.png";
    }

    public void setFilteredTeamList(TeamList teamList, String forLeagueID){
        TeamList tempList = new TeamList();
        for (Team team: teamList) {
            if (team.league_id.equalsIgnoreCase(forLeagueID))
                tempList.add(team);
        }
        m_filteredTeamList.setValue(tempList);
    }
}
