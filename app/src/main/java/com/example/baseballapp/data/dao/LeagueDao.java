package com.example.baseballapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.baseballapp.classes.league.League;

import java.util.List;

@Dao
public interface LeagueDao {
    @Insert
    void insertLeague(League league);

    @Query("SELECT * FROM league_table")
    List<League> getAllLeagues();

    @Delete
    void deleteLeague(League league);

    @Update
    void updateLeague(League league);

    @Insert
    void insertMultipleLeagues(List<League> leagueList);

    @Query("DELETE FROM league_table")
    void deleteAllLeagues();
}
