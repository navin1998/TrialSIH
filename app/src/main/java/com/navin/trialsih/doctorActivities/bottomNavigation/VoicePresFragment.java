package com.navin.trialsih.doctorActivities.bottomNavigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.navin.trialsih.R;
import com.navin.trialsih.doctorActivities.DoctorDashboardActivity;

public class VoicePresFragment extends Fragment {

    private VoicePresViewModel voicePresViewModel;
    private View v;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        voicePresViewModel =
                ViewModelProviders.of(this).get(VoicePresViewModel.class);
        v = inflater.inflate(R.layout.doctor_fragment_voice_pres, container, false);

        ((DoctorDashboardActivity) getActivity()).getSupportActionBar().setTitle("Voice Pres");



        return v;
    }
}