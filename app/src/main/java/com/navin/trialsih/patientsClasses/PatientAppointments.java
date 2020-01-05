package com.navin.trialsih.patientsClasses;

public class PatientAppointments {

    private String appointmentDoctorName;
    private String appointmentDoctorRegNumber;
    private String appointmentDoctorPhone;
    private String appointmentPositionInQueue;
    private String appointmentFee;
    private String appointmentFeeStatus;
    private String appointmentWayToConnect;


    public PatientAppointments(String appointmentDoctorName, String appointmentDoctorRegNumber, String appointmentDoctorPhone, String appointmentPositionInQueue, String appointmentFee, String appointmentFeeStatus, String appointmentWayToConnect) {
        this.appointmentDoctorName = appointmentDoctorName;
        this.appointmentDoctorRegNumber = appointmentDoctorRegNumber;
        this.appointmentDoctorPhone = appointmentDoctorPhone;
        this.appointmentPositionInQueue = appointmentPositionInQueue;
        this.appointmentFee = appointmentFee;
        this.appointmentFeeStatus = appointmentFeeStatus;
        this.appointmentWayToConnect = appointmentWayToConnect;
    }


    public PatientAppointments() {
    }


    public String getAppointmentWayToConnect() {
        return appointmentWayToConnect;
    }

    public void setAppointmentWayToConnect(String appointmentWayToConnect) {
        this.appointmentWayToConnect = appointmentWayToConnect;
    }

    public String getAppointmentDoctorPhone() {
        return appointmentDoctorPhone;
    }

    public void setAppointmentDoctorPhone(String appointmentDoctorPhone) {
        this.appointmentDoctorPhone = appointmentDoctorPhone;
    }

    public String getAppointmentDoctorName() {
        return appointmentDoctorName;
    }

    public void setAppointmentDoctorName(String appointmentDoctorName) {
        this.appointmentDoctorName = appointmentDoctorName;
    }

    public String getAppointmentDoctorRegNumber() {
        return appointmentDoctorRegNumber;
    }

    public void setAppointmentDoctorRegNumber(String appointmentDoctorRegNumber) {
        this.appointmentDoctorRegNumber = appointmentDoctorRegNumber;
    }

    public String getAppointmentPositionInQueue() {
        return appointmentPositionInQueue;
    }

    public void setAppointmentPositionInQueue(String appointmentPositionInQueue) {
        this.appointmentPositionInQueue = appointmentPositionInQueue;
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
