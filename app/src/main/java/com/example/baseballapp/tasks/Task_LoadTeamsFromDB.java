package com.example.baseballapp.tasks;

import android.os.AsyncTask;

import com.example.baseballapp.classes.MLB.MLBTicket;
import com.example.baseballapp.classes.roomDB.Room_MLBTicket;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.data.BaseballAppRoomDatabase;
import com.example.baseballapp.data.MLBDataLayer;

import java.util.ArrayList;
import java.util.List;

public class Task_LoadTeamsFromDB extends AsyncTask<MLBDataLayer, Object, List<Team>> {
    private String TAG = "Task_LoadTeamsFromDB";
    private MLBDataLayer mRepo;
    @Override
    protected List<Team> doInBackground(MLBDataLayer... repoL) {
        mRepo = repoL[0];

        BaseballAppRoomDatabase db = BaseballAppRoomDatabase.getInstance(mRepo.baseContext);
        //database tabel inlezen, als klaar, in postexecute zet in repo
        List<Team> teamsList = (List<Team>) db.teamDao().getAllTeams();
        return teamsList;
    }

    @Override
    protected void onPostExecute(List<Team> l){
        super.onPostExecute(l);
        ArrayList<Team> mlbteamslist = new ArrayList<>();
        for (Team dbteam : l) {
            mRepo.teamList.add(dbteam);

            // load the images
            WebFetchImageTask webTask = new WebFetchImageTask(mRepo.baseContext);
            webTask.m_image =  null;
            webTask.execute(dbteam);
        }

        mRepo.initLeaguesFromTeams();
        mRepo.addCompletedTask("TeamsLoaded_FromDB");
    }
}