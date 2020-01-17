package com.pranks.trialsih.patientActivities.ui.prevTransactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.pranks.trialsih.R;
import com.pranks.trialsih.patientActivities.PatientDashboardActivity;
import com.pranks.trialsih.patientAdapters.PatientHistoryAdapter;
import com.pranks.trialsih.patientAdapters.PatientTransactionHistoryAdapter;
import com.pranks.trialsih.patientsClasses.PatientHistory;
import com.pranks.trialsih.patientsClasses.PatientTransactionHistory;

import java.util.ArrayList;
import java.util.List;

public class PrevTransactionsFragment extends Fragment {

    private PrevTransactionsViewModel prevTransactionsViewModel;
    private View v;


    private static String UID;

    private ImageView cannotFind;
    private TextView cannotFindText;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressCircle;

    private List<PatientTransactionHistory> list;
    private List<PatientTransactionHistory> secondList;
    private PatientTransactionHistoryAdapter transactionHistoryAdapter;


    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String USER_TYPE_PATIENT = "patients";
    private final static String PREV_APPOINTMENTS = "prevAppointments";
    private final static String PROFILE = "profile";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        prevTransactionsViewModel =
                ViewModelProviders.of(this).get(PrevTransactionsViewModel.class);
        v = inflater.inflate(R.layout.fragment_patient_prev_transactions, container, false);

        ((PatientDashboardActivity) getActivity()).getSupportActionBar().setTitle("Previous Transactions");


        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mProgressCircle = v.findViewById(R.id.progress_circle);
        cannotFind = v.findViewById(R.id.cannotfind);
        cannotFindText = v.findViewById(R.id.cannotfind_text);

        loadTransactions();


        return v;
    }




    private void loadTransactions() {

        list = new ArrayList<>();
        secondList = new ArrayList<>();
        transactionHistoryAdapter = new PatientTransactionHistoryAdapter(getContext(), list);

        mRecyclerView.setAdapter(null);

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
                    secondList = new ArrayList<>();
                    list.clear();
                    secondList.clear();

                    mRecyclerView.setAdapter(null);
                    transactionHistoryAdapter = null;

                    ArrayList<String> listOfRegNumbers = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        listOfRegNumbers.add(snapshot.getKey());
                    }


                    for (String regNumber : listOfRegNumbers)
                    {

                        final DatabaseReference iRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(regNumber).child(PROFILE);

                        /**
                         *
                         * getting image of doctor...
                         *
                         */


                        iRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot imageSnapshot) {


                                String imageUrl = imageSnapshot.child("doctorPhotoUri").getValue().toString();

                                for (DataSnapshot shot : dataSnapshot.child(regNumber).getChildren()) {
                                    list.add(new PatientTransactionHistory(shot.child("appointmentDoctorName").getValue().toString(), imageUrl, shot.child("appointmentFee").getValue().toString(),shot.child("appointmentFeeStatus").getValue().toString()));
                                }

                                secondList.add(list.get(0));
                                list.clear();


                                transactionHistoryAdapter = new PatientTransactionHistoryAdapter(getContext(), secondList);

                                mRecyclerView.setHasFixedSize(true);
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                                mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                                mRecyclerView.setAdapter(transactionHistoryAdapter);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        /**
                         *
                         * getting image is done...
                         *
                         */

                    }

                    //Toast.makeText(getContext(), "Size: " + list.size(), Toast.LENGTH_SHORT).show();

//                    transactionHistoryAdapter = new PatientTransactionHistoryAdapter(getContext(), list);
//
//                    mRecyclerView.setHasFixedSize(true);
//                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//
//                    mRecyclerView.setAdapter(transactionHistoryAdapter);

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