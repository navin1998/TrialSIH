package com.navin.trialsih.patientActivities.ui.prevAppoint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.navin.trialsih.R;

public class PrevAppointmentsFragment extends Fragment {

    private PrevAppointmentsViewModel prevAppointmentsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        prevAppointmentsViewModel =
                ViewModelProviders.of(this).get(PrevAppointmentsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_patient_prev_appointments, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        prevAppointmentsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}