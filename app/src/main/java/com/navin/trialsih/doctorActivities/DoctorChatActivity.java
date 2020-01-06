package com.navin.trialsih.doctorActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navin.trialsih.R;
import com.navin.trialsih.doctorClasses.DoctorAppointments;
import com.navin.trialsih.patientsClasses.PatientAppointments;

import java.io.IOException;
import java.util.UUID;

public class DoctorChatActivity extends AppCompatActivity {

    private String PATIENT_UID;
    private String DOCTOR_REG_NUMBER;
    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String USER_TYPE_PATIENT = "patients";
    private final static String ACTIVE_APPOINTMENTS = "activeAppointments";
    private final static String PREV_APPOINTMENTS = "prevAppointments";

    private static final String TAG = DoctorChatActivity.class.getSimpleName();
    private static final int USER = 10001;
    private static final int BOT = 10002;

    private String uuid = UUID.randomUUID().toString();
    private LinearLayout chatLayout;
    private EditText queryEditText;

    private Context mContext;
    private View v;
    int counting=0;

    DatabaseReference mrefrence;
    int count=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_chat);

        mrefrence=FirebaseDatabase.getInstance().getReference();

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


        //Toast.makeText(this, "Patient UID: " + PATIENT_UID + "\nDoctor reg number: " + DOCTOR_REG_NUMBER, Toast.LENGTH_SHORT).show();


        final ScrollView scrollview = findViewById(R.id.chatScrollView);
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        chatLayout = findViewById(R.id.chatLayout);

        ImageView sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this::sendMessage);

        queryEditText = findViewById(R.id.queryEditText);
        queryEditText.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        sendMessage(sendBtn);
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });

        getTextFromPatient();

  }


    private void sendMessage(View view) {
        String msg = queryEditText.getText().toString();
        if (msg.trim().isEmpty()) {
            Toast.makeText(DoctorChatActivity.this, "Please enter your query!", Toast.LENGTH_LONG).show();
        } else {
            showTextView(msg, USER);
            mrefrence.child("doctors").child(DOCTOR_REG_NUMBER).child("activeAppointments").child(PATIENT_UID).child("doctor_chat").child("statement"+count).setValue(msg);
            count++;
            queryEditText.setText("");

            //call Patient response


        }
    }

    void getTextFromPatient(){
                    try {
                        // read the message sent to this client
                        mrefrence.child("doctors").child(DOCTOR_REG_NUMBER).child("activeAppointments")
                                .child(PATIENT_UID).child("patient_chat").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int size=0;
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    size++;
                                }
                                int h1=1;
                                for(DataSnapshot ds1:dataSnapshot.getChildren()){
                                    if(h1==size){
                                        showTextView(dataSnapshot.child("statement"+size).getValue().toString(),BOT);
                                        break;
                                    }
                                    h1++;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } catch (Exception e) {

                        e.printStackTrace();
                    }

    }

    private void showTextView(String message, int type) {
        FrameLayout layout;
        switch (type) {
            case USER:
                layout = getUserLayout();
                break;
            case BOT:
                layout = getBotLayout();
                break;
            default:
                layout = getBotLayout();
                break;
        }
        layout.setFocusableInTouchMode(true);
        chatLayout.addView(layout); // move focus to text view to automatically make it scroll up if softfocus
        TextView tv = layout.findViewById(R.id.chatMsg);
        tv.setText(message);
        layout.requestFocus();
        queryEditText.requestFocus(); // change focus back to edit text to continue typing
    }



    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(DoctorChatActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(DoctorChatActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
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

        getActiveAppointmentsInDoctorNode();
        getActiveAppointmentsInPatientNode();

        Intent intent = new Intent(mContext, DoctorDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }


    private void getActiveAppointmentsInDoctorNode()
    {

        final DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(DOCTOR_REG_NUMBER).child(ACTIVE_APPOINTMENTS).child(PATIENT_UID);

        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DoctorAppointments doctorAppointments = dataSnapshot.getValue(DoctorAppointments.class);

                dRef.setValue(null);

                try {

                    dRef.child("doctor_chat").setValue(null);
                    dRef.child("patient_chat").setValue(null);

                }
                catch (Exception e){}

                moveToPrevAppointmentsInDoctorNode(doctorAppointments);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    private void getActiveAppointmentsInPatientNode()
    {

        final DatabaseReference pRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_PATIENT).child(PATIENT_UID).child(ACTIVE_APPOINTMENTS).child(DOCTOR_REG_NUMBER);

        pRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                PatientAppointments patientAppointments = dataSnapshot.getValue(PatientAppointments.class);

                pRef.setValue(null);

                moveToPrevAppointmentsInPatientNode(patientAppointments);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void moveToPrevAppointmentsInDoctorNode(final DoctorAppointments doctorAppointments)
    {

        final DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(DOCTOR_REG_NUMBER).child(PREV_APPOINTMENTS);


        try {
            if (dRef.getKey() == null) {

                // try will run if no node with this name is present...

            }
        }catch (Exception e){

            dRef.setValue("");

        }


        dRef.child(PATIENT_UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                try {
                    if (dRef.child(PATIENT_UID).getKey() == null) {

                        // try will run if no node with this name is present...

                    }
                }catch (Exception e){

                    dRef.child(PATIENT_UID).setValue("");

                }

                String nameOfNode = "";

                try {
                    nameOfNode = PREV_APPOINTMENTS + (dataSnapshot.child(PREV_APPOINTMENTS).child(PATIENT_UID).getChildrenCount() + 1);
                }
                catch (Exception e)
                {
                    nameOfNode = PREV_APPOINTMENTS + "1";
                }

                dRef.child(PATIENT_UID).child(nameOfNode).setValue(doctorAppointments)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(mContext, "Appointment added to history section", Toast.LENGTH_LONG).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void moveToPrevAppointmentsInPatientNode(final PatientAppointments patientAppointments)
    {

        final DatabaseReference pRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_PATIENT).child(PATIENT_UID).child(PREV_APPOINTMENTS);

        try {
            if (pRef.getKey() == null) {

                // try will run if no node with this name is present...

            }
        }catch (Exception e){

            pRef.setValue("");

        }

        pRef.child(DOCTOR_REG_NUMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                try {
                    if (pRef.child(DOCTOR_REG_NUMBER).getKey() == null) {

                        // try will run if no node with this name is present...

                    }
                }catch (Exception e){

                    pRef.child(DOCTOR_REG_NUMBER).setValue("");

                }

                String nameOfNode = "";

                try {
                    nameOfNode = PREV_APPOINTMENTS + (dataSnapshot.child(PREV_APPOINTMENTS).child(DOCTOR_REG_NUMBER).getChildrenCount() + 1);
                }
                catch (Exception e)
                {
                    nameOfNode = PREV_APPOINTMENTS + "1";
                }


                pRef.child(DOCTOR_REG_NUMBER).child(nameOfNode).setValue(patientAppointments)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                // successfully added to previous appointments section...

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
