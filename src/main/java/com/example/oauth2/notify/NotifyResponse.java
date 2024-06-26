package com.example.oauth2.notify;


public class NotifyResponse {

    private int notiId;
    private String description;
    private boolean notiStatus;
    private boolean deletedNoti;


    public NotifyResponse(int notiId, String description, boolean notiStatus, boolean deletedNoti) {
        this.notiId = notiId;
        this.description = description;
        this.notiStatus = notiStatus;
        this.deletedNoti = deletedNoti;
    }

    // Getters and setters

    public int getNotiId() {
        return notiId;
    }

    public void setNotiId(int notiId) {
        this.notiId = notiId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isNotiStatus() {
        return notiStatus;
    }

    public void setNotiStatus(boolean notiStatus) {
        this.notiStatus = notiStatus;
    }

    public boolean isDeletedNoti() {
        return deletedNoti;
    }

    public void setDeletedNoti(boolean deletedNoti) {
        this.deletedNoti = deletedNoti;
    }

}
