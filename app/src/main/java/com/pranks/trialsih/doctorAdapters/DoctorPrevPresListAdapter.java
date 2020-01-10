package com.pranks.trialsih.doctorAdapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.pranks.trialsih.R;
import com.pranks.trialsih.doctorClasses.DoctorPrevPresLinkItem;

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
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        root = LayoutInflater.from(mContext).inflate(R.layout.doctor_prev_pres_item, parent, false);

        return new ImageViewHolder(root);

    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        final DoctorPrevPresLinkItem linkItem = mUploads.get(position);

        holder.email.setText(linkItem.getFileUrl());
        holder.date.setText(linkItem.getDate());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDownloadDialog(holder.email.getText().toString());

            }
        });

    }


    private void showDownloadDialog(String url)
    {

        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_confirm_download, null);
        final Button confirmBtn = alertLayout.findViewById(R.id.btn_confirm);
        final Button declineBtn = alertLayout.findViewById(R.id.btn_decline);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mContext.startActivity(myIntent);


                dialog.dismiss();
            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView email;
        public TextView date;
        public CardView cardView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.cardView_link);
            date = itemView.findViewById(R.id.cardView_date);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }


}
