package com.example.baseballapp.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.baseballapp.R;
import com.example.baseballapp.classes.MLB.MLBGame;
import com.example.baseballapp.classes.MLB.MLBTicket;
import com.example.baseballapp.databinding.DialogQrcodeBinding;
import com.example.baseballapp.databinding.DialogScheduleBinding;

public class QRCodeDialog extends DialogFragment {
    private DialogQrcodeBinding binding;
    private MLBTicket ticketToShow;
    public QRCodeDialog(MLBTicket ticket){
        ticketToShow = ticket;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.TransparentDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogQrcodeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.dialogqrTicketnr.setText("TicketNr : " + ticketToShow.ticketNumber);
        binding.dialogqrQrcodeimage.setImageBitmap(ticketToShow.m_image);

        binding.dialogqrInfotxt.setText(ticketToShow.stadium + "\n"+ ticketToShow.getTicketInfoString());

        binding.dialogqrButtonreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissNow();
            }
        });
    }

    }
