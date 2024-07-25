package com.example.oauth2.SapoStore.repository;

import com.example.oauth2.SapoStore.model.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
    Page<Product> findAll(Pageable pageable);
    @Query("select o from Product o where o.slug=:slug")
    Optional<Product> findProductBySlug(String slug);
    @Query("select o from Product o where o.category.slug=:slug")
    Page<Product> getProductByCategory(String slug, Pageable pageable);
    @Query("select o from Product o where o.proName like %:key%")
    Page<Product> searchProductByKey(String key, Pageable pageable);
}
