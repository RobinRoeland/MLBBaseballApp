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
import com.example.baseballapp.classes.league.LeagueList;
import com.example.baseballapp.data.MLBDataLayer;
import com.example.baseballapp.tasks.WebFetchImageTask;
import com.example.baseballapp.ui.TeamSelectionActViewModel;

public class LeagueAdapter extends RecyclerView.Adapter<LeagueAdapter.LeagueViewHolder> {
    private Context m_context;
    private LeagueList m_displayList;
    private TeamSelectionActViewModel m_model;

    public LeagueAdapter(TeamSelectionActViewModel model, Context context, LeagueList leagueList){
        m_context = context;
        m_displayList = leagueList;
        m_model = model;
    }

    public void setDisplayList(LeagueList leagueList){
        m_displayList = leagueList;
    }

    @NonNull
    @Override
    public LeagueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(m_context);
        View view = inflater.inflate(R.layout.card_league_rv, parent, false);

        LeagueAdapter.LeagueViewHolder holder = new LeagueViewHolder(view, this);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                League league = m_displayList.get(pos);
                m_model.m_currentLeagueID = league.id;
                m_model.setFilteredTeamList(MLBDataLayer.getInstance().teamList, league.id);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LeagueViewHolder holder, int position) {
        League league = m_displayList.get(position);
        holder.m_leagueName.setText(league.name);

        if (league.m_image != null)
        {
            holder.m_leagueImage.setImageBitmap(league.m_image);
        }
        else {
            WebFetchImageTask webTask = new WebFetchImageTask();
            webTask.m_image = holder.m_leagueImage;
            webTask.execute(league);
        }
    }

    @Override
    public int getItemCount() {
        return m_displayList.size();
    }

    public class LeagueViewHolder extends RecyclerView.ViewHolder{
        private LeagueAdapter m_adapter;
        private TextView m_leagueName;
        private ImageView m_leagueImage;

        public LeagueViewHolder(@NonNull View itemView, LeagueAdapter ad) {
            super(itemView);
            m_adapter = ad;
            m_leagueName = itemView.findViewById(R.id.cardLeagueTV);
            m_leagueImage = itemView.findViewById(R.id.cardLeagueImg);
        }
    }
}
