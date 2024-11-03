package com.example.oauth2.SapoStore.service;

import com.example.oauth2.SapoStore.model.OrderDetail;
import com.example.oauth2.SapoStore.model.ProductOfStore;
import com.example.oauth2.SapoStore.model.TransactionVNPay;
import com.example.oauth2.SapoStore.payload.reponse.OrderDetailResponse;
import com.example.oauth2.SapoStore.repository.OrderDetailRepository;
import com.example.oauth2.SapoStore.repository.ProductOfStoreRepository;
import com.example.oauth2.SapoStore.repository.TransactionVNPayRepository;
import com.example.oauth2.config.OrderProcessor;
import com.example.oauth2.util.ProcessUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class OrderService {
    private final BlockingQueue<Long> orderQueue = new LinkedBlockingQueue<>();
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductOfStoreRepository productOfStoreRepository;
    @Autowired
    private TransactionVNPayRepository transactionVNPayRepository;

    @PostConstruct
    public void init() {
        Thread worker = new Thread(new OrderProcessor(orderQueue));
        worker.start();
    }

    public void addOrderToQueue(Long orderId) throws InterruptedException {
        orderQueue.put(orderId);
        System.out.println("Yêu cầu đặt hàng đã được đưa vào hàng đợi: " + orderId);
    }
    public OrderDetail orderToCart(ProductOfStore productOfStore, int quantity, String emailCustomer){
        //Lưu vào giỏ hàng. OrderID chưa được đẩy vào Queue
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setQuantity(quantity);
        orderDetail.setProductOfStore(productOfStore);
        orderDetail.setCreatedAt(ProcessUtils.getCurrentDay());
        orderDetail.setEmailCustomer(emailCustomer);
        orderDetail.setPrice_total((long) (productOfStore.getPriceO()*quantity*(100- productOfStore.getDiscount())/100));
        orderDetailRepository.save(orderDetail);
        return orderDetail;
    }
    public OrderDetailResponse initOrderPaymentInCart(OrderDetail orderDetail){
        ProductOfStore productOfStore= orderDetail.getProductOfStore();
        productOfStore.setQuantity(productOfStore.getQuantity()-orderDetail.getQuantity());
        productOfStoreRepository.save(productOfStore);
        return  OrderDetailResponse.cloneFromOrderDetail(orderDetail);
    }
    public OrderDetailResponse initOrderPayment(ProductOfStore productOfStore, int quantity, String emailCustomer){
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setQuantity(quantity);
        orderDetail.setProductOfStore(productOfStore);
        orderDetail.setCreatedAt(ProcessUtils.getCurrentDay());
        orderDetail.setEmailCustomer(emailCustomer);
        orderDetail.setPrice_total((long) (productOfStore.getPriceO()*quantity*(100- productOfStore.getDiscount())/100));
        orderDetailRepository.save(orderDetail);
        productOfStore.setQuantity(productOfStore.getQuantity()-quantity);
        productOfStoreRepository.save(productOfStore);
        return  OrderDetailResponse.cloneFromOrderDetail(orderDetail);
    }
    public TransactionVNPay intTransactionPaymentVNPay(OrderDetail orderDetail){
        TransactionVNPay transactionVNPay = new TransactionVNPay();
        transactionVNPay.setTransID(UUID.randomUUID());
        transactionVNPay.setIntTransactionTime(ProcessUtils.getMiliseconds());
        transactionVNPay.setOrderId(orderDetail.getId());
        transactionVNPay.setAmount(orderDetail.getPrice_total());
        transactionVNPay.setEmailCustomer(orderDetail.getEmailCustomer());
        transactionVNPayRepository.save(transactionVNPay);
        System.out.println("Khởi tạo giao dịch");
        return transactionVNPay;
    }


}
