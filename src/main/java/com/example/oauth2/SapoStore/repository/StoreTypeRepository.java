package com.example.oauth2.SapoStore.repository;



import com.example.oauth2.SapoStore.model.StoreType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreTypeRepository extends JpaRepository<StoreType,Integer> {
    @Query("select o from StoreType o where o.slug=:slug")
    StoreType findBySlug(String slug);
}
