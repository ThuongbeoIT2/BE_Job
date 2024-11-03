package com.example.oauth2.SapoStore.repository;

import com.example.oauth2.SapoStore.model.ProductOfStoreImage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOfStoreImageRepository extends JpaRepository<ProductOfStoreImage,Long> {
    @Query("select o from productofstoreimage o where o.productOfStore.id=:id")
    List<ProductOfStoreImage> getAllProductOfStoreImageByProduct(long id);
    @Query("select o from productofstoreimage o where o.productOfStore.id=:id ")
    List<ProductOfStoreImage> getProductOfStoreImageByProduct(long id);
}
