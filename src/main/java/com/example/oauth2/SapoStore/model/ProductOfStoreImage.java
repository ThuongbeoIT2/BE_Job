package com.example.oauth2.SapoStore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "productofstoreimage")
@Table(name = "productofstoreimage")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ProductOfStoreImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String description;
    private String urlImage;
    private boolean status;
    @ManyToOne
    @JoinColumn(name = "product_img_id")
    private ProductOfStore productOfStore;
}
