package com.example.oauth2.SapoStore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "orderdetail")
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int quantity;
    private Date createdAt;
    @Column(nullable = false)
    private long price_total;
    @Column(nullable = false)
    private boolean VNPAY;
    @Column
    private String initOrderStatus;
    @Column(nullable = false)
    private String emailCustomer;
    @Column
    private  String isPayment;
    @Column
    private  boolean isDelete = false;
    @Column
    private boolean isFeedback = false;
    @ManyToOne
    @JoinColumn(name = "pos")
    private ProductOfStore productOfStore;
}
