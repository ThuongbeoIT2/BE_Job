package com.example.oauth2.SapoStore.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StoreIntroduce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    private String link_facebook;
    private String link_instagram;
    private String link_zalo;
    private String hotline;
    private Date createdAt;
    private Date updatedAt;

}
