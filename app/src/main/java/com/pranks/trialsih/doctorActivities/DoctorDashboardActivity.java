package com.pranks.trialsih.doctorActivities;

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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranks.trialsih.R;
import com.pranks.trialsih.SignInActivity;
import com.pranks.trialsih.doctorActivities.bottomNavigation.HomeFragment;
import com.pranks.trialsih.doctorActivities.bottomNavigation.ProfileFragment;
import com.pranks.trialsih.doctorActivities.bottomNavigation.RecyclerViewFragment;
import com.pranks.trialsih.doctorActivities.bottomNavigation.VoicePresFragment;
import com.pranks.trialsih.doctorActivities.navigationDrawer.HistoryFragment;
import com.pranks.trialsih.doctorActivities.navigationDrawer.SettingsFragment;
import com.pranks.trialsih.doctorAdapters.DoctorPrevPresListAdapter;
import com.pranks.trialsih.doctorClasses.DoctorPrevPresLinkItem;
import com.pranks.trialsih.doctorDBHelpers.DoctorCredentialsDBHelper;

import java.util.ArrayList;

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

    private final static String PATIENT_PRES = "patientPrescriptions";

    private AppBarConfiguration mAppBarConfiguration;

    private NavigationView navigationView;

    private Toolbar toolbar;

    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doctor_dashboard);
        toolbar = findViewById(R.id.doctor_toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.doctor_drawer_layout);
        navigationView = findViewById(R.id.doctor_nav_view);

        mContext = this;
        v = getWindow().getDecorView().getRootView();

        REG_NUMBER = getRegNumber();


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_doctor_appointment, R.id.nav_doctor_profile, R.id.nav_doctor_history,
                R.id.nav_doctor_settings, R.id.nav_doctor_recycler_view)
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
                checkAllNavTrue();
                checkAllBottomNavTrue();
                isHomeShowing = true;
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                navigationView.getMenu().getItem(0).setChecked(true);
                fragment = new HomeFragment();
                break;

            case R.id.doctor_mic:
                checkAllNavFalse();
                isHomeShowing = false;
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                fragment = new VoicePresFragment();
                break;

            case R.id.doctor_profile:
            case R.id.nav_doctor_profile_icon:
                checkAllNavTrue();
                checkAllBottomNavTrue();
                isHomeShowing = false;
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                navigationView.getMenu().getItem(1).setChecked(true);
                fragment = new ProfileFragment();
                break;

            case R.id.nav_doctor_history_icon:
                isHomeShowing = false;
                checkAllBottomNavFalse();
                navigationView.getMenu().getItem(2).setChecked(true);
                fragment = new HistoryFragment();
                break;

            case R.id.nav_doctor_settings_icon:
                isHomeShowing = false;
                checkAllBottomNavFalse();
                navigationView.getMenu().getItem(3).setChecked(true);
                fragment = new SettingsFragment();
                break;


