package com.example.oauth2.config;

import com.example.oauth2.SapoStore.model.OrderDetail;
import com.example.oauth2.SapoStore.model.ProductOfStore;
import com.example.oauth2.SapoStore.repository.OrderDetailRepository;
import com.example.oauth2.SapoStore.repository.ProductOfStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;

@Service
public class OrderProcessor implements Runnable {

    private final BlockingQueue<Long> orderQueue;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductOfStoreRepository productOfStoreRepository;

    @Autowired
    public OrderProcessor(BlockingQueue<Long> orderQueue, OrderDetailRepository orderDetailRepository,
                          ProductOfStoreRepository productOfStoreRepository) {
        this.orderQueue = orderQueue;
        this.orderDetailRepository = orderDetailRepository;
        this.productOfStoreRepository = productOfStoreRepository;
    }


    @Override
    public void run() {
        try {
            while (true) {
                long orderId = orderQueue.take();
                processOrder(orderId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    @Transactional
    public void processOrder(Long orderId) {
        System.out.println("Processing order: " + orderId);

        try {
            Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(orderId);

            if (!optionalOrderDetail.isPresent()) {
                System.out.println("Order not found: " + orderId);
                return;
            }

            OrderDetail orderDetail = optionalOrderDetail.get();
            ProductOfStore productOfStore = orderDetail.getProductOfStore();

            // Check stock availability
            if (orderDetail.getQuantity() > productOfStore.getQuantity()) {
                orderDetail.setInitOrderStatus("FAILED");
                System.out.println("Order failed due to insufficient stock: " + orderId);
            } else {
                // Process order and update stock
                orderDetail.setInitOrderStatus("SUCCESS");
                productOfStore.setQuantity(productOfStore.getQuantity() - orderDetail.getQuantity());

                // Save the updated product quantity
                productOfStoreRepository.save(productOfStore);
                System.out.println("Order successfully processed: " + orderId);
            }

            // Save the order status update
            orderDetailRepository.save(orderDetail);

        } catch (DataAccessException ex) {
            System.err.println("Database error while processing order: " + orderId);
            ex.printStackTrace();
        } catch (Exception ex) {
            System.err.println("Unexpected error while processing order: " + orderId);
            ex.printStackTrace();
        }
    }

}
