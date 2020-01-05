package com.navin.trialsih.doctorActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navin.trialsih.R;
import com.navin.trialsih.SignInActivity;
import com.navin.trialsih.doctorActivities.bottomNavigation.HomeFragment;
import com.navin.trialsih.doctorActivities.bottomNavigation.ProfileFragment;
import com.navin.trialsih.doctorActivities.bottomNavigation.VoicePresFragment;
import com.navin.trialsih.doctorActivities.navigationDrawer.HistoryFragment;
import com.navin.trialsih.doctorActivities.navigationDrawer.SettingsFragment;
import com.navin.trialsih.doctorDBHelpers.DoctorCredentialsDBHelper;

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

    private AppBarConfiguration mAppBarConfiguration;

    private NavigationView navigationView;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doctor_dashboard);
        toolbar = findViewById(R.id.doctor_toolbar);
        setSupportActionBar(toolbar);
        final DrawerLayout drawer = findViewById(R.id.doctor_drawer_layout);
        navigationView = findViewById(R.id.doctor_nav_view);

        mContext = this;
        v = getWindow().getDecorView().getRootView();

        REG_NUMBER = getRegNumber();


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_doctor_appointment, R.id.nav_doctor_profile, R.id.nav_doctor_history,
                R.id.nav_doctor_settings)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.doctor_nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        bottomNavigationView = findViewById(R.id.doctor_bottom_navigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                displaySelectedScreen(menuItem.getItemId());

                return true;
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                displaySelectedScreen(menuItem.getItemId());

                return true;
            }



        });


        loadNavHeader();

        showHomeFragment();
        checkProfileCompletion();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.doctor_nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        NavController navController = Navigation.findNavController(this, R.id.doctor_nav_host_fragment);

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;


        Bundle bundle = new Bundle();
        bundle.putString("regNumber", REG_NUMBER);

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.doctor_appointments:
            case R.id.nav_doctor_appointment_icon:
                isHomeShowing = true;
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                navigationView.getMenu().getItem(0).setChecked(true);
                fragment = new HomeFragment();
                break;

            case R.id.doctor_mic:
                isHomeShowing = false;
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                fragment = new VoicePresFragment();
                break;

            case R.id.doctor_profile:
            case R.id.nav_doctor_profile_icon:
                isHomeShowing = false;
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                navigationView.getMenu().getItem(1).setChecked(true);
                fragment = new ProfileFragment();
                break;

            case R.id.nav_doctor_history_icon:
                isHomeShowing = false;
                navigationView.getMenu().getItem(2).setChecked(true);
                fragment = new HistoryFragment();
                break;

            case R.id.nav_doctor_settings_icon:
                isHomeShowing = false;
                navigationView.getMenu().getItem(3).setChecked(true);
                fragment = new SettingsFragment();
                break;

            case R.id.nav_doctor_logout_icon:
                showLogoutDialog();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.doctor_nav_host_fragment, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.doctor_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    @Override
    public void onBackPressed() {

        if (!isHomeShowing) {

            Bundle bundle = new Bundle();
            bundle.putString("regNumber", REG_NUMBER);

            Fragment fragment = new HomeFragment();

            fragment.setArguments(bundle);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.doctor_nav_host_fragment, fragment);
            ft.commit();

            bottomNavigationView.getMenu().getItem(0).setChecked(true);
            navigationView.getMenu().getItem(0).setChecked(true);

            isHomeShowing = true;
        }
        else
        {
            super.onBackPressed();
        }

    }


    private void showHomeFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putString("regNumber", REG_NUMBER);

        Fragment fragment = new HomeFragment();

        fragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.doctor_nav_host_fragment, fragment);
        ft.commit();

        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        navigationView.getMenu().getItem(0).setChecked(true);

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


//    @Override
//    protected void onStart() {
//        super.onStart();
//
//
//        loadNavHeader();
//
//    }


    private void loadNavHeader()
    {

        try {

            final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(PROFILE);


            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 3) {

                        NavigationView navigationView = (NavigationView) findViewById(R.id.doctor_nav_view);
                        View hView = navigationView.getHeaderView(0);

                        ImageView userPic = (ImageView) hView.findViewById(R.id.doctor_nav_profile_pic);
                        TextView userName = (TextView) hView.findViewById(R.id.doctor_nav_name);
                        TextView userReg = (TextView) hView.findViewById(R.id.doctor_nav_reg);

                        String doctorName = dataSnapshot.child("doctorName").getValue().toString();
                        String doctorRegNumber = dataSnapshot.child("doctorRegNumber").getValue().toString();
                        String doctorPhotoUri = dataSnapshot.child("doctorPhotoUri").getValue().toString();

                        try {
                            Glide.with(mContext)
                                    .load(doctorPhotoUri)
                                    .placeholder(R.drawable.man)
                                    .into(userPic);
                        } catch (Exception e) {
                        }

                        userName.setText(doctorName);
                        userReg.setText(doctorRegNumber);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e){}

    }


    private void showLogoutDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Sure to logout?");
        builder.setCancelable(false);

        builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(mContext, "Successfully signed out", Toast.LENGTH_SHORT).show();

                updateSignedInPreferencesAndLogOut();

                dialog.cancel();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();

    }


    private void updateSignedInPreferencesAndLogOut()
    {

        SharedPreferences doctorSignInPref = mContext.getSharedPreferences("doctorSingInPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = doctorSignInPref.edit();
        editor.putBoolean("isDoctorSignedIn", false);
        editor.commit();

        SharedPreferences doctorRegNumberPref = mContext.getSharedPreferences("doctorRegNumberPref", MODE_PRIVATE);
        SharedPreferences.Editor regEditor = doctorRegNumberPref.edit();
        regEditor.putString("regNumber", null);
        regEditor.commit();


        DoctorCredentialsDBHelper dbHelper = new DoctorCredentialsDBHelper(mContext);
        dbHelper.deleteDatabase();


        Intent intent = new Intent(mContext, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();

    }

}
