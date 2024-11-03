package com.example.oauth2.SapoStore.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "comment")
@Table(name = "comment")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cmtId;
    private String description;
    private String avatar_user;
    private String email_user;
    private Date createdAt;
    private int evaluate;
    private String urlImage;
    @ManyToOne
    @JoinColumn(name = "productOfStoreId")
    private ProductOfStore productOfStore;
}
