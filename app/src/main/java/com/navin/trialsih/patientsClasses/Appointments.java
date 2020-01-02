package com.navin.trialsih.patientsClasses;

public class Appointments {

    private String appointmentFee;
    private String appointmentFeeStatus;
    private String appointmentDoctorName;
    private String appointmentDoctorPhone;

    public Appointments(String appointmentFee, String appointmentFeeStatus, String appointmentDoctorName, String appointmentDoctorPhone) {
        this.appointmentFee = appointmentFee;
        this.appointmentFeeStatus = appointmentFeeStatus;
        this.appointmentDoctorName = appointmentDoctorName;
        this.appointmentDoctorPhone = appointmentDoctorPhone;
    }

    public Appointments() {
    }

    public String getAppointmentDoctorName() {
        return appointmentDoctorName;
    }

    public void setAppointmentDoctorName(String appointmentDoctorName) {
        this.appointmentDoctorName = appointmentDoctorName;
    }

    public String getAppointmentDoctorPhone() {
        return appointmentDoctorPhone;
    }

    public void setAppointmentDoctorPhone(String appointmentDoctorPhone) {
        this.appointmentDoctorPhone = appointmentDoctorPhone;
    }

    public String getAppointmentFee() {
        return appointmentFee;
    }

    public void setAppointmentFee(String appointmentFee) {
        this.appointmentFee = appointmentFee;
    }

    public String getAppointmentFeeStatus() {
        return appointmentFeeStatus;
    }

    public void setAppointmentFeeStatus(String appointmentFeeStatus) {
        this.appointmentFeeStatus = appointmentFeeStatus;
    }
}
