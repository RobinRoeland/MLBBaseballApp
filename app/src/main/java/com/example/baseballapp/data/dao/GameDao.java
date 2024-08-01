package com.example.baseballapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.baseballapp.classes.roomDB.Room_MLBGame;

import java.util.List;

@Dao
public interface GameDao {
    @Insert
    void insertGame(Room_MLBGame game);

    @Query("SELECT * FROM mlbgame_table")
    List<Room_MLBGame> getAllgames();

    @Delete
    void deleteGame(Room_MLBGame game);

    @Update
    void updateGame(Room_MLBGame game);

    @Insert
    void insertMultipleGames(List<Room_MLBGame> gameList);

    // Method to delete users based on their age
    @Query("DELETE FROM mlbgame_table WHERE hometeam_id = :teamid OR awayteam_id = :teamid")
    void deleteGamesForTeamId(int teamid);
    
    @Query("DELETE FROM mlbgame_table")
    void deleteAllGames();
}