//            case R.id.nav_doctor_search_icon:
//                isHomeShowing = true;
//                showHomeFragment();
//                checkAllBottomNavFalse();
//                navigationView.getMenu().getItem(0).setChecked(true);
//                showSearchDialog();
//                break;

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

            checkAllBottomNavTrue();
            checkAllNavTrue();
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

        checkAllBottomNavTrue();
        checkAllNavTrue();

        isHomeShowing = true;
    }


    public String getRegNumber()
    {

        DoctorCredentialsDBHelper dbHelper = new DoctorCredentialsDBHelper(this);

        if (dbHelper.getRegNumber() != null)
        {
            return dbHelper.getRegNumber();
        }
        else {
            SharedPreferences doctorRegNumberPref = getSharedPreferences("doctorRegNumberPref", MODE_PRIVATE);

            String reg = doctorRegNumberPref.getString("regNumber", null);

            return reg;
        }

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


    /**
     *
     * this was for the search icon in iour app, now for no use, but please don't delete it...
     *
     *
     */


    private void showSearchDialog()
    {

        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_email, null);
        final Button cancelBtn = alertLayout.findViewById(R.id.btn_cancel);
        final Button searchBtn = alertLayout.findViewById(R.id.btn_search);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);
        final TextView editText = alertLayout.findViewById(R.id.editText_email);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(false);

        final android.app.AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editText.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(mContext, "Please enter a mail", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    showHomeFragment();

                    navigationView.getMenu().getItem(3).setChecked(false);
                    navigationView.getMenu().getItem(0).setChecked(true);
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(editText.getText()
                .toString()).matches())
                {
                    Toast.makeText(mContext, "Please enter a valid mail", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    showHomeFragment();

                    navigationView.getMenu().getItem(3).setChecked(false);
                    navigationView.getMenu().getItem(0).setChecked(true);
                    return;
                }


                //  start fragment for recycler view...
                showRecyclerViewFragment(editText.getText().toString().trim().toLowerCase());


                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                checkAllBottomNavTrue();
                checkAllNavTrue();
                navigationView.getMenu().getItem(0).setChecked(true);
                bottomNavigationView.getMenu().getItem(0).setChecked(true);

                dialog.dismiss();

            }
        });


    }


    private void checkAllBottomNavFalse()
    {
        bottomNavigationView.getMenu().getItem(0).setCheckable(false);
        bottomNavigationView.getMenu().getItem(1).setCheckable(false);
        bottomNavigationView.getMenu().getItem(2).setCheckable(false);
    }


    private void checkAllNavFalse()
    {
        navigationView.getMenu().getItem(0).setCheckable(false);
        navigationView.getMenu().getItem(1).setCheckable(false);
        navigationView.getMenu().getItem(2).setCheckable(false);
        navigationView.getMenu().getItem(3).setCheckable(false);
        navigationView.getMenu().getItem(4).setCheckable(false);
    }


    private void checkAllBottomNavTrue()
    {
        bottomNavigationView.getMenu().getItem(0).setCheckable(true);
        bottomNavigationView.getMenu().getItem(1).setCheckable(true);
        bottomNavigationView.getMenu().getItem(2).setCheckable(true);
    }

    private void checkAllNavTrue()
    {
        navigationView.getMenu().getItem(0).setCheckable(true);
        navigationView.getMenu().getItem(1).setCheckable(true);
        navigationView.getMenu().getItem(2).setCheckable(true);
        navigationView.getMenu().getItem(3).setCheckable(true);
        navigationView.getMenu().getItem(4).setCheckable(false);
    }


    private void showRecyclerViewFragment(String EMAIL)
    {
        Bundle bundle = new Bundle();
        bundle.putString("regNumber", REG_NUMBER);
        bundle.putString("patientMail", EMAIL);

        Fragment fragment = new RecyclerViewFragment();

        fragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.doctor_nav_host_fragment, fragment);
        ft.commit();

        checkAllBottomNavFalse();
        checkAllNavTrue();

        navigationView.getMenu().getItem(3).setChecked(true);

        isHomeShowing = false;
    }



    private void checkForPreviousPres(final String email)
    {

        String finalMail = email.replaceAll("\\.", "");

        final DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(PATIENT_PRES).child(finalMail);

        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //if (dataSnapshot.exists())
                //{
                    showRecyclerViewDialog(finalMail);
                Toast.makeText(mContext, "Mail: " + finalMail, Toast.LENGTH_SHORT).show();
                //}
               /* else
                {
                    Toast.makeText(mContext, "No previous history found" , Toast.LENGTH_LONG).show();
                }*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void showRecyclerViewDialog(String email)
    {

        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_for_list_of_prev_pres, null);
        final ProgressBar mPCircle = alertLayout.findViewById(R.id.progress_circle);
        final RecyclerView mRView = alertLayout.findViewById(R.id.recycler_view_link);


        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        addItemsToRecyclerView(email, mRView, mPCircle);


    }


    private void addItemsToRecyclerView(String email, RecyclerView mRView, ProgressBar mPCircle)
    {

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(PATIENT_PRES).child("kundan3316@gmailcom").child("document1578253867067");

        try {

            if (mRef.getKey() != null) {

                mRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        ArrayList<String> listOfEmails = new ArrayList<>();
                        final ArrayList<DoctorPrevPresLinkItem> list = new ArrayList<>();

                        Toast.makeText(mContext, "Size: " + list.size(), Toast.LENGTH_SHORT).show();

                        list.add(dataSnapshot.getValue(DoctorPrevPresLinkItem.class));


                        DoctorPrevPresListAdapter mAdapter = new DoctorPrevPresListAdapter(mContext, list);
                        mRView.setHasFixedSize(true);
                        mRView.setLayoutManager(new LinearLayoutManager(mContext));

                        mRView.setItemAnimator(new DefaultItemAnimator());

                        mRView.setAdapter(mAdapter);

                        mPCircle.setVisibility(View.GONE);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Toast.makeText(mContext, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        mPCircle.setVisibility(View.GONE);

                    }
                });


            }
            else
            {

                mPCircle.setVisibility(View.GONE);
                Toast.makeText(mContext, "No previous history found", Toast.LENGTH_LONG).show();

            }
        }
        catch (Exception e)
        {

            mPCircle.setVisibility(View.GONE);
            // no patientPrescriptions node is present...
            Toast.makeText(mContext, "No previous history found", Toast.LENGTH_LONG).show();

        }


    }



}
