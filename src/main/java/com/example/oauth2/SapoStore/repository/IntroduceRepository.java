package com.example.oauth2.SapoStore.repository;

import com.example.oauth2.SapoStore.model.StoreIntroduce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IntroduceRepository extends JpaRepository<StoreIntroduce,Integer> {
    @Query("select o.storeIntroduce from store o where o.storeCode=:storeCode")
    Optional<StoreIntroduce> getStoreIntroduceByStoreCOde(UUID storeCode);
}
