package com.example.oauth2.SapoStore.service;

import com.example.oauth2.SapoStore.model.Cart;
import com.example.oauth2.SapoStore.model.OrderDetail;
import com.example.oauth2.SapoStore.payload.reponse.OrderDetailResponse;
import com.example.oauth2.SapoStore.payload.request.OrderDetailRequest;
import com.example.oauth2.SapoStore.repository.CartRepository;
import com.example.oauth2.SapoStore.repository.OrderDetailRepository;
import com.example.oauth2.SapoStore.service.iservice.IOrderDetailService;
import com.example.oauth2.util.ProcessUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderDetailService implements IOrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartRepository cartRepository;
    @Override
    public Page<OrderDetailResponse> findAll(Pageable pageable) {
        return orderDetailRepository.findAll(pageable).map(OrderDetailResponse::cloneFromOrderDetail);
    }

    @Override
    public Page<OrderDetailResponse> getOrderDetailByStore(String storeCode,Pageable pageable) {
        return orderDetailRepository.getOrderDetailByStore(storeCode,pageable).map(OrderDetailResponse::cloneFromOrderDetail);
    }

    @Override
    public Page<OrderDetailResponse> getOrderDetailByProduct(long id, Pageable pageable,String storeCode) {
        return orderDetailRepository.getOrderDetailByProduct(id, pageable,storeCode).map(OrderDetailResponse::cloneFromOrderDetail);
    }

    @Override
    public List<OrderDetailResponse> getOrderDetailByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return orderDetailRepository.getOrderDetailByUser(email).stream().map(OrderDetailResponse::cloneFromOrderDetail).collect(Collectors.toList());
    }

    @Override
    public void Insert(OrderDetailRequest orderDetailRequest) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setQuantity(orderDetailRequest.getQuantity());
        orderDetail.setProductOfStore(orderDetailRequest.getProductOfStore());
        orderDetail.setPrice_total(orderDetailRequest.getQuantity()*orderDetailRequest.getProductOfStore().getPriceO());
        orderDetail.setCreatedAt(ProcessUtils.getCurrentDay());
        orderDetail.setEmailCustomer(SecurityContextHolder.getContext().getAuthentication().getName());
        orderDetailRepository.save(orderDetail);
    }

    private Cart findCartByUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return cartRepository.findCartByUser(email);
    }

    @Override
    public void Update(int quantity, OrderDetail orderDetail) {
        orderDetail.setQuantity(quantity);
        orderDetailRepository.save(orderDetail);
    }

    @Override
    public void delete(OrderDetail orderDetail) {
        orderDetailRepository.delete(orderDetail);
    }

    @Override
    public Optional<OrderDetail> getProductOSByUser(long productOSID, String emailCustomer) {
        return orderDetailRepository.getProductOSByUser(productOSID,emailCustomer);
    }


}
