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

public class AddAppointment extends AppCompatActivity implements BookADoctorAdapter.CallBackInterface {


    private static String APPOINTMENT_DOCTOR_NAME;
    private static String APPOINTMENT_DOCTOR_PHONE;
    private static String APPOINTMENT_DOCTOR_REG_NUMBER;
    private static String APPOINTMENT_DOCTOR_POSITION_IN_QUEUE;
    private static String APPOINTMENT_DOCTOR_FEE;
    private static String APPOINTMENT_DOCTOR_FEE_STATUS;


    private static String APPOINTMENT_WAY_OF_CONNECT;


    private static String APPOINTMENT_PATIENT_NAME;
    private static String APPOINTMENT_PATIENT_UID;
    private static String APPOINTMENT_PATIENT_PHONE;
    private static String APPOINTMENT_PATIENT_FEE;
    private static String APPOINTMENT_PATIENT_FEE_STATUS;


    private Context mContext;
    private View v;

    private ImageView cannotFind;
    private TextView cannotFindText;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressCircle;
    private List<DoctorDetails> list;
    private BookADoctorAdapter bookADoctorAdapter;

    private String wayToConnect;

    private final static int UPI_PAYMENT = 0;

    private static String PATIENT_UID;

    private final static String PROFILE = "profile";
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

        APPOINTMENT_PATIENT_UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        if (snapshot.child(PROFILE).getChildrenCount() > 3)
                        {
                            listOfRegNumber.add(snapshot.getKey());

                            list.add(snapshot.child(PROFILE).getValue(DoctorDetails.class));

                        }
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








    // code for handling payments...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {

