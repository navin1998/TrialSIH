package com.navin.trialsih.doctorAdapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navin.trialsih.R;
import com.navin.trialsih.doctorActivities.DoctorChatActivity;
import com.navin.trialsih.doctorClasses.DoctorAppointments;

import java.util.List;

public class DoctorAppointmentsAdapter extends RecyclerView.Adapter<DoctorAppointmentsAdapter.ImageViewHolder>{


    private Context mContext;
    private List<DoctorAppointments> mUploads;
    private View root;

    private static String REG_NUMBER;

    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String PROFILE = "profile";
    private final static String ACTIVE_APPOINTMENTS = "activeAppointments";
    private final static String APPOINTMENT_FEE_STATUS = "appointmentFeeStatus";

    public DoctorAppointmentsAdapter(Context context, List<DoctorAppointments> uploads) {
        mContext = context;
        mUploads = uploads;

        getRegNumber();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        root = LayoutInflater.from(mContext).inflate(R.layout.doctor_appointments_item, parent, false);

        return new ImageViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        getRegNumber();

        final DoctorAppointments appointments = mUploads.get(position);
        holder.textViewName.setText(appointments.getAppointmentPatientName());
        holder.textViewType.setText(appointments.getAppointmentPatientPhone());
        
        holder.textViewCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verifyForChat(appointments.getAppointmentPatientUid());

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
            textViewCalendar = itemView.findViewById(R.id.cardView_chat_with_doctor);
            textViewType = itemView.findViewById(R.id.cardView_commodityType);
            imageView = itemView.findViewById(R.id.cardView_commodityImageView);
        }
    }


    private void getRegNumber()
    {
        SharedPreferences doctorRegNumberPref = mContext.getSharedPreferences("doctorRegNumberPref", Context.MODE_PRIVATE);

        REG_NUMBER = doctorRegNumberPref.getString("regNumber", null);

    }


    private void verifyForChat(final String uid)
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
                    showChatConfirmDialogForOnlinePayment();
                }
                else
                {
                    showChatConfirmDialogForCash();
                }

                dialog.cancel();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                dialog.cancel();

            }
        });

    }


    private void showChatConfirmDialogForOnlinePayment()
    {
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_confirm_chat_for_paid, null);
        final Button noBtn = alertLayout.findViewById(R.id.btn_no);
        final Button yesBtn = alertLayout.findViewById(R.id.btn_yes);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mContext.startActivity(new Intent(mContext, DoctorChatActivity.class));

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



    private void showChatConfirmDialogForCash()
    {
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_confirm_chat_for_non_paid, null);
        final Button noBtn = alertLayout.findViewById(R.id.btn_no);
        final Button yesBtn = alertLayout.findViewById(R.id.btn_yes);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mContext.startActivity(new Intent(mContext, DoctorChatActivity.class));

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


}
