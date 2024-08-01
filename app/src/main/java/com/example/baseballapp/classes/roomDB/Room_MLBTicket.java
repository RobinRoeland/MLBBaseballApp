package com.example.baseballapp.classes.roomDB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.baseballapp.classes.MLB.MLBTicket;

@Entity(tableName = "mlbticket_table")
public class Room_MLBTicket {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ticketnumber")
    public String ticketNumber;
    @ColumnInfo(name = "mlbgame_guid")
    public String mlbgame_guid;
    @ColumnInfo(name = "hometeam_id")
    public String hometeam_id;
    @ColumnInfo(name = "awayteam_id")
    public String awayteam_id;
    @ColumnInfo(name = "stadium")
    public String stadium;
    @ColumnInfo(name = "ticketprice")
    public float ticketPrice;
    @ColumnInfo(name = "venuezone")
    public String venueZone;
    @ColumnInfo(name = "venuebox")
    public String venueBox;
    @ColumnInfo(name = "venueseat")
    public String venueSeat;
    @ColumnInfo(name = "gamedate")
    public String gamedate;
    @ColumnInfo(name = "purchasedby")
    public String purchasedBy;
    @ColumnInfo(name = "purchasedemail")
    public String purchasedEmail;
    @ColumnInfo(name = "qrcodeimage")
    public String QRCodeImageName;
    @ColumnInfo(name = "localfilesubfolder")
    public String localfilesubfolder;
    @ColumnInfo(name = "fullimage_url")
    public String fullImageURL;
    public Room_MLBTicket()
    {
    }
    public Room_MLBTicket(MLBTicket fromTicket) {
        ticketNumber = fromTicket.ticketNumber;
        ticketPrice = fromTicket.ticketPrice;
        venueZone = fromTicket.venueZone;
        venueBox = fromTicket.venueBox;
        venueSeat = fromTicket.venueSeat;
        purchasedBy = fromTicket.purchasedBy;
        purchasedEmail = fromTicket.purchasedEmail;
        // enkel gebruikt indien gekend, games ophalen is enkel voor team, tickets kunnen voor andere eerder gekozen teams zijn
        // veiligste is om enkel hometeam id en away team id te gebruiken, die zijn altijd gekend
        mlbgame_guid = fromTicket.mlbGame.gameGuid;
        hometeam_id = fromTicket.homeTeam.mlb_org_id;
        awayteam_id = fromTicket.awayTeam.mlb_org_id;
        gamedate = fromTicket.gameDate;
        QRCodeImageName = fromTicket.m_imageName;
        fullImageURL = fromTicket.m_fullImageURL;
        localfilesubfolder = fromTicket.m_localFileSubFolder;
        stadium = fromTicket.stadium;
    }
}
