package com.example.oauth2.SapoStore.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

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
    private long transID;
    @ManyToOne
    @JoinColumn(name = "methodId")
    private PaymentMethod paymentMethod;
    @ManyToOne
    @JoinColumn(name = "oderStatus")
    private OrderStatus orderStatus;
}
