package com.navin.trialsih.patientActivities.ui.activeAppoint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.provider.Contacts;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navin.trialsih.R;
import com.navin.trialsih.patientActivities.AddAppointment;
import com.navin.trialsih.patientActivities.PatientDashboardActivity;
import com.navin.trialsih.patientAdapters.AppointmentsAdapter;
import com.navin.trialsih.patientsClasses.Appointments;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private View v;

    private ImageView cannotFind;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressCircle;
    private List<Appointments> list;
    private AppointmentsAdapter appointmentsAdapter;

    private FloatingActionButton floatingActionButton;

    private static String UID;
    private final static String USER_TYPE = "patients";
    private final static String ACTIVE_APPOINTMENTS = "appointments";
    private final static String PREVIOUS_APPOINTMENTS = "prevAppointments";
    private final static String PREVIOUS_TRANSACTIONS = "prevTransactions";
    private final static String PROFILE = "profile";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        v = inflater.inflate(R.layout.fragment_home, container, false);

        ((PatientDashboardActivity) getActivity()).getSupportActionBar().setTitle("Home");


        list = new ArrayList<>();

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mProgressCircle = v.findViewById(R.id.progress_circle);
        cannotFind = v.findViewById(R.id.cannotfind);

        floatingActionButton = v.findViewById(R.id.add_appointments);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Sure to add appointments?");
                builder.setCancelable(true);

                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {

                        isProfileComplete();

                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                try{alertDialog.show();}catch (Exception e) {alertDialog.dismiss();}
            }
        });

        loadAppointments();

        return v;
    }

    private boolean isProfileComplete()
    {
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final boolean isComplete = false;

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE).child(UID).child(PROFILE);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.getChildrenCount() > 3)
                    {
                        Intent intent = new Intent(getContext(), AddAppointment.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Snackbar.make(getView(), "Complete your profile first", Snackbar.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Snackbar.make(getView(), "Complete your profile first", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return isComplete;
    }

    private void loadAppointments()
    {

        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE).child(UID).child(ACTIVE_APPOINTMENTS);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                {
                    cannotFind.setVisibility(View.VISIBLE);
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UID = user.getUid();

    }
}