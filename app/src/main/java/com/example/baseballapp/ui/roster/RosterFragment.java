package com.example.baseballapp.ui.roster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.baseballapp.databinding.FragmentRosterBinding;

public class RosterFragment extends Fragment {

    private FragmentRosterBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RosterViewModel rosterViewModel =
                new ViewModelProvider(this).get(RosterViewModel.class);

        binding = FragmentRosterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}