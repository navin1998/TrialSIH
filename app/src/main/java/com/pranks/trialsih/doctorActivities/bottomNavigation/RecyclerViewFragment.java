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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranks.trialsih.R;
import com.pranks.trialsih.doctorActivities.DoctorDashboardActivity;
import com.pranks.trialsih.doctorAdapters.DoctorPrevPresListAdapter;
import com.pranks.trialsih.doctorClasses.DoctorPrevPresLinkItem;
import com.pranks.trialsih.doctorDBHelpers.DoctorCredentialsDBHelper;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewFragment extends Fragment {

    private RecyclerViewViewModel recyclerViewViewModel;

    private static String REG_NUMBER;
    private static String EMAIL;

    private Context mContext;

    private ImageView cannotFind;
    private TextView cannotFindText;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressCircle;

    private List<DoctorPrevPresLinkItem> list;
    private DoctorPrevPresListAdapter doctorPrevPresListAdapter;


    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String USER_TYPE_PATIENT = "patients";
    private final static String ACTIVE_APPOINTMENTS = "activeAppointments";
    private final static String PROFILE = "profile";
    private final static String PATIENT_PRES = "patientPrescriptions";


    private View v;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recyclerViewViewModel =
                ViewModelProviders.of(this).get(RecyclerViewViewModel.class);
        v = inflater.inflate(R.layout.doctor_fragment_recycler_view, container, false);

        mContext = getContext();

        ((DoctorDashboardActivity) getActivity()).getSupportActionBar().setTitle("Previous Prescriptions");

        list = new ArrayList<>();

        try {

            Bundle bundle = getArguments();
            REG_NUMBER = bundle.getString("regNumber");
            EMAIL = bundle.getString("patientMail");
        }
        catch (Exception e) {

            try {

                Bundle bundle = getArguments();
                REG_NUMBER = bundle.getString("regNumber");
                EMAIL = bundle.getString("patientMail");

            }
            catch (Exception ex) {
                REG_NUMBER = getRegNumber();
            }
        }

        EMAIL = EMAIL.replaceAll("\\.", "");

        mRecyclerView = v.findViewById(R.id.recycler_view_link);
        cannotFind = v.findViewById(R.id.cannotfind);
        cannotFindText = v.findViewById(R.id.cannotfind_text);
        mProgressCircle = v.findViewById(R.id.progress_circle);

        loadItemsInRecyclerView();

        return v;
    }

    private void loadItemsInRecyclerView()
    {

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(PATIENT_PRES).child(EMAIL);

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
                    doctorPrevPresListAdapter = null;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        list.add(snapshot.getValue(DoctorPrevPresLinkItem.class));
                    }

                    //Toast.makeText(mContext, "Size: " + list.size(), Toast.LENGTH_SHORT).show();

                    doctorPrevPresListAdapter = new DoctorPrevPresListAdapter(getContext(), list);

                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                    mRecyclerView.setAdapter(doctorPrevPresListAdapter);

                    cannotFind.setVisibility(View.INVISIBLE);
                    mProgressCircle.setVisibility(View.GONE);
                    cannotFindText.setVisibility(View.INVISIBLE);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Snackbar.make(v, "Database error: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();

            }
        });



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