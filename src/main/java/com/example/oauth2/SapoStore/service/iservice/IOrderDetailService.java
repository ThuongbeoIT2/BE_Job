package com.example.oauth2.SapoStore.service.iservice;

import com.example.oauth2.SapoStore.model.OrderDetail;
import com.example.oauth2.SapoStore.model.ProductOfStore;
import com.example.oauth2.SapoStore.payload.reponse.OrderDetailResponse;
import com.example.oauth2.SapoStore.payload.request.OrderDetailRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IOrderDetailService {
    Page<OrderDetailResponse> findAll(Pageable pageable);
    Page<OrderDetailResponse> getOrderDetailByStore(String storeCode,Pageable pageable);
    Page<OrderDetailResponse> getOrderDetailByProduct(long id,Pageable pageable, String storeCode);
    List<OrderDetailResponse> getOrderDetailByUser();
    void Insert(OrderDetailRequest orderDetailRequest);
    void Update(int quantity,OrderDetail orderDetail);
    void delete(OrderDetail orderDetail);
    Optional<OrderDetail> getProductOSByUser(long productOSID, String emailCustomer);
}
