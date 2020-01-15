package com.pranks.trialsih.patientActivities.bottomNavigation;

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

import com.pranks.trialsih.R;
import com.pranks.trialsih.patientActivities.PatientDashboardActivity;
import com.pranks.trialsih.patientActivities.ui.prevTransactions.PrevTransactionsViewModel;

public class ChatFragment extends Fragment {

    private ChatFragmentViewModel chatFragmentViewModel;
    private View v;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatFragmentViewModel =
                ViewModelProviders.of(this).get(ChatFragmentViewModel.class);
        v = inflater.inflate(R.layout.fragment_patient_prev_transactions, container, false);

        ((PatientDashboardActivity) getActivity()).getSupportActionBar().setTitle("Chat");


        final TextView textView = v.findViewById(R.id.text_tools);
        chatFragmentViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return v;
    }
}