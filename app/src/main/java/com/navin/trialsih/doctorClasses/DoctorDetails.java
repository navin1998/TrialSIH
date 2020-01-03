package com.navin.trialsih.doctorClasses;

public class DoctorDetails
{

    private String doctorName;
    private String doctorRegNumber;
    private String doctorGender;
    private String doctorPhone;
    private String doctorBookingPhone;
    private String doctorExperience;
    private String doctorQualifications;
    private String doctorClinicAddress;
    private String doctorSatisfiedPatientsNumber;
    private String doctorMedicalCouncil;
    private String doctorYearOfReg;
    private String doctorPhotoUri;
    private String doctorMail;


    public DoctorDetails(String doctorName, String doctorRegNumber, String doctorGender, String doctorPhone, String doctorBookingPhone, String doctorExperience, String doctorQualifications, String doctorClinicAddress, String doctorSatisfiedPatientsNumber, String doctorMedicalCouncil, String doctorYearOfReg, String doctorPhotoUri, String doctorMail) {
        this.doctorName = doctorName;
        this.doctorRegNumber = doctorRegNumber;
        this.doctorGender = doctorGender;
        this.doctorPhone = doctorPhone;
        this.doctorBookingPhone = doctorBookingPhone;
        this.doctorExperience = doctorExperience;
        this.doctorQualifications = doctorQualifications;
        this.doctorClinicAddress = doctorClinicAddress;
        this.doctorSatisfiedPatientsNumber = doctorSatisfiedPatientsNumber;
        this.doctorMedicalCouncil = doctorMedicalCouncil;
        this.doctorYearOfReg = doctorYearOfReg;
        this.doctorPhotoUri = doctorPhotoUri;
        this.doctorMail = doctorMail;
    }

    public DoctorDetails() {
    }


    public String getDoctorGender() {
        return doctorGender;
    }

    public void setDoctorGender(String doctorGender) {
        this.doctorGender = doctorGender;
    }

    public String getDoctorMail() {
        return doctorMail;
    }

    public void setDoctorMail(String doctorMail) {
        this.doctorMail = doctorMail;
    }

    public String getDoctorPhotoUri() {
        return doctorPhotoUri;
    }

    public void setDoctorPhotoUri(String doctorPhotoUri) {
        this.doctorPhotoUri = doctorPhotoUri;
    }

    public String getDoctorMedicalCouncil() {
        return doctorMedicalCouncil;
    }

    public void setDoctorMedicalCouncil(String doctorMedicalCouncil) {
        this.doctorMedicalCouncil = doctorMedicalCouncil;
    }

    public String getDoctorYearOfReg() {
        return doctorYearOfReg;
    }

    public void setDoctorYearOfReg(String doctorYearOfReg) {
        this.doctorYearOfReg = doctorYearOfReg;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorRegNumber() {
        return doctorRegNumber;
    }

    public void setDoctorRegNumber(String doctorRegNumber) {
        this.doctorRegNumber = doctorRegNumber;
    }

    public String getDoctorPhone() {
        return doctorPhone;
    }

    public void setDoctorPhone(String doctorPhone) {
        this.doctorPhone = doctorPhone;
    }

    public String getDoctorBookingPhone() {
        return doctorBookingPhone;
    }

    public void setDoctorBookingPhone(String doctorBookingPhone) {
        this.doctorBookingPhone = doctorBookingPhone;
    }

    public String getDoctorExperience() {
        return doctorExperience;
    }

    public void setDoctorExperience(String doctorExperience) {
        this.doctorExperience = doctorExperience;
    }

    public String getDoctorQualifications() {
        return doctorQualifications;
    }

    public void setDoctorQualifications(String doctorQualifications) {
        this.doctorQualifications = doctorQualifications;
    }

    public String getDoctorClinicAddress() {
        return doctorClinicAddress;
    }

    public void setDoctorClinicAddress(String doctorClinicAddress) {
        this.doctorClinicAddress = doctorClinicAddress;
    }

    public String getDoctorSatisfiedPatientsNumber() {
        return doctorSatisfiedPatientsNumber;
    }

    public void setDoctorSatisfiedPatientsNumber(String doctorSatisfiedPatientsNumber) {
        this.doctorSatisfiedPatientsNumber = doctorSatisfiedPatientsNumber;
    }
}
