package com.example.oauth2.SapoStore.controller;

import com.example.oauth2.SapoStore.model.Product;
import com.example.oauth2.SapoStore.model.Store;
import com.example.oauth2.SapoStore.repository.*;
import com.example.oauth2.model.User;
import com.example.oauth2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
