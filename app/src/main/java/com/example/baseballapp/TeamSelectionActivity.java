package com.example.baseballapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.baseballapp.classes.MLB.MLBGame;
import com.example.baseballapp.classes.league.League;
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

        leagueViewModel = new TeamSelectionActViewModel();
        leagueViewModel.initImages(getBaseContext()); //laods the mlblogobmp

        // initialse repo for teamselection
        MLBDataLayer.getInstance().initialiseForTeamSelection();

        LayoutInflater inflater = getLayoutInflater();
        View root = inflater.inflate(R.layout.activity_team_selection, null);
        setContentView(root);
        binding = ActivityTeamSelectionBinding.bind(root);

        if (leagueViewModel.m_mlbLogo.m_image != null)
        {
            binding.mlbImg.setImageBitmap(leagueViewModel.m_mlbLogo.m_image);
        }

        leagueViewModel.m_displayLeagueList = MLBDataLayer.getInstance().m_leagueList.getValue();
        LeagueAdapter leagueAdapter = new LeagueAdapter(leagueViewModel, getBaseContext(), leagueViewModel.m_displayLeagueList);
        binding.schRecyclerLeagues.setAdapter(leagueAdapter);
        LinearLayoutManager lManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.schRecyclerLeagues.setLayoutManager(lManager);

        TeamAdapter teamAdapter = new TeamAdapter(getBaseContext(), leagueViewModel.m_filteredTeamList.getValue(),MLBDataLayer.getInstance());
        binding.schRecyclerTeams.setAdapter(teamAdapter);
        LinearLayoutManager tManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        binding.schRecyclerTeams.setLayoutManager(tManager);

        initObservers();
    }
    public void initObservers(){
        MLBDataLayer.getInstance().m_leagueList.observe(this, new Observer<LeagueList>() {
            @Override
            public void onChanged(LeagueList leagueList) {
                if(leagueList==null)
                    return;
                leagueViewModel.m_displayLeagueList = leagueList;
                LeagueAdapter adapter = (LeagueAdapter)binding.schRecyclerLeagues.getAdapter();
                adapter.setDisplayList(leagueList);
                adapter.notifyDataSetChanged();
                binding.schRecyclerTeams.scrollToPosition(0);

                if(leagueViewModel.m_currentLeagueID.getValue() != null) {
                    if (leagueViewModel.m_currentLeagueID.getValue().equals("")) {
                        // no current selected, pick first as selected
                        League defaultleague = leagueList.get(0);
                        leagueViewModel.m_currentLeagueID.postValue(defaultleague.id);
                    }
                }
            }
        });
        //when selected league is changed, change teamsfiltered list
        leagueViewModel.m_currentLeagueID.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String changedLeagueId) {
                leagueViewModel.setFilteredTeamList(MLBDataLayer.getInstance().teamList, changedLeagueId);
                LeagueAdapter adapter = (LeagueAdapter)binding.schRecyclerLeagues.getAdapter();
                adapter.notifyDataSetChanged();//this will call onbindviewholders to change selected color

            }
        });
        // check for change of teamlist (happens on league selection
        leagueViewModel.m_filteredTeamList.observe(this, new Observer<TeamList>() {
            @Override
            public void onChanged(TeamList teams) {
                TeamAdapter adapter = (TeamAdapter)binding.schRecyclerTeams.getAdapter();
                adapter.setDisplayList(teams);
                adapter.notifyDataSetChanged();
                binding.schRecyclerTeams.scrollToPosition(0);
            }
        });

        // if the selected team is changed (set from onclick in teamadapter in recycler, then jump to detail of team and go to load activity
        MLBDataLayer.getInstance().m_selectedTeam.observe(this, new Observer<Team>() {
            @Override
            public void onChanged(Team team) {
                if (team != null){
                    Intent goToActivity = new Intent(getBaseContext(), LoadActivity.class);
                    goToActivity.putExtra("startupphase", "phaseloadtwo_teamselection");
                    goToActivity.putExtra("selected_team_set", true);
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