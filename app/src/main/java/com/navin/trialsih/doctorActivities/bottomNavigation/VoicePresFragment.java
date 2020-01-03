package com.navin.trialsih.doctorActivities.bottomNavigation;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.navin.trialsih.R;
import com.navin.trialsih.doctorActivities.DoctorDashboardActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VoicePresFragment extends Fragment {

    private VoicePresViewModel voicePresViewModel;
    private View v;

    private ListView symptoms;
    private ListView diagnosis;
    private ListView prescriptions;
    private ListView advices;

    private ImageButton btn;

    private ListView[] listViewArr;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        voicePresViewModel =
                ViewModelProviders.of(this).get(VoicePresViewModel.class);
        v = inflater.inflate(R.layout.doctor_fragment_voice_pres, container, false);

        ((DoctorDashboardActivity) getActivity()).getSupportActionBar().setTitle("Voice Pres");

        symptoms = v.findViewById(R.id.patientSymptoms);
        diagnosis = v.findViewById(R.id.patientDiagnosis);
        prescriptions = v.findViewById(R.id.patientPrescriptions);
        advices = v.findViewById(R.id.patientAdvices);

        btn = v.findViewById(R.id.patientMicBtn);

        listViewArr = new ListView[]{symptoms, diagnosis, prescriptions, advices};


        String[] fruits = new String[] {
                "Navin",
                "Aditya",
                "Rakesh",
                "Kundan",
                "Avinash"
        };

        final List<String> fruits_list = new ArrayList<String>(Arrays.asList(fruits));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_list_item_1, fruits_list);

        symptoms.setAdapter(arrayAdapter);
        diagnosis.setAdapter(arrayAdapter);
        prescriptions.setAdapter(arrayAdapter);
        advices.setAdapter(arrayAdapter);


        enableNestedScrolling();


        return v;
    }

    private void enableNestedScrolling()
    {

        for (ListView l : listViewArr)
        {
            l.setOnTouchListener(new ListView.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                    v.onTouchEvent(event);
                    return true;
                }
            });
        }

    }

}