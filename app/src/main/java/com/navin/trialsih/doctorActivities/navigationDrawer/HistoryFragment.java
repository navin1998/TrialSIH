package com.navin.trialsih.doctorActivities.navigationDrawer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navin.trialsih.R;
import com.navin.trialsih.doctorActivities.DoctorDashboardActivity;
import com.navin.trialsih.doctorActivities.bottomNavigation.HomeViewModel;
import com.navin.trialsih.doctorAdapters.AppointmentsAdapter;
import com.navin.trialsih.doctorClasses.Appointments;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private static String REG_NUMBER;

    private HomeViewModel homeViewModel;
    private View v;

    private ImageView cannotFind;
    private TextView cannotFindText;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressCircle;

    private List<Appointments> list;
    private AppointmentsAdapter appointmentsAdapter;


    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String USER_TYPE_PATIENT = "patients";
    private final static String ACTIVE_APPOINTMENTS = "appointments";
    private final static String PROFILE = "profile";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        v = inflater.inflate(R.layout.doctor_fragment_home, container, false);

        ((DoctorDashboardActivity) getActivity()).getSupportActionBar().setTitle("Home");

        list = new ArrayList<>();

        Bundle bundle = getArguments();
        REG_NUMBER = bundle.getString("regNumber");

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mProgressCircle = v.findViewById(R.id.progress_circle);
        cannotFind = v.findViewById(R.id.cannotfind);
        cannotFindText = v.findViewById(R.id.cannotfind_text);

        loadAppointments();

        return v;
    }


    private void loadAppointments()
    {

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(ACTIVE_APPOINTMENTS);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                {
                    cannotFind.setVisibility(View.VISIBLE);
                    cannotFindText.setVisibility(View.VISIBLE);
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }
                else
                {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        Appointments appointments = snapshot.getValue(Appointments.class);
                        list.add(appointments);
                    }

                    appointmentsAdapter = new AppointmentsAdapter(getContext(), list);

                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                    mRecyclerView.setAdapter(appointmentsAdapter);

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

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = getArguments();
        REG_NUMBER = bundle.getString("regNumber");
    }
}