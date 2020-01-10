package com.pranks.trialsih.doctorClasses;

public class DoctorPrevPresLinkItem {

    private String fileUrl;
    private String date;

    public DoctorPrevPresLinkItem(String fileUrl, String date) {
        this.fileUrl = fileUrl;
        this.date = date;
    }

    public DoctorPrevPresLinkItem() {
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
