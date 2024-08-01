package com.example.baseballapp.tasks;

import android.os.AsyncTask;

import com.example.baseballapp.classes.MLB.MLBApiResponse;
import com.example.baseballapp.classes.MLB.MLBDate;
import com.example.baseballapp.classes.MLB.MLBGame;
import com.example.baseballapp.classes.roomDB.Room_MLBGame;
import com.example.baseballapp.data.BaseballAppRoomDatabase;
import com.example.baseballapp.data.MLBDataLayer;

import java.util.ArrayList;
import java.util.List;

public class Task_LoadGamesFromDB extends AsyncTask<MLBDataLayer, Object, List<Room_MLBGame>> {
    private final String TAG = "Task_LoadGames";
    private MLBDataLayer mRepo;
    @Override
    protected List<Room_MLBGame> doInBackground(MLBDataLayer... repoL) {
        mRepo = repoL[0];

        BaseballAppRoomDatabase db = BaseballAppRoomDatabase.getInstance(mRepo.baseContext);
        //database tabel inlezen, als klaar, in postexecute zet in repo
        List<Room_MLBGame> gamesList = db.gameDao().getAllgames();
        return gamesList;
    }

    @Override
    protected void onPostExecute(List<Room_MLBGame> l){
        super.onPostExecute(l);

        MLBApiResponse response = new MLBApiResponse();
        // for each game read from roomdb :from room flat structure obj, make structured obj as from real response
        ArrayList<MLBGame> mlbgamesList = new ArrayList<>();
        for (Room_MLBGame dbGame : l) {
            MLBGame newGame = new MLBGame();
            newGame.initialiseWith(dbGame, MLBDataLayer.getInstance());
            mlbgamesList.add(newGame);
        }
        // now add games to response structure
        response.dates = new ArrayList<MLBDate>();
        for(MLBGame g : mlbgamesList) {
            MLBDate date = response.getMLBDate(g.mlbgamedate);
            if(date == null) {
                //add a new one
                MLBDate newDate = new MLBDate();
                newDate.date = g.mlbgamedate;
                newDate.games = new ArrayList<MLBGame>();
                response.dates.add(newDate);
                date = newDate;
            }
            date.games.add(g);
        }

        mRepo.MLBGamesList = response;
        mRepo.addCompletedTask("GameListReady");
    }
}