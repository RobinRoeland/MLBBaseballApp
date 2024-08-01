package com.example.baseballapp.data;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.baseballapp.classes.league.League;
import com.example.baseballapp.classes.roomDB.Room_MLBGame;
import com.example.baseballapp.classes.roomDB.Room_MLBRosterEntry;
import com.example.baseballapp.classes.roomDB.Room_MLBTicket;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.data.dao.GameDao;
import com.example.baseballapp.data.dao.LeagueDao;
import com.example.baseballapp.data.dao.RosterDao;
import com.example.baseballapp.data.dao.TeamDao;
import com.example.baseballapp.data.dao.TicketDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Room_MLBGame.class ,League.class, Team.class, Room_MLBTicket.class, Room_MLBRosterEntry.class}, version = 1, exportSchema = false)
@TypeConverters({TimestampConverter.class})
public abstract class BaseballAppRoomDatabase extends RoomDatabase {
    public abstract LeagueDao leagueDao();
    public abstract TeamDao teamDao();
    public abstract RosterDao rosterDao();
    public abstract TicketDao ticketDao();
    public abstract GameDao gameDao();

    private static volatile BaseballAppRoomDatabase INSTANCE;

    private static final int numberOfThreads = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(numberOfThreads);

    public static BaseballAppRoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (BaseballAppRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    BaseballAppRoomDatabase.class, "BaseballApp_Database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public void CleanDataBase(){
        ticketDao().deleteAllTickets();
        rosterDao().deleteAllRosterEntries();
        gameDao().deleteAllGames();
        teamDao().deleteAllTeams();
        leagueDao().deleteAllLeagues();
    }

}


