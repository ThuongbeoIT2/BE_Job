package com.example.oauth2.SapoStore.controller;

import com.example.oauth2.SapoStore.model.Product;
import com.example.oauth2.SapoStore.model.Store;
import com.example.oauth2.SapoStore.modelStatistical.AdminStatistical;
import com.example.oauth2.SapoStore.repository.*;
import com.example.oauth2.model.User;
import com.example.oauth2.payload.ApiResponse;
import com.example.oauth2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class StatisticalController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductOfStoreRepository productOfStoreRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private TransactionVNPayRepository transactionVNPayRepository;
    @Autowired
    private BillPaymentRepository billPaymentRepository;

    @GetMapping(value = "/admin/statistical")
    ResponseEntity<ApiResponse> adminStatistical(){
        int user = userRepository.findAll().size();
        int store = storeRepository.findAll().size();
        int product = productRepository.findAll().size();
        int productOS = productOfStoreRepository.findAll().size();
        long revenue = orderDetailRepository.getRevenue();
        int orderDetail = orderDetailRepository.findAll().size();
        AdminStatistical adminStatistical = new AdminStatistical();
        adminStatistical.setUser(user);
        adminStatistical.setStore(store);
        adminStatistical.setProduct(product);
        adminStatistical.setProductOfStore(productOS);
        adminStatistical.setOrderDetail(orderDetail);
        adminStatistical.setRevenue(revenue);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK","OK",adminStatistical));
    }
//    @GetMapping(value = "/manager/statistical")
//    ResponseEntity<ApiResponse> adminStatistical(String storeCode){
//        int productOS = productOfStoreRepository.findAll().size();
//        long revenue = orderDetailRepository.getRevenueWithStore(storeCode);
//        int orderDetail = orderDetailRepository.findAll().size();
//        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK","OK",""));
//    }

}
