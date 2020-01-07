package com.navin.trialsih.doctorActivities.navigationDrawer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import com.navin.trialsih.doctorAdapters.DoctorAppointmentsAdapter;
import com.navin.trialsih.doctorAdapters.DoctorHistoryAdapter;
import com.navin.trialsih.doctorClasses.DoctorAppointments;
import com.navin.trialsih.doctorClasses.DoctorHistory;
import com.navin.trialsih.doctorDBHelpers.DoctorCredentialsDBHelper;
import com.navin.trialsih.patientActivities.ui.share.ShareViewModel;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;

    private View v;


    private static String REG_NUMBER;

    private Context mContext;

    private ImageView cannotFind;
    private TextView cannotFindText;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressCircle;

    private List<DoctorHistory> list;
    private DoctorHistoryAdapter doctorHistoryAdapter;


    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String USER_TYPE_PATIENT = "patients";
    private final static String PREV_APPOINTMENTS = "prevAppointments";
    private final static String PROFILE = "profile";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        v = inflater.inflate(R.layout.doctor_fragment_history, container, false);

        ((DoctorDashboardActivity) getActivity()).getSupportActionBar().setTitle("History");

        list = new ArrayList<>();

        try {

            Bundle bundle = getArguments();
            REG_NUMBER = bundle.getString("regNumber");
        }
        catch (Exception e) {

            try {

                Bundle bundle = getArguments();
                REG_NUMBER = bundle.getString("regNumber");

            }
            catch (Exception ex) {
                REG_NUMBER = getRegNumber();
            }
        }

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

        list = new ArrayList<>();
        doctorHistoryAdapter = new DoctorHistoryAdapter(getContext(), list);

        list.clear();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(PREV_APPOINTMENTS);

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
                    doctorHistoryAdapter = null;

                    ArrayList<String> listOfUid = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        listOfUid.add(snapshot.getKey());
                    }


                    for (String uid : listOfUid)
                    {
                        for (DataSnapshot shot : dataSnapshot.child(uid).getChildren()) {
                            list.add(shot.getValue(DoctorHistory.class));
                        }
                    }


                    doctorHistoryAdapter = new DoctorHistoryAdapter(getContext(), list);

                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                    mRecyclerView.setAdapter(doctorHistoryAdapter);

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

        try {

            Bundle bundle = getArguments();
            REG_NUMBER = bundle.getString("regNumber");
        }
        catch (Exception e) {

            REG_NUMBER = getRegNumber();
        }

    }

    private String getRegNumber()
    {
        String reg = null;

        DoctorCredentialsDBHelper dbHelper = new DoctorCredentialsDBHelper(mContext);

        reg = dbHelper.getRegNumber();

//        if (reg == null)
//        {
//            SharedPreferences doctorRegNumberPref = mContext.getSharedPreferences("doctorRegNumberPref", MODE_PRIVATE);
//
//            reg = doctorRegNumberPref.getString("regNumber", null);
//
//        }

        return reg;

    }


}