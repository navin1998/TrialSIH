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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
import com.google.android.gms.auth.api.phone.SmsRetriever;
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
import com.pranks.trialsih.doctorDBHelpers.NeuralNetworkDBHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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


        if (!checkInLocalDatabase())
        {
            new NeuralNetworks().execute();
        }


        /**
         *
         *
         *
         * below code is for demonstration purpose only...
         * remove after use or debugging...
         *
         *
         */


        // this code is for demonstration...
        else
        {
            NeuralNetworkDBHelper dbHelper = new NeuralNetworkDBHelper(mContext);
            ArrayList<String> l = dbHelper.getNames();
            Toast.makeText(mContext, "Size: " + l.get(100), Toast.LENGTH_SHORT).show();
        }


        /**
         *
         *
         *
         * demonstration code ends above...
         *
         */



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
                setBottomNavCheckableTrue();
                isHomeShowing = true;
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                navigationView.getMenu().getItem(0).setChecked(true);
                fragment = new HomeFragment();
                break;

            case R.id.doctor_mic:
                if (getProfileCompletePref()) {
                    setBottomNavCheckableTrue();
                    checkAllNavFalse();
                    isHomeShowing = false;
                    bottomNavigationView.getMenu().getItem(1).setChecked(true);
                    fragment = new VoicePresFragment();
                }
                break;

            case R.id.doctor_profile:
            case R.id.nav_doctor_profile_icon:
                setBottomNavCheckableTrue();
                isHomeShowing = false;
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                navigationView.getMenu().getItem(1).setChecked(true);
                fragment = new ProfileFragment();
                break;

            case R.id.nav_doctor_history_icon:
                if (getProfileCompletePref()) {
                    isHomeShowing = false;
                    setBottomNavCheckableFalse();
                    navigationView.getMenu().getItem(2).setChecked(true);
                    fragment = new HistoryFragment();
                }
                break;

            case R.id.nav_doctor_settings_icon:
                if (getProfileCompletePref()) {
                    isHomeShowing = false;
                    setBottomNavCheckableFalse();
                    navigationView.getMenu().getItem(3).setChecked(true);
                    fragment = new SettingsFragment();
                }
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

    private void disableOptions()
    {
        for (int i = 2; i < 4; i++)
        {
            navigationView.getMenu().getItem(i).setEnabled(false);
        }


        bottomNavigationView.getMenu().getItem(1).setEnabled(false);

    }


    private void enableOptions()
    {
        for (int i = 2; i < 4; i++)
        {
            navigationView.getMenu().getItem(i).setEnabled(true);
        }


        bottomNavigationView.getMenu().getItem(1).setEnabled(true);
    }


    @Override
    public void onBackPressed() {

        if (!isHomeShowing) {

            Bundle bundle = new Bundle();
            bundle.putString("regNumber", REG_NUMBER);

            setBottomNavCheckableTrue();

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

        setBottomNavCheckableTrue();

        Fragment fragment = new HomeFragment();

        fragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.doctor_nav_host_fragment, fragment);
        ft.commit();

        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        navigationView.getMenu().getItem(0).setChecked(true);

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

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() <= 3)
                {
                    saveProfilePrefs(false);
                    disableOptions();
                    Snackbar.make(v, "All options are disabled till profile completion", Snackbar.LENGTH_SHORT).show();
                }else
                {
                    saveProfilePrefs(true);
                    enableOptions();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private boolean getProfileCompletePref()
    {
        SharedPreferences doctorProfilePref = mContext.getSharedPreferences("doctorProfilePref",MODE_PRIVATE);
        return doctorProfilePref.getBoolean("isProfileComplete", false);
    }

    private void saveProfilePrefs(boolean bool)
    {
        SharedPreferences doctorProfilePref = mContext.getSharedPreferences("doctorProfilePref",MODE_PRIVATE);
        SharedPreferences.Editor editor = doctorProfilePref.edit();
        editor.putBoolean("isProfileComplete", bool);
        editor.commit();
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


                setBottomNavCheckableTrue();
                navigationView.getMenu().getItem(0).setChecked(true);
                bottomNavigationView.getMenu().getItem(0).setChecked(true);

                dialog.dismiss();

            }
        });


    }

    private void checkAllNavFalse()
    {
        navigationView.getMenu().getItem(0).setChecked(false);
        navigationView.getMenu().getItem(1).setChecked(false);
        navigationView.getMenu().getItem(2).setChecked(false);
        navigationView.getMenu().getItem(3).setChecked(false);
    }

    private void setBottomNavCheckableFalse()
    {
        bottomNavigationView.getMenu().getItem(0).setCheckable(false);
        bottomNavigationView.getMenu().getItem(1).setCheckable(false);
        bottomNavigationView.getMenu().getItem(2).setCheckable(false);
    }

    private void setBottomNavCheckableTrue()
    {
        bottomNavigationView.getMenu().getItem(0).setCheckable(true);
        bottomNavigationView.getMenu().getItem(1).setCheckable(true);
        bottomNavigationView.getMenu().getItem(2).setCheckable(true);
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

        setBottomNavCheckableFalse();

        //navigationView.getMenu().getItem(3).setChecked(true);

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




    // check if all arraylist database is saved or not for use in prescriptions...
    private boolean checkInLocalDatabase()
    {

        SharedPreferences localDatabasePrefs = mContext.getSharedPreferences("localDatabasePrefs",MODE_PRIVATE);

        return localDatabasePrefs.getBoolean("savedInLocalDatabase", false);

    }







    // asynchronous class for saving to local database...
    public class NeuralNetworks extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        boolean errorFound;
        String error;

        String nameUrl = "https://parenting.firstcry.com/articles/150-unique-indian-baby-boy-names-with-meaning/";


        String[] boyNameUrls = new String[]
                {
                        // boys names...
                        "https://nriol.com/babynames/indian-boy-baby-names-baa.asp",
                        "https://nriol.com/babynames/indian-boy-baby-names-bbb.asp",
                        "https://nriol.com/babynames/bcc.asp",
                        "https://nriol.com/babynames/indian-boy-baby-names-bdd.asp",
                        "https://nriol.com/babynames/bee.asp",
                        "https://nriol.com/babynames/bff.asp",
                        "https://nriol.com/babynames/bgg.asp",
                        "https://nriol.com/babynames/bhh.asp",
                        "https://nriol.com/babynames/bii.asp",
                        "https://nriol.com/babynames/bjj.asp",
                        "https://nriol.com/babynames/bkk.asp",
                        "https://nriol.com/babynames/bll.asp",
                        "https://nriol.com/babynames/indian-boy-baby-names-bmm.asp",
                        "https://nriol.com/babynames/indian-boy-baby-names-bnn.asp",
                        "https://nriol.com/babynames/boo.asp",
                        "https://nriol.com/babynames/indian-boy-baby-names-bpp.asp",
                        "https://nriol.com/babynames/bqq.asp",
                        "https://nriol.com/babynames/indian-boy-baby-names-brr.asp",
                        "https://nriol.com/babynames/indian-boy-baby-names-bss.asp",
                        "https://nriol.com/babynames/btt.asp",
                        "https://nriol.com/babynames/buu.asp",
                        "https://nriol.com/babynames/indian-boy-baby-names-bvv.asp",
                        "https://nriol.com/babynames/bww.asp",
                        "https://nriol.com/babynames/bxx.asp",
                        "https://nriol.com/babynames/byy.asp",
                        "https://nriol.com/babynames/bzz.asp"
                };


        String[] girlNameUrls = new String[]
                {
                        //girls names...
                        "https://nriol.com/babynames/indian-girl-baby-names-gaa.asp",
                        "https://nriol.com/babynames/gbb.asp",
                        "https://nriol.com/babynames/gcc.asp",
                        "https://nriol.com/babynames/gdd.asp",
                        "https://nriol.com/babynames/gee.asp",
                        "https://nriol.com/babynames/gff.asp",
                        "https://nriol.com/babynames/ggg.asp",
                        "https://nriol.com/babynames/ghh.asp",
                        "https://nriol.com/babynames/gii.asp",
                        "https://nriol.com/babynames/gjj.asp",
                        "https://nriol.com/babynames/indian-girl-baby-names-gkk.asp",
                        "https://nriol.com/babynames/gll.asp",
                        "https://nriol.com/babynames/indian-girl-baby-names-gmm.asp",
                        "https://nriol.com/babynames/gnn.asp",
                        "https://nriol.com/babynames/goo.asp",
                        "https://nriol.com/babynames/indian-girl-baby-names-gpp.asp",

                        //no girl names with q alphabet...
                        "https://nriol.com/babynames/grr.asp",
                        "https://nriol.com/babynames/indian-girl-baby-names-gss.asp",
                        "https://nriol.com/babynames/gtt.asp",
                        "https://nriol.com/babynames/guu.asp",
                        "https://nriol.com/babynames/gvv.asp",
                        "https://nriol.com/babynames/gww.asp",
                        "https://nriol.com/babynames/gxx.asp",
                        "https://nriol.com/babynames/gyy.asp",
                        "https://nriol.com/babynames/gzz.asp"
                };


        String[] surnameUrls = new String[]
                {
                        "https://www.familyeducation.com/baby-names/browse-origin/surname/indian",
                        "https://www.familyeducation.com/baby-names/browse-origin/surname/indian?page=1",
                        "https://www.familyeducation.com/baby-names/browse-origin/surname/indian?page=2",
                        "https://www.familyeducation.com/baby-names/browse-origin/surname/indian?page=3",
                        "https://www.familyeducation.com/baby-names/browse-origin/surname/indian?page=4",
                        "https://www.familyeducation.com/baby-names/browse-origin/surname/indian?page=5"
                };


        ArrayList<String> preNames;
        String boyNameString = "";
        String girlNameString = "";

        String namesString = "";
        String surnamesString = "";

        ArrayList<String> nameList = new ArrayList<>();

        NeuralNetworkDBHelper dbHelper = new NeuralNetworkDBHelper(mContext);

        Elements e1;
        Elements e2;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(mContext);
            dialog.setTitle("Contacting server");
            dialog.setMessage("This is one time process, please wait...");
            dialog.setCancelable(false);
            dialog.show();

        }


        @Override
        protected Void doInBackground(Void... voids) {


            try {

                Document doc = Jsoup.connect(nameUrl).get();
                e1 = doc.select("td[width=141]");
                for (Element e : e1)
                {
                    boyNameString += e.text().toLowerCase() + "\n";
                    nameList.add(e.text().toLowerCase());

                    dbHelper.addName(e.text().toLowerCase());

                }


                // boys name scrapping...
                for (String url : boyNameUrls)
                {

                    doc = Jsoup.connect(url).get();
                    e2 = doc.select("td.colleft");
                    for (Element e : e2)
                    {
                        boyNameString += e.text().toLowerCase() + "\n";
                        nameList.add(e.text().toLowerCase());

                        dbHelper.addName(e.text().toLowerCase());
                    }

                }


                // girls name scrapping...
                for (String url : girlNameUrls)
                {

                    doc = Jsoup.connect(url).get();
                    e2 = doc.select("td.colleft1");
                    for (Element e : e2)
                    {
                        girlNameString += e.text().toLowerCase() + "\n";
                        nameList.add(e.text().toLowerCase());

                        dbHelper.addName(e.text().toLowerCase());
                    }

                }



                // surnames scrapping...
                for (String url : surnameUrls)
                {

                    Document doc2 = Jsoup.connect(url).get();
                    e2 = doc2.select("section.clearfix li a[href]");
                    for (Element e : e2)
                    {
                        surnamesString += e.text().toLowerCase() + "\n";
                        nameList.add(e.text().toLowerCase());

                        dbHelper.addName(e.text().toLowerCase());
                    }

                }


                namesString += boyNameString;
                namesString += girlNameString;

                namesString += surnamesString;


            }
            catch (Exception e) {

                errorFound = true;
                error = e.getMessage();

            }


            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            dialog.dismiss();

            if (!errorFound)
            {
                saveLocalDatabasePref(true);
            }
            else
            {
                saveLocalDatabasePref(false);
            }

        }

    }




    private void saveLocalDatabasePref(boolean bool) {

        SharedPreferences localDatabasePrefs = mContext.getSharedPreferences("localDatabasePrefs",MODE_PRIVATE);
        SharedPreferences.Editor localDatabasePrefsEditor = localDatabasePrefs.edit();
        localDatabasePrefsEditor.putBoolean("savedInLocalDatabase", bool);
        localDatabasePrefsEditor.commit();

    }


}
