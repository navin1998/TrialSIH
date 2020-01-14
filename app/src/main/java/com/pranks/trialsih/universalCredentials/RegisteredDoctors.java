package com.pranks.trialsih.universalCredentials;

import java.util.ArrayList;
import java.util.Random;

public class RegisteredDoctors {

    private ArrayList<String> DOCTOR_NAME;
    private ArrayList<String> DOCTOR_REG_NUMBER;
    private ArrayList<String> DOCTOR_YEAR_OF_REG;
    private ArrayList<String> DOCTOR_MEDICAL_COUNCIL;

    public RegisteredDoctors() {

        DOCTOR_NAME = new ArrayList<>();
        DOCTOR_REG_NUMBER = new ArrayList<>();
        DOCTOR_YEAR_OF_REG = new ArrayList<>();
        DOCTOR_MEDICAL_COUNCIL = new ArrayList<>();

        DOCTOR_NAME.clear();
        DOCTOR_REG_NUMBER.clear();
        DOCTOR_YEAR_OF_REG.clear();
        DOCTOR_MEDICAL_COUNCIL.clear();

        addDoctorsName();
        addDoctorsRegNumber();
        addDoctorsYearOfReg();
        addDoctorsMedicalCouncil();

    }

    private void addDoctorsName()
    {
        DOCTOR_NAME.add("NAVIN BHARTI");
        DOCTOR_NAME.add("RAKESH SINGH RAJPUT");
        DOCTOR_NAME.add("KUNDAN KUMAR JHA");
        DOCTOR_NAME.add("PRABHKIRAT SINGH");
        DOCTOR_NAME.add("ANANT MALHOTRA");
        DOCTOR_NAME.add("SMRITI PAL");
    }

    private void addDoctorsRegNumber()
    {
        DOCTOR_REG_NUMBER.add("1806012");
        DOCTOR_REG_NUMBER.add("1806001");
        DOCTOR_REG_NUMBER.add("1806003");
        DOCTOR_REG_NUMBER.add("1806002");
        DOCTOR_REG_NUMBER.add("1806064");
        DOCTOR_REG_NUMBER.add("1806191");
    }

    private void addDoctorsYearOfReg()
    {
        DOCTOR_YEAR_OF_REG.add("2001");
        DOCTOR_YEAR_OF_REG.add("1998");
        DOCTOR_YEAR_OF_REG.add("2003");
        DOCTOR_YEAR_OF_REG.add("2000");
        DOCTOR_YEAR_OF_REG.add("2005");
        DOCTOR_YEAR_OF_REG.add("2007");
    }

    private void addDoctorsMedicalCouncil()
    {
        DOCTOR_MEDICAL_COUNCIL.add("BIHAR MEDICAL COUNCIL");
        DOCTOR_MEDICAL_COUNCIL.add("WEST BENGAL MEDICAL COUNCIL");
        DOCTOR_MEDICAL_COUNCIL.add("PUNJAB MEDICAL COUNCIL");
        DOCTOR_MEDICAL_COUNCIL.add("UTTAR PRADESH MEDICAL COUNCIL");
        DOCTOR_MEDICAL_COUNCIL.add("GUJARAT MEDICAL COUNCIL");
        DOCTOR_MEDICAL_COUNCIL.add("ORISSA MEDICAL COUNCIL");
    }

    private int generateRandomIndex()
    {
        int i = DOCTOR_NAME.size();
        Random random = new Random();
        return random.nextInt(i);
    }


//    public void addNewDoctor(String name, String regNumber)
//    {
//        DOCTOR_NAME.add(name.toUpperCase());
//        DOCTOR_REG_NUMBER.add(regNumber.toUpperCase());
//        DOCTOR_YEAR_OF_REG.add(DOCTOR_YEAR_OF_REG.get(generateRandomIndex()));
//        DOCTOR_MEDICAL_COUNCIL.add(DOCTOR_MEDICAL_COUNCIL.get(generateRandomIndex()));
//    }


    private int getIndex(String regNumber)
    {
        int index = -1;

        for (int i = 0; i < DOCTOR_REG_NUMBER.size(); i++)
        {
            if (regNumber.toLowerCase().equals(DOCTOR_REG_NUMBER.get(i).toLowerCase()))
            {
                index = i;
                break;
            }
        }

        return index;
    }

    public boolean checkForRegistered(String regNumber)
    {
        int i = getIndex(regNumber);

        return DOCTOR_REG_NUMBER.get(i).toLowerCase().equals(regNumber.toLowerCase());

    }


    public String getDoctorName(String regNumber)
    {
        return DOCTOR_NAME.get(getIndex(regNumber));
    }

    public String getDoctorRegNumber(String regNumber)
    {
        return DOCTOR_REG_NUMBER.get(getIndex(regNumber));
    }

    public String getDoctorYearOfReg(String regNumber)
    {
        return DOCTOR_YEAR_OF_REG.get(getIndex(regNumber));
    }

    public String getDoctorMedicalCouncil(String regNumber)
    {
        return DOCTOR_MEDICAL_COUNCIL.get(getIndex(regNumber));
    }

    public String getRegYearForNewDoctor()
    {
        return DOCTOR_YEAR_OF_REG.get(generateRandomIndex());
    }

    public String getMedicalCouncilForNewDoctor()
    {
        return DOCTOR_MEDICAL_COUNCIL.get(generateRandomIndex());
    }

}
