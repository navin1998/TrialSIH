package com.navin.trialsih.patientActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.navin.trialsih.R;

public class PatientChatActivity extends AppCompatActivity {


    private static String DOCTOR_REG_NUMBER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_chat);

        getSupportActionBar().setTitle("Chat");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        try {
            DOCTOR_REG_NUMBER = bundle.getString("doctorRegNumber");
        }catch (Exception e){}


        Toast.makeText(this, "REG: " + DOCTOR_REG_NUMBER, Toast.LENGTH_SHORT).show();


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
