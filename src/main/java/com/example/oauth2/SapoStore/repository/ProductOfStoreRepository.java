package com.example.oauth2.SapoStore.repository;

import com.example.oauth2.SapoStore.model.ProductOfStore;
import com.example.oauth2.SapoStore.page.SapoPageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductOfStoreRepository extends JpaRepository<ProductOfStore,Long> {
    Page<ProductOfStore> findAll(Pageable pageable);
    @Query("select o from productofstore o where o.store.storeCode=:storeCode")
    Page<ProductOfStore> findProductByStore(String storeCode, Pageable pageable);
    @Query("select o from productofstore o where o.store.storeCode=:storeCode and o.product.category.slug=:slug")
    Page<ProductOfStore> getProductByCategoryandStore(String slug,String storeCode, Pageable pageable);
    @Query("select o from productofstore o where o.product.proName like %:key% and o.store.storeCode=:storeCode")
    Page<ProductOfStore> searchProductOfStoreByKey(String key,String storeCode, Pageable pageable);
    @Query("select o from productofstore o where o.store.storeCode=:storeCode and o.product.slug=:slug")
    Optional<ProductOfStore> isExistProductOfStore(String slug, String storeCode);
    @Query("select o from productofstore o where o.product.slug=:slug")
    Page<ProductOfStore> getProductOfStoreBySlug(String slug, SapoPageRequest sapoPageRequest);
}
