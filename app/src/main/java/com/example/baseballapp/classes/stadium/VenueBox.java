package com.example.baseballapp.classes.stadium;

import android.util.Log;

import java.util.Random;

public class VenueBox {
    public String m_BoxName;
    public int m_BoxSeats;
    public float m_BoxPrice;

    @Override
    public String toString(){
        return m_BoxName;
    }

    public void parseFromString(String partsFromFile){
        String[] parts = partsFromFile.split(";");
        if (parts.length < 3){
            Log.e("VenueBox", "Insufficient number of fields in parse of venuebox: " + partsFromFile);
            return;
        }

        m_BoxName = parts[0];
        m_BoxSeats = Integer.parseInt(parts[1]);
        m_BoxPrice = Float.parseFloat(parts[2]);
    }

    public String getRandomSeatNr() {
        Random r = new Random();
        int seatnr = r.nextInt(m_BoxSeats) + 1;
        return String.valueOf(seatnr);
    }
}
