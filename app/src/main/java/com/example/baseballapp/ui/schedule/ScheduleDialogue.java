package com.example.baseballapp.ui.schedule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.DialogFragment;

import com.example.baseballapp.R;
import com.example.baseballapp.classes.BitMapItem;
import com.example.baseballapp.classes.MLB.MLBGame;
import com.example.baseballapp.classes.MLB.MLBTeamInfo;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.data.MLBDataLayer;
import com.example.baseballapp.databinding.DialogScheduleBinding;
import com.example.baseballapp.tasks.WebFetchImageTask;
import com.example.baseballapp.ui.tickets.TicketDialog;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ScheduleDialogue extends DialogFragment {
    private DialogScheduleBinding binding;

    private MLBGame m_game;
    private BitMapItem bmpForStadium;
    private BitMapItem bmpForHome;
    private BitMapItem bmpForAway;

    public ScheduleDialogue(MLBGame game){
        m_game = game;
        bmpForStadium = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.TransparentDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogScheduleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Team homeTeam = MLBDataLayer.getInstance().getTeamWithMLBID(m_game.getHomeTeam().id);
        Team awayTeam = MLBDataLayer.getInstance().getTeamWithMLBID(m_game.getOpponent(homeTeam).id);

        //Load seating data from venue
        String fileName = "assets/seats_" + homeTeam.name_abbrev + ".txt";
        m_game.venue.OpenStadiumTicketsZoneFile(getContext(), fileName);

        //Stadium image
        bmpForStadium = new BitMapItem();
        bmpForStadium.m_imageName = "S_" + homeTeam.name_abbrev + ".webp";
        bmpForStadium.m_localFileSubFolder = "/images/stadium";
        bmpForStadium.m_fullImageURL = "http://www.jursairplanefactory.com/baseballimg/" + "stadium/" + bmpForStadium.m_imageName;
        WebFetchImageTask webTaskStadium = new WebFetchImageTask(getContext());
        webTaskStadium.m_image = binding.dialogScheduleStadium;
        webTaskStadium.execute(bmpForStadium);

        //Stadium Name
        binding.dialogScheduleStadiumname.setText(homeTeam.venue_name);

        //Stadium Address
        String address = homeTeam.address_line1 + ", " + homeTeam.address_zip + " " + homeTeam.city + " " + homeTeam.state;
        binding.dialogScheduleStadiumAddress.setText(address);

        //Home Team name
        binding.dialogScheduleHometeam.setText(homeTeam.name_short);
        //Away Team name
        binding.dialogScheduleAwayteam.setText(awayTeam.name_short);

        //Home Team image
        WebFetchImageTask webTaskHome = new WebFetchImageTask(getContext());
        webTaskHome.m_image = binding.dialogScheduleHometeamImage;
        webTaskHome.execute(homeTeam);

        //Away Team image
        WebFetchImageTask webTaskAway = new WebFetchImageTask(getContext());
        webTaskAway.m_image = binding.dialogScheduleAwayteamImage;
        webTaskAway.execute(awayTeam);

        //Division
        binding.dialogScheduleDevision.setText(homeTeam.division_full);

        //Time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
        LocalDateTime parsedDateTime = LocalDateTime.parse(m_game.gameDate, formatter);

        // Convert LocalDateTime to ZonedDateTime with UTC time zone
        ZonedDateTime zonedDateTimeUtc = ZonedDateTime.of(parsedDateTime, ZoneId.of("UTC"));

        // Convert ZonedDateTime to New York time zone
        ZonedDateTime homeTeamZone = zonedDateTimeUtc.withZoneSameInstant(ZoneId.of(homeTeam.time_zone_alt));

        DateTimeFormatter parsedformatter = DateTimeFormatter.ofPattern("dd-MM HH:mm a z");
        binding.dialogScheduleTime.setText(homeTeamZone.format(parsedformatter));

        binding.dialogScheduleAccomodation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTimeFormatter parsedformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                ZonedDateTime checkout = homeTeamZone.plusDays(1);

                String link = "https://www.booking.com/searchresults.en-gb.html?ss="+ homeTeam.city +"&ssne=" + homeTeam.city + "&lang=en-gb&sb=1&src_elem=sb&dest_type=city&checkin=" + homeTeamZone.format(parsedformatter) + "&checkout=" + checkout.format(parsedformatter) + "&group_adults=1&no_rooms=1&group_children=0";

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(getActivity(), Uri.parse(link));
            }
        });

        binding.dialogScheduleTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketDialog dialog = new TicketDialog(m_game);
                dismiss();
                dialog.show(getParentFragmentManager(), "ticket");
            }
        });
    }
}