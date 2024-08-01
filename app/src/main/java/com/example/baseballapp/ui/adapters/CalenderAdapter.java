package com.example.baseballapp.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baseballapp.R;
import com.example.baseballapp.classes.MLB.MLBGame;
import com.example.baseballapp.classes.MLB.MLBTeamInfo;
import com.example.baseballapp.classes.functionlib.CalenderHelpFunctions;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.data.MLBDataLayer;
import com.example.baseballapp.ui.dialogs.ScheduleDialog;
import com.example.baseballapp.ui.schedule.ScheduleFragment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CalenderAdapter extends RecyclerView.Adapter<CalenderAdapter.BaseCalenderItemViewHolder> {
    enum ECalenderViewTypes {
        noCalenderDay,
        calenderDay,
        gameDay
    }

    private final MLBDataLayer repo;
    
    private LocalDateTime m_datum;
    public Context m_context;
    private ECalenderViewTypes viewTypes;
    private final ArrayList<List<MLBGame>> foundGameForMonth;
    private final ScheduleFragment m_parentFragment;

    public CalenderAdapter(Context c, LocalDateTime date, ScheduleFragment f){
        repo = MLBDataLayer.getInstance();
        m_context = c;
        m_datum = date;
        foundGameForMonth = new ArrayList<>();
        m_parentFragment = f;
        initGamesInMonth();
    }

    public void setDate(LocalDateTime date){
        m_datum = date;
        initGamesInMonth();
    }

    public LocalDateTime getDate(){
        return m_datum;
    }

    private void initGamesInMonth(){
        foundGameForMonth.clear();
        for (int i = 0; i < 31; i++) {
            foundGameForMonth.add(null);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //deze functie bepaald voor elke positie welke soort viewholder zal gemaakt worden in onCreateViewHolder

        int daysInMonth = CalenderHelpFunctions.calculateDaysInMonth(m_datum.getYear(), m_datum.getMonthValue());
        int firstDayPos = CalenderHelpFunctions.getStartday(m_datum.getYear(), m_datum.getMonthValue()) - 1;
        int lastDayPos = firstDayPos + daysInMonth;

        int dagnummer = position + 1;
        if (dagnummer > daysInMonth || dagnummer < 1)
            return ECalenderViewTypes.noCalenderDay.ordinal();

        LocalDateTime datePos = m_datum.withDayOfMonth(position + 1);
        List<MLBGame> games = repo.getGames(datePos, repo.m_selectedTeam.getValue());
        foundGameForMonth.set(position, games);

        if (games.size() > 0){
           return ECalenderViewTypes.gameDay.ordinal();
        }

        return ECalenderViewTypes.calenderDay.ordinal();
    }

    @NonNull
    @Override
    public CalenderAdapter.BaseCalenderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(m_context);
        View view = null;
        CalenderAdapter.BaseCalenderItemViewHolder holder = null;
        switch (viewType){
            //no calender day
            case 0:
                view = inflater.inflate(R.layout.card_calender_no_date, parent, false);
                 holder = new CalenderAdapter.BaseCalenderItemViewHolder(view, this);
                break;

            //calender day
            case 1:
                view = inflater.inflate(R.layout.card_calander_no_game_day, parent, false);
                holder = new CalenderAdapter.CalenderNoGameItemViewHolder(view, this);
                break;

            //game day
            case 2:
                view = inflater.inflate(R.layout.card_schedule_date, parent, false);
                holder = new CalenderAdapter.CalenderGameItemViewHolder(view, this);

                BaseCalenderItemViewHolder finalHolder = holder;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = finalHolder.getAdapterPosition();
                        List<MLBGame> games = foundGameForMonth.get(pos);
                        if (games != null && games.size() > 0) {
                            ScheduleDialog dialogue = new ScheduleDialog(games.get(0));
                            dialogue.show(m_parentFragment.getParentFragmentManager(), "schedule_dialog");
                        }
                    }
                });
                break;
        }
        return holder;
    }

    public int getDayNumber(int pos){
        int firstDayPos = CalenderHelpFunctions.getStartday(m_datum.getYear(), m_datum.getMonthValue()) - 1;
        return pos + 1;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull BaseCalenderItemViewHolder holder, int position) {
        if (holder instanceof CalenderNoGameItemViewHolder){
            CalenderNoGameItemViewHolder myHolder = (CalenderNoGameItemViewHolder)holder;
            myHolder.m_dayNumber.setText(String.valueOf(getDayNumber(position)));
            LocalDateTime datum = m_datum.withDayOfMonth(position + 1);
            holder.m_datum = datum;
            myHolder.m_dayAbrev.setText(datum.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        }
        else if(holder instanceof CalenderGameItemViewHolder){
            CalenderGameItemViewHolder myHolder = (CalenderGameItemViewHolder)holder;
            myHolder.m_dayNumber.setText(String.valueOf(getDayNumber(position)));
            LocalDateTime datum = m_datum.withDayOfMonth(position + 1);
            myHolder.m_dayAbrev.setText(datum.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
            holder.m_datum = datum;
            List<MLBGame> games = foundGameForMonth.get(position);
            if (games != null && games.size() > 0) {
                MLBGame g = games.get(0);
                MLBTeamInfo opponentTeamInfo = g.getOpponentInfo(repo.m_selectedTeam.getValue());
                Team opponentTeam = repo.getTeamWithMLBID(opponentTeamInfo.id);

                if(opponentTeam.m_image != null)
                    myHolder.m_teamLogo.setImageBitmap(opponentTeam.m_image);


                //Deze data krijgen wij niet binnen met the API maar kunnen we wel handelen
                //myHolder.m_flight
                //myHolder.m_promo
                //myHolder.m_promoDesc
                if(g.isInThePast()) {
                    myHolder.m_flight.setVisibility(View.GONE);
                    myHolder.m_ticket.setVisibility(View.GONE);
                    myHolder.m_booking.setVisibility(View.GONE);
                }
                else {
                    myHolder.m_flight.setVisibility(View.VISIBLE);
                    myHolder.m_ticket.setVisibility(View.VISIBLE);
                    myHolder.m_booking.setVisibility(View.VISIBLE);
                }
                if(g.description != null && !g.description.equals("")) {
                    // there is a special event
                    myHolder.m_promo.setVisibility(View.VISIBLE);
                    myHolder.m_promoDesc.setText(g.description);
                }
                else {
                    myHolder.m_promo.setVisibility(View.GONE);
                    myHolder.m_promoDesc.setText("");
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
                LocalDateTime parsedDateTime = LocalDateTime.parse(g.gameDate, formatter);

                // Convert LocalDateTime to ZonedDateTime with UTC time zone
                ZonedDateTime zonedDateTimeUtc = ZonedDateTime.of(parsedDateTime, ZoneId.of("UTC"));

                Team homeTeam = g.getHomeTeam(repo);

                // Convert ZonedDateTime to New York time zone
                ZonedDateTime homeTeamZone = zonedDateTimeUtc.withZoneSameInstant(ZoneId.of(homeTeam.time_zone_alt));

                DateTimeFormatter parsedformatter = DateTimeFormatter.ofPattern("HH:mm a z");
                myHolder.m_time.setText(homeTeamZone.format(parsedformatter));

                if(g.gameType.equalsIgnoreCase("s"))
                {
                    //spring training
                    myHolder.m_card.setBackgroundResource(R.drawable.sh_calender_springtraining_border);
                    myHolder.m_teamName.setText("vs. ");
                }
                else if(g.isHomeTeam(repo.m_selectedTeam.getValue())){
                    myHolder.m_card.setBackgroundResource(R.drawable.sh_calender_hometeam_border);
                    myHolder.m_teamName.setText("vs. ");
                }
                else{
                    myHolder.m_card.setBackgroundResource(R.drawable.sh_calender_awayteam_border);
                    myHolder.m_teamName.setText("@ ");
                }

                myHolder.m_teamName.append(opponentTeam.name);
            }
        }
    }

    @Override
    public int getItemCount() {
        return CalenderHelpFunctions.calculateDaysInMonth(m_datum.getYear(), m_datum.getMonthValue());
    }

    public class BaseCalenderItemViewHolder extends RecyclerView.ViewHolder{
        private final CalenderAdapter m_adapter;
        private LocalDateTime m_datum;

        public BaseCalenderItemViewHolder(@NonNull View itemView, CalenderAdapter ad) {
            super(itemView);
            m_adapter = ad;
            m_datum = null;
        }
    }

    public class CalenderNoGameItemViewHolder extends CalenderAdapter.BaseCalenderItemViewHolder{
        public TextView m_dayNumber;
        public TextView m_dayAbrev;

        public CalenderNoGameItemViewHolder(@NonNull View itemView, CalenderAdapter ad) {
            super(itemView, ad);
            m_dayNumber = itemView.findViewById(R.id.cardNoGameCalenderDay);
            m_dayAbrev = itemView.findViewById(R.id.cardNoGameCalenderDayAbr);
        }
    }

    public class CalenderGameItemViewHolder extends CalenderAdapter.BaseCalenderItemViewHolder{
        public CardView m_card;
        public TextView m_dayNumber;
        public ImageView m_teamLogo;
        public TextView m_teamName;
        public TextView m_time;
        public TextView m_promoDesc;
        public ImageView m_ticket;
        public ImageView m_promo;
        public ImageView m_booking;
        public ImageView m_flight;
        public TextView m_dayAbrev;

        public CalenderGameItemViewHolder(@NonNull View itemView, CalenderAdapter ad) {
            super(itemView, ad);
            m_card = itemView.findViewById(R.id.cardGameCalender);
            m_dayNumber = itemView.findViewById(R.id.cardGameCalenderDay);
            m_teamLogo = itemView.findViewById(R.id.cardGameTeamImg);
            m_teamName = itemView.findViewById(R.id.cardGameAtTeam);
            m_time = itemView.findViewById(R.id.cardGameTime);
            m_promoDesc = itemView.findViewById(R.id.cardGamePromoDesc);
            m_ticket = itemView.findViewById(R.id.cardGameTickets);
            m_promo = itemView.findViewById(R.id.cardGamePromotion);
            m_booking = itemView.findViewById(R.id.cardGameBooking);
            m_flight = itemView.findViewById(R.id.cardGameFlight);
            m_dayAbrev = itemView.findViewById(R.id.cardGameCalenderDayAbr);
        }
    }
}
