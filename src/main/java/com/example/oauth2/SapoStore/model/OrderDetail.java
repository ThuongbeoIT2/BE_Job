package com.example.oauth2.SapoStore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
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
    private long pricetotal;
    @ManyToOne
    @JoinColumn(name = "order_detail_id")
    private Cart cart;
}
