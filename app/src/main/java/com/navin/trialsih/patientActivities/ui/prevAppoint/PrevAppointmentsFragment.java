package com.navin.trialsih.patientActivities.ui.prevAppoint;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navin.trialsih.R;
import com.navin.trialsih.doctorAdapters.DoctorHistoryAdapter;
import com.navin.trialsih.doctorClasses.DoctorHistory;
import com.navin.trialsih.patientActivities.PatientDashboardActivity;
import com.navin.trialsih.patientAdapters.PatientHistoryAdapter;
import com.navin.trialsih.patientsClasses.PatientAppointments;
import com.navin.trialsih.patientsClasses.PatientHistory;

import java.util.ArrayList;
import java.util.List;

public class PrevAppointmentsFragment extends Fragment {

    private PrevAppointmentsViewModel prevAppointmentsViewModel;
    private View v;

    private static String UID;
    
    private ImageView cannotFind;
    private TextView cannotFindText;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressCircle;

    private List<PatientHistory> list;
    private PatientHistoryAdapter patientHistoryAdapter;


    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String USER_TYPE_PATIENT = "patients";
    private final static String PREV_APPOINTMENTS = "prevAppointments";
    private final static String PROFILE = "profile";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        prevAppointmentsViewModel =
                ViewModelProviders.of(this).get(PrevAppointmentsViewModel.class);
        v = inflater.inflate(R.layout.fragment_patient_prev_appointments, container, false);

        ((PatientDashboardActivity) getActivity()).getSupportActionBar().setTitle("Previous Appointments");

        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mProgressCircle = v.findViewById(R.id.progress_circle);
        cannotFind = v.findViewById(R.id.cannotfind);
        cannotFindText = v.findViewById(R.id.cannotfind_text);

        loadAppointments();


        return v;
    }

    private void loadAppointments() {

        list = new ArrayList<>();
        patientHistoryAdapter = new PatientHistoryAdapter(getContext(), list);

        list.clear();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_PATIENT).child(UID).child(PREV_APPOINTMENTS);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                {
                    mRecyclerView.setAdapter(null);
                    cannotFind.setVisibility(View.VISIBLE);
                    cannotFindText.setVisibility(View.VISIBLE);
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }
                else
                {

                    mRecyclerView.setAdapter(null);
                    list = new ArrayList<>();
                    list.clear();

                    mRecyclerView.setAdapter(null);
                    patientHistoryAdapter = null;

                    ArrayList<String> listOfRegNumbers = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        listOfRegNumbers.add(snapshot.getKey());
                    }


                    for (String regNumber : listOfRegNumbers)
                    {
                        for (DataSnapshot shot : dataSnapshot.child(regNumber).getChildren()) {
                            list.add(shot.getValue(PatientHistory.class));
                        }
                    }


                    patientHistoryAdapter = new PatientHistoryAdapter(getContext(), list);

                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                    mRecyclerView.setAdapter(patientHistoryAdapter);

                    cannotFind.setVisibility(View.INVISIBLE);
                    mProgressCircle.setVisibility(View.GONE);
                    cannotFindText.setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}