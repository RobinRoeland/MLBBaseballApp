package com.example.baseballapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.baseballapp.classes.roomDB.Room_MLBRosterEntry;

import java.util.List;

@Dao
public interface RosterDao {
    @Insert
    void insertRosterEntry(Room_MLBRosterEntry entry);

    @Query("SELECT * FROM roster_table")
    List<Room_MLBRosterEntry> getAllRosterEntries();

    @Delete
    void deleteRosterEntry(Room_MLBRosterEntry entry);

    @Update
    void updateRosterEntry(Room_MLBRosterEntry entry);

    @Insert
    void insertMultipleRosterEntries(List<Room_MLBRosterEntry> entryList);

    @Query("DELETE FROM roster_table")
    void deleteAllRosterEntries();

    // Method to delete players linked to a team with given id
    @Query("DELETE FROM roster_table WHERE parentTeamId = :teamid")
    void deleteRosterEntryByTeamId(int teamid);

    // return a list of all parent team id's found in the rostertable.
    @Query("SELECT distinct parentTeamId FROM roster_table")
    List<Integer> getDistinctTeamsFromRoster();

    @Query("SELECT * FROM roster_table WHERE parentTeamId = :teamid")
    List<Room_MLBRosterEntry> getAllRosterEntriesForTeam(int teamid);

}
