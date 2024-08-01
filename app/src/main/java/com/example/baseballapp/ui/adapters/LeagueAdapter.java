package com.example.baseballapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baseballapp.R;
import com.example.baseballapp.classes.league.League;
import com.example.baseballapp.classes.league.LeagueList;
import com.example.baseballapp.ui.TeamSelectionActViewModel;

public class LeagueAdapter extends RecyclerView.Adapter<LeagueAdapter.LeagueViewHolder> {
    private final Context m_context;
    private LeagueList m_displayList;
    private final TeamSelectionActViewModel m_model;

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
                m_model.m_currentLeagueID.postValue(league.id);
                //this will trigger observe in teamselectionactivity
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
        if(league.id  == m_model.m_currentLeagueID.getValue()) {
            @ColorInt int color = ContextCompat.getColor(m_context, R.color.league_selected);
            holder.m_card.setCardBackgroundColor(color);
        }
        else {
            @ColorInt int color = ContextCompat.getColor(m_context, R.color.league_notselected);
            holder.m_card.setCardBackgroundColor(color);
        }

    }

    @Override
    public int getItemCount() {
        return m_displayList.size();
    }

    public class LeagueViewHolder extends RecyclerView.ViewHolder{
        private final LeagueAdapter m_adapter;
        private final TextView m_leagueName;
        private final ImageView m_leagueImage;
        private final CardView m_card;

        public LeagueViewHolder(@NonNull View itemView, LeagueAdapter ad) {
            super(itemView);
            m_adapter = ad;
            m_card = itemView.findViewById(R.id.cardLeague_card);
            m_leagueName = itemView.findViewById(R.id.cardLeagueTV);
            m_leagueImage = itemView.findViewById(R.id.cardLeagueImg);
        }
    }
}
