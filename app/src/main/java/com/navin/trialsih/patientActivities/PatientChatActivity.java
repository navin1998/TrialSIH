package com.navin.trialsih.patientActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navin.trialsih.R;
import com.navin.trialsih.doctorActivities.DoctorChatActivity;

import java.util.UUID;

public class PatientChatActivity extends AppCompatActivity {


    private static String DOCTOR_REG_NUMBER;

    private static final String TAG = DoctorChatActivity.class.getSimpleName();
    private static final int USER = 10001;
    private static final int BOT = 10002;

    private String uuid = UUID.randomUUID().toString();
    private LinearLayout chatLayout;
    private EditText queryEditText;

    FirebaseAuth mAuth;

    DatabaseReference mrefrence;
    int count=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_chat);
        mrefrence= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Chat");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        try {
            DOCTOR_REG_NUMBER = bundle.getString("doctorRegNumber");
        }catch (Exception e){}


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

        getTextFromDoctor();


    }




    private void sendMessage(View view) {
        String msg = queryEditText.getText().toString();
        if (msg.trim().isEmpty()) {
            Toast.makeText(PatientChatActivity.this, "Please enter your query!", Toast.LENGTH_LONG).show();
        } else {
            showTextView(msg, USER);
            mrefrence.child("doctors").child(DOCTOR_REG_NUMBER).child("activeAppointments").child(mAuth.getCurrentUser().getUid()).child("patient_chat").child("statement"+count).setValue(msg);
            count++;
            queryEditText.setText("");


        }
    }

    void getTextFromDoctor(){
        try {
            // read the message sent to this client
            mrefrence.child("doctors").child(DOCTOR_REG_NUMBER).child("activeAppointments")
                    .child(mAuth.getCurrentUser().getUid()).child("doctor_chat").addValueEventListener(new ValueEventListener() {
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
        LayoutInflater inflater = LayoutInflater.from(PatientChatActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(PatientChatActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
        }

        return true;
    }


}