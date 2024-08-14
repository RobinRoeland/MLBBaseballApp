package com.example.baseballapp.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.room.RoomDatabase;

import com.example.baseballapp.classes.MLB.MLBApiResponse;
import com.example.baseballapp.classes.MLB.MLBDate;
import com.example.baseballapp.classes.MLB.MLBGame;
import com.example.baseballapp.classes.MLB.MLBTicket;
import com.example.baseballapp.classes.league.LeagueList;
import com.example.baseballapp.classes.league.League;
import com.example.baseballapp.classes.MLBroster.MLBRosterEntry;
import com.example.baseballapp.classes.MLBroster.MLBRosterResponse;
import com.example.baseballapp.classes.roomDB.Room_MLBGame;
import com.example.baseballapp.classes.roomDB.Room_MLBRosterEntry;
import com.example.baseballapp.classes.roomDB.Room_MLBTicket;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.classes.team.TeamList;
import com.example.baseballapp.tasks.GetGamesTask;
import com.example.baseballapp.tasks.GetRosterTask;
import com.example.baseballapp.tasks.GetTeamsTask;
import com.example.baseballapp.tasks.Task_LoadGamesFromDB;
import com.example.baseballapp.tasks.Task_LoadTeamsFromDB;
import com.example.baseballapp.tasks.Task_LoadTeamRosterFromDB;
import com.example.baseballapp.tasks.Task_LoadTicketsFromDB;
import com.example.baseballapp.tasks.Task_LoadTeamsThatHaveOfflineRosterFromDB;
import com.example.baseballapp.tasks.WebFetchImageTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MLBDataLayer {
    public static MLBDataLayer dataLayer = null;
    private ArrayList<String> m_CompletedTasks; //when threads are finished, they post a message that they are ready (for loadactivity)
    public TeamList teamList;
    public ArrayList<Team> m_OfflineAvailableTeamsList; //only filled in offline mode to indicate which teams have roster data
    public MLBApiResponse MLBGamesList;
    public MutableLiveData<LeagueList> m_leagueList;
    public MutableLiveData<Team> m_selectedTeam;
    public MLBRosterResponse m_teamRoster;
    public Context baseContext;
    public boolean m_VerifyIfOnline;
    public MLBTicket m_tempTicketDuringPayment;

    public MutableLiveData<List<MLBTicket>> m_TicketList;
    //paypal data
    public String m_PaymentSessionToken_Paypal;
    public String PAYPAL_CLIENT_ID;
    public String PAYPAL_SECRET_KEY1;

    MLBDataLayer(){
        m_CompletedTasks = new ArrayList<String>();
        teamList = new TeamList();
        LeagueList emptyList = new LeagueList();
        MLBGamesList = new MLBApiResponse();
        m_leagueList = new MutableLiveData<LeagueList>();
        m_leagueList.setValue(emptyList);
        m_selectedTeam = new MutableLiveData<Team>();
        m_selectedTeam.setValue(null);
        m_TicketList = new MutableLiveData<List<MLBTicket>>();
        m_TicketList.setValue(new ArrayList<MLBTicket>());
        m_teamRoster = null;
        baseContext = null;
        m_PaymentSessionToken_Paypal = "";
        m_VerifyIfOnline = false;
        m_tempTicketDuringPayment = null;
        m_OfflineAvailableTeamsList = new ArrayList<>();
        PAYPAL_CLIENT_ID = "AdbT4aXxgxiqs6DZi4WlhbNL2FdAJIlIwleHJRpkqrxbe66fjn6XAZLtSC91IAiDyVNtu4HcfggP-Uu9";
        PAYPAL_SECRET_KEY1 = "EJxr5kQ8GO0Zf-MtJN9b-o3ILy1N_cUOH71I9s0hw4FtsPeL_UjcrDLi-SgxU0O5Qq3HOL_Wpd14M33g";
    }

    public static synchronized MLBDataLayer getInstance(){
        if (dataLayer == null){
            dataLayer = new MLBDataLayer();
        }

        return dataLayer;
    }

    public void initTeams() {
        GetTeamsTask task = new GetTeamsTask();
        task.execute(2024);
    }

    public boolean isTeamDetailsAvailableOffline(Team t) {
        if (m_OfflineAvailableTeamsList.contains(t))
            return true;
        return false;
    }

    public boolean isOnline() {
        return m_VerifyIfOnline;
    }

    public void initialiseForTeamSelection() {
        // oproepen bij teamselection activity, als terug naar teamselection
        //alles terug klaarzetten voor nieuwe teamselectie en wachten op loaded
        // wissen van vorige geselecteerde team in repo

        clearCompletedTasks();

        m_selectedTeam.setValue(null);
        if(MLBGamesList == null)
            MLBGamesList = new MLBApiResponse();
        else
            MLBGamesList.RemoveAllGames();

        m_teamRoster = null;
        
        //remove all tickets
        List<MLBTicket> ticketlist = m_TicketList.getValue();
        ticketlist.clear();
        m_TicketList.postValue(ticketlist);
    }

    public void initGamesForSelectedTeam() {
        removeCompletedTask("GameListReady");
        GetGamesTask task = new GetGamesTask();
        String[] params = { "2024", m_selectedTeam.getValue().mlb_org_id };
        task.execute(params);
    }
    public void initLeaguesFromTeams(){
        LeagueList leagueList = m_leagueList.getValue();
        leagueList.clear();
        for (Team t: teamList) {
            if (!leagueList.LeagueIDExists(t.league_id)){
                League l = new League();
                l.name = t.league_full;
                l.shortName = t.league;
                l.id = t.league_id;
                l.m_imageName = t.league+".png";
                l.m_fullImageURL = "http://www.jursairplanefactory.com/baseballimg/team/"+ l.m_imageName;
                l.m_localFileSubFolder = "/images/league";
                leagueList.add(l);

                // invoke load images
                WebFetchImageTask webTask = new WebFetchImageTask(baseContext);
                webTask.m_image = null;
                webTask.execute(l);
            }
        }
        m_leagueList.setValue(leagueList);

    }

    public List<MLBGame> getGames(LocalDateTime datum, Team forTeam){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = datum.format(formatter);
        List<MLBGame> games = new ArrayList<>();
        if (MLBGamesList.dates != null){
            for (MLBDate date: MLBGamesList.dates){
                if (date.date.equals(formattedDate)){
                    games = date.getGamesForTeam(forTeam);
                }
            }
        }
        return games;
    }

    public Team getTeamWithMLBID(int mlbID){
        for (Team t: teamList){
            if(Integer.parseInt(t.mlb_org_id) == mlbID)
                return t;
        }
        return null;
    }

    public boolean areTeamsAvailableAndImagesRead(){
        if (teamList.size() == 0 || m_leagueList.getValue().size() == 0)
            return false;

        // team  images loaded ?
        for (Team t: teamList) {
            if (t.m_image == null)
                return false;
        }
        // league images loaded ?
        for (League l: m_leagueList.getValue()) {
            if (l.m_image == null)
                return false;
        }

        return true;
    }
    public boolean isStartupPhaseTwo_IsLoadReady() {
        // check if games are read
        if(isTaskCompleted("GameListReady") && isTaskCompleted("TeamRosterReady")) {
            //check if all players are read and player image is loaded
            if(m_teamRoster != null) {
                for (MLBRosterEntry entr : m_teamRoster.roster) {
                    if (entr.MLBPerson.m_image == null)
                        return false;
                }
            }
            //check if tickets are loaded
            if(isTaskCompleted("TicketsLoadedFromDB") == false)
                return false;

            return true;
        }
        return false;
    }
    public boolean isStartupPhaseTwo_FullyReady() {
        if(!isStartupPhaseTwo_IsLoadReady())
            return false;

        if(isOnline()) {
            //when online also save to roomdb must be completed, whenoffline no saving
            if(!isTaskCompleted("RosterSaved_ToDB") ||
                   !isTaskCompleted("GamesSaved_ToDB"))
                return false;

        }
        return true;
    }

    public void initRosterForTeam(Team team) {
        GetRosterTask task = new GetRosterTask();
        task.execute(team);
    }

    public void Get_PaymentSessionToken(){

          // een accesstoken is een soort van sessie
        String input = PAYPAL_CLIENT_ID + ":" + PAYPAL_SECRET_KEY1;
        String AUTH = Base64.getEncoder().encodeToString(input.getBytes());

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", "Basic "+ AUTH);
        String jsonString = "grant_type=client_credentials";
        HttpEntity entity = new StringEntity(jsonString, "utf-8");
        client.post(baseContext, "https://api-m.sandbox.paypal.com/v1/oauth2/token", entity, "application/x-www-form-urlencoded",new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.e("RESPONSE", response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                try {
                    JSONObject jobj = new JSONObject(response);
                    m_PaymentSessionToken_Paypal = jobj.getString("access_token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void setGamesList(MLBApiResponse response) {
        MLBGamesList = response;

        // remove double games (game is double if same gamePK and same gameGUID as another game in the answer
        // the api sometimes returns games twice
        ArrayList<Integer> listPrimaryKeys = new ArrayList<Integer>();
        for (MLBDate agamedate: MLBGamesList.dates) {
            for(MLBGame game : agamedate.getAllGames()) {
                if(listPrimaryKeys.contains(game.gamePk))
                {
                    //found a double
                    Log.d("games", "Found a double game: " + game.gamePk + " : " + game.gameGuid);
                    response.removeGame(game);
                }
                else
                    listPrimaryKeys.add(game.gamePk);
            }
        }
        // games list is ready to be used
        addCompletedTask("GameListReady");
    }

    public void saveNewTicketInRoomDB(MLBTicket ticket, Context c) {
        BaseballAppRoomDatabase db = BaseballAppRoomDatabase.getInstance(c);
         BaseballAppRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //Erna alles toevoegen
                // convert mlbticket to flat ticketrecord
                Room_MLBTicket roomticket = new Room_MLBTicket(ticket);
                db.ticketDao().insertTicket(roomticket);
            }
        });
    }

    public void saveMLBGamesInRoomDB() {
        BaseballAppRoomDatabase db = BaseballAppRoomDatabase.getInstance(baseContext);
        BaseballAppRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // MLBgames worden opgehaald na teamselection en enkel de games waar het team away of home team is
                // meteen na ophalen opgeslagen in room, eerst voorgaande games waar selected team is home of away wissen
                // dan nieuwe saven, zo kennen we altijd enkel de laatste nieuwe
                Team selectedTeam = m_selectedTeam.getValue();
                int teamid = Integer.parseInt(selectedTeam.mlb_org_id);
                db.gameDao().deleteGamesForTeamId(teamid);

                // convert mlbgame to flat room_game record for saving in roomDB
                ArrayList<Room_MLBGame> listgamestosave = new ArrayList<>();
                int count = 0;
                for (MLBDate agamedate: MLBGamesList.dates) {
                    for(MLBGame game : agamedate.getGamesForTeam(selectedTeam)) {
                        //Log.d("games", game.gamePk + " : " + game.teams.home.team.name + " vs " + game.teams.away.team.name + " -> " + String.valueOf(count));
                        Room_MLBGame a_db_game = new Room_MLBGame();
                        boolean isValid = a_db_game.initialiseFrom(game, agamedate, MLBDataLayer.getInstance());
                        count++;
                        //Log.d("games", a_db_game.gamePk + " : " + a_db_game.home_teamname + " vs " + a_db_game.away_teamname + " -> " + String.valueOf(count));
                        /*for(Room_MLBGame g : listgamestosave) {
                            if (g.key == a_db_game.key)
                                Log.d("DUBBEL !!! ", "DUBBEL !!! " + a_db_game.key + " : " + a_db_game.home_teamname + " vs " + a_db_game.away_teamname + " -> " + String.valueOf(count));
                        }*/
                        if(isValid)
                            listgamestosave.add(a_db_game);
                        else
                            Log.e("MLBGamesResponse", "Invalid game found : " + String.valueOf(game.gamePk));

                    }
                }
                if(listgamestosave.size() > 0)
                    db.gameDao().insertMultipleGames(listgamestosave);

                addCompletedTask("GamesSaved_ToDB");
            }
        });
    }

    public void loadTicketsFromRoomDB() {
        Task_LoadTicketsFromDB taskLoadTicketsFromDB = new Task_LoadTicketsFromDB();
        taskLoadTicketsFromDB.execute(this);
    }

    public void loadTeamsFromRoomDB() {
        Task_LoadTeamsFromDB task = new Task_LoadTeamsFromDB();
        task.execute(this);
    }

    public void loadGamesFromRoomDB() {
        Task_LoadGamesFromDB task = new Task_LoadGamesFromDB();
        task.execute(this);
    }

    public void loadTeamRosterFromRoomDB(Team team) {
        Task_LoadTeamRosterFromDB task = new Task_LoadTeamRosterFromDB(team);
        task.execute(this);
    }

    public void saveLeaguesToRoomDB() {
        BaseballAppRoomDatabase db = BaseballAppRoomDatabase.getInstance(baseContext);
        BaseballAppRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                db.leagueDao().deleteAllLeagues();
                List<League> leagueList = new ArrayList<League>();
                leagueList.addAll(m_leagueList.getValue());
                db.leagueDao().insertMultipleLeagues(leagueList);
                addCompletedTask("LeaguesSaved_ToDB");
            }
        });

    }

    public void saveTeamsToRoomDB() {
        BaseballAppRoomDatabase db = BaseballAppRoomDatabase.getInstance(baseContext);
        BaseballAppRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                db.teamDao().deleteAllTeams();
                db.teamDao().insertMultipleTeams(teamList);
                addCompletedTask("TeamsSaved_ToDB");
            }
        });

    }
    public void saveSelectedTeamRosterToRoomDB() {
        Team tosave = m_selectedTeam.getValue();
        BaseballAppRoomDatabase db = BaseballAppRoomDatabase.getInstance(baseContext);
        BaseballAppRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                db.rosterDao().deleteRosterEntryByTeamId(Integer.parseInt(tosave.mlb_org_id));

                //convert MLBRosterEntries to Room_MLBRosterEntry flat record structure
                List<Room_MLBRosterEntry> listOfRosterEntries = new ArrayList<Room_MLBRosterEntry>();
                for (MLBRosterEntry entry : m_teamRoster.roster) {
                    Room_MLBRosterEntry room_entry = new Room_MLBRosterEntry(entry);
                    listOfRosterEntries.add(room_entry);
                }
                db.rosterDao().insertMultipleRosterEntries(listOfRosterEntries);
                addCompletedTask("RosterSaved_ToDB");
            }
        });
    }

    public void loadAvailabilityOfTeamsInOfflineMode() {
        Task_LoadTeamsThatHaveOfflineRosterFromDB task = new Task_LoadTeamsThatHaveOfflineRosterFromDB();
        task.execute(this);
    }

    public void setOfflineAvailableTeamsList(ArrayList<Team> mlbavailableteamslist) {
        m_OfflineAvailableTeamsList = mlbavailableteamslist;
    }
    public void addCompletedTask(String taskname) {
        if(!m_CompletedTasks.contains(taskname))
            m_CompletedTasks.add(taskname);
    }
    public void removeCompletedTask(String taskname) {
        if(m_CompletedTasks.contains(taskname))
            m_CompletedTasks.remove(taskname);
    }
    public void clearCompletedTasks() {
        //only keep online check
        for (int idx = m_CompletedTasks.size()-1;idx >=0;idx--) {
            String s = m_CompletedTasks.get(idx);
            if(!s.equalsIgnoreCase("CheckedIfOnline"))
                m_CompletedTasks.remove(idx);
        }
    }
    public boolean isTaskCompleted(String taskname) {
        if(m_CompletedTasks.contains(taskname))
            return true;
        return false;
    }
    public void sortTicketsByGameDate() {
        // bubble sort on gamedate
        MLBTicket ticket1;
        MLBTicket ticket2;
        List<MLBTicket> ticketlist = m_TicketList.getValue();
        for(int i=0; i < ticketlist.size()-1;i++) {
            for(int j=i+1; j < ticketlist.size();j++) {
                ticket1 = ticketlist.get(i);
                ticket2 = ticketlist.get(j);
                if(ticket2.getGameDate_ZonedDateTime().isBefore(ticket1.getGameDate_ZonedDateTime())) {
                    ticketlist.set(i, ticket2);
                    ticketlist.set(j, ticket1);
                }
            }
        }
        m_TicketList.setValue(ticketlist);
    }
}
