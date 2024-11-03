package com.example.oauth2.config;

import com.example.oauth2.SapoStore.model.BillPayment;
import com.example.oauth2.SapoStore.model.OrderDetail;
import com.example.oauth2.SapoStore.model.OrderStatus;
import com.example.oauth2.SapoStore.repository.BillPaymentRepository;
import com.example.oauth2.SapoStore.repository.OrderDetailRepository;
import com.example.oauth2.SapoStore.repository.OrderStatusRepository;
import com.example.oauth2.SapoStore.service.OrderService;


import java.util.concurrent.BlockingQueue;

public class OrderProcessor implements Runnable {

    private final BlockingQueue<Long> orderQueue;

    public OrderProcessor(BlockingQueue<Long> orderQueue) {
        this.orderQueue = orderQueue;
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

    private void processOrder(Long orderId) {
        System.out.println("Đang xử lý đơn hàng: " + orderId);
        try {

            // Giả lập thời gian xử lý đơn hàng (3 giây)
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Đã xử lý xong đơn hàng: " + orderId);
    }

}
