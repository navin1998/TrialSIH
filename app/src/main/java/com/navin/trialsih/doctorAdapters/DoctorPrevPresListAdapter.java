package com.navin.trialsih.doctorAdapters;

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
import com.navin.trialsih.doctorClasses.DoctorPrevPresLinkItem;

import java.util.List;

public class DoctorPrevPresListAdapter extends RecyclerView.Adapter<DoctorPrevPresListAdapter.ImageViewHolder>{

    private Context mContext;
    private List<DoctorPrevPresLinkItem> mUploads;
    private View root;

    public DoctorPrevPresListAdapter(Context context, List<DoctorPrevPresLinkItem> uploads) {

        mContext = context;
        mUploads = uploads;

    }


    @NonNull
    @Override
    public DoctorPrevPresListAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        root = LayoutInflater.from(mContext).inflate(R.layout.doctor_prev_pres_item, parent, false);

        return new ImageViewHolder(root);

    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        final DoctorPrevPresLinkItem linkItem = mUploads.get(position);
        holder.email.setText(linkItem.getFileUrl());
        holder.date.setText(linkItem.getDate());

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView email;
        public TextView date;

        public ImageViewHolder(View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.cardView_link);
            date = itemView.findViewById(R.id.cardView_date);

        }
    }


}
