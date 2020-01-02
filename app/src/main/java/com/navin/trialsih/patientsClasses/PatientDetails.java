package com.navin.trialsih.patientsClasses;

public class PatientDetails
{

    private String patientName;
    private String patientAge;
    private String patientGender;
    private String patientBloodGroup;
    private String patientWeight;
    private String patientPhone;
    private String patientMail;

    public PatientDetails(String patientName, String patientAge, String patientGender, String patientBloodGroup, String patientWeight, String patientPhone, String patientMail) {
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.patientGender = patientGender;
        this.patientBloodGroup = patientBloodGroup;
        this.patientWeight = patientWeight;
        this.patientPhone = patientPhone;
        this.patientMail = patientMail;
    }

    public PatientDetails() {
    }


    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getPatientBloodGroup() {
        return patientBloodGroup;
    }

    public void setPatientBloodGroup(String patientBloodGroup) {
        this.patientBloodGroup = patientBloodGroup;
    }

    public String getPatientWeight() {
        return patientWeight;
    }

    public void setPatientWeight(String patientWeight) {
        this.patientWeight = patientWeight;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getPatientMail() {
        return patientMail;
    }

    public void setPatientMail(String patientMail) {
        this.patientMail = patientMail;
    }
}
