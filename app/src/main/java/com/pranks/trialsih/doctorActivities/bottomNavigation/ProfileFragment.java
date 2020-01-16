package com.pranks.trialsih.doctorActivities.bottomNavigation;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pranks.trialsih.R;
import com.pranks.trialsih.doctorActivities.DoctorDashboardActivity;
import com.pranks.trialsih.doctorClasses.DoctorDetails;
import com.pranks.trialsih.doctorDBHelpers.DoctorCredentialsDBHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private static String REG_NUMBER;
    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String ACTIVE_APPOINTMENTS = "appointments";
    private final static String PROFILE = "profile";
    private final static String CLINIC_NAME_CHANGED_DATE = "clinicNameChangedDate";
    private static String PASSWORD;
    private static String DOCTOR_TYPE;
    private final static String url = "https://time.is/Unix_time_now";
    private static boolean forComparison = false;
    private static boolean forSave = false;
    private final static int MINIMUM_DAYS_FOR_NEXT_CLINIC_NAME_CHANGE = 7;

    private Context activityContext;

    private Uri imagePath;
    private String downloadUrl;

    private final int PICK_IMAGE_REQUEST = 71;

    private ArrayList<DoctorDetails> list;

    private ProfileViewModel profileViewModel;
    private View v;

    private TextView nameTop;
    private TextView regNumberTop;
    private TextView name;
    private TextView regNumber;
    private TextView gender;
    private TextView phone;
    private TextView bookingPhone;
    private TextView yearOfExperience;
    private TextView yearOfReg;
    private TextView lastDegree;
    private TextView clinicName;
    private TextView address;
    private TextView medicalCouncil;
    private TextView mail;
    private TextView satisfiedPatients;
    private TextView upiName;
    private TextView upiID;
    private TextView appointmentFee;


    private LinearLayout nameLayout;
    private LinearLayout regNumberLayout;
    private LinearLayout genderLayout;
    private LinearLayout phoneLayout;
    private LinearLayout bookingPhoneLayout;
    private LinearLayout yearOfExperienceLayout;
    private LinearLayout yearOfRegLayout;
    private LinearLayout lastDegreeLayout;
    private LinearLayout clinicNameLayout;
    private LinearLayout addressLayout;
    private LinearLayout medicalCouncilLayout;
    private LinearLayout mailLayout;
    private LinearLayout satisfiedPatientsLayout;
    private LinearLayout upiNameLayout;
    private LinearLayout upiIDLayout;
    private LinearLayout appointmentFeeLayout;


    private Button editBtn;
    private ImageView doctorPic;

    private boolean onEdit = false;

    private LinearLayout[] layoutArr;


    private DoctorDetails doctorUploadDetails;

    private Calendar calendar;

    private boolean isImagePresent = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        v = inflater.inflate(R.layout.doctor_fragment_profile, container, false);

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

        activityContext = getContext();


        ((DoctorDashboardActivity) getActivity()).getSupportActionBar().setTitle("Profile");

        onEdit = false;

        list = new ArrayList<>();
        calendar = Calendar.getInstance();

        nameTop = v.findViewById(R.id.doctor_name_top);
        regNumberTop = v.findViewById(R.id.doctor_reg_number_top);
        name = v.findViewById(R.id.doctor_name);
        regNumber = v.findViewById(R.id.doctor_reg_number);
        gender = v.findViewById(R.id.doctor_gender);
        phone = v.findViewById(R.id.doctor_phone);
        bookingPhone = v.findViewById(R.id.doctor_booking_phone);
        yearOfExperience = v.findViewById(R.id.doctor_experience);
        yearOfReg = v.findViewById(R.id.doctor_year_of_registration);
        lastDegree = v.findViewById(R.id.doctor_qualification);
        clinicName = v.findViewById(R.id.doctor_clinic_name);
        address = v.findViewById(R.id.doctor_address);
        medicalCouncil = v.findViewById(R.id.doctor_council);
        mail = v.findViewById(R.id.doctor_mail);
        satisfiedPatients = v.findViewById(R.id.doctor_satisfied_patients);
        upiName = v.findViewById(R.id.doctor_upi_name);
        upiID = v.findViewById(R.id.doctor_upi_id);
        appointmentFee = v.findViewById(R.id.doctor_fee);


        nameLayout = v.findViewById(R.id.doctor_name_layout);
        regNumberLayout = v.findViewById(R.id.doctor_reg_number_layout);
        genderLayout = v.findViewById(R.id.doctor_gender_layout);
        phoneLayout = v.findViewById(R.id.doctor_phone_layout);
        bookingPhoneLayout = v.findViewById(R.id.doctor_booking_phone_layout);
        yearOfExperienceLayout = v.findViewById(R.id.doctor_experience_layout);
        yearOfRegLayout = v.findViewById(R.id.doctor_year_of_registration_layout);
        lastDegreeLayout = v.findViewById(R.id.doctor_qualification_layout);
        clinicNameLayout = v.findViewById(R.id.doctor_clinic_name_layout);
        addressLayout = v.findViewById(R.id.doctor_address_layout);
        medicalCouncilLayout = v.findViewById(R.id.doctor_council_layout);
        mailLayout = v.findViewById(R.id.doctor_mail_layout);
        satisfiedPatientsLayout = v.findViewById(R.id.doctor_satisfied_patients_layout);
        upiNameLayout = v.findViewById(R.id.doctor_upi_name_layout);
        upiIDLayout = v.findViewById(R.id.doctor_upi_id_layout);
        appointmentFeeLayout = v.findViewById(R.id.doctor_fee_layout);


        editBtn = v.findViewById(R.id.doctor_profile_edit_btn);
        doctorPic = v.findViewById(R.id.doctor_pic);

        getPassword();
        getDoctorType();

        loadProfile();


        layoutArr = new LinearLayout[]{nameLayout, regNumberLayout, genderLayout, phoneLayout, bookingPhoneLayout, yearOfExperienceLayout, yearOfRegLayout, lastDegreeLayout, clinicNameLayout, addressLayout, medicalCouncilLayout, mailLayout, satisfiedPatientsLayout, upiNameLayout, upiIDLayout, appointmentFeeLayout};

        for (LinearLayout ll : layoutArr)
        {
            ll.setOnClickListener(this);
        }

        doctorPic.setOnClickListener(this);
        editBtn.setOnClickListener(this);

        regNumber.setText(REG_NUMBER);

        return v;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {

            case R.id.doctor_name_layout:
            case R.id.doctor_reg_number_layout:
            case R.id.doctor_gender_layout:
            case R.id.doctor_phone_layout:
            case R.id.doctor_booking_phone_layout:
            case R.id.doctor_experience_layout:
            case R.id.doctor_year_of_registration_layout:
            case R.id.doctor_qualification_layout:
            case R.id.doctor_clinic_name_layout:
            case R.id.doctor_address_layout:
            case R.id.doctor_council_layout:
            case R.id.doctor_mail_layout:
            case R.id.doctor_satisfied_patients_layout:
            case R.id.doctor_pic:
            case R.id.doctor_upi_name_layout:
            case R.id.doctor_upi_id_layout:
            case R.id.doctor_fee_layout:
                if (!onEdit)
                {
                    Snackbar.make(v, "Turn on edit mode by pressing button below", Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    switch (v.getId())
                    {
                        case R.id.doctor_name_layout:
                            askForName();
                            break;

                        case R.id.doctor_reg_number_layout:
                            //askForRegNumber();
                            Snackbar.make(v, "Registration number is non-editable", Snackbar.LENGTH_SHORT).show();
                            break;

                        case R.id.doctor_gender_layout:
                            askForGender();
                            break;

                        case R.id.doctor_phone_layout:
                            askForPhone();
                            break;

                        case R.id.doctor_booking_phone_layout:
                            askForBookingPhone();
                            break;

                        case R.id.doctor_experience_layout:
                            askForExperience();
                            break;

                        case R.id.doctor_year_of_registration_layout:
                            askForRegistrationYear();
                            break;

                        case R.id.doctor_qualification_layout:
                            askForLastDegree();
                            break;

                        case R.id.doctor_clinic_name_layout:
                            checkClinicName();
                            break;

                        case R.id.doctor_address_layout:
                            askForAddress();
                            break;

                        case R.id.doctor_council_layout:
                            askForMedicalCouncil();
                            break;

                        case R.id.doctor_mail_layout:
                            askForMail();
                            break;

                        case R.id.doctor_pic:
                            chooseImage();
                            break;

                        case R.id.doctor_satisfied_patients_layout:
                            Snackbar.make(v, "This field is non-editable", Snackbar.LENGTH_SHORT).show();
                            break;

                        case R.id.doctor_upi_name_layout:
                            askForUpiName();
                            break;

                        case R.id.doctor_upi_id_layout:
                            askForUpiID();
                            break;

                        case R.id.doctor_fee_layout:
                            askForFee();
                            break;

                    }
                }
                break;

            case R.id.doctor_profile_edit_btn:
                if (onEdit)
                {
                    if (validate())
                    {
                        onEdit = false;
                        editBtn.setText("EDIT");


                        uploadImage();
                        //  it will further call upload to firebase method...
                    }



                }
                else
                {

                    doctorUploadDetails = new DoctorDetails(name.getText().toString(), regNumber.getText().toString(), gender.getText().toString(), phone.getText().toString(), bookingPhone.getText().toString(), yearOfExperience.getText().toString(), lastDegree.getText().toString(), clinicName.getText().toString(), address.getText().toString(), satisfiedPatients.getText().toString(), medicalCouncil.getText().toString(), yearOfReg.getText().toString(), null, mail.getText().toString(), upiName.getText().toString(), upiID.getText().toString(), appointmentFee.getText().toString());

                    onEdit = true;
                    editBtn.setText("SAVE");

                }
                break;


        }

    }

    private void checkClinicName()
    {

        forComparison = true;
        forSave = false;
        new getTimeAndDate().execute();


        // if date comparison permits... the above method will ask for clinic name...


    }


    private void askForName()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_credentials, null);
        final Button cancelBtn = alertLayout.findViewById(R.id.btn_cancel);
        final Button okBtn = alertLayout.findViewById(R.id.btn_ok);
        final TextView titleText = alertLayout.findViewById(R.id.title);
        final TextView messageText = alertLayout.findViewById(R.id.message);
        final EditText input = alertLayout.findViewById(R.id.editText);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        titleText.setText("What is your name?");
        messageText.setText("Enter your name below");

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dEst, int dStart, int dEnd) {
                for (int i = start; i < end; ++i)
                {
                    if (!Pattern.compile("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ]*").matcher(String.valueOf(source.charAt(i))).matches())
                    {
                        return "";
                    }
                }

                return null;
            }
        };

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(50)});

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        input.setText(doctorUploadDetails.getDoctorName().toUpperCase());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String testName = input.getText().toString().trim();
                testName = testName.replaceAll(" ", "");

                if (!(testName.length() > 0))
                {
                    nameTop.setText(null);
                    name.setText(null);
                    Toast.makeText(getContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                }
                else {

                    doctorUploadDetails.setDoctorName(input.getText().toString().toUpperCase());
                    nameTop.setText(input.getText().toString().toUpperCase());
                    name.setText(input.getText().toString().toUpperCase());
                }

                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
    }



    private void askForGender()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select your gender");

        builder.setCancelable(false);

        final String[] genderArr = new String[]{"Male", "Female", "Others"};

        String selectedGender = gender.getText().toString().toLowerCase();

        int index = 0;
        for (int i = 0; i < genderArr.length; i++)
        {
            if (genderArr[i].toLowerCase().equals(selectedGender))
            {
                index = i;
                break;
            }
        }

        builder.setSingleChoiceItems(genderArr, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                gender.setText(genderArr[which]);
                doctorUploadDetails.setDoctorGender(genderArr[which]);

                dialog.cancel();

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                gender.setText(genderArr[0]);
                doctorUploadDetails.setDoctorGender(genderArr[0]);

                dialog.cancel();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void askForPhone()
    {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_credentials, null);
        final Button cancelBtn = alertLayout.findViewById(R.id.btn_cancel);
        final Button okBtn = alertLayout.findViewById(R.id.btn_ok);
        final TextView titleText = alertLayout.findViewById(R.id.title);
        final TextView messageText = alertLayout.findViewById(R.id.message);
        final EditText input = alertLayout.findViewById(R.id.editText);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        titleText.setText("Your mobile number?");
        messageText.setText("Enter your contact number below");

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

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(10)});

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_NORMAL);

        input.setText(doctorUploadDetails.getDoctorPhone());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (input.getText().toString().length() != 10)
                {
                    phone.setText(null);
                    Toast.makeText(getContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    phone.setText(input.getText().toString());
                    doctorUploadDetails.setDoctorPhone(input.getText().toString());
                }

                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

    }


    private void askForBookingPhone()
    {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_credentials, null);
        final Button cancelBtn = alertLayout.findViewById(R.id.btn_cancel);
        final Button okBtn = alertLayout.findViewById(R.id.btn_ok);
        final TextView titleText = alertLayout.findViewById(R.id.title);
        final TextView messageText = alertLayout.findViewById(R.id.message);
        final EditText input = alertLayout.findViewById(R.id.editText);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        titleText.setText("Your booking mobile number?");
        messageText.setText("Enter your booking contact number below");

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

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(10)});

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_NORMAL);

        input.setText(doctorUploadDetails.getDoctorBookingPhone());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (input.getText().toString().length() != 10)
                {
                    bookingPhone.setText(null);
                    Toast.makeText(getContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    bookingPhone.setText(input.getText().toString());
                    doctorUploadDetails.setDoctorBookingPhone(input.getText().toString());
                }

                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

    }


    private void askForExperience()
    {
        showExperiencePicker();
    }



    private void showExperiencePicker()
    {

        calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateExperience(year, monthOfYear, dayOfMonth);
            }

        };

        new DatePickerDialog(getContext(), date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void updateExperience(int y, int m, int d)
    {
        int year = 0;
        int month = 0;
        int day = 0;

        Calendar testCalendar = Calendar.getInstance();
        testCalendar.set(y, m, d);

        long msDiff = Calendar.getInstance().getTimeInMillis() - testCalendar.getTimeInMillis();
        long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);

        if (daysDiff < 0)
        {
            Snackbar.make(v, "Select valid date", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (daysDiff < 30)
        {
            Snackbar.make(v, "Minimum 1 month experience required to register", Snackbar.LENGTH_SHORT).show();
            return;
        }

        year = Math.round(daysDiff / 365);
        daysDiff -= (365 * year);
        month = Math.round(daysDiff / 30);
        daysDiff -= (30 * month);

        if (year > 60)
        {
            Snackbar.make(v, "Maximum experience limit is 60 years", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if ((year > 0) && (month > 6))
        {
            year++;
            month = 0;

            yearOfExperience.setText(year + " years");
            doctorUploadDetails.setDoctorExperience(year + " years");

        }
        else if (year > 0)
        {
            if (year == 1)
            {
                yearOfExperience.setText(year + " year");
                doctorUploadDetails.setDoctorExperience(year + " year");
            }
            else {
                yearOfExperience.setText(year + " years");
                doctorUploadDetails.setDoctorExperience(year + " years");
            }
        }
        else if ((year == 0) && (month > 0) && (daysDiff > 15))
        {
            year = 0;
            month++;
            daysDiff = 0;

            yearOfExperience.setText(month + " months");
            doctorUploadDetails.setDoctorExperience(month + " months");
        }
        else
        {

            if (month == 1)
            {
                yearOfExperience.setText(month + " month");
                doctorUploadDetails.setDoctorExperience(month + " month");
            }
            else {
                yearOfExperience.setText(month + " months");
                doctorUploadDetails.setDoctorExperience(month + " months");
            }
        }

        //Toast.makeText(getContext(), "Year: "+ year + " Month: " + month + " Days: " + daysDiff, Toast.LENGTH_SHORT).show();

    }


    private void askForRegistrationYear()
    {
        showRegistrationPicker();
    }


    private void showRegistrationPicker()
    {

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateYearOfRegistration(year, monthOfYear, dayOfMonth);
            }

        };

        new DatePickerDialog(getContext(), date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void updateYearOfRegistration(int y, int m, int d)
    {
        int year = 0;
        int month = 0;
        int day = 0;

        year = Calendar.getInstance().get(Calendar.YEAR);

        if (y > year)
        {
            yearOfReg.setText(null);
            doctorUploadDetails.setDoctorYearOfReg(null);
            Snackbar.make(v, "Select valid year of registration", Snackbar.LENGTH_SHORT).show();
        }
        else
        {
            yearOfReg.setText(String.valueOf(y));
            doctorUploadDetails.setDoctorYearOfReg(String.valueOf(y));
        }

    }


    private void askForLastDegree()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select your last degree");

        builder.setCancelable(false);

        final String[] degreeArr = new String[]{"MBBS", "BMBS", "MBChB", "MBBCh", "MD", "Dr.MuD", "Dr.Med", "DO", "Custom..."};

        String selectedDegree = lastDegree.getText().toString().toLowerCase();

        int index = 0;
        boolean found = false;
        for (int i = 0; i < degreeArr.length; i++)
        {
            if (degreeArr[i].toLowerCase().equals(selectedDegree))
            {
                index = i;
                found = true;
                break;
            }
        }

        if ((!found) && (lastDegree.getText().toString().trim().length() > 0))
        {
            index = degreeArr.length - 1;
        }

        builder.setSingleChoiceItems(degreeArr, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (degreeArr[which].toLowerCase().contains("custom"))
                {
                    dialog.cancel();

                    showCustomDegreeDialog();

                }
                else {

                    lastDegree.setText(degreeArr[which]);
                    doctorUploadDetails.setDoctorQualifications(degreeArr[which]);

                }

                dialog.cancel();

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (lastDegree.getText().toString().trim().length() == 0) {
                    lastDegree.setText(degreeArr[0]);
                    doctorUploadDetails.setDoctorQualifications(degreeArr[0]);
                }

                dialog.cancel();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void askForClinicName()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_credentials, null);
        final Button cancelBtn = alertLayout.findViewById(R.id.btn_cancel);
        final Button okBtn = alertLayout.findViewById(R.id.btn_ok);
        final TextView titleText = alertLayout.findViewById(R.id.title);
        final TextView messageText = alertLayout.findViewById(R.id.message);
        final EditText input = alertLayout.findViewById(R.id.editText);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        titleText.setText("What is your clinic name?");
        messageText.setText("*NOTE: You can change your clinic name only once in " + MINIMUM_DAYS_FOR_NEXT_CLINIC_NAME_CHANGE + " days");

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dEst, int dStart, int dEnd) {
                for (int i = start; i < end; ++i)
                {
                    if (!Pattern.compile("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ]*").matcher(String.valueOf(source.charAt(i))).matches())
                    {
                        return "";
                    }
                }

                return null;
            }
        };

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(50)});

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        input.setText(doctorUploadDetails.getDoctorClinicName().toUpperCase());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String testName = input.getText().toString().trim();
                testName = testName.replaceAll(" ", "");

                if (!(testName.length() > 0))
                {
                    clinicName.setText(null);
                    Toast.makeText(getContext(), "This field can't be empty", Toast.LENGTH_SHORT).show();
                }
                else {

                    doctorUploadDetails.setDoctorClinicName(input.getText().toString().toUpperCase());
                    clinicName.setText(input.getText().toString().toUpperCase());
                }

                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
    }


    private void showCustomDegreeDialog()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_credentials, null);
        final Button cancelBtn = alertLayout.findViewById(R.id.btn_cancel);
        final Button okBtn = alertLayout.findViewById(R.id.btn_ok);
        final TextView titleText = alertLayout.findViewById(R.id.title);
        final TextView messageText = alertLayout.findViewById(R.id.message);
        final EditText input = alertLayout.findViewById(R.id.editText);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        titleText.setText("Your last degree?");
        messageText.setText("Enter your last degree below");

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dEst, int dStart, int dEnd) {
                for (int i = start; i < end; ++i)
                {
                    if (!Pattern.compile("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.]*").matcher(String.valueOf(source.charAt(i))).matches())
                    {
                        return "";
                    }
                }

                return null;
            }
        };

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(30)});

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        input.setText(doctorUploadDetails.getDoctorQualifications());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String testName = input.getText().toString().trim();
                testName = testName.replaceAll(" ", "");

                if (!(testName.length() > 0))
                {
                    lastDegree.setText(null);
                    Toast.makeText(getContext(), "Last degree is required", Toast.LENGTH_SHORT).show();
                }
                else {

                    doctorUploadDetails.setDoctorQualifications(input.getText().toString());
                    lastDegree.setText(input.getText().toString());
                }

                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
    }

    private void askForMail()
    {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_credentials, null);
        final Button cancelBtn = alertLayout.findViewById(R.id.btn_cancel);
        final Button okBtn = alertLayout.findViewById(R.id.btn_ok);
        final TextView titleText = alertLayout.findViewById(R.id.title);
        final TextView messageText = alertLayout.findViewById(R.id.message);
        final EditText input = alertLayout.findViewById(R.id.editText);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        titleText.setText("What is your mail-ID?");
        messageText.setText("Enter your email ID below");

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dEst, int dStart, int dEnd) {
                for (int i = start; i < end; ++i)
                {
                    if (!Pattern.compile("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@1234567890_.]*").matcher(String.valueOf(source.charAt(i))).matches())
                    {
                        return "";
                    }
                }

                return null;
            }
        };

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(50)});

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        input.setText(doctorUploadDetails.getDoctorMail().toUpperCase());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String testName = input.getText().toString().trim();
                testName = testName.replaceAll(" ", "");

                if (!(testName.length() > 0))
                {
                    mail.setText(null);
                    Toast.makeText(getContext(), "Email can't be empty", Toast.LENGTH_SHORT).show();
                }
                else {

                    doctorUploadDetails.setDoctorMail(input.getText().toString().toLowerCase());
                    mail.setText(input.getText().toString().toLowerCase());
                }

                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

    }

    private void askForMedicalCouncil()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select your medical council");

        builder.setCancelable(false);


        //Meghalaya doesn't have medical council...
        String[] indianStates = new String[] {"Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Mizoram", "Nagaland", "Orissa", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal", "Delhi",};

        final ArrayList<String> statesMedicalCouncil = new ArrayList<>();


        for (int i = 0; i < indianStates.length; i++)
        {
            statesMedicalCouncil.add(indianStates[i] + " Medical Council");
        }

        statesMedicalCouncil.add("Other");


        final String selectedCouncil = medicalCouncil.getText().toString().toLowerCase();

        int index = 0;
        boolean found = false;
        for (int i = 0; i < statesMedicalCouncil.size(); i++)
        {
            if (statesMedicalCouncil.get(i).toLowerCase().equals(selectedCouncil))
            {
                index = i;
                found = true;
                break;
            }
        }


        final String[] mcList = new String[statesMedicalCouncil.size()];

        for (int i = 0; i < statesMedicalCouncil.size(); i++)
        {
            mcList[i] = statesMedicalCouncil.get(i);
        }

        if ((!found) && (medicalCouncil.getText().toString().trim().length() > 0))
        {
            index = mcList.length - 1;
        }


        builder.setSingleChoiceItems(mcList, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (statesMedicalCouncil.get(which).toLowerCase().contains("other"))
                {
                    dialog.cancel();

                    showCustomMedicalCouncilDialog();

                }
                else {

                    medicalCouncil.setText(statesMedicalCouncil.get(which));
                    doctorUploadDetails.setDoctorMedicalCouncil(statesMedicalCouncil.get(which));

                }

                dialog.cancel();

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (medicalCouncil.getText().toString().trim().length() == 0) {
                    medicalCouncil.setText(statesMedicalCouncil.get(0));
                    doctorUploadDetails.setDoctorMedicalCouncil(statesMedicalCouncil.get(0));
                }

                dialog.cancel();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }


    private void showCustomMedicalCouncilDialog()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_credentials, null);
        final Button cancelBtn = alertLayout.findViewById(R.id.btn_cancel);
        final Button okBtn = alertLayout.findViewById(R.id.btn_ok);
        final TextView titleText = alertLayout.findViewById(R.id.title);
        final TextView messageText = alertLayout.findViewById(R.id.message);
        final EditText input = alertLayout.findViewById(R.id.editText);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        titleText.setText("Your medical council?");
        messageText.setText("Enter your medical council below");

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dEst, int dStart, int dEnd) {
                for (int i = start; i < end; ++i)
                {
                    if (!Pattern.compile("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ]*").matcher(String.valueOf(source.charAt(i))).matches())
                    {
                        return "";
                    }
                }

                return null;
            }
        };

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(50)});

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        input.setText(doctorUploadDetails.getDoctorMedicalCouncil());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String testName = input.getText().toString().trim();
                testName = testName.replaceAll(" ", "");

                if (!(testName.length() > 0))
                {
                    medicalCouncil.setText(null);
                    Toast.makeText(getContext(), "Medical council is required", Toast.LENGTH_SHORT).show();
                }
                else {

                    doctorUploadDetails.setDoctorMedicalCouncil(input.getText().toString().toUpperCase());
                    medicalCouncil.setText(input.getText().toString().toUpperCase());
                }

                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
    }


    private void askForAddress()
    {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_credentials, null);
        final Button cancelBtn = alertLayout.findViewById(R.id.btn_cancel);
        final Button okBtn = alertLayout.findViewById(R.id.btn_ok);
        final TextView titleText = alertLayout.findViewById(R.id.title);
        final TextView messageText = alertLayout.findViewById(R.id.message);
        final EditText input = alertLayout.findViewById(R.id.editText);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        titleText.setText("Your contact address?");
        messageText.setText("Enter your contact address below");

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dEst, int dStart, int dEnd) {
                for (int i = start; i < end; ++i)
                {
                    if (!Pattern.compile("[1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ\\.\\:\\- ]*").matcher(String.valueOf(source.charAt(i))).matches())
                    {
                        return "";
                    }
                }

                return null;
            }
        };

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(200)});

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);

        input.setText(doctorUploadDetails.getDoctorClinicAddress().toUpperCase());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String testName = input.getText().toString().trim();
                testName = testName.replaceAll(" ", "");

                if (!(testName.length() > 0))
                {
                    address.setText(null);
                    Toast.makeText(getContext(), "Address can't be empty", Toast.LENGTH_SHORT).show();
                }
                else {

                    doctorUploadDetails.setDoctorClinicAddress(input.getText().toString().toUpperCase());
                    address.setText(input.getText().toString().toUpperCase());
                }

                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

    }



    //not working...
    private DatePickerDialog createDialogWithoutDateField() {
        DatePickerDialog dpd = new DatePickerDialog(getContext(), null, 2014, 1, 1);
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
        }
        return dpd;
    }



    private void chooseImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {

            isImagePresent = true;

            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imagePath);
                doctorPic.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    private void uploadImage() {

        if(imagePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("");
            progressDialog.show();

            StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/"+ REG_NUMBER + ".jpg");
            ref.putFile(imagePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            //profile image uploaded successfully...


                            /**
                             *
                             *
                             * newly added code...
                             *
                             *
                             */

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    downloadUrl = uri.toString();

                                    //Toast.makeText(getContext(), "Url: " + downloadUrl, Toast.LENGTH_SHORT).show();

                                    uploadToDatabase(downloadUrl);
                                    //uploading to firebase database...

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });


                            /**
                             *
                             *
                             * newly added code ends here...
                             *
                             */


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage((int)progress+"%" + " uploaded");
                        }
                    });

