package com.example.baseballapp.tasks;

import android.os.AsyncTask;

import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.data.BaseballAppRoomDatabase;
import com.example.baseballapp.data.MLBDataLayer;

import java.util.ArrayList;
import java.util.List;

public class Task_LoadTeamsThatHaveOfflineRosterFromDB extends AsyncTask<MLBDataLayer, Object, List<Integer>> {
    private String TAG = "Task_LoadTeamsThatHaveOfflineRosterFromDB";
    private MLBDataLayer mRepo;
    @Override
    protected List<Integer> doInBackground(MLBDataLayer... repoL) {
        mRepo = repoL[0];

        BaseballAppRoomDatabase db = BaseballAppRoomDatabase.getInstance(mRepo.baseContext);
        //lees van roomDB alle teams die een roster gesaved hebben in de roster tabel
        //indien aanwezig, dan is het team eerder online geraadpleegd en is de data van games en roster gesaved en kan offline gebruikt worden
        //resultaat van deze query is alle team id's die voorkomen in roster_table
        List<Integer> teamsList = (List<Integer>) db.rosterDao().getDistinctTeamsFromRoster();
        return teamsList;
    }

    @Override
    protected void onPostExecute(List<Integer> listTeamIds){
        super.onPostExecute(listTeamIds);
        ArrayList<Team> mlbavailableteamslist = new ArrayList<>();
        for (Integer teamid : listTeamIds) {
            Team t = mRepo.getTeamWithMLBID(teamid);
            if(t != null)
                mlbavailableteamslist.add(t);
        }
        mRepo.setOfflineAvailableTeamsList(mlbavailableteamslist);
        mRepo.addCompletedTask("TeamsHavingOfflineRoster");
    }
}