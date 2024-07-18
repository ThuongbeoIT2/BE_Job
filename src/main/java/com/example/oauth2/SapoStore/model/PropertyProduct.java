package com.example.oauth2.SapoStore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PropertyProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String color;
    private String size;
    private String description;
    @ManyToOne
    @JoinColumn(name = "productOdStoreId")
    private ProductOfStore productOfStore;
}
