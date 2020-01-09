package com.navin.trialsih.patientsClasses;

public class PatientHistory {

    private String appointmentDoctorName;
    private String appointmentDoctorRegNumber;
    private String appointmentDoctorPhone;
    private String appointmentPositionInQueue;
    private String appointmentFee;
    private String appointmentFeeStatus;
    private String appointmentWayToConnect;
    private String appointmentChatStarted;


    public PatientHistory(String appointmentDoctorName, String appointmentDoctorRegNumber, String appointmentDoctorPhone, String appointmentPositionInQueue, String appointmentFee, String appointmentFeeStatus, String appointmentWayToConnect, String appointmentChatStarted) {
        this.appointmentDoctorName = appointmentDoctorName;
        this.appointmentDoctorRegNumber = appointmentDoctorRegNumber;
        this.appointmentDoctorPhone = appointmentDoctorPhone;
        this.appointmentPositionInQueue = appointmentPositionInQueue;
        this.appointmentFee = appointmentFee;
        this.appointmentFeeStatus = appointmentFeeStatus;
        this.appointmentWayToConnect = appointmentWayToConnect;
        this.appointmentChatStarted = appointmentChatStarted;
    }

    public PatientHistory() {
    }


    public String getAppointmentChatStarted() {
        return appointmentChatStarted;
    }

    public void setAppointmentChatStarted(String appointmentChatStarted) {
        this.appointmentChatStarted = appointmentChatStarted;
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
