package com.navin.trialsih.doctorActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.navin.trialsih.R;

public class DoctorChatActivity extends AppCompatActivity {

    private String PATIENT_UID;
    private String DOCTOR_REG_NUMBER;
    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String USER_TYPE_PATIENT = "patients";
    private final static String ACTIVE_APPOINTMENTS = "activeAppointments";


    private Context mContext;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_chat);

        getSupportActionBar().setTitle("Chat");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = this;
        v = getWindow().getDecorView().getRootView();

        try {

            Bundle bundle = getIntent().getExtras();
            PATIENT_UID = bundle.getString("patientUid");
            DOCTOR_REG_NUMBER = bundle.getString("doctorRegNumber");

        }
        catch (Exception e){}


        Toast.makeText(this, "Patient UID: " + PATIENT_UID + "\nDoctor reg number: " + DOCTOR_REG_NUMBER, Toast.LENGTH_SHORT).show();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
                
            case R.id.action_end_chat:
                showEndChatConfirmationDialog();
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.doctor_chat_screen_menu, menu);

        return true;
    }


    private void showEndChatConfirmationDialog()
    {
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_confirm_chat_end, null);
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

                endChat();

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


    private void endChat()
    {
        final DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(DOCTOR_REG_NUMBER).child(ACTIVE_APPOINTMENTS).child(PATIENT_UID);

        try {

            dRef.child("appointmentChatStarted").setValue("ended");

        }
        catch (Exception e){}

        final DatabaseReference pRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_PATIENT).child(PATIENT_UID).child(ACTIVE_APPOINTMENTS).child(DOCTOR_REG_NUMBER);

        try {
            pRef.child("appointmentChatStarted").setValue("ended");
        }catch (Exception e){}


        Snackbar.make(v, "Chat ended by doctor", Snackbar.LENGTH_LONG).show();

        Intent intent = new Intent(mContext, DoctorDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

}
