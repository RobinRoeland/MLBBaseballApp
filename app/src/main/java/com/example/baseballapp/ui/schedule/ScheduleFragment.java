package com.example.baseballapp.ui.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.baseballapp.data.MLBDataLayer;
import com.example.baseballapp.databinding.FragmentScheduleBinding;
import com.example.baseballapp.ui.adapters.CalenderAdapter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;
    private ScheduleViewModel scheduleViewModel;
    private LocalDateTime datum;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);

        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        datum = LocalDateTime.now();
        datum = datum.withDayOfMonth(1);
        setMonth(datum);

        GridLayoutManager Glm = new GridLayoutManager(getContext(), 3);
        binding.calenderRV.setLayoutManager(Glm);

        CalenderAdapter adapter = new CalenderAdapter(getContext(), datum, this);
        binding.calenderRV.setAdapter(adapter);

        binding.scheduleTeamBarLogo.setImageBitmap(MLBDataLayer.getInstance().m_selectedTeam.getValue().m_image);
        binding.scheduleTeamBarName.setText(MLBDataLayer.getInstance().m_selectedTeam.getValue().name_display_full );

        binding.scheduleMonthBarButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goOneMonthBack();
            }
        });

        binding.scheduleMonthBarButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goOneMonthForward();
            }
        });
/*
        ItemTouchHelper itemtouchhelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    // one month back
                    goOneMonthBack();
                }
                if (direction == ItemTouchHelper.RIGHT) {
                    // One month forward
                    goOneMonthForward();
                }
            }
        });
        itemtouchhelper.attachToRecyclerView(binding.calenderRV);

 */

        return root;
    }

    public void goOneMonthForward() {
        datum = datum.plusMonths(1);
        setMonth(datum);
        CalenderAdapter adapter = (CalenderAdapter) binding.calenderRV.getAdapter();
        adapter.setDate(datum);
        adapter.notifyDataSetChanged();
        binding.calenderRV.smoothScrollToPosition(0);
    }
    public void goOneMonthBack() {
        datum = datum.minusMonths(1);
        setMonth(datum);
        CalenderAdapter adapter = (CalenderAdapter) binding.calenderRV.getAdapter();
        adapter.setDate(datum);
        adapter.notifyDataSetChanged();
        binding.calenderRV.smoothScrollToPosition(0);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setMonth(LocalDateTime date){
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        Date datum = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
        String s = sdf.format(datum);
        binding.scheduleMonthBarTV.setText(s);
    }

}