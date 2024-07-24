package com.example.oauth2.SapoStore.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID storeCode;
    @Column(nullable = false)
    private String storeName;
    @Column(nullable = false)
    private String address;
    @Column(name = "email_manager")
    private String email_manager;
    @Column(nullable = false)
    @Digits(integer = 10, fraction = 0)
    @Size(min = 10, max = 10)
    private String phoneNumber;
    private String password;
    private String thumbnail;
    private String description;
    private boolean status;
    private double evaluate;
    private Date createdAt;
    private Date updatedAt;
    private String eKyc;
    private long view;
    @ManyToOne
    @JoinColumn(name = "store_type",foreignKey = @ForeignKey(name = "fk_store_storeType"))
    @JsonBackReference
    private StoreType storetype;
    @OneToOne
    @JoinColumn(name = "introduceId",foreignKey = @ForeignKey(name = "fk_store_introduce"))
    private StoreIntroduce storeIntroduce;
}
