package com.example.baseballapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.data.BaseballAppRoomDatabase;
import com.example.baseballapp.data.MLBDataLayer;
import com.example.baseballapp.databinding.ActivityLoadBinding;
import com.example.baseballapp.tasks.Task_VerifyOnlineStatus;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class LoadActivity extends AppCompatActivity {
    private MLBDataLayer repo;
    private ActivityLoadBinding binding;
    enum loadphases { phaseCheckOnline, phaseWaitingForReturnOnline, phaseLoadOne_readteams, phaseLoadOne_saveToDB, phaseLoadOne_ReadyForMoveToTeamSelectionAct, phaseLoadTwo_Start, phaseLoadTwo_TeamSelectionReady, phaseLoadTwo_saveToDB, phaseEndAppAfterPause }

    private loadphases m_phase;

    private long AcitivityEndTimeInMilleseconds; //to end application after x sec
    private boolean ExitAppAfterMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        LayoutInflater inflater = getLayoutInflater();
        View root = inflater.inflate(R.layout.activity_load, null);
        setContentView(root);
        binding = ActivityLoadBinding.bind(root);

        repo = MLBDataLayer.getInstance();
        repo.baseContext = getBaseContext();

        binding.loadErrorMessageTv.setVisibility(View.INVISIBLE);
        AcitivityEndTimeInMilleseconds = 0;
        ExitAppAfterMessage = false;

        Intent intent = getIntent();
        // after team selection, show load activity again starting from phase 2
        String startupphase = intent.getStringExtra("startupphase");
        boolean selectedTeamSet = intent.getBooleanExtra("selected_team_set", false); // contains 1 if phase 2 team loading
        // default phase 1
        m_phase = loadphases.phaseCheckOnline;

        if(startupphase != null && startupphase.equalsIgnoreCase("phaseloadtwo_teamselection")) {
            // second load after team clicked, start from other phase
            m_phase = loadphases.phaseLoadTwo_Start;

            //if team selected, load logo
            if (selectedTeamSet) {
                Team selectedTeam = repo.m_selectedTeam.getValue();
                //image is already loaded here, can be set from selectedTeam without loading
                binding.loadLogoImage.setImageBitmap(selectedTeam.m_image);
                binding.loadLogoText.setText(selectedTeam.name_display_long);
            }
        }

        startTimer();
    }

    private void startTimer() {
        Timer checkStatusTimer = new Timer();
        checkStatusTimer.schedule(new TimerTask() {
            @Override
            public void run() {

            switch(m_phase) {
                case phaseCheckOnline:
                    // phase 1: step 1, check if online or offline by calling a web link
                    binding.loadBusyWith.setText("Checking online status");
                    Task_VerifyOnlineStatus checkTask = new Task_VerifyOnlineStatus();
                    checkTask.execute(repo);
                    m_phase = loadphases.phaseWaitingForReturnOnline;
                    break;

                case phaseWaitingForReturnOnline:
                    //repo.addCompletedTask("CheckedIfOnline");
                    //repo.m_VerifyIfOnline = true; // force offline mode
                    if(repo.isTaskCompleted("CheckedIfOnline")) {
                        m_phase = loadphases.phaseLoadOne_readteams;
                        if (repo.isOnline()){
                            binding.loadBusyWith.setText("Loading teams and logo's");
                            repo.initTeams();
                        }
                        else {
                            binding.loadBusyWith.setText("Loading teams and logo's from local DB");
                            repo.loadTeamsFromRoomDB();
                            repo.loadAvailabilityOfTeamsInOfflineMode();
                        }
                    }
                    break;

                case phaseLoadOne_readteams:
                    if(repo.isOnline()) {
                        if(repo.areTeamsAvailableAndImagesRead()){
                            binding.loadBusyWith.setText("Saving to local DB");
                            m_phase = loadphases.phaseLoadOne_saveToDB;
                            BaseballAppRoomDatabase db = BaseballAppRoomDatabase.getInstance(MLBDataLayer.getInstance().baseContext);
                            BaseballAppRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    //db.clearAllTables();
                                }
                            });
                            repo.saveLeaguesToRoomDB();
                            repo.saveTeamsToRoomDB();
                        }
                    }
                    else {
                        // offline
                        if(repo.isTaskCompleted("TeamsLoaded_FromDB") && repo.teamList.size() == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showErrorMessage("No teams in RoomDB !\nUse the app online before first using it.\nExiting Application.", 10, true);
                                }
                            });
                            m_phase = loadphases.phaseEndAppAfterPause;
                        }
                        if(repo.isTaskCompleted("TeamsHavingOfflineRoster") &&
                                repo.isTaskCompleted("TeamsLoaded_FromDB"))
                        {
                            m_phase = loadphases.phaseLoadOne_ReadyForMoveToTeamSelectionAct;
                        }
                    }
                    break;
                case phaseLoadOne_saveToDB:
                    // only in online mode, is saving to db, only proceed when save is completed
                    if(repo.isTaskCompleted("TeamsSaved_ToDB") &&
                            repo.isTaskCompleted("LeaguesSaved_ToDB"))
                        m_phase = loadphases.phaseLoadOne_ReadyForMoveToTeamSelectionAct;
                    break;
                case phaseLoadOne_ReadyForMoveToTeamSelectionAct:
                    // save leagues and teams to local db, this is the end of phase One
                    checkStatusTimer.cancel();
                    Intent goToActivitySelTeam = new Intent(getBaseContext(), TeamSelectionActivity.class);
                    startActivity(goToActivitySelTeam);
                    finish();
                    break;

                // volgende zijn de stappen voor 2de manier van laden na teamselectie
                case phaseLoadTwo_Start:
                    binding.loadBusyWith.setText("Loading selected team details");
                    if(repo.isOnline()) {
                        repo.initGamesForSelectedTeam();
                        repo.initRosterForTeam(repo.m_selectedTeam.getValue());
                        m_phase = loadphases.phaseLoadTwo_saveToDB;
                    }
                    else {
                        repo.loadGamesFromRoomDB();
                        repo.loadTeamRosterFromRoomDB(repo.m_selectedTeam.getValue());
                        m_phase = loadphases.phaseLoadTwo_TeamSelectionReady;
                    }
                    repo.loadTicketsFromRoomDB();
                    break;
                case phaseLoadTwo_saveToDB:
                    //only happens when online
                    if(repo.isStartupPhaseTwo_IsLoadReady()) {
                        m_phase = loadphases.phaseLoadTwo_TeamSelectionReady;
                        //start save to db of roster and games
                        // save loaded games to room db
                        repo.saveMLBGamesInRoomDB();
                        repo.saveSelectedTeamRosterToRoomDB();
                    }
                    break;
                case phaseLoadTwo_TeamSelectionReady:
                    if(repo.isStartupPhaseTwo_FullyReady()) {
                        m_phase = loadphases.phaseLoadOne_saveToDB;
                        checkStatusTimer.cancel();
                        Intent goToActivity = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(goToActivity);
                        // by adding finish, this activity will not be on the backstack, so back will go back to team list and not loading screen
                        finish();
                    }
                    break;
                case phaseEndAppAfterPause:
                    Calendar calendarcurtime = Calendar.getInstance();

                    long currenttime  = calendarcurtime.getTimeInMillis();
                    if(currenttime > AcitivityEndTimeInMilleseconds) {
                        if(ExitAppAfterMessage) {
                            //stop the application
                            finishAffinity();
                        }
                        else {
                            getOnBackPressedDispatcher().onBackPressed();
                        }

                    }
                    break;

            }
            }
        }, 1000, 500);

    }
    public void showErrorMessage(String msg, int numsecond, boolean exitApp)
    {
        //if exitapp : true -> end the application
        //if exitapp : false -> pop backstack
        binding.loadSplashBack.setVisibility(View.INVISIBLE);
        binding.loadErrorMessageTv.setVisibility(View.VISIBLE);
        binding.loadErrorMessageTv.setText(msg);

        // Get the current time
        Calendar calendar = Calendar.getInstance();
        // Add 5 seconds to the current time
        calendar.add(Calendar.SECOND, numsecond);
        // Get the updated time
        AcitivityEndTimeInMilleseconds = calendar.getTimeInMillis();
        ExitAppAfterMessage = exitApp;
        if(exitApp)
            binding.loadBusyWith.setText("Exiting application...");
        else
            binding.loadBusyWith.setText("Back to previous screen...");
    }
}