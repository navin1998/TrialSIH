package com.navin.trialsih.doctorActivities.bottomNavigation;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.navin.trialsih.R;
import com.navin.trialsih.doctorActivities.DoctorDashboardActivity;
import com.navin.trialsih.doctorClasses.DoctorDetails;
import com.navin.trialsih.doctorClasses.MonthYearPickerDialog;
import com.navin.trialsih.doctorDBHelpers.DoctorCredentialsDBHelper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private static String REG_NUMBER;
    private final static String USER_TYPE_DOCTOR = "doctors";
    private final static String ACTIVE_APPOINTMENTS = "appointments";
    private final static String PROFILE = "profile";
    private static String PASSWORD;

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

        getPassword();

        loadProfile();

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
        addressLayout = v.findViewById(R.id.doctor_address_layout);
        medicalCouncilLayout = v.findViewById(R.id.doctor_council_layout);
        mailLayout = v.findViewById(R.id.doctor_mail_layout);
        satisfiedPatientsLayout = v.findViewById(R.id.doctor_satisfied_patients_layout);
        upiNameLayout = v.findViewById(R.id.doctor_upi_name_layout);
        upiIDLayout = v.findViewById(R.id.doctor_upi_id_layout);
        appointmentFeeLayout = v.findViewById(R.id.doctor_fee_layout);


        editBtn = v.findViewById(R.id.doctor_profile_edit_btn);
        doctorPic = v.findViewById(R.id.doctor_pic);


        layoutArr = new LinearLayout[]{nameLayout, regNumberLayout, genderLayout, phoneLayout, bookingPhoneLayout, yearOfExperienceLayout, yearOfRegLayout, lastDegreeLayout, addressLayout, medicalCouncilLayout, mailLayout, satisfiedPatientsLayout, upiNameLayout, upiIDLayout, appointmentFeeLayout};

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

                    doctorUploadDetails = new DoctorDetails(name.getText().toString(), regNumber.getText().toString(), gender.getText().toString(), phone.getText().toString(), bookingPhone.getText().toString(), yearOfExperience.getText().toString(), lastDegree.getText().toString(), address.getText().toString(), satisfiedPatients.getText().toString(), medicalCouncil.getText().toString(), yearOfReg.getText().toString(), null, mail.getText().toString(), upiName.getText().toString(), upiID.getText().toString(), appointmentFee.getText().toString());

                    onEdit = true;
                    editBtn.setText("SAVE");

                }
                break;


        }

    }




    private void askForName()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter your name");

        builder.setCancelable(false);

        final EditText input = new EditText(getContext());

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

        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String testName = input.getText().toString().trim();
                testName = testName.replaceAll(" ", "");

                if (!(testName.length() > 0))
                {
                    Snackbar.make(v, "Name can't be empty", Snackbar.LENGTH_SHORT).show();
                }
                else {

                    doctorUploadDetails.setDoctorName(input.getText().toString().toUpperCase());
                    nameTop.setText(input.getText().toString().toUpperCase());
                    name.setText(input.getText().toString().toUpperCase());
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



    private void askForRegNumber()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter your registration number");

        builder.setCancelable(false);

        final EditText input = new EditText(getContext());

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dEst, int dStart, int dEnd) {
                for (int i = start; i < end; ++i)
                {
                    if (!Pattern.compile("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ\\-1234567890]*").matcher(String.valueOf(source.charAt(i))).matches())
                    {
                        return "";
                    }
                }

                return null;
            }
        };

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(20)});

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        input.setText(doctorUploadDetails.getDoctorRegNumber());

        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String testName = input.getText().toString().trim();
                testName = testName.replaceAll(" ", "");

                if (!(testName.length() > 0))
                {
                    Snackbar.make(v, "Name can't be empty", Snackbar.LENGTH_SHORT).show();
                }
                else {

                    doctorUploadDetails.setDoctorRegNumber(input.getText().toString().toUpperCase());
                    regNumberTop.setText(input.getText().toString().toUpperCase());
                    regNumber.setText(input.getText().toString().toUpperCase());
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Enter your 10 digits phone");

        builder.setCancelable(false);

        final EditText input = new EditText(getContext());

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

        input.setText(phone.getText().toString());

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(10)});

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().length() != 10)
                {
                    Snackbar.make(v, "Invalid phone number", Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    phone.setText(input.getText().toString());
                    doctorUploadDetails.setDoctorPhone(input.getText().toString());
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


    private void askForBookingPhone()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Enter your 10 digits booking phone");

        builder.setCancelable(false);

        final EditText input = new EditText(getContext());

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

        input.setText(bookingPhone.getText().toString());

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(10)});

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().length() != 10)
                {
                    Snackbar.make(v, "Invalid phone number", Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    bookingPhone.setText(input.getText().toString());
                    doctorUploadDetails.setDoctorBookingPhone(input.getText().toString());
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

        year = calendar.get(Calendar.YEAR);

        if (y > year)
        {
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
        for (int i = 0; i < degreeArr.length; i++)
        {
            if (degreeArr[i].toLowerCase().equals(selectedDegree))
            {
                index = i;
                break;
            }
        }

        builder.setSingleChoiceItems(degreeArr, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (degreeArr[which].toLowerCase().contains("custom"))
                {
                    dialog.cancel();

                    AlertDialog.Builder innerBuilder = new AlertDialog.Builder(getContext());
                    innerBuilder.setTitle("Enter your last degree");
                    innerBuilder.setCancelable(false);

                    final EditText input = new EditText(getContext());

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

                    input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(20)});

                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

                    input.setText(doctorUploadDetails.getDoctorQualifications());

                    innerBuilder.setView(input);

                    innerBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            lastDegree.setText(input.getText().toString());
                            doctorUploadDetails.setDoctorQualifications(input.getText().toString());

                            dialog.cancel();

                        }
                    });

                    innerBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();

                        }
                    });

                    innerBuilder.show();

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

                lastDegree.setText(degreeArr[0]);
                doctorUploadDetails.setDoctorQualifications(degreeArr[0]);

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


    private void askForMail()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter your mail");

        builder.setCancelable(false);

        final EditText input = new EditText(getContext());

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

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        input.setText(doctorUploadDetails.getDoctorMail());

        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String testName = input.getText().toString().trim();
                testName = testName.replaceAll(" ", "");

                if (!(testName.length() > 0))
                {
                    Snackbar.make(v, "Mail can't be empty", Snackbar.LENGTH_SHORT).show();
                }
                else {

                    doctorUploadDetails.setDoctorMail(input.getText().toString());
                    mail.setText(input.getText().toString());
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
        for (int i = 0; i < statesMedicalCouncil.size(); i++)
        {
            if (statesMedicalCouncil.get(i).toLowerCase().equals(selectedCouncil))
            {
                index = i;
                break;
            }
        }

        final String[] mcList = new String[statesMedicalCouncil.size()];

        for (int i = 0; i < statesMedicalCouncil.size(); i++)
        {
            mcList[i] = statesMedicalCouncil.get(i);
        }


        builder.setSingleChoiceItems(mcList, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (statesMedicalCouncil.get(which).toLowerCase().contains("other"))
                {


                    dialog.cancel();

                    AlertDialog.Builder innerBuilder = new AlertDialog.Builder(getContext());
                    innerBuilder.setTitle("Enter your medical council name");
                    innerBuilder.setCancelable(false);

                    final EditText input = new EditText(getContext());

                    InputFilter filter = new InputFilter() {
                        @Override
                        public CharSequence filter(CharSequence source, int start, int end, Spanned dEst, int dStart, int dEnd) {
                            for (int i = start; i < end; ++i)
                            {
                                if (!Pattern.compile("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ]*").matcher(String.valueOf(source.charAt(i))).matches())
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

                    innerBuilder.setView(input);

                    innerBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            medicalCouncil.setText(input.getText().toString());
                            doctorUploadDetails.setDoctorMedicalCouncil(input.getText().toString());

                            dialog.cancel();

                        }
                    });

                    innerBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();

                        }
                    });

                    innerBuilder.show();



                }

                medicalCouncil.setText(statesMedicalCouncil.get(which));
                doctorUploadDetails.setDoctorMedicalCouncil(statesMedicalCouncil.get(which));

                dialog.cancel();

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                medicalCouncil.setText(statesMedicalCouncil.get(0));
                doctorUploadDetails.setDoctorMedicalCouncil(statesMedicalCouncil.get(0));

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


    private void askForAddress()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter your contact address");

        builder.setCancelable(false);

        final EditText input = new EditText(getContext());

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dEst, int dStart, int dEnd) {
                for (int i = start; i < end; ++i)
                {
                    if (!Pattern.compile("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.:\\- ]*").matcher(String.valueOf(source.charAt(i))).matches())
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

        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String testName = input.getText().toString().trim();
                testName = testName.replaceAll(" ", "");

                if (!(testName.length() > 0))
                {
                    Snackbar.make(v, "Address can't be empty", Snackbar.LENGTH_SHORT).show();
                }
                else {

                    doctorUploadDetails.setDoctorClinicAddress(input.getText().toString().toUpperCase());
                    address.setText(input.getText().toString().toUpperCase());
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



    private void askForUpiName()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter your name registered in UPI App");

        builder.setCancelable(false);

        final EditText input = new EditText(getContext());

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

        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String testName = input.getText().toString().trim();
                testName = testName.replaceAll(" ", "");

                if (!(testName.length() > 0))
                {
                    Snackbar.make(v, "You will not be able to accept UPI Payments", Snackbar.LENGTH_SHORT).show();

                    doctorUploadDetails.setDoctorUpiName("");
                    upiName.setText("");

                }
                else {

                    doctorUploadDetails.setDoctorUpiName(input.getText().toString().toUpperCase());
                    upiName.setText(input.getText().toString());
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


    private void askForUpiID()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter your UPI ID");

        builder.setCancelable(false);

        final EditText input = new EditText(getContext());

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dEst, int dStart, int dEnd) {
                for (int i = start; i < end; ++i)
                {
                    if (!Pattern.compile("[abcdefghijklmnopqrstuvwxyz@1234567890_.]*").matcher(String.valueOf(source.charAt(i))).matches())
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

        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String testName = input.getText().toString().trim();
                testName = testName.replaceAll(" ", "");

                if (!(testName.length() > 0))
                {
                    Snackbar.make(v, "You will not be able to accept UPI Payments", Snackbar.LENGTH_SHORT).show();

                    doctorUploadDetails.setDoctorUpiID("");
                    upiID.setText("");

                }
                else {

                    doctorUploadDetails.setDoctorUpiID(input.getText().toString());
                    upiID.setText(input.getText().toString());
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



    private void askForFee()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Enter your appointment fee");

        builder.setCancelable(false);

        final EditText input = new EditText(getContext());

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

        input.setText(appointmentFee.getText().toString());

        input.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Integer.parseInt(input.getText().toString()) == 0)
                {
                    Snackbar.make(v, "Minimum fee allowed is Re. 1", Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    appointmentFee.setText("Rs. " + input.getText().toString());
                    doctorUploadDetails.setDoctorFee("Rs. " + input.getText().toString());
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


}