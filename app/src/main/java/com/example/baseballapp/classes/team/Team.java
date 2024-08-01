package com.example.baseballapp.classes.team;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.baseballapp.classes.BitMapItem;

@Entity(tableName = "team_table")
public class Team extends BitMapItem {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "team_id")
    public String team_id;
    public String team_code;
    public String venue_short;
    public String sport_id;
    public String league_abbrev;
    public String spring_league_id;
    public String active_sw;
    public String division;
    public String mlb_org_brief;
    public String season;
    public String first_year_of_play;
    public String state;
    public String name_short;
    public String bis_team_code;
    public String venue_id;
    public String name_display_short;
    public String name_display_long;
    public String name_display_brief;
    public String sport_code_name;
    public String spring_league;
    public String division_id;
    public String sport_code;
    public String time_zone_num;
    public String mlb_org;
    public String name_display_full;
    public String all_star_sw;
    public String division_abbrev;
    public String name;
    public String home_opener;
    public String phone_number;
    public String address_zip;
    public String time_zone_text;
    public String venue_name;
    public String division_full;
    public String franchise_code;
    public String city;
    public String time_zone_alt;
    public String address_state;
    public String name_abbrev;
    public String store_url;
    public String file_code;
    public String address_line3;
    public String address_line2;
    public String address_province;
    public String mlb_org_id;
    public String address_line1;
    public String spring_league_full;
    public String spring_league_abbrev;
    public String last_year_of_play;
    public String address;
    public String league_full;
    public String address_country;
    public String base_url;
    public String time_zone;
    public String address_city;
    public String mlb_org_abbrev;
    public String address_intl;
    public String time_zone_generic;
    public String website_url;
    public String sport_code_display;
    public String home_opener_time;
    public String mlb_org_short;
    public String league_id;
    public String league;
}
