package com.example.oauth2.SapoStore.service.iservice;


import com.example.oauth2.SapoStore.model.Event;
import com.example.oauth2.SapoStore.payload.request.EventRequest;

import java.util.List;

public interface EventService {
    Event createEvent(EventRequest eventRequest);
    Event getEventById(int eventId);
    List<Event> getAllEvents();
    Event updateEvent(int eventId, EventRequest eventRequest);
    void deleteEvent(int eventId);
    void displayPendingEvents();
    void removeExpiredEvents();
}