package com.example.baseballapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baseballapp.R;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.classes.team.TeamList;
import com.example.baseballapp.data.MLBDataLayer;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {
    private final Context m_context;
    private TeamList m_displayList;
    private final MLBDataLayer m_repo;

    public TeamAdapter(Context context, TeamList teamList, MLBDataLayer r){
        m_context = context;
        m_displayList = teamList;
        m_repo = r;
    }

    public void setDisplayList(TeamList teamList){
        m_displayList = teamList;
    }

    @NonNull
    @Override
    public TeamAdapter.TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(m_context);
        View view = inflater.inflate(R.layout.card_team_item, parent, false);

        TeamAdapter.TeamViewHolder holder = new TeamAdapter.TeamViewHolder(view, this);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                Team team = m_displayList.get(pos);
                if(!m_repo.isOnline()) {
                    //offline
                    if(m_repo.isTeamDetailsAvailableOffline(team)) {
                        MLBDataLayer.getInstance().m_selectedTeam.setValue(team);
                    }
                    else {
                        Toast.makeText(parent.getContext(), "Team " + team.mlb_org_abbrev + " has not been visited before while online.", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    //online
                    MLBDataLayer.getInstance().m_selectedTeam.setValue(team);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TeamAdapter.TeamViewHolder holder, int position) {
        Team team = m_displayList.get(position);
        holder.m_teamName.setText(team.name_display_full);
        holder.m_teamShortName.setText(team.name_abbrev);

        if (team.m_image != null)
        {
            holder.m_teamImage.setImageBitmap(team.m_image);
        }
        //set background color of cardview if online of not offline available
        if(!m_repo.isTeamDetailsAvailableOffline(team) && !m_repo.isOnline()) {
            @ColorInt int color = ContextCompat.getColor(m_context, R.color.teamNotAvailableOffline);
            holder.m_Card.setCardBackgroundColor(color);
        }
        else {
            @ColorInt int color = ContextCompat.getColor(m_context, R.color.bgndCard);
            holder.m_Card.setCardBackgroundColor(color);
        }
    }

    @Override
    public int getItemCount() {
        return m_displayList.size();
    }

    public class TeamViewHolder extends RecyclerView.ViewHolder{
        private final TeamAdapter m_adapter;
        private final TextView m_teamName;
        private final TextView m_teamShortName;
        private final ImageView m_teamImage;
        private final CardView m_Card;

        public TeamViewHolder(@NonNull View itemView, TeamAdapter ad) {
            super(itemView);
            m_adapter = ad;
            m_teamName = itemView.findViewById(R.id.cardTeamItemName);
            m_teamShortName = itemView.findViewById(R.id.cardTeamItemShortName);
            m_teamImage = itemView.findViewById(R.id.cardTeamItemImg);
            m_Card = itemView.findViewById(R.id.CardTeamItem);
        }
    }
}
