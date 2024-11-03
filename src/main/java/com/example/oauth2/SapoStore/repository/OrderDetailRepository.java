package com.example.oauth2.SapoStore.repository;

import com.example.oauth2.SapoStore.model.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {
    Page<OrderDetail> findAll(Pageable pageable);
    @Query("select o from orderdetail o where o.productOfStore.store.storeCode=:storeCode")
    Page<OrderDetail> getOrderDetailByStore(String storeCode,Pageable pageable);
    @Query("select o from orderdetail  o where o.productOfStore.id=:id and o.productOfStore.store.storeCode=:storeCode order by o.createdAt desc")
    Page<OrderDetail> getOrderDetailByProduct(long id,Pageable pageable,String storeCode);
    @Query("select o from orderdetail  o where o.emailCustomer=:email order by o.createdAt desc ")
    List<OrderDetail> getOrderDetailByUser(String email);
    @Query("select o from orderdetail o where o.productOfStore.id=:productOSID and o.emailCustomer=:emailCustomer")
    Optional<OrderDetail> getProductOSByUser(long productOSID, String emailCustomer);
}