package com.navin.trialsih.patientActivities.ui.prevTransactions;

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
import com.navin.trialsih.patientActivities.PatientDashboardActivity;

public class PrevTransactionsFragment extends Fragment {

    private PrevTransactionsViewModel prevTransactionsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        prevTransactionsViewModel =
                ViewModelProviders.of(this).get(PrevTransactionsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_patient_prev_transactions, container, false);

        ((PatientDashboardActivity) getActivity()).getSupportActionBar().setTitle("Previous Transactions");


        final TextView textView = root.findViewById(R.id.text_tools);
        prevTransactionsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}