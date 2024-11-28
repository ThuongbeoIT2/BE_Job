package com.example.oauth2.SapoStore.controller;

import com.example.oauth2.SapoStore.exception.NotFoundObjectException;
import com.example.oauth2.SapoStore.model.Product;
import com.example.oauth2.SapoStore.model.Store;
import com.example.oauth2.SapoStore.modelStatistical.AdminStatistical;
import com.example.oauth2.SapoStore.modelStatistical.StoreStatistical;
import com.example.oauth2.SapoStore.repository.*;
import com.example.oauth2.globalContanst.GlobalConstant;
import com.example.oauth2.model.User;
import com.example.oauth2.payload.ApiResponse;
import com.example.oauth2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

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
    private CommentRepository commentRepository;
    @Autowired
    private BillPaymentRepository billPaymentRepository;

    @GetMapping(value = "/admin/statistical")
    ResponseEntity<AdminStatistical> adminStatistical(){
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
        return ResponseEntity.status(HttpStatus.OK).body(adminStatistical);
    }
    @PostMapping (value = "/store/statistical")
    ResponseEntity<StoreStatistical> storeStatistical(@RequestParam String storeCode){
        Optional<Store> store = storeRepository.findStoreByCode(storeCode);
        if (store.isEmpty() || !getEmailManager().equalsIgnoreCase(store.get().getEmail_manager())){
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.PRODUCT, GlobalConstant.ErrorCode.MER404);
        }
        int productOS = productOfStoreRepository.findSizeProductByStore(storeCode).size();
        long revenue = orderDetailRepository.getRevenueStore(storeCode);
        int orderDetail = orderDetailRepository.getOrderOfStore(storeCode).size();
        int comment = commentRepository.findFeedBackOfStore(storeCode);
        StoreStatistical storeStatistical = new StoreStatistical();
        storeStatistical.setProductOS(productOS);
        storeStatistical.setRevenue(revenue);
        storeStatistical.setOrderDetail(orderDetail);
        storeStatistical.setComment(comment);
        return ResponseEntity.status(HttpStatus.OK).body(storeStatistical);
    }
    String getEmailManager(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
