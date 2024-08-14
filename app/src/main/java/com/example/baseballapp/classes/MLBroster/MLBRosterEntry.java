package com.example.baseballapp.classes.MLBroster;

import com.example.baseballapp.classes.roomDB.Room_MLBRosterEntry;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MLBRosterEntry {
    @JsonProperty("person")
    public MLBPerson MLBPerson;
    @JsonProperty("jerseyNumber")
    public String jerseyNumber;
    @JsonProperty("position")
    public MLBPosition MLBPosition;
    @JsonProperty("status")
    public MLBPersonStatus MLBPersonStatus;
    @JsonProperty("parentTeamId")
    public int parentTeamId;

    public boolean compliesToFilter(String s) {
        s = s.toLowerCase();
        if(s.equals("")){
            return true;
        }
        if(MLBPerson.fullName.toLowerCase().contains(s)
                || jerseyNumber.toLowerCase().contains(s)
                || MLBPosition.code.toLowerCase().contains(s)
                || MLBPosition.type.toLowerCase().contains(s)
                || MLBPosition.name.toLowerCase().contains(s)
                || MLBPosition.abbreviation.toLowerCase().contains(s) ) {
            return true;
        }
        return false;
    }

    public void initialiseFromDB(Room_MLBRosterEntry entry) {
        jerseyNumber = entry.jerseyNumber;
        parentTeamId = entry.parentTeamId;
        MLBPosition = new MLBPosition();
        MLBPosition.code = entry.position_code;
        MLBPosition.name = entry.position_name;
        MLBPosition.type = entry.position_type;
        MLBPosition.abbreviation = entry.position_abbreviation;
        MLBPerson = new MLBPerson();
        MLBPerson.id = entry.person_id;
        MLBPerson.fullName = entry.person_fullName;
        MLBPerson.link = entry.person_link;
        MLBPerson.m_imageName = entry.imagename;
        MLBPerson.m_fullImageURL = entry.fullImageURL;
        MLBPerson.m_localFileSubFolder = entry.localfilesubfolder;
        MLBPersonStatus = new MLBPersonStatus();
        MLBPersonStatus.code = entry.status_code;
        MLBPersonStatus.description = entry.status_description;
    }
    // getters and setters
}

