package com.navin.trialsih.doctorActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navin.trialsih.R;
import com.navin.trialsih.doctorActivities.bottomNavigation.HomeFragment;
import com.navin.trialsih.doctorActivities.bottomNavigation.ProfileFragment;
import com.navin.trialsih.doctorActivities.bottomNavigation.VoicePresFragment;

public class DoctorDashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    
    private Context mContext;
    private View v;

    private static String REG_NUMBER;

    private boolean isHomeShowing = false;

    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String USER_TYPE_PATIENT = "patients";
    private final static String ACTIVE_APPOINTMENTS = "appointments";
    private final static String PROFILE = "profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);
        
        mContext = this;
        v = getWindow().getDecorView().getRootView();

        REG_NUMBER = getRegNumber();

        isHomeShowing = true;

        bottomNavigationView = findViewById(R.id.doctor_bottom_navigationView);
        
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                
                displaySelectedScreen(menuItem.getItemId());
                
                return true;
            }
        });

        showHomeFragment();
        checkProfileCompletion();

    }


    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        Bundle bundle = new Bundle();
        bundle.putString("regNumber", REG_NUMBER);

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.doctor_appointments:
                isHomeShowing = true;
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                fragment = new HomeFragment();
                break;

            case R.id.doctor_mic:
                isHomeShowing = false;
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                fragment = new VoicePresFragment();
                break;

            case R.id.doctor_profile:
                isHomeShowing = false;
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                fragment = new ProfileFragment();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.doctor_container, fragment);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {

        if (!isHomeShowing) {

            Bundle bundle = new Bundle();
            bundle.putString("regNumber", REG_NUMBER);

            Fragment fragment = new HomeFragment();

            fragment.setArguments(bundle);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.doctor_container, fragment);
            ft.commit();

            bottomNavigationView.getMenu().getItem(0).setChecked(true);

            isHomeShowing = true;
        }
        else
        {
            super.onBackPressed();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        REG_NUMBER = getRegNumber();

        //checkProfileCompletion();

        //showHomeFragment();

    }

    private void showHomeFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putString("regNumber", REG_NUMBER);

        Fragment fragment = new HomeFragment();

        fragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.doctor_container, fragment);
        ft.commit();

        bottomNavigationView.getMenu().getItem(0).setChecked(true);

        isHomeShowing = true;
    }


    private String getRegNumber()
    {
        SharedPreferences doctorRegNumberPref = mContext.getSharedPreferences("doctorRegNumberPref",MODE_PRIVATE);

        String reg = doctorRegNumberPref.getString("regNumber", null);

        return reg;

    }


    private void checkProfileCompletion()
    {

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(PROFILE);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() <= 3)
                {
                    Snackbar.make(v, "All options are disabled till profile completion", Snackbar.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
