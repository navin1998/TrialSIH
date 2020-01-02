package com.navin.trialsih.patientActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navin.trialsih.R;
import com.navin.trialsih.doctorAdapters.BookADoctorAdapter;
import com.navin.trialsih.doctorClasses.DoctorDetails;
import com.navin.trialsih.patientAdapters.AppointmentsAdapter;
import com.navin.trialsih.patientsClasses.Appointments;

import java.util.ArrayList;
import java.util.List;

public class AddAppointment extends AppCompatActivity {

    private Context mContext;
    private View v;

    private ImageView cannotFind;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressCircle;
    private List<DoctorDetails> list;
    private BookADoctorAdapter bookADoctorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        v = getWindow().getDecorView().getRootView();
        mContext = this;

        getSupportActionBar().setTitle("Add Appointments");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mProgressCircle = v.findViewById(R.id.progress_circle);
        cannotFind = v.findViewById(R.id.cannotfind);

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
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("doctors");

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
                    ArrayList<String> listOfRegNumber = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        listOfRegNumber.add(snapshot.getKey());
                    }

                    for (String reg : listOfRegNumber)
                    {
                        final DatabaseReference innerRef = mRef.child(reg).child("profile");

                        innerRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.getChildrenCount() >= 3)
                                {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                    {
                                        DoctorDetails doctorDetails = snapshot.getValue(DoctorDetails.class);
                                        list.add(doctorDetails);
                                    }

                                    bookADoctorAdapter = new BookADoctorAdapter(mContext, list);

                                    mRecyclerView.setHasFixedSize(true);
                                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

                                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                                    mRecyclerView.setAdapter(bookADoctorAdapter);

                                    cannotFind.setVisibility(View.INVISIBLE);
                                    mProgressCircle.setVisibility(View.GONE);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    bookADoctorAdapter = new BookADoctorAdapter(mContext, list);

                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                    mRecyclerView.setAdapter(bookADoctorAdapter);

                    cannotFind.setVisibility(View.INVISIBLE);
                    mProgressCircle.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
