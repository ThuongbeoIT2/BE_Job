package com.example.oauth2.notify;

import org.springframework.context.ApplicationEvent;

public class NewNotificationEvent extends ApplicationEvent {

    private final Notify notification;

    public NewNotificationEvent(Object source, Notify notification) {
        super(source);
        this.notification = notification;
    }

    public Notify getNotification() {
        return notification;
    }
}
