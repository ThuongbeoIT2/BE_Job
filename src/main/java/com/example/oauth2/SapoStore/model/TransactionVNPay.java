package com.example.oauth2.SapoStore.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;
@Getter
@Setter
@Entity(name = "transaction")
@Table(name = "transaction")
public class TransactionVNPay {
    @Id
    private UUID transID;
    @Column(nullable = false)
    private long intTransactionTime;

    private long endTransactionTime;
    @Column(nullable = false)
    private String emailCustomer;

    private String resultCode;

    private String resultDesc;
    @Column(nullable = false)
    private long orderId;
    @Column(nullable = false)
    private long amount;
    private String customerAccountLink;
    private String shopAccountLink;

}
