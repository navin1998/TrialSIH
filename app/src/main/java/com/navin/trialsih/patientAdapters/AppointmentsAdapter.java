package com.navin.trialsih.patientAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.navin.trialsih.R;
import com.navin.trialsih.patientsClasses.Appointments;

import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ImageViewHolder>{


    private Context mContext;
    private List<Appointments> mUploads;
    private View root;

    public AppointmentsAdapter(Context context, List<Appointments> uploads) {
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

        final Appointments appointments = mUploads.get(position);
        holder.textViewName.setText(appointments.getAppointmentDoctorName());
        holder.textViewType.setText(appointments.getAppointmentDoctorPhone());

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
}
