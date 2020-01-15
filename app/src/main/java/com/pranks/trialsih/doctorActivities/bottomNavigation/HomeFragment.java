package com.pranks.trialsih.doctorActivities.bottomNavigation;

import android.content.Context;
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
import com.pranks.trialsih.R;
import com.pranks.trialsih.doctorActivities.DoctorDashboardActivity;
import com.pranks.trialsih.doctorAdapters.DoctorAppointmentsAdapter;
import com.pranks.trialsih.doctorClasses.DoctorAppointments;
import com.pranks.trialsih.doctorDBHelpers.DoctorCredentialsDBHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static String REG_NUMBER;

    private HomeViewModel homeViewModel;
    private View v;

    private Context mContext;

    private ImageView cannotFind;
    private TextView cannotFindText;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressCircle;

    private List<DoctorAppointments> list;
    private DoctorAppointmentsAdapter doctorAppointmentsAdapter;


    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String USER_TYPE_PATIENT = "patients";
    private final static String ACTIVE_APPOINTMENTS = "activeAppointments";
    private final static String PROFILE = "profile";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        v = inflater.inflate(R.layout.doctor_fragment_home, container, false);
        mContext = getContext();

        ((DoctorDashboardActivity) getActivity()).getSupportActionBar().setTitle("Home");

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
        doctorAppointmentsAdapter = new DoctorAppointmentsAdapter(getContext(), list);

        list.clear();

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

                    list = new ArrayList<>();
                    list.clear();

                    mRecyclerView.setAdapter(null);
                    doctorAppointmentsAdapter = null;

                    ArrayList<String> listOfUid = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        listOfUid.add(snapshot.getKey());
                    }


                    for (String uid : listOfUid)
                    {
                        list.add(dataSnapshot.child(uid).getValue(DoctorAppointments.class));
                    }


                    doctorAppointmentsAdapter = new DoctorAppointmentsAdapter(getContext(), list);

                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                    mRecyclerView.setAdapter(doctorAppointmentsAdapter);

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