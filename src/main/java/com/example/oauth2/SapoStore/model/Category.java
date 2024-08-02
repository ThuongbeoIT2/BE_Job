package com.example.oauth2.SapoStore.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "category")
@Table(name = "category")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "type_name")
    private String cateName;
    private String description;
    private String thumbnail;
    private String slug;
    @OneToMany(fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Product> products;
}
