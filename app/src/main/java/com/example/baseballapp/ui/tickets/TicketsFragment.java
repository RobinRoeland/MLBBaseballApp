package com.example.baseballapp.ui.tickets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.baseballapp.classes.MLB.MLBTicket;
import com.example.baseballapp.data.MLBDataLayer;
import com.example.baseballapp.databinding.FragmentTicketsBinding;
import com.example.baseballapp.ui.adapters.TicketAdapter;

import java.util.List;


public class TicketsFragment extends Fragment {

    private FragmentTicketsBinding binding;
    private MLBDataLayer repo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        repo = MLBDataLayer.getInstance();
        TicketsViewModel ticketsViewModel =
                new ViewModelProvider(this).get(TicketsViewModel.class);

        binding = FragmentTicketsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<MLBTicket> myticketslist = repo.m_TicketList.getValue();
        if (myticketslist.size() > 0) {
            binding.ticketsRecyclerView.setVisibility(View.VISIBLE);
            binding.ticketsNoticketsyet.setVisibility(View.GONE);

            TicketAdapter ticketAdapter = new TicketAdapter(getContext(), myticketslist, this);
            binding.ticketsRecyclerView.setAdapter(ticketAdapter);
            LinearLayoutManager tManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            binding.ticketsRecyclerView.setLayoutManager(tManager);
        }
        else
        {
            binding.textTickets.setVisibility(View.GONE);
            binding.ticketsRecyclerView.setVisibility(View.GONE);
            binding.ticketsNoticketsyet.setVisibility(View.VISIBLE);
        }


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}