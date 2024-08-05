package com.example.oauth2.firebase.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class AllDevicesNotificationRequest extends NotificationRequest {
    List<String> deviceTokenList = new ArrayList<>();
}
