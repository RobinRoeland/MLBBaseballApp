package com.example.baseballapp.classes.MLB;

import android.content.Context;
import android.util.Log;

import com.example.baseballapp.LoadActivity;
import com.example.baseballapp.classes.stadium.VenueBox;
import com.example.baseballapp.classes.stadium.VenueZone;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MLBVenue {
    @JsonProperty("id")
    public int id;

    @JsonProperty("name")
    public String name;

    @JsonProperty("link")
    private String link;

    @JsonIgnore
    public ArrayList<VenueZone> m_zones;

    public MLBVenue(){
        m_zones = new ArrayList();
    }

    //Dit leest the structuur van de zones van het stadium uit van een file in de assets src/main/assets folder
    public void OpenStadiumTicketsZoneFile(Context context, String filename) {
        m_zones.clear();

        InputStream inputStream = MLBVenue.class.getClassLoader().getResourceAsStream(filename);
        if (inputStream == null){
            inputStream = MLBVenue.class.getClassLoader().getResourceAsStream("seats_std.txt");
            if (inputStream == null){
                Log.e("MLBVenue", "Missing Standard Seat File");
                return;
            }
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            VenueZone currentZone = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.isEmpty())
                    continue;

                String[] parts = line.split(":");
                if (parts.length > 0) { // CommandoLocatie : DetailInfo afhankelijk van commando locatie
                    String commandoLocatie = parts[0];

                    switch (commandoLocatie){
                        case "Field":
                            currentZone = new VenueZone();
                            currentZone.m_ZoneName = parts[1];
                            m_zones.add(currentZone);
                            break;
                        case "Boxes":
                            if (currentZone == null){
                                Log.e("MLBVenue", "Syntax Error: Boxes given without preceding zone in file name:" + filename);
                                continue;
                            }
                            currentZone.parseMultiBox(parts[1]);
                            break;
                        case "Box":
                            if (currentZone == null){
                                Log.e("MLBVenue", "Syntax Error: Box given without preceding zone in file name:" + filename);
                                continue;
                            }

                            VenueBox box = new VenueBox();
                            box.parseFromString(parts[1]);
                            currentZone.m_BoxesList.add(box);
                            break;
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
