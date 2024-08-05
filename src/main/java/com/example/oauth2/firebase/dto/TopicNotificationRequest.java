package com.example.oauth2.firebase.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class TopicNotificationRequest extends NotificationRequest {
    @NotBlank
    private String topicName;
}
