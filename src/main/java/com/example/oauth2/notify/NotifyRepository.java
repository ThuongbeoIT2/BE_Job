package com.example.oauth2.notify;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotifyRepository extends JpaRepository<Notify,Integer> {
    @Query("select o from notify o where o.user.email=:email and o.deletedNoti=false ")
    List<Notify> findNotifiesByEmail(String email);
}
