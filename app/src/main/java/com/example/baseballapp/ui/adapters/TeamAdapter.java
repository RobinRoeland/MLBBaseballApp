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
import com.example.baseballapp.classes.league.League;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.classes.team.TeamList;
import com.example.baseballapp.data.MLBDataLayer;
import com.example.baseballapp.tasks.WebFetchImageTask;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {
    private Context m_context;
    private TeamList m_displayList;

    public TeamAdapter(Context context, TeamList teamList){
        m_context = context;
        m_displayList = teamList;
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
                MLBDataLayer.getInstance().m_selectedTeam.setValue(team);
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
        else {
            WebFetchImageTask webTask = new WebFetchImageTask();
            webTask.m_image =  holder.m_teamImage;
            webTask.execute(team);
        }
    }

    @Override
    public int getItemCount() {
        return m_displayList.size();
    }

    public class TeamViewHolder extends RecyclerView.ViewHolder{
        private TeamAdapter m_adapter;
        private TextView m_teamName;
        private TextView m_teamShortName;
        private ImageView m_teamImage;

        public TeamViewHolder(@NonNull View itemView, TeamAdapter ad) {
            super(itemView);
            m_adapter = ad;
            m_teamName = itemView.findViewById(R.id.cardTeamItemName);
            m_teamShortName = itemView.findViewById(R.id.cardTeamItemShortName);
            m_teamImage = itemView.findViewById(R.id.cardTeamItemImg);
        }
    }
}
