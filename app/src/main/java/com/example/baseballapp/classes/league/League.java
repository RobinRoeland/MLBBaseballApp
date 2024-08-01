package com.example.baseballapp.classes.league;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.baseballapp.classes.BitMapItem;

@Entity(tableName = "league_table")
public class League extends BitMapItem {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    public String id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "shortname")
    public String shortName;

}
