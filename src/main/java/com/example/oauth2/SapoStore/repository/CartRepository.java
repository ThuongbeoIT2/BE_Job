//package com.example.oauth2.SapoStore.repository;
//
//import com.example.oauth2.SapoStore.model.Cart;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//
//public interface CartRepository extends JpaRepository<Cart,Long> {
//    @Query("select o from cart o where o.user.email=:email")
//    Cart findCartByUser(String email);
//}
