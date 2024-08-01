package com.example.baseballapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baseballapp.R;
import com.example.baseballapp.classes.MLB.MLBTicket;
import com.example.baseballapp.data.MLBDataLayer;
import com.example.baseballapp.tasks.WebFetchImageTask;
import com.example.baseballapp.ui.dialogs.QRCodeDialog;
import com.example.baseballapp.ui.tickets.TicketsFragment;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private final Context m_context;
    private List<MLBTicket> m_ticketDisplayList;
    public TicketsFragment m_ticketsFragment;

    public TicketAdapter(Context context,List<MLBTicket> ticketsList, TicketsFragment fragment){
        m_context = context;
        m_ticketDisplayList = ticketsList;
        m_ticketsFragment = fragment;
    }
    public void setDisplayList(List<MLBTicket> ticketList){
        m_ticketDisplayList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        LayoutInflater inflater = LayoutInflater.from(m_context);
        View view = inflater.inflate(R.layout.card_ticket_item, parent, false);

        TicketAdapter.TicketViewHolder holder = new TicketAdapter.TicketViewHolder(view, this);
        holder.m_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QRCodeDialog dlg = new QRCodeDialog(holder.m_ticket);
                dlg.show(m_ticketsFragment.getParentFragmentManager(), "QRCodeDlg");
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        MLBTicket entry = m_ticketDisplayList.get(position);
        holder.m_ticket = entry;
        holder.m_homeTeam.setText(entry.homeTeam.name_display_long);
        holder.m_awayTeam.setText(entry.awayTeam.name_display_long);
        if (entry.homeTeam.m_image != null)
        {
            holder.m_homeLogo.setImageBitmap(entry.homeTeam.m_image);
        }
        if (entry.awayTeam.m_image != null)
        {
            holder.m_awayLogo.setImageBitmap(entry.awayTeam.m_image);
        }
        if (entry.m_image != null)
            holder.m_qrcode.setImageBitmap(entry.m_image);
        else
        {
            if(entry.m_imageName != "") {
                WebFetchImageTask webTaskAway = new WebFetchImageTask(m_context);
                webTaskAway.m_image = holder.m_qrcode;
                webTaskAway.execute(entry);
            }
        }
        String s = entry.getFormattedStartTime(MLBDataLayer.getInstance());
        holder.m_gamedetails.setText(s + " @ " + entry.stadium);
    }

    @Override
    public int getItemCount() {
        return m_ticketDisplayList.size();
    }

    public class TicketViewHolder extends RecyclerView.ViewHolder{
        private final TicketAdapter m_adapter;
        private final ImageView m_homeLogo;
        private final ImageView m_awayLogo;
        private final TextView m_homeTeam;
        private final TextView m_awayTeam;
        private final ImageView m_qrcode;
        private final TextView m_gamedetails;
        public MLBTicket m_ticket;

        public TicketViewHolder(@NonNull View itemView, TicketAdapter ad) {
            super(itemView);
            m_adapter = ad;
            m_homeLogo = itemView.findViewById(R.id.ticket_home_logo);
            m_awayLogo = itemView.findViewById(R.id.ticket_away_logo);
            m_homeTeam = itemView.findViewById(R.id.ticket_home_txt);
            m_awayTeam = itemView.findViewById(R.id.ticket_away_txt);
            m_qrcode = itemView.findViewById(R.id.ticket_qrcode);
            m_gamedetails = itemView.findViewById(R.id.ticket_gamedetails);
            m_ticket=null;
        }
    }
}
