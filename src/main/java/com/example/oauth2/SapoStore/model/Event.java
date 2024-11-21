package com.example.oauth2.SapoStore.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eventId;
    @Column(unique = true)
    private String title;
    @Column
    private String description;
    @Column(nullable = false)
    private String banner;
    @Column
    private Date startDate;
    @Column
    private Date endDate;
    @Column
    private Date createdDate;
    @Column
    private Date updatedDate;
    @Column
    private boolean isDisplay;

}
