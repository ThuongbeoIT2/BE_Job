package com.example.oauth2.SapoStore.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity(name = "orderstatus")
@Table(name = "orderstatus")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int statusId;
    @Column(unique = true)
    private String Status;
    @OneToMany
    private List<BillPayment> billPayments;
}
