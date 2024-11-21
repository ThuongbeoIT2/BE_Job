package com.example.oauth2.SapoStore.repository;


import com.example.oauth2.SapoStore.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByIsDisplayFalseAndStartDateBefore(Date now);
    List<Event> findByIsDisplayTrueAndEndDateBefore(Date now);
}