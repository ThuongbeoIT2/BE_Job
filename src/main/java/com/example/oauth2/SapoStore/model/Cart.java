package com.example.oauth2.SapoStore.model;

import com.example.oauth2.model.User;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "cart")
@Table(name = "cart")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private int totalProduct;
    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

}
