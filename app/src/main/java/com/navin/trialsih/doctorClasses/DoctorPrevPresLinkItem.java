package com.navin.trialsih.doctorClasses;

public class DoctorPrevPresLinkItem {

    private String FileUrl;
    private String Date;

    public DoctorPrevPresLinkItem(String fileUrl, String date) {
        FileUrl = fileUrl;
        Date = date;
    }


    public DoctorPrevPresLinkItem() {
    }

    public String getFileUrl() {
        return FileUrl;
    }

    public void setFileUrl(String fileUrl) {
        FileUrl = fileUrl;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
