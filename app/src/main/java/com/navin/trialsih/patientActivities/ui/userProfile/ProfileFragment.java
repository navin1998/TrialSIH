package com.navin.trialsih.patientActivities.ui.userProfile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navin.trialsih.R;
import com.navin.trialsih.patientActivities.PatientDashboardActivity;
import com.navin.trialsih.patientsClasses.PatientDetails;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.activation.CommandObject;

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment implements View.OnClickListener{

    private static String UID;
    private final static String USER_TYPE = "patients";
    private final static String ACTIVE_APPOINTMENTS = "appointments";
    private final static String PREVIOUS_APPOINTMENTS = "prevAppointments";
    private final static String PREVIOUS_TRANSACTIONS = "prevTransactions";
    private final static String PROFILE = "profile";

    private ArrayList<PatientDetails> list;

    private ProfileViewModel profileViewModel;

    private View v;

    private TextView nameTop;
    private TextView ageTop;
    private TextView genderTop;
    private TextView name;
    private TextView age;
    private TextView gender;
    private TextView bloodGroup;
    private TextView weight;
    private TextView phone;
    private TextView mail;

    private ImageView patientPic;

    private LinearLayout nameLayout;
    private LinearLayout ageLayout;
    private LinearLayout genderLayout;
    private LinearLayout bloodGroupLayout;
    private LinearLayout weightLayout;
    private LinearLayout phoneLayout;
    private LinearLayout mailLayout;

    private LinearLayout[] layoutArr;

    private Button editBtn;

    private boolean onEdit = false;

    private PatientDetails patientUploadDetails;

    private Calendar calendar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        v = inflater.inflate(R.layout.fragment_patient_profile, container, false);

        ((PatientDashboardActivity) getActivity()).getSupportActionBar().setTitle("Profile");


        onEdit = false;

        list = new ArrayList<>();

        calendar = Calendar.getInstance();

        nameTop = v.findViewById(R.id.patient_name_top);
        ageTop = v.findViewById(R.id.patient_age_top);
        genderTop = v.findViewById(R.id.patient_gender_top);

        name = v.findViewById(R.id.patient_name);
        age = v.findViewById(R.id.patient_age);
        gender = v.findViewById(R.id.patient_gender);
        bloodGroup = v.findViewById(R.id.patient_bg);
        weight = v.findViewById(R.id.patient_weight);
        phone = v.findViewById(R.id.patient_phone);
        mail = v.findViewById(R.id.patient_mail);

        nameLayout = v.findViewById(R.id.patient_name_layout);
        ageLayout = v.findViewById(R.id.patient_age_layout);
        genderLayout = v.findViewById(R.id.patient_gender_layout);
        bloodGroupLayout = v.findViewById(R.id.patient_bg_layout);
        weightLayout = v.findViewById(R.id.patient_weight_layout);
        phoneLayout = v.findViewById(R.id.patient_phone_layout);
        mailLayout = v.findViewById(R.id.patient_mail_layout);
        editBtn = v.findViewById(R.id.profile_edit_btn);

        patientPic = v.findViewById(R.id.patient_pic);

        try {
            Glide.with(getContext())
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .into(patientPic);
        }
        catch (Exception e){}

        try {
            name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            nameTop.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

            mail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        }
        catch (Exception e){}

        try {
            phone.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        }
        catch (Exception e){}

        layoutArr = new LinearLayout[]{nameLayout, ageLayout, genderLayout, bloodGroupLayout, weightLayout, phoneLayout, mailLayout};

        nameLayout.setOnClickListener(this);
        ageLayout.setOnClickListener(this);
        genderLayout.setOnClickListener(this);
        bloodGroupLayout.setOnClickListener(this);
        weightLayout.setOnClickListener(this);
        phoneLayout.setOnClickListener(this);
        mailLayout.setOnClickListener(this);
        editBtn.setOnClickListener(this);

        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadProfile();

        return v;
    }

    private void loadProfile()
    {

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE).child(UID).child(PROFILE);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {

                    list.clear();

                    PatientDetails details = dataSnapshot.getValue(PatientDetails.class);
                    list.add(details);

                    name.setText(list.get(0).getPatientName());
                    nameTop.setText(list.get(0).getPatientName());
                    mail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                    try {
                        phone.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                        phone.setText(list.get(0).getPatientPhone());
                        age.setText(list.get(0).getPatientAge());
                        ageTop.setText(list.get(0).getPatientAge());
                        gender.setText(list.get(0).getPatientGender());
                        genderTop.setText(list.get(0).getPatientGender());
                        bloodGroup.setText(list.get(0).getPatientBloodGroup());
                        weight.setText(list.get(0).getPatientWeight());
                    }
                    catch (Exception e){}


                }

                dialog.cancel();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                dialog.cancel();

            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.patient_name_layout:
            case R.id.patient_age_layout:
            case R.id.patient_gender_layout:
            case R.id.patient_bg_layout:
            case R.id.patient_weight_layout:
            case R.id.patient_phone_layout:
            case R.id.patient_mail_layout:
                if (!onEdit)
                {
                    Snackbar.make(v, "Turn on edit mode by pressing button below", Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    switch (v.getId())
                    {
                        case R.id.patient_name_layout:
                            askForName();
                            break;

                        case R.id.patient_age_layout:
                            askForAge();
                            break;

                        case R.id.patient_gender_layout:
                            askForGender();
                            break;

                        case R.id.patient_bg_layout:
                            askForBloodGroup();
                            break;

                        case R.id.patient_weight_layout:
                            askForWeight();
                            break;

                        case R.id.patient_phone_layout:
                            askForPhone();
                            break;

                        case R.id.patient_mail_layout:
                            Snackbar.make(v, "Mail is non-editable", Snackbar.LENGTH_SHORT).show();
                            break;

                    }
                }
                break;


            case R.id.profile_edit_btn:
                if (onEdit)
                {
                    if (validate())
                    {
                        onEdit = false;
                        editBtn.setText("EDIT");
                        uploadToDatabase();
                        loadProfile();
                    }
                }
                else
                {

                    patientUploadDetails = new PatientDetails(name.getText().toString(), age.getText().toString(), gender.getText().toString(), bloodGroup.getText().toString(), weight.getText().toString(), phone.getText().toString(), mail.getText().toString());

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

        input.setText(patientUploadDetails.getPatientName());

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

                    patientUploadDetails.setPatientName(input.getText().toString());
                    nameTop.setText(input.getText().toString());
                    name.setText(input.getText().toString());
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


    private void askForAge()
    {

        showAgePicker();

    }

    private void showAgePicker()
    {

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateAge(year, monthOfYear, dayOfMonth);
            }

        };

        new DatePickerDialog(getContext(), date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void updateAge(int y, int m, int d)
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
            Snackbar.make(v, "Minimum 1 month age required to register", Snackbar.LENGTH_SHORT).show();
            return;
        }

        year = Math.round(daysDiff / 365);
        daysDiff -= (365 * year);
        month = Math.round(daysDiff / 30);
        daysDiff -= (30 * month);

        if (year > 110)
        {
            Snackbar.make(v, "Maximum age limit is 110 years", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if ((year > 0) && (month > 6))
        {
            year++;
            month = 0;

            age.setText(year + " years");
            ageTop.setText(year + " years");
            patientUploadDetails.setPatientAge(year + " years");

        }
        else if (year > 0)
        {
            if (year == 1)
            {
                age.setText(year + " year");
                ageTop.setText(year + " year");
                patientUploadDetails.setPatientAge(year + " year");
            }
            else {
                age.setText(year + " years");
                ageTop.setText(year + " years");
                patientUploadDetails.setPatientAge(year + " years");
            }
        }
        else if ((year == 0) && (month > 0) && (daysDiff > 15))
        {
            year = 0;
            month++;
            daysDiff = 0;

            age.setText(month + " months");
            ageTop.setText(month + " months");
            patientUploadDetails.setPatientAge(month + " months");
        }
        else
        {

            if (month == 1)
            {
                age.setText(month + " month");
                ageTop.setText(month + " month");
                patientUploadDetails.setPatientAge(month + " month");
            }
            else {
                age.setText(month + " months");
                ageTop.setText(month + " months");
                patientUploadDetails.setPatientAge(month + " months");
            }
        }

        //Toast.makeText(getContext(), "Year: "+ year + " Month: " + month + " Days: " + daysDiff, Toast.LENGTH_SHORT).show();

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
                genderTop.setText(genderArr[which]);
                patientUploadDetails.setPatientGender(genderArr[which]);

                dialog.cancel();

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                gender.setText(genderArr[0]);
                genderTop.setText(genderArr[0]);
                patientUploadDetails.setPatientGender(genderArr[0]);

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

    private void askForBloodGroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select your blood group");

        builder.setCancelable(false);

        final String[] bgArr = new String[]{"A +ve", "B +ve", "AB +ve", "O +ve", "A -ve", "B -ve", "AB -ve", "O -ve"};

        String selectedBG = bloodGroup.getText().toString().toLowerCase();

        int index = 0;
        for (int i = 0; i < bgArr.length; i++)
        {
            if (bgArr[i].toLowerCase().equals(selectedBG))
            {
                index = i;
                break;
            }
        }

        builder.setSingleChoiceItems(bgArr, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                bloodGroup.setText(bgArr[which]);
                patientUploadDetails.setPatientBloodGroup(bgArr[which]);

                dialog.cancel();

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                bloodGroup.setText(bgArr[0]);
                patientUploadDetails.setPatientBloodGroup(bgArr[0]);

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


    private void askForWeight()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose weight (in kgs)");

        builder.setCancelable(false);

        final NumberPicker numberPicker = new NumberPicker(getContext());
        numberPicker.setMaxValue(200);
        numberPicker.setMinValue(10);
        numberPicker.setWrapSelectorWheel(false);

        String selectedWeight = "";

        for (int i = 0; i < weight.getText().toString().length(); i++)
        {
            if ((weight.getText().toString().charAt(i) >= '0') && (weight.getText().toString().charAt(i) <= '9'))
            {
                selectedWeight += weight.getText().toString().charAt(i);
            }
        }

        try {
            numberPicker.setValue(Integer.parseInt(selectedWeight));
        }
        catch (Exception e){}

        builder.setView(numberPicker);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                weight.setText(newVal + " kg");
                patientUploadDetails.setPatientWeight(newVal + " kg");
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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
                    patientUploadDetails.setPatientPhone(input.getText().toString());
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

    private void uploadToDatabase()
    {

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        String patientName = name.getText().toString();
        String patientAge = age.getText().toString();
        String patientGender = gender.getText().toString();
        String patientBloodGroup = bloodGroup.getText().toString();
        String patientWeight = weight.getText().toString();
        String patientPhone = phone.getText().toString();
        String patientMail = mail.getText().toString();


        String patientNamePrefName = "patientNamePrefs" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        String patientNamePrefKey = "patientName" + FirebaseAuth.getInstance().getCurrentUser().getUid();


        // saving patient name for future use in appointments...
        SharedPreferences patientNamePref = getContext().getSharedPreferences(patientNamePrefName, MODE_PRIVATE);
        SharedPreferences.Editor patientNameEditor = patientNamePref.edit();
        patientNameEditor.putString(patientNamePrefKey, patientName);
        patientNameEditor.commit();


        String patientPhonePrefName = "patientPhonePrefs" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        String patientPhonePrefKey = "patientPhone" + FirebaseAuth.getInstance().getCurrentUser().getUid();



        // saving patient phone for future use in appointments...
        SharedPreferences patientPhonePref = getContext().getSharedPreferences(patientPhonePrefName, MODE_PRIVATE);
        SharedPreferences.Editor patientPhoneEditor = patientPhonePref.edit();
        patientPhoneEditor.putString(patientPhonePrefKey, patientPhone);
        patientPhoneEditor.commit();


        final PatientDetails patientDetails = new PatientDetails(patientName, patientAge, patientGender, patientBloodGroup, patientWeight, patientPhone, patientMail);

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE).child(UID).child(PROFILE);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mRef.setValue(patientDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(v, "Updated successfully", Snackbar.LENGTH_SHORT).show();
                    }
                });

                dialog.cancel();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                dialog.cancel();

            }
        });

    }

    private boolean validate()
    {
        if (name.getText().toString().isEmpty())
        {
            Snackbar.make(v, "Name can't be empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (age.getText().toString().isEmpty())
        {
            Snackbar.make(v, "Age can't be empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (gender.getText().toString().isEmpty())
        {
            Snackbar.make(v, "Gender can't be empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (bloodGroup.getText().toString().isEmpty())
        {
            Snackbar.make(v, "Blood group can't be empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (weight.getText().toString().isEmpty())
        {
            Snackbar.make(v, "Weight can't be empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (phone.getText().toString().isEmpty())
        {
            Snackbar.make(v, "Phone can't be empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (mail.getText().toString().isEmpty())
        {
            Snackbar.make(v, "Mail can't be empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}