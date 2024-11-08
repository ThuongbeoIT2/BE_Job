package com.example.oauth2.SapoStore.repository;

import com.example.oauth2.SapoStore.model.TransactionVNPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionVNPayRepository extends JpaRepository<TransactionVNPay, Long> {
    @Query("select o from transaction o where o.orderId=:orderID order by o.intTransactionTime desc ")
    List<TransactionVNPay> findByOrderID(long orderID);
}
