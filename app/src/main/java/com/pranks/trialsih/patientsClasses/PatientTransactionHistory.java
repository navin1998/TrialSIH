package com.pranks.trialsih.patientsClasses;

public class PatientTransactionHistory {

    private String doctorName;
    private String doctorImage;
    private String doctorFee;
    private String doctorFeeStatus;


    public PatientTransactionHistory(String doctorName, String doctorImage, String doctorFee, String doctorFeeStatus) {
        this.doctorName = doctorName;
        this.doctorImage = doctorImage;
        this.doctorFee = doctorFee;
        this.doctorFeeStatus = doctorFeeStatus;
    }

    public PatientTransactionHistory() {
    }


    public String getDoctorFeeStatus() {
        return doctorFeeStatus;
    }

    public void setDoctorFeeStatus(String doctorFeeStatus) {
        this.doctorFeeStatus = doctorFeeStatus;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorImage() {
        return doctorImage;
    }

    public void setDoctorImage(String doctorImage) {
        this.doctorImage = doctorImage;
    }

    public String getDoctorFee() {
        return doctorFee;
    }

    public void setDoctorFee(String doctorFee) {
        this.doctorFee = doctorFee;
    }
}
