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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class OrderService {

    private final BlockingQueue<Long> orderQueue = new LinkedBlockingQueue<>();
    private final OrderDetailRepository orderDetailRepository;
    private final ProductOfStoreRepository productOfStoreRepository;
    private final TransactionVNPayRepository transactionVNPayRepository;

    @Autowired
    public OrderService(OrderDetailRepository orderDetailRepository,
                        ProductOfStoreRepository productOfStoreRepository,
                        TransactionVNPayRepository transactionVNPayRepository) {
        this.orderDetailRepository = orderDetailRepository;
        this.productOfStoreRepository = productOfStoreRepository;
        this.transactionVNPayRepository = transactionVNPayRepository;
    }

    @PostConstruct
    public void init() {
        Thread worker = new Thread(new OrderProcessor(orderQueue, orderDetailRepository, productOfStoreRepository));
        worker.start();
    }

    public void addOrderToQueue(Long orderId) throws InterruptedException {
        orderQueue.put(orderId);
        System.out.println("Order request has been added to the queue: " + orderId);
    }

    public OrderDetail orderToCart(ProductOfStore productOfStore, int quantity, String emailCustomer) {
        // Save the order to cart without pushing it to the queue
        OrderDetail orderDetail = createOrderDetail(productOfStore, quantity, emailCustomer);
        orderDetail.setInitOrderStatus("INIT");
        orderDetailRepository.save(orderDetail);
        return orderDetail;
    }

    @Transactional
    public OrderDetailResponse initOrderPaymentInCart(OrderDetail orderDetail) {
        // Ensure stock is decreased when the order is confirmed for payment
        ProductOfStore productOfStore = orderDetail.getProductOfStore();
        int updatedQuantity = productOfStore.getQuantity() - orderDetail.getQuantity();
        productOfStore.setQuantity(updatedQuantity);
        productOfStoreRepository.save(productOfStore);

        return OrderDetailResponse.cloneFromOrderDetail(orderDetail);
    }

    @Transactional
    public OrderDetailResponse initOrderPayment(ProductOfStore productOfStore, int quantity, String emailCustomer) {
        OrderDetail orderDetail = createOrderDetail(productOfStore, quantity, emailCustomer);
        orderDetailRepository.save(orderDetail);

        // Update product stock
        int updatedQuantity = productOfStore.getQuantity() - quantity;
        productOfStore.setQuantity(updatedQuantity);
        productOfStoreRepository.save(productOfStore);

        return OrderDetailResponse.cloneFromOrderDetail(orderDetail);
    }

    public TransactionVNPay initTransactionPaymentVNPay(OrderDetail orderDetail) {
        TransactionVNPay transactionVNPay = new TransactionVNPay();
        transactionVNPay.setShopAccountLink(orderDetail.getProductOfStore().getStore().getVNPayAccountLink());
        transactionVNPay.setIntTransactionTime(ProcessUtils.getMiliseconds());
        transactionVNPay.setOrderId(orderDetail.getId());
        transactionVNPay.setAmount(orderDetail.getPrice_total());
        transactionVNPay.setEmailCustomer(orderDetail.getEmailCustomer());
        transactionVNPayRepository.save(transactionVNPay);
        System.out.println("Transaction initialized");

        return transactionVNPay;
    }

    private OrderDetail createOrderDetail(ProductOfStore productOfStore, int quantity, String emailCustomer) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setQuantity(quantity);
        orderDetail.setProductOfStore(productOfStore);
        orderDetail.setCreatedAt(ProcessUtils.getCurrentDay());
        orderDetail.setEmailCustomer(emailCustomer);

        // Calculate total price
        long totalPrice = (long) (productOfStore.getPriceO() * quantity * (100 - productOfStore.getDiscount()) / 100);
        orderDetail.setPrice_total(totalPrice);

        return orderDetail;
    }
}
