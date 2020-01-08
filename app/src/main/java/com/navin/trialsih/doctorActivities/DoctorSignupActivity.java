package com.navin.trialsih.doctorActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navin.trialsih.R;
import com.navin.trialsih.doctorClasses.DoctorDetails;
import com.navin.trialsih.universalCredentials.SecurePassword;

import java.io.File;
import java.util.regex.Pattern;

import static androidx.core.content.FileProvider.getUriForFile;

public class DoctorSignupActivity extends AppCompatActivity {

    private TextInputLayout regNumberLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;

    private TextInputEditText regNumber;
    private TextInputEditText password;
    private TextInputEditText confirmPassword;

    private Button signUpBtn;

    private Context mContext;
    private View v;

    private final static String USER_TYPE = "doctors";
    private final static String PROFILE = "profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_doctor_signup);

        mContext = this;
        v = getWindow().getDecorView().getRootView();

        regNumberLayout = findViewById(R.id.doctor_reg_number_layout);
        passwordLayout = findViewById(R.id.doctor_pass_layout);
        confirmPasswordLayout = findViewById(R.id.doctor_confirm_pass_layout);

        regNumber = findViewById(R.id.doctor_reg_number);
        password = findViewById(R.id.doctor_pass);
        confirmPassword = findViewById(R.id.doctor_confirm_pass);

        signUpBtn = findViewById(R.id.doctor_sign_up_btn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                {
                    // if everything is fine, upload data to database...
                    showTypeOfDoctorDialog();
                }
            }
        });

    }


    private boolean validate()
    {

        String reg = regNumber.getText().toString().trim();
        reg = reg.replaceAll(" ", "");

        String pass = password.getText().toString().trim();
        pass = pass.replaceAll(" ", "");

        String cnfrmPass = confirmPassword.getText().toString().trim();
        cnfrmPass = cnfrmPass.replaceAll(" ", "");

        if (reg.isEmpty())
        {
            regNumberLayout.setError("Registration number is required");
            regNumberLayout.requestFocus();
            return false;
        }
        else
        {
            regNumberLayout.setErrorEnabled(false);
        }

        if (pass.isEmpty())
        {
            passwordLayout.setError("Password is required");
            passwordLayout.requestFocus();
            return false;
        }
        else
        {
            passwordLayout.setErrorEnabled(false);
        }

        if ((password.getText().toString().length() < 6) || (password.getText().toString().length() > 16))
        {
            passwordLayout.setError("Password should be 6 to 16 characters long");
            passwordLayout.requestFocus();
            return false;
        }
        else
        {
            passwordLayout.setErrorEnabled(false);
        }

        if (cnfrmPass.isEmpty())
        {
            confirmPasswordLayout.setError("Enter password again");
            confirmPasswordLayout.requestFocus();
            return false;
        }
        else
        {
            confirmPasswordLayout.setErrorEnabled(false);
        }

        if ((confirmPassword.getText().toString().length() < 6) || (confirmPassword.getText().toString().length() > 16))
        {
            confirmPasswordLayout.setError("Password should be 6 to 16 characters long");
            confirmPasswordLayout.requestFocus();
            return false;
        }
        else
        {
            confirmPasswordLayout.setErrorEnabled(false);
        }

        if (!password.getText().toString().equals(confirmPassword.getText().toString()))
        {
            confirmPasswordLayout.setError("Both passwords should be same");
            confirmPasswordLayout.requestFocus();
            return false;
        }
        else
        {
            confirmPasswordLayout.setErrorEnabled(false);
        }

        return true;
    }


    private void showTypeOfDoctorDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Choose which type of doctor you are");
        builder.setCancelable(false);

        String[] doctorTypes = new String[] {"General Practitioner", "Family Physician", "Internal Medicine Physician", "Pediatrician", "Obstetrician/Gynecologist (OB/GYN)", "Surgeon", "Psychiatrist", "Cardiologist", "Dermatologist", "Endocrinologist", "Infectious Disease Physician", "Nephrologist", "Ophthalmologist", "Otolaryngologist", "Pulmonologist", "Neurologist", "Physician Executive", "Radiologist", "Anesthesiologist", "Oncologist", "Gastroenterologist", "Orthopedist"};



        builder.setSingleChoiceItems(doctorTypes, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                showConfirmTypeOfDoctorDialog(doctorTypes[which]);

                dialog.cancel();

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                showConfirmTypeOfDoctorDialog(doctorTypes[0]);

                dialog.cancel();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Snackbar.make(v, "Couldn't register. Try again", Snackbar.LENGTH_LONG).show();

                dialog.cancel();
            }
        });

        builder.show();


    }


    private void showConfirmTypeOfDoctorDialog(String doctorType)
    {
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_confirm_doctor_type, null);
        final Button declineBtn = alertLayout.findViewById(R.id.btn_decline);
        final Button confirmBtn = alertLayout.findViewById(R.id.btn_confirm);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);
        final TextView doctorMsg = alertLayout.findViewById(R.id.doctor_type_text);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);
        doctorMsg.setText("You won't be able to change your type after registration. You have selected: " + doctorType);

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadToFirebase(doctorType);


                dialog.dismiss();
            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, "Registration revoked by the user", Toast.LENGTH_LONG).show();

                dialog.dismiss();

            }
        });
    }


    private void uploadToFirebase(String doctorType)
    {

        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        //check if already registerd...
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE).child(regNumber.getText().toString());

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    Snackbar.make(v, "Already registered, please login", Snackbar.LENGTH_SHORT).show();

                    regNumber.setText("");
                    password.setText("");
                    confirmPassword.setText("");
                    
                    signUpBtn.setEnabled(false);
                    signUpBtn.setClickable(false);
                    signUpBtn.setFocusable(false);

                    signUpBtn.setVisibility(View.INVISIBLE);

                    dialog.cancel();
                }
                else
                {


                    //not registered... Register here...
                    final DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE).child(regNumber.getText().toString()).child(PROFILE);

                    dRef.child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            DoctorDetails doctorDetails = new DoctorDetails();
                            doctorDetails.setDoctorRegNumber(regNumber.getText().toString());

                            dRef.setValue(doctorDetails)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                            dRef.child("doctorType").setValue(doctorType);
                                            dRef.child("password").setValue(getEncryptedPassword());

                                            Snackbar.make(v, "Successfully registered, you may login now", Snackbar.LENGTH_SHORT).show();

                                            regNumber.setText("");
                                            password.setText("");
                                            confirmPassword.setText("");

                                            signUpBtn.setEnabled(false);
                                            signUpBtn.setClickable(false);
                                            signUpBtn.setFocusable(false);

                                            signUpBtn.setVisibility(View.INVISIBLE);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Snackbar.make(v, "Something went wrong", Snackbar.LENGTH_SHORT).show();

                                        }
                                    });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                dialog.cancel();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Snackbar.make(v, "Database error: " + databaseError.getMessage(), Snackbar.LENGTH_SHORT).show();

                dialog.cancel();

            }
        });
    }

    private String getEncryptedPassword()
    {
        SecurePassword securePassword = new SecurePassword();

        String encryptedPass = "";

        try {
            encryptedPass = securePassword.encrypt(password.getText().toString());
        }catch (Exception e)
        {
            Toast.makeText(mContext, "Encryption went wrong", Toast.LENGTH_SHORT).show();
        }

        return encryptedPass;

    }

}
