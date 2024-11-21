package com.example.oauth2.SapoStore.service;


import com.example.oauth2.SapoStore.model.Event;
import com.example.oauth2.SapoStore.payload.request.EventRequest;
import com.example.oauth2.SapoStore.repository.EventRepository;
import com.example.oauth2.SapoStore.service.iservice.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public Event createEvent(EventRequest eventRequest) {
        Event event = new Event();
        event.setTitle(eventRequest.getTitle());
        event.setDescription(eventRequest.getDescription());
        event.setBanner(eventRequest.getBanner());
        event.setStartDate(eventRequest.getStartDate());
        event.setEndDate(eventRequest.getEndDate());
        event.setCreatedDate(new Date());
        event.setDisplay(false); // Mặc định chưa hiển thị
        return eventRepository.save(event);
    }

    @Override
    public Event getEventById(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event updateEvent(int eventId, EventRequest eventRequest) {
        Event existingEvent = getEventById(eventId);
        existingEvent.setTitle(eventRequest.getTitle());
        existingEvent.setDescription(eventRequest.getDescription());
        existingEvent.setBanner(eventRequest.getBanner());
        existingEvent.setStartDate(eventRequest.getStartDate());
        existingEvent.setEndDate(eventRequest.getEndDate());
        existingEvent.setUpdatedDate(new Date());
        return eventRepository.save(existingEvent);
    }

    @Override
    public void deleteEvent(int eventId) {
        eventRepository.deleteById(eventId);
    }

    @Override
    public void displayPendingEvents() {
        List<Event> pendingEvents = eventRepository.findByIsDisplayFalseAndStartDateBefore(new Date());
        for (Event event : pendingEvents) {
            event.setDisplay(true);
            eventRepository.save(event);
            System.out.println("Event displayed: " + event.getTitle());
        }
    }

    @Override
    public void removeExpiredEvents() {
        List<Event> expiredEvents = eventRepository.findByIsDisplayTrueAndEndDateBefore(new Date());
        for (Event event : expiredEvents) {
            eventRepository.delete(event);
            System.out.println("Event removed: " + event.getTitle());
        }
    }
}