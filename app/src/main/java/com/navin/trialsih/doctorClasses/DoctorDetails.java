package com.navin.trialsih.doctorClasses;

public class DoctorDetails
{

    private String doctorName;
    private String doctorRegNumber;
    private String doctorPhone;
    private String doctorBookingPhone;
    private String doctorExperience;
    private String doctorQualifications;
    private String doctorClinicAddress;
    private String doctorSatisfiedPatientsNumber;
    private String doctorProfileCompleted;


    public DoctorDetails(String doctorName, String doctorRegNumber, String doctorPhone, String doctorBookingPhone, String doctorExperience, String doctorQualifications, String doctorClinicAddress, String doctorSatisfiedPatientsNumber, String doctorProfileCompleted) {
        this.doctorName = doctorName;
        this.doctorRegNumber = doctorRegNumber;
        this.doctorPhone = doctorPhone;
        this.doctorBookingPhone = doctorBookingPhone;
        this.doctorExperience = doctorExperience;
        this.doctorQualifications = doctorQualifications;
        this.doctorClinicAddress = doctorClinicAddress;
        this.doctorSatisfiedPatientsNumber = doctorSatisfiedPatientsNumber;
        this.doctorProfileCompleted = doctorProfileCompleted;
    }




    public DoctorDetails() {
    }

    public String getDoctorProfileCompleted() {
        return doctorProfileCompleted;
    }

    public void setDoctorProfileCompleted(String doctorProfileCompleted) {
        this.doctorProfileCompleted = doctorProfileCompleted;
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
