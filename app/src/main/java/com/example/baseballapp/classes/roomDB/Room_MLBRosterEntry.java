package com.example.baseballapp.classes.roomDB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.baseballapp.classes.MLB.MLBTicket;
import com.example.baseballapp.classes.MLBroster.MLBPerson;
import com.example.baseballapp.classes.MLBroster.MLBPersonStatus;
import com.example.baseballapp.classes.MLBroster.MLBPosition;
import com.example.baseballapp.classes.MLBroster.MLBRosterEntry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity(tableName = "roster_table")
public class Room_MLBRosterEntry {
    public String jerseyNumber;
    public int parentTeamId;
    //MLBPerson
    public int person_id;
    public String person_fullName;
    public String person_link;
    //MLBPosition
    public String position_code;
    public String position_name;
    public String position_type;
    public String position_abbreviation;
    //MLBPersonStatus;
    public String status_code;
    public String status_description;
    // Bitmapitem base from MLBPerson
    public String imagename;
    public String localfilesubfolder;
    public String fullImageURL;
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    @JsonIgnore
    public String generatedPrimaryKey;

    public Room_MLBRosterEntry()
    {
    }
    public Room_MLBRosterEntry(MLBRosterEntry fromRosterEntry) {
        jerseyNumber = fromRosterEntry.jerseyNumber;
        parentTeamId = fromRosterEntry.parentTeamId;
        person_id = fromRosterEntry.MLBPerson.id;
        person_fullName = fromRosterEntry.MLBPerson.fullName;
        person_link = fromRosterEntry.MLBPerson.link;
        imagename = fromRosterEntry.MLBPerson.m_imageName;
        fullImageURL = fromRosterEntry.MLBPerson.m_fullImageURL;
        localfilesubfolder = fromRosterEntry.MLBPerson.m_localFileSubFolder;
        position_code = fromRosterEntry.MLBPosition.code;
        position_abbreviation = fromRosterEntry.MLBPosition.abbreviation;
        position_type = fromRosterEntry.MLBPosition.type;
        position_name = fromRosterEntry.MLBPosition.name;
        status_code = fromRosterEntry.MLBPersonStatus.code;
        status_description = fromRosterEntry.MLBPersonStatus.description;
        // generate a primary key for saving later in room DB
        generatedPrimaryKey = fromRosterEntry.parentTeamId + "_" + fromRosterEntry.MLBPerson.id;

    }
}
