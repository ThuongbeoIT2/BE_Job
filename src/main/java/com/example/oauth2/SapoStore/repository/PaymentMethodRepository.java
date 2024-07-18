package com.example.oauth2.SapoStore.repository;

import com.example.oauth2.SapoStore.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    @Query("select o from PaymentMethod o where o.slug=:slug")
    PaymentMethod findBySlug(String slug);
}
