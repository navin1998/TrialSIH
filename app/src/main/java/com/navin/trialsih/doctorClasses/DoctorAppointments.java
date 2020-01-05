package com.navin.trialsih.doctorClasses;

public class DoctorAppointments {

    private String appointmentFee;
    private String appointmentFeeStatus;
    private String appointmentPatientName;
    private String appointmentPatientPhone;
    private String appointmentPatientUid;
    private String appointmentWayToConnect;

    public DoctorAppointments(String appointmentFee, String appointmentFeeStatus, String appointmentPatientName, String appointmentPatientPhone, String appointmentPatientUid, String appointmentWayToConnect) {
        this.appointmentFee = appointmentFee;
        this.appointmentFeeStatus = appointmentFeeStatus;
        this.appointmentPatientName = appointmentPatientName;
        this.appointmentPatientPhone = appointmentPatientPhone;
        this.appointmentPatientUid = appointmentPatientUid;
        this.appointmentWayToConnect = appointmentWayToConnect;
    }

    public DoctorAppointments() {
    }


    public String getAppointmentWayToConnect() {
        return appointmentWayToConnect;
    }

    public void setAppointmentWayToConnect(String appointmentWayToConnect) {
        this.appointmentWayToConnect = appointmentWayToConnect;
    }

    public String getAppointmentPatientUid() {
        return appointmentPatientUid;
    }

    public void setAppointmentPatientUid(String appointmentPatientUid) {
        this.appointmentPatientUid = appointmentPatientUid;
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
