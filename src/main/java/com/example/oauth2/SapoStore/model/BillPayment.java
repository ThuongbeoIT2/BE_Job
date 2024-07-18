package com.example.oauth2.SapoStore.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
@Getter
@Setter
public class BillPayment extends OrderDetail{
    private boolean isPayment;
    @ManyToOne
    @JoinColumn(name = "methodId")
    private PaymentMethod paymentMethod;
}
