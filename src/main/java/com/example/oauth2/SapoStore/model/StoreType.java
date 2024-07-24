package com.example.oauth2.SapoStore.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StoreType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "type_name")
    private String typeName;
    private String slug;
    private String description;
    private String thumbnail;

    @OneToMany
    @JsonManagedReference
    private Set<Store> stores;
}