                        Bundle bundle = data.getExtras();
                        wayToConnect = bundle.getString("wayToConnect");

                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);

                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }



    private void upiPaymentDataOperation(final ArrayList<String> data) {
        if (isConnectionAvailable(mContext)) {
            String str = data.get(0);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user";
                }
            }

            if (status.equals("success")) {


                //Code to handle successful transaction here.
                Toast.makeText(mContext, "Transaction successful", Toast.LENGTH_SHORT).show();


                // getting patientName saved in sharedPreferences for future use...
                APPOINTMENT_PATIENT_NAME = getPatientName();


                // getting patientPhone saved in sharedPreferences for future use...
                APPOINTMENT_PATIENT_PHONE = getPatientPhone();



                // add patient details to patient's database...
                addAppointmentToPatientNode();


                // add patient details to doctor's database...
                addAppointmentToDoctorNode();


            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {

                Toast.makeText(mContext, "Payment cancelled by user", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(mContext, "Transaction failed. Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void handlePayments(String upiId, String name, String note, String amount, String wayToConnect, String regNumber, String doctorName, String positionInQueue, String doctorPhone)
    {

        APPOINTMENT_PATIENT_FEE = "Rs. " + amount;
        APPOINTMENT_WAY_OF_CONNECT = wayToConnect;
        APPOINTMENT_DOCTOR_NAME = name;
        APPOINTMENT_DOCTOR_REG_NUMBER = regNumber;
        APPOINTMENT_DOCTOR_PHONE = doctorPhone;
        APPOINTMENT_DOCTOR_FEE = "Rs. " + amount;
        APPOINTMENT_DOCTOR_FEE_STATUS = "paid";
        APPOINTMENT_DOCTOR_POSITION_IN_QUEUE = String.valueOf(Integer.parseInt(positionInQueue) + 1);

        Uri uri = Uri.parse("upi://pay")
                .buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);


        Bundle bundle = new Bundle();
        bundle.putString("wayToConnect", wayToConnect);


        //this code is only for payTm...
        String appPackageName = "net.one97.paytm";


        upiPayIntent.setPackage(appPackageName);

        if (upiPayIntent != null)
        {


            startActivityForResult(upiPayIntent, UPI_PAYMENT);

        }
        else {
            Toast.makeText(mContext, "Install PayTm for payment on our application", Toast.LENGTH_SHORT).show();
        }

    }



    private void addAppointmentToPatientNode()
    {

        final PatientAppointments patientAppointments = new PatientAppointments();
        patientAppointments.setAppointmentDoctorName(APPOINTMENT_DOCTOR_NAME);
        patientAppointments.setAppointmentDoctorPhone(APPOINTMENT_DOCTOR_PHONE);
        patientAppointments.setAppointmentDoctorRegNumber(APPOINTMENT_DOCTOR_REG_NUMBER);
        patientAppointments.setAppointmentFee(APPOINTMENT_DOCTOR_FEE);
        patientAppointments.setAppointmentFeeStatus(APPOINTMENT_DOCTOR_FEE_STATUS);
        patientAppointments.setAppointmentPositionInQueue(APPOINTMENT_DOCTOR_POSITION_IN_QUEUE);
        patientAppointments.setAppointmentWayToConnect(APPOINTMENT_WAY_OF_CONNECT);
        patientAppointments.setAppointmentChatStarted(APPOINTMENT_CHAT_STARTED);


        PATIENT_UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_PATIENT).child(PATIENT_UID).child(PATIENT_ACTIVE_APPOINTMENTS).child(APPOINTMENT_DOCTOR_REG_NUMBER);


        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mRef.setValue(patientAppointments)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Snackbar.make(v, "Appointment added successfully, you are at position " + APPOINTMENT_DOCTOR_POSITION_IN_QUEUE + " in the queue", Snackbar.LENGTH_LONG).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(v, "Error: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private String getPatientName()
    {

        String patientNamePrefName = "patientNamePrefs" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        String patientNamePrefKey = "patientName" + FirebaseAuth.getInstance().getCurrentUser().getUid();

        SharedPreferences patientNamePref = mContext.getSharedPreferences(patientNamePrefName, MODE_PRIVATE);

        APPOINTMENT_PATIENT_NAME = patientNamePref.getString(patientNamePrefKey, null);

        return APPOINTMENT_PATIENT_NAME;

    }


    private String getPatientPhone()
    {

        String patientPhonePrefName = "patientPhonePrefs" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        String patientPhonePrefKey = "patientPhone" + FirebaseAuth.getInstance().getCurrentUser().getUid();



        SharedPreferences patientPhonePref = mContext.getSharedPreferences(patientPhonePrefName, MODE_PRIVATE);

        APPOINTMENT_PATIENT_PHONE = patientPhonePref.getString(patientPhonePrefKey, "0000000000");

        return APPOINTMENT_PATIENT_PHONE;

    }


    private void addAppointmentToDoctorNode()
    {

        APPOINTMENT_PATIENT_PHONE = getPatientPhone();
        APPOINTMENT_PATIENT_UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        APPOINTMENT_PATIENT_FEE = APPOINTMENT_PATIENT_FEE;
        APPOINTMENT_PATIENT_FEE_STATUS = "paid";
        APPOINTMENT_PATIENT_NAME = getPatientName();


        final DoctorAppointments doctorAppointments = new DoctorAppointments();

        doctorAppointments.setAppointmentFee(APPOINTMENT_PATIENT_FEE);
        doctorAppointments.setAppointmentFeeStatus(APPOINTMENT_PATIENT_FEE_STATUS);
        doctorAppointments.setAppointmentPatientName(APPOINTMENT_PATIENT_NAME);
        doctorAppointments.setAppointmentPatientPhone(APPOINTMENT_PATIENT_PHONE);
        doctorAppointments.setAppointmentPatientUid(APPOINTMENT_PATIENT_UID);
        doctorAppointments.setAppointmentWayToConnect(APPOINTMENT_WAY_OF_CONNECT);
        doctorAppointments.setAppointmentPositionInQueue(APPOINTMENT_DOCTOR_POSITION_IN_QUEUE);
        doctorAppointments.setAppointmentChatStarted(APPOINTMENT_CHAT_STARTED);


        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(APPOINTMENT_DOCTOR_REG_NUMBER).child(DOCTOR_ACTIVE_APPOINTMENTS).child(PATIENT_UID);


        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mRef.setValue(doctorAppointments)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(mContext, "Also uploaded to doctor's database", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(mContext, "Upload to doctor database went wrong", Toast.LENGTH_SHORT).show();

                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



}
