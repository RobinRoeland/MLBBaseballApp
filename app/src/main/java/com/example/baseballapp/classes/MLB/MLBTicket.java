package com.example.baseballapp.classes.MLB;

import com.example.baseballapp.classes.BitMapItem;
import com.example.baseballapp.classes.roomDB.Room_MLBTicket;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.data.MLBDataLayer;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MLBTicket extends BitMapItem {
    public String ticketNumber;
    public float ticketPrice;
    public String venueZone;
    public String venueBox;
    public String venueSeat;
    public String gameDate;
    public String purchasedBy;
    public String purchasedEmail;
    public String stadium;

    public MLBGame mlbGame;
    public Team homeTeam;
    public Team awayTeam;

    public String getFormattedStartTime(MLBDataLayer r) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
        LocalDateTime parsedDateTime = LocalDateTime.parse(gameDate, formatter);
        // Convert LocalDateTime to ZonedDateTime with UTC time zone
        ZonedDateTime zonedDateTimeUtc = ZonedDateTime.of(parsedDateTime, ZoneId.of("UTC"));

        DateTimeFormatter formatterOut = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // Format the LocalDateTime to a string
        String formattedDate = parsedDateTime.format(formatterOut);

        // Convert ZonedDateTime to hometeam time zone
        ZonedDateTime homeTeamZone = zonedDateTimeUtc.withZoneSameInstant(ZoneId.of(homeTeam.time_zone_alt));
        DateTimeFormatter parsedformatter = DateTimeFormatter.ofPattern("HH:mm a z");
        return formattedDate + " : " + homeTeamZone.format(parsedformatter);
    }

    public void initialiseWith(Room_MLBTicket dbticket, MLBDataLayer repo) {
        ticketNumber = dbticket.ticketNumber;
        ticketPrice = dbticket.ticketPrice;
        venueZone = dbticket.venueZone;
        venueBox = dbticket.venueBox;
        venueSeat = dbticket.venueSeat;
        purchasedBy = dbticket.purchasedBy;
        purchasedEmail = dbticket.purchasedEmail;
        //repo.getGameByGUID()
        homeTeam = repo.getTeamWithMLBID(Integer.parseInt(dbticket.hometeam_id));
        awayTeam = repo.getTeamWithMLBID(Integer.parseInt(dbticket.awayteam_id));
        m_imageName = dbticket.QRCodeImageName;
        gameDate = dbticket.gamedate;
        m_fullImageURL = dbticket.fullImageURL;
        m_localFileSubFolder = dbticket.localfilesubfolder;
        stadium = dbticket.stadium;
    }

    public String getTicketInfoString() {
        return "Zone : " + venueZone + "\nBox : " + venueBox + "\nSeat : " + venueSeat;
    }

    public ZonedDateTime getGameDate_ZonedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
        LocalDateTime utcDateTime = LocalDateTime.parse(gameDate, formatter);
        // Convert LocalDateTime to ZonedDateTime with UTC time zone
        ZonedDateTime zonedDateTimeUtc = ZonedDateTime.of(utcDateTime, ZoneId.of("UTC"));
        return zonedDateTimeUtc;
    }
}
