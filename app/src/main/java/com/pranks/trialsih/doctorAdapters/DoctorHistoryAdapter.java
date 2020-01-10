package com.pranks.trialsih.doctorAdapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranks.trialsih.R;
import com.pranks.trialsih.doctorActivities.DoctorChatActivity;
import com.pranks.trialsih.doctorClasses.DoctorHistory;
import com.pranks.trialsih.doctorDBHelpers.DoctorCredentialsDBHelper;

import java.util.List;

public class DoctorHistoryAdapter extends RecyclerView.Adapter<DoctorHistoryAdapter.ImageViewHolder>{


    private Context mContext;
    private List<DoctorHistory> mUploads;
    private View root;

    private static String REG_NUMBER;

    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String USER_TYPE_PATIENT = "patients";
    private final static String PROFILE = "profile";
    private final static String ACTIVE_APPOINTMENTS = "activeAppointments";
    private final static String APPOINTMENT_FEE_STATUS = "appointmentFeeStatus";
    private final static String APPOINTMENT_CHAT_STATUS = "appointmentChatStarted";

    public DoctorHistoryAdapter(Context context, List<DoctorHistory> uploads) {

        mContext = context;
        mUploads = uploads;

        getRegNumber();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        root = LayoutInflater.from(mContext).inflate(R.layout.doctor_history_item, parent, false);

        return new ImageViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        getRegNumber();

        final DoctorHistory history = mUploads.get(position);
        holder.textViewName.setText(history.getAppointmentPatientName());

        String status = history.getAppointmentFeeStatus();

        status = status.toLowerCase();

        if (status.equals("paid"))
        {
            holder.textViewAppointmentType.setText("UPI PAID");
            holder.textViewAppointmentType.setBackground(mContext.getResources().getDrawable(R.drawable.background_online_text));
        }
        else
        {
            holder.textViewAppointmentType.setText("CASH PAID");
            holder.textViewAppointmentType.setBackground(mContext.getResources().getDrawable(R.drawable.background_offline_text));

        }

        
        holder.textViewCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPatientProfileDialog(history.getAppointmentPatientUid(), history.getAppointmentFee(), history.getAppointmentWayToConnect());

            }
        });

    }


    private void showPatientProfileDialog(String UID, String FEE, String WAY_TO_CONNECT)
    {

        final ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.patient_profile_dialog_layout, null);


        final TextView patientName = alertLayout.findViewById(R.id.dialogPatientName);
        final TextView patientGender = alertLayout.findViewById(R.id.dialogPatientGender);

        /**
         *
         *final TextView patientPhone = alertLayout.findViewById(R.id.dialogPatientPhone);
         *
         *
         */

        final TextView patientAge = alertLayout.findViewById(R.id.dialogPatientAge);
        final TextView patientBloodGroup = alertLayout.findViewById(R.id.dialogPatientBloodGroup);
        final TextView patientWeight = alertLayout.findViewById(R.id.dialogPatientWeight);

        /**
         * 
         *final TextView patientMail = alertLayout.findViewById(R.id.dialogPatientMail);
         *
         */
        final TextView patientFee = alertLayout.findViewById(R.id.dialogPatientFee);
        final TextView patientWayToConnect = alertLayout.findViewById(R.id.dialogPatientWayToConnect);


        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_PATIENT).child(UID).child(PROFILE);


        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                patientName.setText(dataSnapshot.child("patientName").getValue().toString());
                patientGender.setText(dataSnapshot.child("patientGender").getValue().toString());
                //patientPhone.setText(dataSnapshot.child("patientPhone").getValue().toString());
                patientAge.setText(dataSnapshot.child("patientAge").getValue().toString());
                patientBloodGroup.setText(dataSnapshot.child("patientBloodGroup").getValue().toString());
                patientWeight.setText(dataSnapshot.child("patientWeight").getValue().toString());
                //patientMail.setText(dataSnapshot.child("patientMail").getValue().toString());
                patientFee.setText(FEE);
                patientWayToConnect.setText(WAY_TO_CONNECT.toUpperCase());


                pDialog.cancel();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(mContext, "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                pDialog.cancel();

            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
        pDialog.cancel();

    }


    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewCalendar;
        public TextView textViewAppointmentType;
        public ImageView imageView;
        public CardView cardView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.gallery_cardView);
            textViewName = itemView.findViewById(R.id.cardView_commodityName);
            textViewCalendar = itemView.findViewById(R.id.cardView_chat_with_doctor);
            textViewAppointmentType = itemView.findViewById(R.id.cardView_appointmentType);
            imageView = itemView.findViewById(R.id.cardView_commodityImageView);

        }
    }


    private void getRegNumber()
    {
        DoctorCredentialsDBHelper dbHelper = new DoctorCredentialsDBHelper(mContext);

        REG_NUMBER = dbHelper.getRegNumber();

    }


    private void verifyForChat(final String uid, final String reg, final Bundle bundle)
    {

        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(ACTIVE_APPOINTMENTS);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(uid).child(APPOINTMENT_FEE_STATUS).getValue().toString().toLowerCase().equals("paid"))
                {
                    try {
                        if (dataSnapshot.child(uid).child(APPOINTMENT_CHAT_STATUS).getValue().toString().toLowerCase().equals("yes")) {


                            Intent i = new Intent(mContext, DoctorChatActivity.class);
                            i.putExtras(bundle);
                            mContext.startActivity(i);


                        }
                        else if (dataSnapshot.child(uid).child(APPOINTMENT_CHAT_STATUS).getValue().toString().toLowerCase().equals("ended"))
                        {
                            //showChatRestartDialog(uid, reg, bundle);
                            Toast.makeText(mContext, "Chat has already ended", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            showChatConfirmDialogForOnlinePayment(uid, reg, bundle);
                        }
                    }catch (Exception e) {

                        showChatConfirmDialogForOnlinePayment(uid, reg, bundle);
                    }

                }
                else
                {

                    try {

                        if (dataSnapshot.child(uid).child(APPOINTMENT_CHAT_STATUS).getValue().toString().toLowerCase().equals("yes")) {

                            Intent i = new Intent(mContext, DoctorChatActivity.class);
                            i.putExtras(bundle);
                            mContext.startActivity(i);

                        }
                        else if (dataSnapshot.child(uid).child(APPOINTMENT_CHAT_STATUS).getValue().toString().toLowerCase().equals("ended"))
                        {
                            //showChatRestartDialog(uid, reg, bundle);
                            Toast.makeText(mContext, "Chat has already ended", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            showChatConfirmDialogForCash(uid, reg, bundle);
                        }
                    }
                    catch (Exception e)
                    {
                        showChatConfirmDialogForCash(uid, reg, bundle);
                    }

                }

                dialog.cancel();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                dialog.cancel();

            }
        });

    }


    private void showChatConfirmDialogForOnlinePayment(final String uid, final String reg, final Bundle bundle)
    {
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_confirm_chat_for_paid, null);
        final Button noBtn = alertLayout.findViewById(R.id.btn_no);
        final Button yesBtn = alertLayout.findViewById(R.id.btn_yes);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, DoctorChatActivity.class);
                i.putExtras(bundle);
                mContext.startActivity(i);

                //  update in database that chat has started...
                updateChatStartStatusInPatientNode(uid, reg, bundle);
                updateChatStartStatusInDoctorNode(uid, reg, bundle);


                dialog.dismiss();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
    }



    private void showChatConfirmDialogForCash(final String uid, final String reg, final Bundle bundle)
    {
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_confirm_chat_for_non_paid, null);
        final Button noBtn = alertLayout.findViewById(R.id.btn_no);
        final Button yesBtn = alertLayout.findViewById(R.id.btn_yes);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, DoctorChatActivity.class);
                i.putExtras(bundle);
                mContext.startActivity(i);


                //  update in database that chat has started...
                updateChatStartStatusInPatientNode(uid, reg, bundle);
                updateChatStartStatusInDoctorNode(uid, reg, bundle);



                dialog.dismiss();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
    }


    private void updateChatStartStatusInPatientNode(String uid, String reg, Bundle bundle)
    {

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_PATIENT).child(uid).child(ACTIVE_APPOINTMENTS).child(reg);

        try {
            mRef.child("appointmentChatStarted").setValue("yes");
        }catch (Exception e){}


    }


    private void updateChatStartStatusInDoctorNode(String uid, String reg, Bundle bundle)
    {

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(reg).child(ACTIVE_APPOINTMENTS).child(uid);

        try {

            mRef.child("appointmentChatStarted").setValue("yes");

        }
        catch (Exception e){}

    }


    private void showChatRestartDialog(final String uid, final String reg, final Bundle bundle)
    {
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_confirm_chat_restart, null);
        final Button noBtn = alertLayout.findViewById(R.id.btn_no);
        final Button yesBtn = alertLayout.findViewById(R.id.btn_yes);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, DoctorChatActivity.class);
                i.putExtras(bundle);
                mContext.startActivity(i);


                //  update in database that chat has started...
                resetChatStatusInDatabase(uid, reg);



                dialog.dismiss();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
    }


    private void resetChatStatusInDatabase(String uid, String reg)
    {

        final DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(reg).child(ACTIVE_APPOINTMENTS).child(uid);

        try {

            dRef.child("appointmentChatStarted").setValue("yes");

        }
        catch (Exception e){}


        final DatabaseReference pRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_PATIENT).child(uid).child(ACTIVE_APPOINTMENTS).child(reg);

        try {
            pRef.child("appointmentChatStarted").setValue("yes");
        }catch (Exception e){}

    }


}
