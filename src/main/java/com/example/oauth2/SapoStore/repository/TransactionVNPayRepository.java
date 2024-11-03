package com.example.oauth2.SapoStore.repository;

import com.example.oauth2.SapoStore.model.TransactionVNPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionVNPayRepository extends JpaRepository<TransactionVNPay, UUID> {
    @Query("select o from transaction o where o.orderId=:orderID")
    Optional<TransactionVNPay> findByOrderID(String orderID);
}
