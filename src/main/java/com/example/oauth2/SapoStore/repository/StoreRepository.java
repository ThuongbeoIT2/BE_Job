package com.example.oauth2.SapoStore.repository;

import com.example.oauth2.SapoStore.model.Store;
import com.example.oauth2.SapoStore.payload.reponse.StoreResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store,Long> {
    Page<Store> findAll(Pageable pageable);
    @Query("select o from store o where o.storeCode=:storeCode")
    Optional<Store> findStoreByCode(String storeCode);
    @Query("select o from store o where o.storetype.slug=:slug")
    Page<Store> getStoreByType(String slug, Pageable pageable);
    @Query("select o from store o where o.email_manager=:email")
    Optional<Store> getStoreByEmailManager(String email);
    @Query("select o from store o where o.storeName like %:key%")
    Page<Store> searchStoreByKey(String key, Pageable pageable);
    @Query("select o from store o where o.status= false order by o.createdAt desc ")
    List<Store> getAllInActive();
}
