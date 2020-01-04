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
import com.google.android.material.snackbar.Snackbar;
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

    private final static String CONNECT_MODE_CHAT = "chat";
    private final static String CONNECT_MODE_VISIT = "visit";
    
    private final static String PAY_MODE_ONLINE = "online";
    private final static String PAY_MODE_CASH = "cash";

    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String PROFILE = "profile";
    private final static String DOCTOR_UPI_NAME = "doctorUpiName";
    private final static String DOCTOR_UPI_ID = "doctorUpiID";
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

                Toast.makeText(mContext, "Payment will start. Connect method: " + wayToConnect + "    Reg Number: " + regNumber, Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

    }



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
    
    
    
    private void showLastConfirmationDialog(String regNumber)
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

                dialog.dismiss();

            }
        });
    }
    


}
