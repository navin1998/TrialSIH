package com.navin.trialsih.patientActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navin.trialsih.R;
import com.navin.trialsih.patientAdapters.BookADoctorAdapter;
import com.navin.trialsih.doctorClasses.DoctorAppointments;
import com.navin.trialsih.doctorClasses.DoctorDetails;
import com.navin.trialsih.patientsClasses.PatientAppointments;

import java.util.ArrayList;
import java.util.List;

import static com.navin.trialsih.patientAdapters.BookADoctorAdapter.isConnectionAvailable;

public class AddAppointment extends AppCompatActivity {


    private Context mContext;
    private View v;

    private ImageView cannotFind;
    private TextView cannotFindText;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressCircle;
    private List<DoctorDetails> list;
    private BookADoctorAdapter bookADoctorAdapter;

    private static String DOCTOR_TYPE;

    private final static String PROFILE = "profile";
    private final static String TYPE_OF_DOCTOR = "doctorType";
    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String USER_TYPE_PATIENT = "patients";
    private final static String PATIENT_ACTIVE_APPOINTMENTS = "activeAppointments";
    private final static String DOCTOR_ACTIVE_APPOINTMENTS = "activeAppointments";
    private final static String PATIENT_PHONE = "patientPhone";
    private final static String PATIENT_NAME = "patientName";
    private final static String APPOINTMENT_CHAT_STARTED = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        v = getWindow().getDecorView().getRootView();
        mContext = this;

        getSupportActionBar().setTitle("Add Appointments");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        DOCTOR_TYPE = bundle.getString("doctorType");

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mProgressCircle = v.findViewById(R.id.progress_circle);
        cannotFind = v.findViewById(R.id.cannotfind);
        cannotFindText = v.findViewById(R.id.cannotfind_text);

        loadDoctorsList();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
        }

        return true;
    }

    private void loadDoctorsList()
    {
        list = new ArrayList<>();
        list.clear();

        mRecyclerView.setAdapter(null);

        bookADoctorAdapter = new BookADoctorAdapter(mContext, list);

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR);

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
                    final ArrayList<String> listOfRegNumber = new ArrayList<>();

                    boolean isFound = false;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        if (snapshot.child(PROFILE).getChildrenCount() > 3)
                        {
                            if (snapshot.child(PROFILE).child(TYPE_OF_DOCTOR).getValue().toString().equals(DOCTOR_TYPE))
                            {
                                isFound = true;

                                listOfRegNumber.add(snapshot.getKey());

                                list.add(snapshot.child(PROFILE).getValue(DoctorDetails.class));
                            }
                            else
                            {

                                isFound = false;

                            }

                        }
                    }

                    if (!isFound)
                    {
                        cannotFind.setVisibility(View.VISIBLE);
                        cannotFindText.setVisibility(View.VISIBLE);
                        mProgressCircle.setVisibility(View.INVISIBLE);

                        Toast.makeText(mContext, "No doctor found for doctor type: " + DOCTOR_TYPE, Toast.LENGTH_LONG).show();

                        return;
                    }

                    ArrayList<DoctorDetails> newList = new ArrayList<>();

                    for (int i = 0; i < listOfRegNumber.size(); i++)
                    {

                        newList.add(list.get(i));

                    }

                    if (listOfRegNumber.size() == 0) {
                        cannotFind.setVisibility(View.VISIBLE);
                        cannotFindText.setVisibility(View.VISIBLE);
                        mProgressCircle.setVisibility(View.GONE);
                    }


                    bookADoctorAdapter = new BookADoctorAdapter(mContext, newList);

                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                    mRecyclerView.setAdapter(bookADoctorAdapter);

                    cannotFind.setVisibility(View.INVISIBLE);
                    cannotFindText.setVisibility(View.INVISIBLE);
                    mProgressCircle.setVisibility(View.GONE);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        loadDoctorsList();

    }



}
