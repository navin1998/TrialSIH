package com.navin.trialsih.doctorActivities.navigationDrawer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.navin.trialsih.R;
import com.navin.trialsih.doctorActivities.DoctorDashboardActivity;
import com.navin.trialsih.patientActivities.ui.share.ShareViewModel;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;

    private View v;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        v = inflater.inflate(R.layout.fragment_share, container, false);

        ((DoctorDashboardActivity) getActivity()).getSupportActionBar().setTitle("History");

        final TextView textView = v.findViewById(R.id.text_share);
        historyViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return v;
    }
}