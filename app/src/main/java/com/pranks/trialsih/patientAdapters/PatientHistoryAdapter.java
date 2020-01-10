package com.pranks.trialsih.patientAdapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranks.trialsih.R;
import com.pranks.trialsih.patientsClasses.PatientHistory;

import java.util.List;

public class PatientHistoryAdapter extends RecyclerView.Adapter<PatientHistoryAdapter.ImageViewHolder>{


    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String PROFILE = "profile";


    private Context mContext;
    private List<PatientHistory> mUploads;
    private View root;

    public PatientHistoryAdapter(Context context, List<PatientHistory> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        root = LayoutInflater.from(mContext).inflate(R.layout.patient_history_item, parent, false);

        return new ImageViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        final PatientHistory history = mUploads.get(position);
        holder.textViewName.setText(history.getAppointmentDoctorName());
        holder.textViewType.setText(history.getAppointmentDoctorPhone());


        holder.textViewCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProfileDialog(history.getAppointmentDoctorRegNumber());

            }
        });


    }



    private void showProfileDialog(final String regNumber)
    {

        final ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();


        String REG_NUMBER = regNumber;

        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.doctor_profile_history_dialog_layout, null);

        final ImageView doctorPic = alertLayout.findViewById(R.id.dialogDoctorProfilePic);

        final TextView doctorName = alertLayout.findViewById(R.id.dialogDoctorName);
        final TextView doctorRegNumber = alertLayout.findViewById(R.id.dialogDoctorRegNumber);
        final TextView doctorGender = alertLayout.findViewById(R.id.dialogDoctorGender);
        final TextView doctorPhone = alertLayout.findViewById(R.id.dialogDoctorBookingPhone);
        final TextView doctorAddress = alertLayout.findViewById(R.id.dialogDoctorAddress);
        final TextView doctorFee = alertLayout.findViewById(R.id.dialogDoctorFee);


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

                doctorAddress.setText(dataSnapshot.child("doctorClinicAddress").getValue().toString());
                doctorFee.setText(dataSnapshot.child("doctorFee").getValue().toString());


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
        pDialog.hide();

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
            textViewCalendar = itemView.findViewById(R.id.cardView_details);
            textViewType = itemView.findViewById(R.id.cardView_commodityType);
            imageView = itemView.findViewById(R.id.cardView_commodityImageView);

        }
    }

}
