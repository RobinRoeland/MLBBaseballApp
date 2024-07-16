package com.example.baseballapp.ui.roster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RosterViewModel rosterViewModel =
                new ViewModelProvider(this).get(RosterViewModel.class);

        List<MLBRosterEntry> roster =  MLBDataLayer.getInstance().getRosterForTeam(MLBDataLayer.getInstance().m_selectedTeam.getValue());

        binding = FragmentRosterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (roster != null){
            binding.rosterPlayerRecycler.setVisibility(View.VISIBLE);
            binding.rosterNotFoundText.setVisibility(View.GONE);
            RosterAdapter rosterAdapter = new RosterAdapter(getContext(), roster);
            binding.rosterPlayerRecycler.setAdapter(rosterAdapter);
            LinearLayoutManager tManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            binding.rosterPlayerRecycler.setLayoutManager(tManager);
        }
        else {
            binding.rosterPlayerRecycler.setVisibility(View.GONE);
            binding.rosterNotFoundText.setVisibility(View.VISIBLE);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}