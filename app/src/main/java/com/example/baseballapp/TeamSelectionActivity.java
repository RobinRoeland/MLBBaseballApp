package com.example.baseballapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.baseballapp.classes.league.LeagueList;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.classes.team.TeamList;
import com.example.baseballapp.data.MLBDataLayer;
import com.example.baseballapp.databinding.ActivityTeamSelectionBinding;
import com.example.baseballapp.tasks.WebFetchImageTask;
import com.example.baseballapp.ui.TeamSelectionActViewModel;
import com.example.baseballapp.ui.adapters.LeagueAdapter;
import com.example.baseballapp.ui.adapters.TeamAdapter;

public class TeamSelectionActivity extends AppCompatActivity {

    TeamSelectionActViewModel leagueViewModel;
    ActivityTeamSelectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        MLBDataLayer.getInstance().init();

        leagueViewModel = new TeamSelectionActViewModel();

        LayoutInflater inflater = getLayoutInflater();
        View root = inflater.inflate(R.layout.activity_team_selection, null);
        setContentView(root);
        binding = ActivityTeamSelectionBinding.bind(root);

        if (leagueViewModel.m_mlbLogo.m_image != null)
        {
            binding.mlbImg.setImageBitmap(leagueViewModel.m_mlbLogo.m_image);
        }
        else {
            WebFetchImageTask webTask = new WebFetchImageTask();
            webTask.m_image = binding.mlbImg;
            webTask.execute(leagueViewModel.m_mlbLogo);
        }

        leagueViewModel.m_displayLeagueList = MLBDataLayer.getInstance().m_leagueList.getValue();
        LeagueAdapter leagueAdapter = new LeagueAdapter(leagueViewModel, getBaseContext(), leagueViewModel.m_displayLeagueList);
        binding.schRecyclerLeagues.setAdapter(leagueAdapter);
        LinearLayoutManager lManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.schRecyclerLeagues.setLayoutManager(lManager);

        TeamAdapter teamAdapter = new TeamAdapter(getBaseContext(), leagueViewModel.m_filteredTeamList.getValue());
        binding.schRecyclerTeams.setAdapter(teamAdapter);
        LinearLayoutManager tManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        binding.schRecyclerTeams.setLayoutManager(tManager);

        initObservers();
    }
    public void initObservers(){
        MLBDataLayer.getInstance().m_leagueList.observe(this, new Observer<LeagueList>() {
            @Override
            public void onChanged(LeagueList leagueList) {
                leagueViewModel.m_displayLeagueList = leagueList;
                LeagueAdapter adapter = (LeagueAdapter)binding.schRecyclerLeagues.getAdapter();
                adapter.setDisplayList(leagueList);
                adapter.notifyDataSetChanged();
            }
        });

        leagueViewModel.m_filteredTeamList.observe(this, new Observer<TeamList>() {
            @Override
            public void onChanged(TeamList teams) {
                TeamAdapter adapter = (TeamAdapter)binding.schRecyclerTeams.getAdapter();
                adapter.setDisplayList(teams);
                adapter.notifyDataSetChanged();
            }
        });

        MLBDataLayer.getInstance().m_selectedTeam.observe(this, new Observer<Team>() {
            @Override
            public void onChanged(Team team) {
                if (team != null){
                    MLBDataLayer.getInstance().initGamesForSelectedTeam();
                    Intent goToActivity = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(goToActivity);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}