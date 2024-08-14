package com.example.baseballapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.baseballapp.classes.MLB.MLBTicket;
import com.example.baseballapp.classes.team.Team;

import java.util.List;

@Dao
public interface TeamDao {
    @Insert
    void insertTeam(Team team);

    @Query("SELECT * FROM team_table")
    List<Team> getAllTeams();

    @Delete
    void deleteTeam(Team team);

    @Update
    void updateTeam(Team team);

    @Insert
    void insertMultipleTeams(List<Team> teamList);

    @Query("DELETE FROM team_table")
    void deleteAllTeams();
}
