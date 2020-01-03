package com.navin.trialsih.doctorActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navin.trialsih.R;
import com.navin.trialsih.universalCredentials.SecurePassword;

public class DoctorSignInActivity extends AppCompatActivity {

    private TextInputLayout regNumberLayout;
    private TextInputLayout passwordLayout;

    private TextInputEditText regNumber;
    private TextInputEditText password;

    private Button signInBtn;

    private Context mContext;
    private View v;

    private final static String USER_TYPE = "doctors";
    private final static String PROFILE = "profile";
    private static String REG_NUMBER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_doctor_signin);

        mContext = this;
        v = getWindow().getDecorView().getRootView();

        regNumberLayout = findViewById(R.id.doctor_reg_number_layout);
        passwordLayout = findViewById(R.id.doctor_pass_layout);

        regNumber = findViewById(R.id.doctor_reg_number);
        password = findViewById(R.id.doctor_pass);

        signInBtn = findViewById(R.id.doctor_sign_in_btn);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    //everything is right... do sign in...
                    doLogIn();

                }
            }
        });


    }


    private boolean validate() {

        String reg = regNumber.getText().toString().trim();
        reg = reg.replaceAll(" ", "");

        String pass = password.getText().toString().trim();
        pass = pass.replaceAll(" ", "");

        if (reg.isEmpty()) {
            regNumberLayout.setError("Registration number is required");
            regNumberLayout.requestFocus();
            return false;
        } else {

            REG_NUMBER = regNumber.getText().toString();

            regNumberLayout.setErrorEnabled(false);
        }

        if (pass.isEmpty()) {
            passwordLayout.setError("Password is required");
            passwordLayout.requestFocus();
            return false;
        } else {
            passwordLayout.setErrorEnabled(false);
        }

        if ((password.getText().toString().length() < 6) || (password.getText().toString().length() > 16)) {
            passwordLayout.setError("Password should be 6 to 16 characters long");
            passwordLayout.requestFocus();
            return false;
        } else {
            passwordLayout.setErrorEnabled(false);
        }


        return true;
    }


    private void doLogIn()
    {

        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE).child(REG_NUMBER).child(PROFILE);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists())
                {
                    Snackbar.make(v, "Not registered yet, sign up first", Snackbar.LENGTH_SHORT).show();
                }
                else
                {

                    String REG_NUMBER;
                    String PASSWORD;

                    REG_NUMBER = dataSnapshot.child("doctorRegNumber").getValue().toString();
                    PASSWORD = dataSnapshot.child("password").getValue().toString();

                    String DECRYPTED_PASSWORD = "";

                    SecurePassword securePassword = new SecurePassword();
                    try {
                        DECRYPTED_PASSWORD = securePassword.decrypt(PASSWORD);
                    }
                    catch (Exception e)
                    {
                        Snackbar.make(v, "Decryption went wrong", Snackbar.LENGTH_SHORT).show();
                    }

                    if (REG_NUMBER.equals(regNumber.getText().toString().trim()))
                    {
                        if (DECRYPTED_PASSWORD.equals(password.getText().toString()))
                        {
                            startDoctorProfileAndSavePreferences();
                        }
                        else
                        {
                            Snackbar.make(v, "Wrong password, try again", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Snackbar.make(v, "Invalid registration number", Snackbar.LENGTH_SHORT).show();
                    }

                }

                dialog.cancel();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                dialog.cancel();

            }
        });

    }


    private void startDoctorProfileAndSavePreferences()
    {

        SharedPreferences doctorSignInPref = mContext.getSharedPreferences("doctorSingInPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = doctorSignInPref.edit();
        editor.putBoolean("isDoctorSignedIn", true);
        editor.commit();

        SharedPreferences doctorRegNumberPref = mContext.getSharedPreferences("doctorRegNumberPref", MODE_PRIVATE);
        SharedPreferences.Editor regEditor = doctorRegNumberPref.edit();
        regEditor.putString("regNumber", REG_NUMBER);
        regEditor.commit();

        Bundle bundle = new Bundle();
        bundle.putString("regNumber", REG_NUMBER);

        Intent intent = new Intent(mContext, DoctorDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        intent.putExtras(bundle);

        startActivity(intent);
        finish();

    }


}
