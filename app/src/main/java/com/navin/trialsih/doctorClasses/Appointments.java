package com.navin.trialsih.doctorClasses;

public class Appointments {

    private String appointmentFee;
    private String appointmentFeeStatus;
    private String appointmentPatientName;
    private String appointmentPatientPhone;

    public Appointments(String appointmentFee, String appointmentFeeStatus, String appointmentPatientName, String appointmentPatientPhone) {
        this.appointmentFee = appointmentFee;
        this.appointmentFeeStatus = appointmentFeeStatus;
        this.appointmentPatientName = appointmentPatientName;
        this.appointmentPatientPhone = appointmentPatientPhone;
    }


    public Appointments() {
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

    public String getAppointmentPatientName() {
        return appointmentPatientName;
    }

    public void setAppointmentPatientName(String appointmentPatientName) {
        this.appointmentPatientName = appointmentPatientName;
    }

    public String getAppointmentPatientPhone() {
        return appointmentPatientPhone;
    }

    public void setAppointmentPatientPhone(String appointmentPatientPhone) {
        this.appointmentPatientPhone = appointmentPatientPhone;
    }
}
