package com.example.baseballapp.tasks;

import android.os.AsyncTask;

import com.example.baseballapp.classes.MLB.MLBTicket;
import com.example.baseballapp.classes.roomDB.Room_MLBTicket;
import com.example.baseballapp.data.BaseballAppRoomDatabase;
import com.example.baseballapp.data.MLBDataLayer;

import java.util.ArrayList;
import java.util.List;

public class Task_LoadTicketsFromDB extends AsyncTask<MLBDataLayer, Object, List<Room_MLBTicket>> {
    private final String TAG = "Task_LoadTickets";
    private MLBDataLayer mRepo;
    @Override
    protected List<Room_MLBTicket> doInBackground(MLBDataLayer... repoL) {
        mRepo = repoL[0];

        BaseballAppRoomDatabase db = BaseballAppRoomDatabase.getInstance(mRepo.baseContext);
        //database tabel inlezen, als klaar, in postexecute zet in repo
        List<Room_MLBTicket> ticketList = db.ticketDao().getAllTickets();
        return ticketList;
    }

    @Override
    protected void onPostExecute(List<Room_MLBTicket> l){
        super.onPostExecute(l);
        ArrayList<MLBTicket> mlbticketslist = new ArrayList<>();
        for (Room_MLBTicket dbticket : l) {
            MLBTicket newTicket = new MLBTicket();
            newTicket.initialiseWith(dbticket, MLBDataLayer.getInstance());
            mlbticketslist.add(newTicket);
        }
        mRepo.m_TicketList.setValue(mlbticketslist);
        mRepo.sortTicketsByGameDate();
        mRepo.addCompletedTask("TicketsLoadedFromDB");
    }
}