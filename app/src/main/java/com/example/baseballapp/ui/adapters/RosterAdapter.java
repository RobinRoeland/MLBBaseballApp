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
import com.example.baseballapp.classes.MLBroster.MLBRosterEntry;

import java.util.List;

public class RosterAdapter extends RecyclerView.Adapter<RosterAdapter.RosterViewHolder> {
    private final Context m_context;
    private List<MLBRosterEntry> m_displayList;

    public RosterAdapter(Context context,List<MLBRosterEntry> rosterList){
        m_context = context;
        m_displayList = rosterList;
    }
    public void setDisplayList(List<MLBRosterEntry> rosterList){
        m_displayList = rosterList;
    }

    @NonNull
    @Override
    public RosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        LayoutInflater inflater = LayoutInflater.from(m_context);
        View view = inflater.inflate(R.layout.card_roster_player_item, parent, false);

        RosterAdapter.RosterViewHolder holder = new RosterAdapter.RosterViewHolder(view, this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RosterViewHolder holder, int position) {
        MLBRosterEntry entry = m_displayList.get(position);
        holder.m_playerName.setText(entry.MLBPerson.fullName);
        holder.m_playerPosition.setText(entry.MLBPosition.abbreviation);
        String jersynr = String.format("%02d", Integer.parseInt(entry.jerseyNumber));
        holder.m_playerNr.setText(jersynr);

        if (entry.MLBPerson.m_image != null)
        {
            holder.m_playerImage.setImageBitmap(entry.MLBPerson.m_image);
        }
    }

    @Override
    public int getItemCount() {
        return m_displayList.size();
    }

    public class RosterViewHolder extends RecyclerView.ViewHolder{
        private final RosterAdapter m_adapter;
        private final ImageView m_playerImage;
        private final TextView m_playerName;
        private final TextView m_playerPosition;
        private final TextView m_playerNr;


        public RosterViewHolder(@NonNull View itemView, RosterAdapter ad) {
            super(itemView);
            m_adapter = ad;
            m_playerImage = itemView.findViewById(R.id.cardroster_player_image);
            m_playerName = itemView.findViewById(R.id.cardroster_player_name);
            m_playerPosition = itemView.findViewById(R.id.cardroster_player_position);
            m_playerNr = itemView.findViewById(R.id.cardroster_player_number);
        }
    }
}