//            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//
//                    downloadUrl = uri.toString();
//
//                    //Toast.makeText(getContext(), "Url: " + downloadUrl, Toast.LENGTH_SHORT).show();
//
//                    uploadToDatabase(downloadUrl);
//                    //uploading to firebase database...
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle any errors
//                }
//            });
        }
        else
        {
            if (doctorPic.getDrawable() != null) {
                uploadToDatabase(downloadUrl);
            }
            else
            {
                Snackbar.make(v, "Please select your profile picture", Snackbar.LENGTH_SHORT).show();
            }
        }
    }


    private boolean validate()
    {

        String doctorName = name.getText().toString().trim();
        doctorName = doctorName.replaceAll(" ", "");

        String doctorRegNumber = regNumber.getText().toString().trim();
        doctorRegNumber = doctorRegNumber.replaceAll(" ", "");

        String doctorGender = gender.getText().toString().trim();
        doctorGender = doctorGender.replaceAll(" ", "");

        String doctorPhone = phone.getText().toString().trim();
        doctorPhone = doctorPhone.replaceAll(" ", "");

        String doctorBookingPhone = bookingPhone.getText().toString().trim();
        doctorBookingPhone = doctorBookingPhone.replaceAll(" ", "");

        String doctorExp = yearOfExperience.getText().toString().trim();
        doctorExp = doctorExp.replaceAll(" ", "");

        String doctorRegYear = yearOfReg.getText().toString().trim();
        doctorRegYear = doctorRegYear.replaceAll(" ", "");

        String doctorLastDegree = lastDegree.getText().toString().trim();
        doctorLastDegree = doctorLastDegree.replaceAll(" ", "");

        String doctorAddress = address.getText().toString().trim();
        doctorAddress = doctorAddress.replaceAll(" ", "");

        String doctorMedicalCouncil = medicalCouncil.getText().toString().trim();
        doctorMedicalCouncil = doctorMedicalCouncil.replaceAll(" ", "");

        String doctorMail = mail.getText().toString().trim();
        doctorMail = doctorMail.replaceAll(" ", "");

        String doctorUpiName = upiName.getText().toString().trim();
        doctorUpiName = doctorUpiName.replaceAll(" ", "");

        String doctorUpiID = upiID.getText().toString().trim();
        doctorUpiID = doctorUpiID.replaceAll(" ", "");

        String doctorFee = appointmentFee.getText().toString().trim();
        doctorFee = doctorFee.replaceAll(" ", "");


        if (imagePath == null && downloadUrl == null)
        {
            Snackbar.make(v, "Select your profile picture first", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (doctorName.isEmpty())
        {
            Snackbar.make(v, "Name can't be empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (doctorRegNumber.isEmpty())
        {
            Snackbar.make(v, "Reg. number can't be empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (doctorGender.isEmpty())
        {
            Snackbar.make(v, "Gender can't be empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (doctorPhone.isEmpty())
        {
            Snackbar.make(v, "Phone can't be empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (doctorBookingPhone.isEmpty())
        {
            Snackbar.make(v, "Booking phone can't be empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (doctorExp.isEmpty())
        {
            Snackbar.make(v, "Please enter your year of experience", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (doctorRegYear.isEmpty())
        {
            Snackbar.make(v, "Please enter your year of registration", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (doctorLastDegree.isEmpty())
        {
            Snackbar.make(v, "Please enter your last degree", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (doctorAddress.isEmpty())
        {
            Snackbar.make(v, "Please enter your contact address", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (doctorMedicalCouncil.isEmpty())
        {
            Snackbar.make(v, "Please choose your medical council name", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (doctorMail.isEmpty())
        {
            Snackbar.make(v, "Please enter your mail-ID", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mail.getText().toString()).matches())
        {
            Snackbar.make(v, "Please enter valid mail-ID", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (doctorFee.isEmpty())
        {
            Snackbar.make(v, "Please enter your appointment fee", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if ((doctorUpiName.length() == 0) && (doctorUpiID.length() > 0))
        {
            Snackbar.make(v, "Please enter both UPI name and ID or none", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if ((doctorUpiID.length() == 0) && (doctorUpiName.length() > 0))
        {
            Snackbar.make(v, "Please enter both UPI name and ID or none", Snackbar.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }


    private void uploadToDatabase(String imageUrl)
    {

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(PROFILE);

        final DoctorDetails doctorDetails = new DoctorDetails();

        doctorDetails.setDoctorName(name.getText().toString().toUpperCase());
        doctorDetails.setDoctorRegNumber(yearOfReg.getText().toString());
        doctorDetails.setDoctorGender(gender.getText().toString());
        doctorDetails.setDoctorPhone(phone.getText().toString());
        doctorDetails.setDoctorBookingPhone(bookingPhone.getText().toString());
        doctorDetails.setDoctorExperience(yearOfExperience.getText().toString());
        doctorDetails.setDoctorYearOfReg(yearOfReg.getText().toString());
        doctorDetails.setDoctorQualifications(lastDegree.getText().toString().toUpperCase());
        doctorDetails.setDoctorClinicName(clinicName.getText().toString().toUpperCase());
        doctorDetails.setDoctorClinicAddress(address.getText().toString());
        doctorDetails.setDoctorMedicalCouncil(medicalCouncil.getText().toString());
        doctorDetails.setDoctorMail(mail.getText().toString());
        doctorDetails.setDoctorPhotoUri(imageUrl);
        doctorDetails.setDoctorSatisfiedPatientsNumber(String.valueOf(0));
        doctorDetails.setDoctorUpiName(upiName.getText().toString());
        doctorDetails.setDoctorUpiID(upiID.getText().toString());
        doctorDetails.setDoctorFee(appointmentFee.getText().toString());

        mRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mRef.setValue(doctorDetails)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                mRef.child("doctorRegNumber").setValue(REG_NUMBER);
                                mRef.child("password").setValue(PASSWORD);
                                mRef.child("doctorType").setValue(DOCTOR_TYPE);


                                // save clinic name changed prefs...
                                forSave = true;
                                forComparison = false;
                                new getTimeAndDate().execute();


                                saveProfilePrefs();


                                Snackbar.make(v, "Updated successfully", Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(v, "Something went wrong", Snackbar.LENGTH_SHORT).show();
                            }
                        });

                dialog.cancel();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Snackbar.make(v, "Database error: " + databaseError.getMessage(), Snackbar.LENGTH_SHORT).show();

                dialog.cancel();

            }
        });

        dialog.cancel();


    }

    private void saveProfilePrefs() {

        SharedPreferences doctorProfilePref = getContext().getSharedPreferences("doctorProfilePref",MODE_PRIVATE);
        SharedPreferences.Editor editor = doctorProfilePref.edit();
        editor.putBoolean("isProfileComplete", true);
        editor.commit();

    }

    private void saveClinicNameChangedDate(String timeInMillis)
    {

        SharedPreferences clinicNamePref = getContext().getSharedPreferences(CLINIC_NAME_CHANGED_DATE, MODE_PRIVATE);
        SharedPreferences.Editor clinicNameEditor = clinicNamePref.edit();
        clinicNameEditor.putString(CLINIC_NAME_CHANGED_DATE, timeInMillis);
        clinicNameEditor.commit();

    }


    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = getArguments();
        REG_NUMBER = bundle.getString("regNumber");


        getPassword();

    }

    private void loadProfile()
    {

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(PROFILE);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 3)
                {
                    list.clear();

                    DoctorDetails doctorDetails = dataSnapshot.getValue(DoctorDetails.class);
                    list.add(doctorDetails);

                    try {
                        Glide.with(getContext())
                                .load(list.get(0).getDoctorPhotoUri())
                                .placeholder(R.drawable.man)
                                .into(doctorPic);

                    }catch (Exception e) {}

                    nameTop.setText(list.get(0).getDoctorName());
                    regNumberTop.setText(list.get(0).getDoctorRegNumber());
                    name.setText(list.get(0).getDoctorName());
                    gender.setText(list.get(0).getDoctorGender());
                    phone.setText(list.get(0).getDoctorPhone());
                    bookingPhone.setText(list.get(0).getDoctorBookingPhone());
                    yearOfExperience.setText(list.get(0).getDoctorExperience());
                    yearOfReg.setText(list.get(0).getDoctorYearOfReg());
                    lastDegree.setText(list.get(0).getDoctorQualifications());
                    clinicName.setText(list.get(0).getDoctorClinicName());
                    address.setText(list.get(0).getDoctorClinicAddress());
                    medicalCouncil.setText(list.get(0).getDoctorMedicalCouncil());
                    mail.setText(list.get(0).getDoctorMail());
                    satisfiedPatients.setText(list.get(0).getDoctorSatisfiedPatientsNumber());
                    if (list.get(0).getDoctorUpiName().trim() != null) {
                        upiName.setText(list.get(0).getDoctorUpiName());
                    }
                    if (list.get(0).getDoctorUpiID().trim() != null) {
                        upiID.setText(list.get(0).getDoctorUpiID());
                    }
                    appointmentFee.setText(list.get(0).getDoctorFee());

                    downloadUrl = list.get(0).getDoctorPhotoUri();

                }

                dialog.cancel();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                dialog.cancel();

            }
        });

    }


    private void getPassword()
    {
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(PROFILE);

        try {

            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    PASSWORD = dataSnapshot.child("password").getValue().toString();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e)
        {
            Snackbar.make(v, "Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        }

    }


    private void askForUpiName()
    {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_credentials, null);
        final Button cancelBtn = alertLayout.findViewById(R.id.btn_cancel);
        final Button okBtn = alertLayout.findViewById(R.id.btn_ok);
        final TextView titleText = alertLayout.findViewById(R.id.title);
        final TextView messageText = alertLayout.findViewById(R.id.message);
        final EditText input = alertLayout.findViewById(R.id.editText);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        titleText.setText("Your name in UPI app?");
        messageText.setText("Enter name as registered in your UPI app");

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dEst, int dStart, int dEnd) {
                for (int i = start; i < end; ++i)
                {
                    if (!Pattern.compile("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ]*").matcher(String.valueOf(source.charAt(i))).matches())
                    {
                        return "";
                    }
                }

                return null;
            }
        };

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(50)});

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        input.setText(doctorUploadDetails.getDoctorUpiName());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String testName = input.getText().toString().trim();
                testName = testName.replaceAll(" ", "");

                if (!(testName.length() > 0))
                {
                    Toast.makeText(getContext(), "You will not be able to accept UPI Payments", Toast.LENGTH_SHORT).show();

                    doctorUploadDetails.setDoctorUpiName("");
                    upiName.setText("");

                }
                else {

                    doctorUploadDetails.setDoctorUpiName(input.getText().toString());
                    upiName.setText(input.getText().toString());
                }

                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

    }


    private void askForUpiID()
    {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_credentials, null);
        final Button cancelBtn = alertLayout.findViewById(R.id.btn_cancel);
        final Button okBtn = alertLayout.findViewById(R.id.btn_ok);
        final TextView titleText = alertLayout.findViewById(R.id.title);
        final TextView messageText = alertLayout.findViewById(R.id.message);
        final EditText input = alertLayout.findViewById(R.id.editText);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        titleText.setText("Your UPI ID?");
        messageText.setText("Enter UPI ID as registered in your UPI app");

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dEst, int dStart, int dEnd) {
                for (int i = start; i < end; ++i)
                {
                    if (!Pattern.compile("[abcdefghijklmnopqrstuvwxyz@1234567890\\_\\.]*").matcher(String.valueOf(source.charAt(i))).matches())
                    {
                        return "";
                    }
                }

                return null;
            }
        };

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(50)});

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        input.setText(doctorUploadDetails.getDoctorUpiID());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String testName = input.getText().toString().trim();
                testName = testName.replaceAll(" ", "");

                if (!(testName.length() > 0))
                {
                    Toast.makeText(getContext(), "You will not be able to accept UPI Payments", Toast.LENGTH_SHORT).show();

                    doctorUploadDetails.setDoctorUpiID("");
                    upiID.setText("");

                }
                else {

                    doctorUploadDetails.setDoctorUpiID(input.getText().toString());
                    upiID.setText(input.getText().toString());
                }

                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

    }


    private void askForFee()
    {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ask_for_credentials, null);
        final Button cancelBtn = alertLayout.findViewById(R.id.btn_cancel);
        final Button okBtn = alertLayout.findViewById(R.id.btn_ok);
        final TextView titleText = alertLayout.findViewById(R.id.title);
        final TextView messageText = alertLayout.findViewById(R.id.message);
        final EditText input = alertLayout.findViewById(R.id.editText);

        titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);

        titleText.setText("Your appointment fee?");
        messageText.setText("Enter your appointment fee below");

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

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_NORMAL);

        input.setText(doctorUploadDetails.getDoctorFee());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertLayout);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (Integer.parseInt(input.getText().toString()) == 0) {
                        appointmentFee.setText(null);
                        Toast.makeText(getContext(), "Minimum fee allowed is Re. 1", Toast.LENGTH_SHORT).show();
                    } else {
                        appointmentFee.setText("Rs. " + input.getText().toString());
                        doctorUploadDetails.setDoctorFee("Rs. " + input.getText().toString());
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(getContext(), "Minimum fee allowed is Re. 1", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

    }


    private String getRegNumber()
    {
        String reg = null;

        DoctorCredentialsDBHelper dbHelper = new DoctorCredentialsDBHelper(getContext());

        reg = dbHelper.getRegNumber();

//        if (reg == null)
//        {
//            SharedPreferences doctorRegNumberPref = mContext.getSharedPreferences("doctorRegNumberPref", MODE_PRIVATE);
//
//            reg = doctorRegNumberPref.getString("regNumber", null);
//
//        }

        return reg;

    }


    private void getDoctorType()
    {
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE_DOCTOR).child(REG_NUMBER).child(PROFILE);

        try {

            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    DOCTOR_TYPE = dataSnapshot.child("doctorType").getValue().toString();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e)
        {
            Snackbar.make(v, "Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        }

    }


    public class getTimeAndDate extends AsyncTask<Void, Void, Void>
    {

        ProgressDialog dialog;

        String text;
        String timeInMillis = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(getContext());
            dialog.setMessage("Checking permissions, please wait...");
            dialog.setCancelable(false);
            if (forComparison) {
                dialog.show();
            }
            else if (forSave) {
                dialog.dismiss();
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                Document doc = Jsoup.connect(url).get();
                text = doc.text();

            }
            catch (Exception e) {}

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            boolean timeFound = false;

            for (int i = 0; i < text.length(); i++)
            {
                if (Character.isDigit(text.charAt(i)))
                {
                    timeFound = true;
                    timeInMillis += text.charAt(i);
                }
                else if (!Character.isDigit(text.charAt(i)) && timeFound)
                {
                    break;
                }
            }

            if (forSave)
            {
                saveClinicNameChangedDate(timeInMillis);
            }

            if (forComparison)
            {
                startDateComparison(timeInMillis);
            }

            dialog.dismiss();

        }

    }

    private String getEarlierTimeForNameChange()
    {

        SharedPreferences clinicNamePref = getContext().getSharedPreferences(CLINIC_NAME_CHANGED_DATE, MODE_PRIVATE);

        String time = clinicNamePref.getString(CLINIC_NAME_CHANGED_DATE, "0000000000");

        return time;

    }

    private void startDateComparison(String timeInMillis) {

        long earlierTime = Long.parseLong(getEarlierTimeForNameChange());
        long currentTime = Long.parseLong(timeInMillis);

        long daysDifference = TimeUnit.MILLISECONDS.toDays(currentTime - earlierTime);

        if (daysDifference >= MINIMUM_DAYS_FOR_NEXT_CLINIC_NAME_CHANGE)
        {
            askForClinicName();
        }
        else
        {
            long d = MINIMUM_DAYS_FOR_NEXT_CLINIC_NAME_CHANGE - daysDifference;

            if (d > 1) {
                Snackbar.make(v, "You can change your clinic name in next " + d + " days", Snackbar.LENGTH_LONG).show();
            }
            else if (d == 1)
            {
                Snackbar.make(v, "You can change your clinic name the next day", Snackbar.LENGTH_LONG).show();
            }
        }

    }


}