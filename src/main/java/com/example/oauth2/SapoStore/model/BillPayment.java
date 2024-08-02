package com.example.oauth2.SapoStore.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "billpayment")
@Table(name = "billpayment")
@Getter
@Setter
public class BillPayment{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billID;
    private long orderID;
    private boolean isPayment;
    @ManyToOne
    @JoinColumn(name = "methodId")
    private PaymentMethod paymentMethod;
    @ManyToOne
    @JoinColumn(name = "oderStatus")
    private OrderStatus orderStatus;
}
