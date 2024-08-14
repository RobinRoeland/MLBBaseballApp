package com.example.baseballapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.baseballapp.classes.MLB.MLBTicket;
import com.example.baseballapp.classes.roomDB.Room_MLBTicket;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface TicketDao {
    @Insert
    void insertTicket(Room_MLBTicket ticket);

    @Query("SELECT * FROM mlbticket_table")
    List<Room_MLBTicket> getAllTickets();

    @Delete
    void deleteTicket(Room_MLBTicket ticket);

    @Update
    void updateTicket(Room_MLBTicket ticket);

    @Insert
    void insertMultipleTickets(List<Room_MLBTicket> ticketList);

    @Query("DELETE FROM mlbticket_table")
    void deleteAllTickets();
}
