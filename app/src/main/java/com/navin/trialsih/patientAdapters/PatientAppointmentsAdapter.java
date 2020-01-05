package com.navin.trialsih.patientAdapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navin.trialsih.R;
import com.navin.trialsih.doctorActivities.DoctorChatActivity;
import com.navin.trialsih.patientActivities.PatientChatActivity;
import com.navin.trialsih.patientsClasses.PatientAppointments;

import java.util.List;

public class PatientAppointmentsAdapter extends RecyclerView.Adapter<PatientAppointmentsAdapter.ImageViewHolder>{


    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String USER_TYPE_PATIENT = "patients";
    private final static String PROFILE = "profile";
    private final static String ACTIVE_APPOINTMENTS = "activeAppointments";
    private final static String APPOINTMENT_FEE_STATUS = "appointmentFeeStatus";
    private final static String APPOINTMENT_CHAT_STATUS = "appointmentChatStarted";


    private Context mContext;
    private List<PatientAppointments> mUploads;
    private View root;

    public PatientAppointmentsAdapter(Context context, List<PatientAppointments> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        root = LayoutInflater.from(mContext).inflate(R.layout.appointments_item, parent, false);

        return new ImageViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        final PatientAppointments appointments = mUploads.get(position);
        holder.textViewName.setText(appointments.getAppointmentDoctorName());
        holder.textViewType.setText(appointments.getAppointmentDoctorPhone());


        final Bundle bundle = new Bundle();
        bundle.putString("doctorRegNumber", appointments.getAppointmentDoctorRegNumber());


        holder.textViewCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verifyForChat(FirebaseAuth.getInstance().getCurrentUser().getUid(), appointments.getAppointmentDoctorRegNumber(), bundle);

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



    private void verifyForChat(final String uid, final String reg, final Bundle bundle)
    {

        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_PATIENT).child(uid).child(ACTIVE_APPOINTMENTS);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    if (dataSnapshot.child(reg).child(APPOINTMENT_FEE_STATUS).getValue().toString().toLowerCase().equals("paid")) {
                        try {
                            if (dataSnapshot.child(reg).child(APPOINTMENT_CHAT_STATUS).getValue().toString().toLowerCase().equals("yes")) {

                               Intent intent = new Intent(mContext, PatientChatActivity.class);
                               intent.putExtras(bundle);
                               mContext.startActivity(intent);


                            }
                            else if (dataSnapshot.child(reg).child(APPOINTMENT_CHAT_STATUS).getValue().toString().toLowerCase().equals("ended"))
                            {
                                Toast.makeText(mContext, "Chat has already ended", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(mContext, "Please wait for doctor to start the chat", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {

                            Toast.makeText(mContext, "Please wait for doctor to start the chat", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        try {
                            if (dataSnapshot.child(reg).child(APPOINTMENT_CHAT_STATUS).getValue().toString().toLowerCase().equals("yes")) {

                                Intent intent = new Intent(mContext, PatientChatActivity.class);
                                intent.putExtras(bundle);
                                mContext.startActivity(intent);

                            }
                            else if (dataSnapshot.child(reg).child(APPOINTMENT_CHAT_STATUS).getValue().toString().toLowerCase().equals("ended"))
                            {
                                Toast.makeText(mContext, "Chat has already ended", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(mContext, "Please wait for doctor to start the chat", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {

                            Toast.makeText(mContext, "Please wait for doctor to start the chat", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                catch (Exception e)
                {

                    if (dataSnapshot.child(reg).child(APPOINTMENT_FEE_STATUS).getValue().toString().toLowerCase().equals("paid")) {
                        try {
                            if (dataSnapshot.child(reg).child(APPOINTMENT_CHAT_STATUS).getValue().toString().toLowerCase().equals("yes")) {

                                Intent intent = new Intent(mContext, PatientChatActivity.class);
                                intent.putExtras(bundle);
                                mContext.startActivity(intent);

                            }
                            else if (dataSnapshot.child(reg).child(APPOINTMENT_CHAT_STATUS).getValue().toString().toLowerCase().equals("ended"))
                            {
                                Toast.makeText(mContext, "Chat has already ended", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(mContext, "Please wait for doctor to start the chat", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception exc) {

                            Toast.makeText(mContext, "Please wait for doctor to start the chat", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        try {
                            if (dataSnapshot.child(reg).child(APPOINTMENT_CHAT_STATUS).getValue().toString().toLowerCase().equals("yes")) {


                                Intent intent = new Intent(mContext, PatientChatActivity.class);
                                intent.putExtras(bundle);
                                mContext.startActivity(intent);

                            }
                            else if (dataSnapshot.child(reg).child(APPOINTMENT_CHAT_STATUS).getValue().toString().toLowerCase().equals("ended"))
                            {
                                Toast.makeText(mContext, "Chat has already ended", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(mContext, "Please wait for doctor to start the chat", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {

                            Toast.makeText(mContext, "Please wait for doctor to start the chat", Toast.LENGTH_SHORT).show();
                        }

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


}
