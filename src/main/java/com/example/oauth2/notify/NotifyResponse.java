package com.example.oauth2.notify;

import lombok.Data;

@Data
public class NotifyResponse {

    private int notiId;
    private String description;
    private boolean notiStatus;
    private boolean deletedNoti;



}
