package com.example.oauth2.SapoStore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOfStore {
    @Id
    private Long id;
    private Long priceI;
    private Long priceO;
    private double discount;
    private String CU="VND";
    private long view;
    private boolean status;
    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "storeId")
    private Store store;
    @OneToMany
    private Set<ProductOfStoreImage> productOfStoreImages;
    @OneToMany
    private Set<Comment>comments;
}
