package com.example.oauth2.config;

import com.example.oauth2.SapoStore.service.iservice.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class EventScheduler {

    @Autowired
    private EventService eventService;


    @Scheduled(cron = "0 0 0 * * *")
    public void displayPendingEventsJob() {
        System.out.println("Running displayPendingEventsJob at 00:00...");
        eventService.displayPendingEvents();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void removeExpiredEventsJob() {
        System.out.println("Running removeExpiredEventsJob at 00:00...");
        eventService.removeExpiredEvents();
    }
}