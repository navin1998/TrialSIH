package com.pranks.trialsih.patientActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.pranks.trialsih.R;
import com.pranks.trialsih.doctorClasses.DoctorAppointments;
import com.pranks.trialsih.patientsClasses.PatientAppointments;
import com.pranks.trialsih.patientAdapters.BookADoctorAdapter;

import java.util.ArrayList;

public class ConfirmPaymentActivity extends AppCompatActivity {


    private TextView payName;
    private TextView payAmount;
    private TextView payUpiID;

    private Button payButton;

    private Context mContext;
    private View v;

    private static String APPOINTMENT_DOCTOR_NAME;
    private static String APPOINTMENT_DOCTOR_PHONE;
    private static String APPOINTMENT_DOCTOR_REG_NUMBER;
    private static String APPOINTMENT_DOCTOR_POSITION_IN_QUEUE;
    private static String APPOINTMENT_DOCTOR_FEE;
    private static String APPOINTMENT_DOCTOR_FEE_STATUS;

    private final static String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    private final static int GOOGLE_PAY_REQUEST_CODE = 123;


    private static String APPOINTMENT_WAY_OF_CONNECT;


    private static String APPOINTMENT_PATIENT_NAME;
    private static String APPOINTMENT_PATIENT_UID;
    private static String APPOINTMENT_PATIENT_PHONE;
    private static String APPOINTMENT_PATIENT_FEE;
    private static String APPOINTMENT_PATIENT_FEE_STATUS;


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



    private static String MERCHANT_UPI;
    private static String MERCHANT_NAME;
    private static String MERCHANT_AMOUNT;
    private static final String MERCHANT_NOTE = "Appointment Fee";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();


        setContentView(R.layout.activity_confirm_payment);

        mContext = this;
        v = getWindow().getDecorView().getRootView();


        Bundle bundle = getIntent().getExtras();
        MERCHANT_UPI = bundle.getString("payUpiID");
        MERCHANT_NAME = bundle.getString("payName");
        MERCHANT_AMOUNT = bundle.getString("payAmount");

        APPOINTMENT_WAY_OF_CONNECT = bundle.getString("payWayToConnect");
        APPOINTMENT_DOCTOR_REG_NUMBER = bundle.getString("payRegNumber");
        APPOINTMENT_DOCTOR_NAME = bundle.getString("payDoctorName");
        APPOINTMENT_DOCTOR_POSITION_IN_QUEUE = bundle.getString("payPositionInQueue");
        APPOINTMENT_DOCTOR_PHONE = bundle.getString("payDoctorPhone");

        APPOINTMENT_DOCTOR_FEE = MERCHANT_AMOUNT;
        APPOINTMENT_PATIENT_FEE = MERCHANT_AMOUNT;


        payName = v.findViewById(R.id.pay_doctor_name);
        payUpiID = v.findViewById(R.id.pay_upi_id);
        payAmount = v.findViewById(R.id.pay_amount);

        payButton = v.findViewById(R.id.pay_btn);

        setData();

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPayment();

            }
        });

    }


    private void setData()
    {

        payName.setText(MERCHANT_NAME);
        payUpiID.setText(MERCHANT_UPI);
        payAmount.setText("INR " + MERCHANT_AMOUNT);

    }




    // method to start payment...
    private void startPayment()
    {


        // for only Google pay app...
//        Uri uri =
//                new Uri.Builder()
//                        .scheme("upi")
//                        .authority("pay")
//                        .appendQueryParameter("pa", MERCHANT_UPI)
//                        .appendQueryParameter("pn", MERCHANT_NAME)
////                        .appendQueryParameter("mc", "your-merchant-code")
////                        .appendQueryParameter("tr", "your-transaction-ref-id")
//                        .appendQueryParameter("tn", MERCHANT_NOTE)
//                        .appendQueryParameter("am", MERCHANT_AMOUNT)
//                        .appendQueryParameter("cu", "INR")
////                        .appendQueryParameter("url", "your-transaction-url")
//                        .build();
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(uri);
//        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
//        startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);







        // for all upi apps...
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", MERCHANT_UPI)
                .appendQueryParameter("pn", MERCHANT_NAME)
                .appendQueryParameter("tn", MERCHANT_NOTE)
                .appendQueryParameter("am", MERCHANT_AMOUNT)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(mContext,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }

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




    private void upiPaymentDataOperation(final ArrayList<String> data)
    {

        if (BookADoctorAdapter.isConnectionAvailable(mContext))
        {

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


                payButton.setVisibility(View.GONE);

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


                                showConfirmationDialog(APPOINTMENT_DOCTOR_POSITION_IN_QUEUE);

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


    private void showConfirmationDialog(String msg)
    {

        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_payment_successful, null);
        final Button okBtn = alertLayout.findViewById(R.id.btn_ok);
        final TextView confirmationText = alertLayout.findViewById(R.id.confirmation_text);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(false);

        confirmationText.setText("Appointment added successfully, you are at position " + msg + " in the queue");

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

    }




}
