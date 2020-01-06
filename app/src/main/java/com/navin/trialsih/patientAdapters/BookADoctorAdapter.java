package com.navin.trialsih.patientAdapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.navin.trialsih.doctorClasses.DoctorAppointments;
import com.navin.trialsih.doctorClasses.DoctorDetails;
import com.navin.trialsih.patientActivities.ConfirmPaymentActivity;
import com.navin.trialsih.patientsClasses.PatientAppointments;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class BookADoctorAdapter extends RecyclerView.Adapter<BookADoctorAdapter.ImageViewHolder> {



    private static String APPOINTMENT_DOCTOR_NAME;
    private static String APPOINTMENT_DOCTOR_PHONE;
    private static String APPOINTMENT_DOCTOR_REG_NUMBER;
    private static String APPOINTMENT_DOCTOR_POSITION_IN_QUEUE;
    private static String APPOINTMENT_DOCTOR_FEE;
    private static String APPOINTMENT_DOCTOR_FEE_STATUS;

    private static String APPOINTMENT_POSITION_IN_QUEUE;

    private static final String APPOINTMENT_CHAT_STARTED = "no";


    private static final String WAY_OF_CONNECT_VISIT = "visit";


    private static String APPOINTMENT_WAY_OF_CONNECT;


    private static String APPOINTMENT_PATIENT_NAME;
    private static String APPOINTMENT_PATIENT_UID;
    private static String APPOINTMENT_PATIENT_PHONE;
    private static String APPOINTMENT_PATIENT_FEE;
    private static String APPOINTMENT_PATIENT_FEE_STATUS;


    private static final String FEE_STATUS_UNPAID = "unpaid";


    private static String PATIENT_UID;

    private final static String USER_TYPE_PATIENT = "patients";
    private final static String PATIENT_ACTIVE_APPOINTMENTS = "activeAppointments";
    private final static String PATIENT_PHONE = "patientPhone";
    private final static String PATIENT_NAME = "patientName";


    private Context mContext;
    private List<DoctorDetails> mUploads;
    private View root;

    private final static String CONNECT_MODE_CHAT = "chat";
    private final static String CONNECT_MODE_VISIT = "visit";
    
    private final static String PAY_MODE_ONLINE = "online";
    private final static String PAY_MODE_CASH = "cash";


    final int UPI_PAYMENT = 0;

    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String PROFILE = "profile";
    private final static String DOCTOR_UPI_NAME = "doctorUpiName";
    private final static String DOCTOR_ACTIVE_APPOINTMENTS= "activeAppointments";
    private final static String DOCTOR_UPI_ID = "doctorUpiID";
    private final static String DOCTOR_FEE = "doctorFee";
    private final static String DOCTOR_NAME = "doctorName";
    private final static String DOCTOR_BOOKING_PHONE = "doctorBookingPhone";
    private static String REG_NUMBER;
    private DatabaseReference docReference = FirebaseDatabase.getInstance().getReference().child("doctors");





    public BookADoctorAdapter(Context context, List<DoctorDetails> uploads) {
        mContext = context;
        mUploads = uploads;

        getPatientName();

        getPatientPhone();
        
    }

    @NonNull
    @Override
    public BookADoctorAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        root = LayoutInflater.from(mContext).inflate(R.layout.doctor_item, parent, false);

        return new ImageViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookADoctorAdapter.ImageViewHolder holder, int position) {

        final DoctorDetails doctorDetails = mUploads.get(position);
        try {
            holder.textViewName.setText(doctorDetails.getDoctorName());
            holder.textViewType.setText(doctorDetails.getDoctorBookingPhone());

            try
            {

                Glide.with(mContext)
                        .load(doctorDetails.getDoctorPhotoUri())
                        .placeholder(R.drawable.man)
                        .into(holder.imageView);


            }catch (Exception e) {}

        }
        catch (Exception e) {}



        holder.textViewCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBookingDialog(doctorDetails.getDoctorRegNumber());
            }
        });



    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewType;
        public TextView textViewCalendar;
        public ImageView imageView;
        public CardView cardView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.gallery_cardView);
            textViewName = itemView.findViewById(R.id.cardView_commodityName);
            textViewCalendar = itemView.findViewById(R.id.cardView_book_appointment);
            textViewType = itemView.findViewById(R.id.cardView_commodityType);
            imageView = itemView.findViewById(R.id.cardView_commodityImageView);
        }
    }


    private void showBookingDialog(final String regNumber)
    {

        REG_NUMBER = regNumber;

        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.doctor_profile_dialog_layout, null);


        final Button bookBtn = alertLayout.findViewById(R.id.dialogDoctorBookAppointmentBtn);

        final ImageView doctorPic = alertLayout.findViewById(R.id.dialogDoctorProfilePic);

        final TextView doctorName = alertLayout.findViewById(R.id.dialogDoctorName);
        final TextView doctorRegNumber = alertLayout.findViewById(R.id.dialogDoctorRegNumber);
        final TextView doctorGender = alertLayout.findViewById(R.id.dialogDoctorGender);
        final TextView doctorPhone = alertLayout.findViewById(R.id.dialogDoctorBookingPhone);
        final TextView doctorExperience = alertLayout.findViewById(R.id.dialogDoctorExpYear);
        final TextView doctorLastDegree = alertLayout.findViewById(R.id.dialogDoctorLastDegree);
        final TextView doctorAddress = alertLayout.findViewById(R.id.dialogDoctorAddress);
        final TextView doctorFee = alertLayout.findViewById(R.id.dialogDoctorFee);
        final TextView doctorSatisfiedPatients = alertLayout.findViewById(R.id.dialogDoctorSatisfiedPatients);


        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(PROFILE);


        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try
                {
                    Glide.with(mContext)
                            .load(dataSnapshot.child("doctorPhotoUri").getValue().toString())
                            .placeholder(R.drawable.man)
                            .into(doctorPic);
                }catch (Exception e){}


                doctorName.setText(dataSnapshot.child("doctorName").getValue().toString());
                doctorRegNumber.setText(dataSnapshot.child("doctorRegNumber").getValue().toString());
                doctorGender.setText(dataSnapshot.child("doctorGender").getValue().toString());
                doctorPhone.setText(dataSnapshot.child("doctorPhone").getValue().toString());
                doctorExperience.setText(dataSnapshot.child("doctorExperience").getValue().toString());
                doctorLastDegree.setText(dataSnapshot.child("doctorQualifications").getValue().toString());
                doctorAddress.setText(dataSnapshot.child("doctorClinicAddress").getValue().toString());
                doctorFee.setText(dataSnapshot.child("doctorFee").getValue().toString());
                doctorSatisfiedPatients.setText(dataSnapshot.child("doctorSatisfiedPatientsNumber").getValue().toString());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(mContext, "Available soon", Toast.LENGTH_SHORT).show();

                proceedForAppointment(regNumber);
                
                dialog.dismiss();
            }
        });

    }


    private void proceedForAppointment(final String regNumber)
    {

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(regNumber).child(PROFILE);


        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {

                    boolean isOnline = false;

                    try {
                        // it means that doctor is accepting online payments...
                        if (dataSnapshot.child(DOCTOR_UPI_NAME).getValue().toString().length() > 0) {
                            if (dataSnapshot.child(DOCTOR_UPI_ID).getValue().toString().length() > 0) {
                                isOnline = true;
                                confirmBookingForOnlinePay(regNumber);
                            }
                        }
                    }
                    catch (Exception e){}


                    //it means doctor is accepting cash only...
                    if (!isOnline)
                    {
                        isOnline = false;
                        confirmBookingForCashPay(regNumber);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    private void confirmBookingForOnlinePay(final String regNumber)
    {

        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_book_online, null);
        final Button onlineBtn = alertLayout.findViewById(R.id.btn_online);
        final Button offlineBtn = alertLayout.findViewById(R.id.btn_offline);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        onlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showWayToConnectDialog(regNumber);

                dialog.dismiss();
            }
        });

        offlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showBenefitsOfOnlinePaymentsDialog(regNumber);

                dialog.dismiss();

            }
        });

    }

    private void confirmBookingForCashPay(final String regNumber)
    {

        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_book_offline, null);
        final Button confirmBtn = alertLayout.findViewById(R.id.btn_confirm);
        final Button declineBtn = alertLayout.findViewById(R.id.btn_decline);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLastConfirmationDialog(regNumber);

                dialog.dismiss();
            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, "Cancelled by the user", Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });

    }


    private void showWayToConnectDialog(final String regNumber)
    {

        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_book_online_contact_way, null);
        final Button chatBtn = alertLayout.findViewById(R.id.btn_chat);
        final Button visitBtn = alertLayout.findViewById(R.id.btn_visit);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProceedToPayDialog(regNumber, CONNECT_MODE_CHAT);

                dialog.dismiss();
            }
        });

        visitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProceedToPayDialog(regNumber, CONNECT_MODE_VISIT);

                dialog.dismiss();

            }
        });


    }


    private void showProceedToPayDialog(final String regNumber, final String wayToConnect)
    {

        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_proceed_to_pay, null);
        final Button payBtn = alertLayout.findViewById(R.id.btn_pay);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //start payment, user want to connect with method parameter 'String wayToConnect'...


                //start payment method...
                /**
                 *
                 * starting of payment process, after getting doctorData()...
                 *
                 */
                getDoctorData(regNumber, wayToConnect);



                dialog.dismiss();
            }
        });

    }



    /********************************/


    /**
     *
     * payment starts here...
     *
     * onActivityResult() is in AddAppointment.java activity inside 'patientActivities' folder...
     *
     *
     * @param regNumber
     */


    void getDoctorData(final String regNumber, final String wayToConnect)
    {

        PATIENT_UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        docReference.child(regNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String upiId = "", name = "", note = "Appointment Fee", amount = "";

                String doctorName = "";
                String positionInQueue = "";
                String doctorPhone = "";

                upiId = dataSnapshot.child(PROFILE).child(DOCTOR_UPI_ID).getValue().toString();
                name = dataSnapshot.child(PROFILE).child(DOCTOR_UPI_NAME).getValue().toString();
                amount = dataSnapshot.child(PROFILE).child(DOCTOR_FEE).getValue().toString();
                doctorName = dataSnapshot.child(PROFILE).child(DOCTOR_NAME).getValue().toString();
                doctorPhone = dataSnapshot.child(PROFILE).child(DOCTOR_BOOKING_PHONE).getValue().toString();




                // check for already registered...
                ArrayList<String> listOfUid = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.child(DOCTOR_ACTIVE_APPOINTMENTS).getChildren())
                {
                    listOfUid.add(snapshot.getKey());
                }


                if (listOfUid.contains(PATIENT_UID))
                {
                    Toast.makeText(mContext, "Already registered", Toast.LENGTH_SHORT).show();

                    return;

                }



                try {

                    positionInQueue = String.valueOf(dataSnapshot.child(DOCTOR_ACTIVE_APPOINTMENTS).getChildrenCount());

                }
                catch (Exception e){}


                String finalAmount = "";

                for (int i = 0; i < amount.length(); i++)
                {
                    if (Character.isDigit(amount.charAt(i)))
                    {
                        finalAmount += amount.charAt(i);
                    }
                }


                /***
                 *
                 *
                 * putting credentials to bundle...
                 *
                 *
                 */

                Bundle bundle = new Bundle();
                bundle.putString("payUpiID", upiId);
                bundle.putString("payName", name);
                bundle.putString("payAmount", finalAmount);
                bundle.putString("payWayToConnect", wayToConnect);
                bundle.putString("payRegNumber", regNumber);
                bundle.putString("payDoctorName", doctorName);
                bundle.putString("payPositionInQueue", positionInQueue);
                bundle.putString("payDoctorPhone", doctorPhone);


                //starting payment activity...
                Intent intent = new Intent(mContext, ConfirmPaymentActivity.class);
                intent.putExtras(bundle);
                mContext.startActivity(intent);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static boolean isConnectionAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }


    /**
     *
     * payment ends here...
     *
     *
     * onActivityResult() is in AddAppointment.java activity...
     *
     *
     * @param regNumber
     */


    private void showBenefitsOfOnlinePaymentsDialog(final String regNumber)
    {


        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_benefits_of_online_payment, null);
        final Button cashBtn = alertLayout.findViewById(R.id.btn_cash);
        final Button onlineBtn = alertLayout.findViewById(R.id.btn_online);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        cashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLastConfirmationDialog(regNumber);
                
                dialog.dismiss();
            }
        });

        onlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showWayToConnectDialog(regNumber);

                dialog.dismiss();

            }
        });

    }
    
    
    
    private void showLastConfirmationDialog(final String regNumber)
    {
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_confirm_booking, null);
        final Button cancelBtn = alertLayout.findViewById(R.id.btn_cancel);
        final Button confirmBtn = alertLayout.findViewById(R.id.btn_confirm);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, "Operation cancelled by the user", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  this is cash transaction...
                //  upload as a non-paid appointment...

                APPOINTMENT_PATIENT_NAME = getPatientName();
                APPOINTMENT_PATIENT_PHONE = getPatientPhone();

                getDoctorDataForOfflineTransactions(regNumber);

                dialog.dismiss();

            }
        });

    }


    /**
     *
     * getDoctorDataForOfflineTransactions()...
     *
     *
     */




    void getDoctorDataForOfflineTransactions(final String regNumber)
    {

        PATIENT_UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        docReference.child(regNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String upiId = "", name = "", note = "Appointment Fee", amount = "";

                String doctorName = "";
                String positionInQueue = "";
                String doctorPhone = "";

                upiId = dataSnapshot.child(PROFILE).child(DOCTOR_UPI_ID).getValue().toString();
                name = dataSnapshot.child(PROFILE).child(DOCTOR_UPI_NAME).getValue().toString();
                amount = dataSnapshot.child(PROFILE).child(DOCTOR_FEE).getValue().toString();
                doctorName = dataSnapshot.child(PROFILE).child(DOCTOR_NAME).getValue().toString();
                doctorPhone = dataSnapshot.child(PROFILE).child(DOCTOR_BOOKING_PHONE).getValue().toString();


                ArrayList<String> listOfUid = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.child(DOCTOR_ACTIVE_APPOINTMENTS).getChildren())
                {
                    listOfUid.add(snapshot.getKey());
                }


                if (listOfUid.contains(PATIENT_UID))
                {
                    Toast.makeText(mContext, "Already registered", Toast.LENGTH_SHORT).show();

                    return;

                }


                try {

                    positionInQueue = String.valueOf(dataSnapshot.child(DOCTOR_ACTIVE_APPOINTMENTS).getChildrenCount());
                    positionInQueue = String.valueOf(Integer.parseInt(positionInQueue) + 1);

                }
                catch (Exception e){}


                String finalAmount = "";

                for (int i = 0; i < amount.length(); i++)
                {
                    if (Character.isDigit(amount.charAt(i)))
                    {
                        finalAmount += amount.charAt(i);
                    }
                }


                addAppointmentToPatientNode(doctorName, doctorPhone, regNumber, amount, positionInQueue);

                addAppointmentToDoctorNode(amount, positionInQueue);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    /**
     *
     *
     * getDoctorDataForOfflineTransactions() ends here...
     *
     */



    private void addAppointmentToPatientNode(String name, String phone, String regNumber, String fee, final String position)
    {

        APPOINTMENT_DOCTOR_NAME = name;
        APPOINTMENT_DOCTOR_PHONE = phone;
        APPOINTMENT_DOCTOR_REG_NUMBER = regNumber;
        APPOINTMENT_DOCTOR_FEE = fee;
        APPOINTMENT_DOCTOR_POSITION_IN_QUEUE = position;
        APPOINTMENT_DOCTOR_FEE_STATUS = FEE_STATUS_UNPAID;
        APPOINTMENT_WAY_OF_CONNECT = WAY_OF_CONNECT_VISIT;

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


        mRef.setValue(patientAppointments)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(mContext, "Appointment added successfully, you are at position " + APPOINTMENT_DOCTOR_POSITION_IN_QUEUE + " in the queue", Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

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


    private void addAppointmentToDoctorNode(String fee, String position)
    {

        APPOINTMENT_PATIENT_PHONE = getPatientPhone();
        APPOINTMENT_PATIENT_UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        APPOINTMENT_PATIENT_FEE = fee;
        APPOINTMENT_PATIENT_FEE_STATUS = FEE_STATUS_UNPAID;
        APPOINTMENT_PATIENT_NAME = getPatientName();
        APPOINTMENT_WAY_OF_CONNECT = WAY_OF_CONNECT_VISIT;
        APPOINTMENT_POSITION_IN_QUEUE = position;


        final DoctorAppointments doctorAppointments = new DoctorAppointments();

        doctorAppointments.setAppointmentFee(APPOINTMENT_PATIENT_FEE);
        doctorAppointments.setAppointmentFeeStatus(APPOINTMENT_PATIENT_FEE_STATUS);
        doctorAppointments.setAppointmentPatientName(APPOINTMENT_PATIENT_NAME);
        doctorAppointments.setAppointmentPatientPhone(APPOINTMENT_PATIENT_PHONE);
        doctorAppointments.setAppointmentPatientUid(APPOINTMENT_PATIENT_UID);
        doctorAppointments.setAppointmentWayToConnect(APPOINTMENT_WAY_OF_CONNECT);
        doctorAppointments.setAppointmentPositionInQueue(APPOINTMENT_POSITION_IN_QUEUE);
        doctorAppointments.setAppointmentChatStarted(APPOINTMENT_CHAT_STARTED);


        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(APPOINTMENT_DOCTOR_REG_NUMBER).child(DOCTOR_ACTIVE_APPOINTMENTS).child(PATIENT_UID);


        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mRef.setValue(doctorAppointments)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                //successfully added to database...

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
