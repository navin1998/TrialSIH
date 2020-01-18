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
import com.pranks.trialsih.patientsClasses.PatientTransactionHistory;

import java.util.List;

public class PatientTransactionHistoryAdapter extends RecyclerView.Adapter<PatientTransactionHistoryAdapter.ImageViewHolder>{


    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String PROFILE = "profile";


    private Context mContext;
    private List<PatientTransactionHistory> mUploads;
    private View root;

    public PatientTransactionHistoryAdapter(Context context, List<PatientTransactionHistory> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        root = LayoutInflater.from(mContext).inflate(R.layout.patient_transaction_item, parent, false);

        return new ImageViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        final PatientTransactionHistory history = mUploads.get(position);
        holder.textViewName.setText(history.getDoctorName());
        holder.textViewAmount.setText(history.getDoctorFee());


        if (history.getDoctorFeeStatus().toLowerCase().equals("paid"))
        {
            holder.textViewPaymentType.setText("UPI PAID");
            holder.textViewPaymentType.setBackground(mContext.getResources().getDrawable(R.drawable.background_online_text));
        }
        else
        {
            holder.textViewPaymentType.setText("CASH PAID");
            holder.textViewPaymentType.setBackground(mContext.getResources().getDrawable(R.drawable.background_offline_text));
        }


        try
        {
            Glide.with(mContext)
                    .load(history.getDoctorImage())
                    .placeholder(R.drawable.man)
                    .into(holder.imageView);
        }
        catch (Exception e){}


    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewAmount;
        public TextView textViewPaymentType;
        public ImageView imageView;
        public CardView cardView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.gallery_cardView);
            textViewName = itemView.findViewById(R.id.cardView_doctorName);
            textViewAmount = itemView.findViewById(R.id.cardView_amount);
            textViewPaymentType = itemView.findViewById(R.id.cardView_paymentType);
            imageView = itemView.findViewById(R.id.cardView_doctorImage);

        }
    }

}
