package com.example.oauth2.SapoStore.model;

import lombok.*;

import javax.persistence.*;

@Entity(name = "product")
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int proId;
    @Column(nullable = false)
    private String proName;
    @Column(unique = true)
    private String slug;
    @Column(nullable = false)
    private String thumbnail;
    private String description;
    private boolean isHotSale = true;
    @ManyToOne
    @JoinColumn(name = "cateId",foreignKey = @ForeignKey(name = "fk_category_product"))
    private Category category;


}
