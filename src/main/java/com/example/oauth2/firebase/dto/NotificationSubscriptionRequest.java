package com.example.oauth2.firebase.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class NotificationSubscriptionRequest {
    @NotBlank
    private String deviceToken;
    @NotBlank
    private String topicName;
}
