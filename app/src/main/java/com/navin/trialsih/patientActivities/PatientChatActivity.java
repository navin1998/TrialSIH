package com.navin.trialsih.patientActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.navin.trialsih.R;
import com.navin.trialsih.doctorActivities.DoctorChatActivity;

import java.util.UUID;

public class PatientChatActivity extends AppCompatActivity {

    DatabaseReference mrefrence;
    int count=1;

    private static final String TAG = DoctorChatActivity.class.getSimpleName();
    private static final int USER = 10001;
    private static final int BOT = 10002;

    private String uuid = UUID.randomUUID().toString();
    private LinearLayout chatLayout;
    private EditText queryEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_chat);

        getSupportActionBar().setTitle("Chat");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
