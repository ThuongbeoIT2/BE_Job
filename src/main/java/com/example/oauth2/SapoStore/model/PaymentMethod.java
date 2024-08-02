package com.example.oauth2.SapoStore.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "paymentmethod")
@Table(name = "paymentmethod")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true)
    private String method;
    @Column(unique = true)
    private String slug;
    private String description;
    @OneToMany
    private Set<BillPayment> billPayments;
}
