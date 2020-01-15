package com.pranks.trialsih.doctorActivities.navigationDrawer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranks.trialsih.R;
import com.pranks.trialsih.doctorActivities.DoctorDashboardActivity;
import com.pranks.trialsih.doctorActivities.bottomNavigation.JavaApiDemo;
import com.pranks.trialsih.doctorDBHelpers.DoctorCredentialsDBHelper;
import com.pranks.trialsih.universalCredentials.SecurePassword;

import java.util.regex.Pattern;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;
    private Button changePassBtn;

    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String PASSWORD = "password";
    private final static String DOCTOR_MAIL = "doctorMail";
    private final static String OTP = "otps";
    private final static String PROFILE = "profile";


    private String REG_NUMBER;

    private View v;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        v = inflater.inflate(R.layout.doctor_fragment_settings, container, false);

        ((DoctorDashboardActivity) getActivity()).getSupportActionBar().setTitle("Settings");


        try {

            Bundle bundle = getArguments();
            REG_NUMBER = bundle.getString("regNumber");
        }
        catch (Exception e) {

            try {

                Bundle bundle = getArguments();
                REG_NUMBER = bundle.getString("regNumber");

            }
            catch (Exception ex) {
                REG_NUMBER = getRegNumber();
            }
        }


        changePassBtn = v.findViewById(R.id.change_password);

        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEmailAndSendOTP();
            }
        });

        return v;
    }

    private void getEmailAndSendOTP()
    {

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(PROFILE);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 3)
                {

                    String mail = dataSnapshot.child(DOCTOR_MAIL).getValue().toString();


                    /**
                     *
                     *
                     * send OTP as well as upload to OTPs section or node in Firebase database...
                     *
                     *
                     * And after sending, we're asking the user to enter the sent OTP for validation...
                     *
                     *
                     * Sending otp takes some time, so show a Toast for please wait...
                     *
                     *
                     */


                    sendOTPAndUploadToFirebase(mail);
                    askForOTP();


                    Toast.makeText(getContext(), "Please wait...", Toast.LENGTH_SHORT).show();


                }
                else
                {
                    Snackbar.make(v, "Complete your profile first", Snackbar.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    private void sendOTPAndUploadToFirebase(String EMAIL) {

        String generatedOTP = String.valueOf((int)Math.floor(Math.random() * (1000000 - 100000) + 100000));

        Context context = getContext();
        String subject = "Password reset for Registration Number: " + REG_NUMBER;
        String message = "Your OTP for password reset is:\n" + generatedOTP;
        String filePath = "";

        uploadOTPToFirebase(generatedOTP);

        JavaApiDemo javaApiDemo = new JavaApiDemo(context, EMAIL, subject, message, filePath);
        javaApiDemo.execute();


    }

    private void uploadOTPToFirebase(String generatedOTP) {

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(OTP);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mRef.child(REG_NUMBER).setValue(generatedOTP)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                // OTP uploaded to database...

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Snackbar.make(v, "Something went wrong", Snackbar.LENGTH_LONG).show();

                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void askForOTP()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_credentials, null);
        final Button cancelBtn = alertLayout.findViewById(R.id.btn_cancel);
        final Button okBtn = alertLayout.findViewById(R.id.btn_ok);
        final TextView titleText = alertLayout.findViewById(R.id.title);
        final TextView messageText = alertLayout.findViewById(R.id.message);
        final EditText input = alertLayout.findViewById(R.id.editText);

        okBtn.setText("VALIDATE");

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        titleText.setText("Verify your OTP?");
        messageText.setText("Enter the OTP you received on your registered mail-ID");

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dEst, int dStart, int dEnd) {
                for (int i = start; i < end; ++i)
                {
                    if (!Pattern.compile("[1234567890]*").matcher(String.valueOf(source.charAt(i))).matches())
                    {
                        return "";
                    }
                }

                return null;
            }
        };

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6)});

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String testName = input.getText().toString();

                if (!(testName.length() > 0))
                {
                    Toast.makeText(getContext(), "Please enter the OTP sent to your registered mail", Toast.LENGTH_LONG).show();
                }
                else {

                    validateOTP(input.getText().toString(), dialog);
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Cancelled by the user", Toast.LENGTH_SHORT).show();

                final DatabaseReference oRef = FirebaseDatabase.getInstance().getReference().child(OTP);

                oRef.child(REG_NUMBER).setValue(null);

                dialog.dismiss();

            }
        });
    }

    private void validateOTP(String otp, AlertDialog dialog)
    {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(OTP);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String firebaseOtp = "";

                try {
                    firebaseOtp = dataSnapshot.child(REG_NUMBER).getValue().toString();
                }
                catch (Exception e)
                {
                    Toast.makeText(getContext(), "Error while sending OTP, try again", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

                if (firebaseOtp.equals(otp))
                {
                    //Toast.makeText(getContext(), "Validation success", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                    progressDialog.dismiss();

                    askForNewPassword();

                }
                else
                {
                    Toast.makeText(getContext(), "Invalid OTP, try again", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                progressDialog.dismiss();

            }
        });

    }


    private void askForNewPassword()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_pdf_password, null);
        final Button okBtn = alertLayout.findViewById(R.id.btn_ok);
        final TextView titleText = alertLayout.findViewById(R.id.title_text);
        final TextView messageText = alertLayout.findViewById(R.id.message_text);
        final EditText pass = alertLayout.findViewById(R.id.editText_password);
        final EditText confirmPass = alertLayout.findViewById(R.id.editText_confirm_password);


        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        titleText.setText("New Password?");
        messageText.setText("Enter your new password");

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dEst, int dStart, int dEnd) {
                for (int i = start; i < end; ++i)
                {
                    if (!Pattern.compile("[1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz\\@\\!\\#\\$\\%\\^\\&\\*\\(\\)\\_]*").matcher(String.valueOf(source.charAt(i))).matches())
                    {
                        return "";
                    }
                }

                return null;
            }
        };

        pass.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(16)});
        confirmPass.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(16)});

        pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String p = pass.getText().toString();
                String cp = confirmPass.getText().toString();

                if (validatePass(p, cp))
                {

                    uploadNewPasswordAndDeleteOTP(p);

                    dialog.dismiss();

                }
            }
        });

    }

    private void uploadNewPasswordAndDeleteOTP(String password)
    {

        final DatabaseReference pRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(PROFILE);

        String encryptedPass;
        try {
            SecurePassword securePassword = new SecurePassword();
            encryptedPass = securePassword.encrypt(password);
        }
        catch (Exception e) {encryptedPass = password;}

        pRef.child(PASSWORD).setValue(encryptedPass)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Snackbar.make(v, "Password updated successfully", Snackbar.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Snackbar.make(v, "Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show();

                    }
                });


        final DatabaseReference oRef = FirebaseDatabase.getInstance().getReference().child(OTP);

        oRef.child(REG_NUMBER).setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // successfully deleted from the database...

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // not deleted from the database...

                    }
                });


    }


    private boolean validatePass(String password, String confirmPassword)
    {

        if (password.isEmpty())
        {
            Toast.makeText(getContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6 || password.length() > 16)
        {
            Toast.makeText(getContext(), "Password should be 6 to 16 characters long", Toast.LENGTH_LONG).show();
            return false;
        }
        if (confirmPassword.isEmpty())
        {
            Toast.makeText(getContext(), "Enter the same password again", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (confirmPassword.length() < 6 || confirmPassword.length() > 16)
        {
            Toast.makeText(getContext(), "Password should be 6 to 16 characters long", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!password.equals(confirmPassword))
        {
            Toast.makeText(getContext(), "Both passwords should be same", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }


    private String getRegNumber()
    {
        String reg = null;

        DoctorCredentialsDBHelper dbHelper = new DoctorCredentialsDBHelper(getContext());

        reg = dbHelper.getRegNumber();

        return reg;

    }

}