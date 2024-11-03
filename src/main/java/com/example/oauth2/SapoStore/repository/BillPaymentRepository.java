package com.example.oauth2.SapoStore.repository;

import com.example.oauth2.SapoStore.model.BillPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillPayment,Long> {
    @Query("select o from billpayment o where o.orderID=:orderID")
    Optional<BillPayment> findByOrderID(String orderID);
}
