package com.pranks.trialsih.patientActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranks.trialsih.R;
import com.pranks.trialsih.SignInActivity;
import com.pranks.trialsih.patientActivities.bottomNavigation.ChatFragment;
import com.pranks.trialsih.patientActivities.ui.activeAppoint.HomeFragment;
import com.pranks.trialsih.patientActivities.ui.prevAppoint.PrevAppointmentsFragment;
import com.pranks.trialsih.patientActivities.ui.prevTransactions.PrevTransactionsFragment;
import com.pranks.trialsih.patientActivities.ui.userProfile.ProfileFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class PatientDashboardActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView bottomNavigationView;

    private final static String USER_TYPE_PATIENT = "patients";
    private final static String PROFILE = "profile";
    private static String UID;

    private ImageView profilePic;
    private TextView name;
    private TextView email;

    private Context mContext;
    private View v;

    private boolean isHomeShowing = false;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    private Toolbar toolbar;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mContext = this;
        v = getWindow().getDecorView().getRootView();

        isHomeShowing = true;

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


        bottomNavigationView = findViewById(R.id.patient_bottom_navigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                displaySelectedScreen(menuItem.getItemId());

                return true;
            }
        });


        profilePic = findViewById(R.id.patient_profile_pic);
        name = findViewById(R.id.patient_name);
        email = findViewById(R.id.patient_email);

        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        checkProfileCompletion(UID);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                displaySelectedScreen(menuItem.getItemId());

                return true;
            }
        });

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

    private void showLogoutDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Sure to logout?");
        builder.setCancelable(false);

        builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut();

                Toast.makeText(mContext, "Successfully signed out", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(mContext, SignInActivity.class));
                finish();

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


    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.patient_appointments:
            case R.id.nav_patient_appointment:
                isHomeShowing = true;
                setBottomNavCheckableTrue();
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                navigationView.getMenu().getItem(0).setChecked(true);
                fragment = new HomeFragment();
                break;

            case R.id.nav_patient_profile:
                isHomeShowing = false;
                setBottomNavCheckableFalse();
                checkAllBottomNavFalse();
                navigationView.getMenu().getItem(1).setChecked(true);
                fragment = new ProfileFragment();
                break;

            case R.id.nav_patient_previous_appointment:
                isHomeShowing = false;
                setBottomNavCheckableFalse();
                checkAllBottomNavFalse();
                navigationView.getMenu().getItem(2).setChecked(true);
                fragment = new PrevAppointmentsFragment();
                break;

            case R.id.nav_patient_previous_transactions:
                isHomeShowing = false;
                setBottomNavCheckableFalse();
                checkAllBottomNavFalse();
                navigationView.getMenu().getItem(3).setChecked(true);
                fragment = new PrevTransactionsFragment();
                break;

            case R.id.patient_chat:
                isHomeShowing = false;
                setBottomNavCheckableTrue();
                checkAllNavFalse();
                fragment = new ChatFragment();
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                break;

            case R.id.nav_patient_logout:
                showLogoutDialog();
                break;

            case R.id.nav_patient_feedback:
                feedbackClicked();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void setBottomNavCheckableFalse()
    {
        bottomNavigationView.getMenu().getItem(0).setCheckable(false);
        bottomNavigationView.getMenu().getItem(1).setCheckable(false);
    }

    private void setBottomNavCheckableTrue()
    {
        bottomNavigationView.getMenu().getItem(0).setCheckable(true);
        bottomNavigationView.getMenu().getItem(1).setCheckable(true);
    }

    private void checkAllBottomNavFalse()
    {
        bottomNavigationView.getMenu().getItem(0).setChecked(false);
        bottomNavigationView.getMenu().getItem(1).setChecked(false);
    }


    private void checkAllNavFalse()
    {
        navigationView.getMenu().getItem(0).setChecked(false);
        navigationView.getMenu().getItem(1).setChecked(false);
        navigationView.getMenu().getItem(2).setChecked(false);
        navigationView.getMenu().getItem(3).setChecked(false);
    }


    @Override
    public void onBackPressed() {

        if (!isHomeShowing) {

            setBottomNavCheckableTrue();

            Fragment fragment = new HomeFragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, fragment);
            ft.commit();

            navigationView.getMenu().getItem(0).setChecked(true);
            bottomNavigationView.getMenu().getItem(0).setChecked(true);

            isHomeShowing = true;
        }
        else
        {
            super.onBackPressed();
        }

    }



    private void feedbackClicked()
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {getResources().getString(R.string.feedback_mail_ID)});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Your feedback subject");
        intent.putExtra(Intent.EXTRA_TEXT, "Your feedback here...");

        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
        {
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
            {
                best = info;
            }
        }
        if (best != null)
        {
            intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
            startActivity(intent);
        }
        else
        {
            Snackbar.make(v, "Couldn't find Gmail on your device", Snackbar.LENGTH_LONG).show();
        }
    }


    private void checkProfileCompletion(String UID)
    {

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_PATIENT).child(UID).child(PROFILE);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists())
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

    private void saveProfilePrefs(boolean bool)
    {
        SharedPreferences patientProfilePref = mContext.getSharedPreferences("patientProfilePref",MODE_PRIVATE);
        SharedPreferences.Editor editor = patientProfilePref.edit();
        editor.putBoolean("isPatientProfileComplete", bool);
        editor.commit();
    }


}
