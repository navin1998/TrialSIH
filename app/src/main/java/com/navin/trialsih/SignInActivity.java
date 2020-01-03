package com.navin.trialsih;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.navin.trialsih.doctorActivities.DoctorDashboardActivity;
import com.navin.trialsih.doctorActivities.DoctorSignInActivity;
import com.navin.trialsih.doctorActivities.DoctorSignupActivity;
import com.navin.trialsih.patientActivities.PatientDashboardActivity;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    private Button signInButton;
    private Button signInDoctor;
    private Button signUpDoctor;
    private FirebaseAuth firebaseAuth;

    private Context mContext;
    private View v;

    private final static int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;

    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String USER_TYPE_PATIENT = "patients";
    private final static String ACTIVE_APPOINTMENTS = "appointments";
    private final static String PROFILE = "profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_signin);

        mContext = this;
        v = getWindow().getDecorView().getRootView();

        firebaseAuth = FirebaseAuth.getInstance();

        signInButton = (Button) findViewById(R.id.sign_in_button);
        signInDoctor = (Button) findViewById(R.id.signin_doctor);
        signUpDoctor = (Button) findViewById(R.id.signup_doctor);

        signInButton.setOnClickListener(this);
        signInDoctor.setOnClickListener(this);
        signUpDoctor.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(SignInActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(mContext, "Signed in as: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(mContext, PatientDashboardActivity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.sign_in_button:
                signIn();
                break;

            case R.id.signin_doctor:
                startDoctorSignin();
                break;

            case R.id.signup_doctor:
                startDoctorSignup();
                break;
        }
    }

    private void startDoctorSignup()
    {
        startActivity(new Intent(mContext, DoctorSignupActivity.class));
    }

    private void startDoctorSignin()
    {
        startActivity(new Intent(mContext, DoctorSignInActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
        {
            startActivity(new Intent(mContext, PatientDashboardActivity.class));
            finish();
        }
        else if (startDoctorProfileAndSavePreferences())
        {
            startActivity(new Intent(mContext, DoctorDashboardActivity.class));
            finish();
        }

    }

    private boolean startDoctorProfileAndSavePreferences()
    {

        SharedPreferences doctorSignInPref = mContext.getSharedPreferences("doctorSingInPref",MODE_PRIVATE);
        boolean isSignedIn = doctorSignInPref.getBoolean("isDoctorSignedIn", false);

        return isSignedIn;

    }

}