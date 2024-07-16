package com.example.baseballapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.baseballapp.data.MLBDataLayer;
import com.example.baseballapp.databinding.ActivityLoadBinding;
import com.example.baseballapp.databinding.ActivityTeamSelectionBinding;
import com.example.baseballapp.tasks.Task_VerifyOnlineStatus;

import java.util.Timer;
import java.util.TimerTask;


public class LoadActivity extends AppCompatActivity {
    private MLBDataLayer repo;
    private ActivityLoadBinding binding;
    enum loadphases { phaseCheckOnline, phaseWaitingForReturnOnline, phaseLoadOne, phaseLoadTwo_TeamSelection };
    private loadphases m_phase;

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

        Intent intent = getIntent();
        // after team selection, show load activity again starting from phase 2
        String startupphase = intent.getStringExtra("startupphase");
        // default phase 1
        m_phase = loadphases.phaseCheckOnline;
        if(startupphase != null && startupphase.equalsIgnoreCase("phaseloadtwo_teamselection"))
            m_phase = loadphases.phaseLoadTwo_TeamSelection;

        handleStartOfActivityBasedOnPhase();

        startTimer();
    }

    private void startTimer() {
        Timer checkStatusTimer = new Timer();
        checkStatusTimer.schedule(new TimerTask() {
            @Override
            public void run() {

            switch(m_phase) {
                case phaseCheckOnline:
                    Task_VerifyOnlineStatus checkTask = new Task_VerifyOnlineStatus();
                    checkTask.execute(repo);
                    binding.loadBusyWith.setText("Checking online status.");
                    m_phase = loadphases.phaseWaitingForReturnOnline;
                    break;

                case phaseWaitingForReturnOnline:
                    if (repo.m_CheckedIfOnline){
                        m_phase = loadphases.phaseLoadOne;
                        handleStartOfActivityBasedOnPhase();
                    }
                    break;

                case phaseLoadOne:
                    if(repo.isStartupPhaseOneReady()){
                        checkStatusTimer.cancel();
                        Intent goToActivity = new Intent(getBaseContext(), TeamSelectionActivity.class);
                        startActivity(goToActivity);
                        finish();
                    }
                    break;

                case phaseLoadTwo_TeamSelection:
                    checkStatusTimer.cancel();
                    Intent goToActivity = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(goToActivity);
                    // by adding finish, this activity will not be on the backstack, so back will go back to team list and not loading screen
                    finish();
                    break;
            }
            }
        }, 1000, 500);

    }

    private void handleStartOfActivityBasedOnPhase() {
        switch (m_phase) {
            case phaseLoadOne:
                binding.loadBusyWith.setText("Loading teams and logo's");
                repo.startupLoadPhaseOne();
                break;
            case phaseLoadTwo_TeamSelection:
                binding.loadBusyWith.setText("Loading selected team details");
                repo.initGamesForSelectedTeam();
                repo.initRosterForTeam(repo.m_selectedTeam.getValue());
                break;

        }
    }
}