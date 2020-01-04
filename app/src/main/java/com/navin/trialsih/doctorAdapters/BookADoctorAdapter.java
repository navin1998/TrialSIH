package com.navin.trialsih.doctorAdapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navin.trialsih.R;
import com.navin.trialsih.doctorClasses.DoctorDetails;
import com.navin.trialsih.patientAdapters.AppointmentsAdapter;
import com.navin.trialsih.patientsClasses.Appointments;

import java.util.List;

public class BookADoctorAdapter extends RecyclerView.Adapter<BookADoctorAdapter.ImageViewHolder> {

    private Context mContext;
    private List<DoctorDetails> mUploads;
    private View root;


    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String PROFILE = "profile";
    private static String REG_NUMBER;

    public BookADoctorAdapter(Context context, List<DoctorDetails> uploads) {
        mContext = context;
        mUploads = uploads;
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


    private void showBookingDialog(String regNumber)
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

                Toast.makeText(mContext, "Available soon", Toast.LENGTH_SHORT).show();
                
                dialog.dismiss();
            }
        });

    }


}
