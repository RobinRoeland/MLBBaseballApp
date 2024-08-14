package com.example.baseballapp.tasks;

import android.os.AsyncTask;

import com.example.baseballapp.classes.MLB.MLBApiResponse;
import com.example.baseballapp.classes.MLB.MLBDate;
import com.example.baseballapp.classes.MLB.MLBGame;
import com.example.baseballapp.classes.MLBroster.MLBPerson;
import com.example.baseballapp.classes.MLBroster.MLBRosterEntry;
import com.example.baseballapp.classes.MLBroster.MLBRosterResponse;
import com.example.baseballapp.classes.roomDB.Room_MLBGame;
import com.example.baseballapp.classes.roomDB.Room_MLBRosterEntry;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.data.BaseballAppRoomDatabase;
import com.example.baseballapp.data.MLBDataLayer;

import java.util.ArrayList;
import java.util.List;

public class Task_LoadTeamRosterFromDB extends AsyncTask<MLBDataLayer, Object, List<Room_MLBRosterEntry>> {
    private String TAG = "Task_LoadTeamRosterFromDB";
    private MLBDataLayer mRepo;
    private Team m_forTeam;
    public Task_LoadTeamRosterFromDB(Team t) {
        m_forTeam = t;
    }
    @Override
    protected List<Room_MLBRosterEntry> doInBackground(MLBDataLayer... repoL) {
        mRepo = repoL[0];

        BaseballAppRoomDatabase db = BaseballAppRoomDatabase.getInstance(mRepo.baseContext);
        //database tabel inlezen, als klaar, in postexecute zet in repo
        List<Room_MLBRosterEntry> playerRosterList = (List<Room_MLBRosterEntry>) db.rosterDao().getAllRosterEntriesForTeam(Integer.parseInt(m_forTeam.mlb_org_id));
        return playerRosterList;
    }

    @Override
    protected void onPostExecute(List<Room_MLBRosterEntry> listWithResults){
        super.onPostExecute(listWithResults);

        ArrayList<MLBRosterEntry> listMLBRosterEntries = new ArrayList<MLBRosterEntry>();
        for (Room_MLBRosterEntry entry: listWithResults) {
            MLBRosterEntry rosterEntry = new MLBRosterEntry();
            rosterEntry.initialiseFromDB(entry);

            listMLBRosterEntries.add(rosterEntry);

            // load images
            WebFetchImageTask webTask = new WebFetchImageTask(mRepo.baseContext);
            webTask.m_image =  null;
            webTask.execute(rosterEntry.MLBPerson);
        }
        MLBRosterResponse response = new MLBRosterResponse();
        response.teamId = Integer.parseInt(m_forTeam.mlb_org_id);
        response.roster = listMLBRosterEntries;
        mRepo.m_teamRoster = response;
        mRepo.addCompletedTask("TeamRosterReady");
    }
}