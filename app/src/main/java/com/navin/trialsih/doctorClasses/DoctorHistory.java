package com.navin.trialsih.doctorClasses;

public class DoctorHistory {

    private String appointmentFee;
    private String appointmentFeeStatus;
    private String appointmentPatientName;
    private String appointmentPatientPhone;
    private String appointmentPatientUid;
    private String appointmentWayToConnect;
    private String appointmentPositionInQueue;
    private String appointmentChatStarted;

    public DoctorHistory(String appointmentFee, String appointmentFeeStatus, String appointmentPatientName, String appointmentPatientPhone, String appointmentPatientUid, String appointmentWayToConnect, String appointmentPositionInQueue, String appointmentChatStarted) {
        this.appointmentFee = appointmentFee;
        this.appointmentFeeStatus = appointmentFeeStatus;
        this.appointmentPatientName = appointmentPatientName;
        this.appointmentPatientPhone = appointmentPatientPhone;
        this.appointmentPatientUid = appointmentPatientUid;
        this.appointmentWayToConnect = appointmentWayToConnect;
        this.appointmentPositionInQueue = appointmentPositionInQueue;
        this.appointmentChatStarted = appointmentChatStarted;
    }

    public DoctorHistory() {
    }

    public String getAppointmentChatStarted() {
        return appointmentChatStarted;
    }

    public void setAppointmentChatStarted(String appointmentChatStarted) {
        this.appointmentChatStarted = appointmentChatStarted;
    }

    public String getAppointmentPositionInQueue() {
        return appointmentPositionInQueue;
    }

    public void setAppointmentPositionInQueue(String appointmentPositionInQueue) {
        this.appointmentPositionInQueue = appointmentPositionInQueue;
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
