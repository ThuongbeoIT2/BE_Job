package com.example.oauth2.SapoStore.repository;

import com.example.oauth2.SapoStore.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus,Integer> {
    @Query("select o from orderstatus o where o.Status=:status")
    Optional<OrderStatus> findOrderStatusByString(String status);
}
