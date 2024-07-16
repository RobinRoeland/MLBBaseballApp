package com.example.baseballapp.classes.stadium;

import android.util.Log;

import java.util.ArrayList;

public class VenueZone {
    public String m_ZoneName;
    public ArrayList<VenueBox> m_BoxesList;

    public VenueZone(){
        m_BoxesList = new ArrayList<>();
    }
    @Override
    public String toString(){
        return m_ZoneName;
    }

    public void parseMultiBox(String partsFromFile){
        String[] parts = partsFromFile.split(";");
        if (parts.length < 4){
            Log.e("VenueZone", "Insufficient number of fields in parse of venueboxes: " + partsFromFile);
            return;
        }

        //0 = beginbox
        int beginbox = Integer.parseInt(parts[0]);
        //1 = eindbox
        int eindbox = Integer.parseInt(parts[1]);

        if (eindbox < beginbox){
            Log.e("VenueZone", "Boxnumbering incorrect: endbox is smaller than beginbox: " + partsFromFile);
            return;
        }

        VenueBox currentBox = null;
        for (int i = beginbox; i <= eindbox; i++) {
            currentBox = new VenueBox();
            currentBox.m_BoxName = String.valueOf(i);
            //2 = aantal seats
            currentBox.m_BoxSeats = Integer.parseInt(parts[2]);
            //3 = prijs
            currentBox.m_BoxPrice = Float.parseFloat(parts[3]);

            m_BoxesList.add(currentBox);
        }
    }
}
