package com.example.baseballapp.ui.roster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.baseballapp.classes.MLBroster.MLBRosterEntry;
import com.example.baseballapp.data.MLBDataLayer;
import com.example.baseballapp.databinding.FragmentRosterBinding;
import com.example.baseballapp.ui.adapters.RosterAdapter;

import java.util.List;

public class RosterFragment extends Fragment {

    private FragmentRosterBinding binding;
    private RosterViewModel rosterViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rosterViewModel =
                new ViewModelProvider(this).get(RosterViewModel.class);

        List<MLBRosterEntry> roster =  MLBDataLayer.getInstance().getRosterForTeam(MLBDataLayer.getInstance().m_selectedTeam.getValue());
        rosterViewModel.initialisePersonsListFromRepo();

        binding = FragmentRosterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (rosterViewModel.m_displayPersonsList.size() > 0){
            binding.rosterPlayerRecycler.setVisibility(View.VISIBLE);
            binding.rosterNotFoundText.setVisibility(View.GONE);
            RosterAdapter rosterAdapter = new RosterAdapter(getContext(), rosterViewModel.m_displayPersonsList);
            binding.rosterPlayerRecycler.setAdapter(rosterAdapter);
            LinearLayoutManager tManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            binding.rosterPlayerRecycler.setLayoutManager(tManager);
        }
        else {
            binding.rosterPlayerRecycler.setVisibility(View.GONE);
            binding.rosterNotFoundText.setVisibility(View.VISIBLE);
        }
        binding.rosterFilterTextBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //filter on entered
                rosterViewModel.FilterListOnEnteredSearchString(s);
                RosterAdapter rosterAdapter = (RosterAdapter)binding.rosterPlayerRecycler.getAdapter();
                rosterAdapter.setDisplayList(rosterViewModel.m_displayPersonsList);
                rosterAdapter.notifyDataSetChanged();
                return true;
            }
        });
        binding.rosterFilterTextBox.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //refill all
                rosterViewModel.initialisePersonsListFromRepo();
                RosterAdapter rosterAdapter = (RosterAdapter)binding.rosterPlayerRecycler.getAdapter();
                rosterAdapter.setDisplayList(rosterViewModel.m_displayPersonsList);
                rosterAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}