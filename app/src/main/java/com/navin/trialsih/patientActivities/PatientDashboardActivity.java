package com.navin.trialsih.patientActivities;

import android.content.Context;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.navin.trialsih.R;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PatientDashboardActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private ImageView profilePic;
    private TextView name;
    private TextView email;

    private Context mContext;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mContext = this;
        v = getWindow().getDecorView().getRootView();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_patient_appointment, R.id.nav_patient_profile, R.id.nav_patient_previous_appointment,
                R.id.nav_patient_previous_transactions, R.id.nav_patient_share, R.id.nav_patient_feedback)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        profilePic = findViewById(R.id.patient_profile_pic);
        name = findViewById(R.id.patient_name);
        email = findViewById(R.id.patient_email);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.patient_dashboard, menu);
//        return true;
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);

        ImageView userPic = (ImageView) hView.findViewById(R.id.patient_profile_pic);
        TextView userName = (TextView)hView.findViewById(R.id.patient_name);
        TextView userMail = (TextView)hView.findViewById(R.id.patient_email);

        Glide.with(mContext)
                .load(user.getPhotoUrl())
                .into(userPic);

        userName.setText(user.getDisplayName());
        userMail.setText(user.getEmail());

    }
}
