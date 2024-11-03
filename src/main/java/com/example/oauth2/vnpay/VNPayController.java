package com.example.oauth2.vnpay;

import com.example.oauth2.SapoStore.model.*;
import com.example.oauth2.SapoStore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController

public class VNPayController {
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private TransactionVNPayRepository transactionVNPayRepository;
    @Autowired
    private ProductOfStoreRepository productOfStoreRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private BillPaymentRepository billPaymentRepository;
    @Autowired
    private OrderStatusRepository orderStatusRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;


    @PostMapping("/submitOrder")
    public ResponseEntity<Map<String, String>> submitOrder(@RequestParam("amount") int orderTotal,
                                                           @RequestParam("orderInfo") String orderInfo,
                                                           HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, baseUrl);
        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", vnpayUrl);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/vnpay-payment")
    public ResponseEntity<PaymentResponse> handleVNPayPayment(
            @RequestParam long vnp_Amount,
            @RequestParam String vnp_BankCode,
            @RequestParam String vnp_BankTranNo,
            @RequestParam String vnp_CardType,
            @RequestParam String vnp_OrderInfo,
            @RequestParam String vnp_PayDate,
            @RequestParam String vnp_ResponseCode,
            @RequestParam String vnp_TmnCode,
            @RequestParam String vnp_TransactionNo,
            @RequestParam String vnp_TransactionStatus,
            @RequestParam String vnp_TxnRef,
            @RequestParam String vnp_SecureHash) {

        PaymentResponse response = new PaymentResponse();
        response.setVnp_Amount(vnp_Amount);
        response.setVnp_BankCode(vnp_BankCode);
        response.setVnp_BankTranNo(vnp_BankTranNo);
        response.setVnp_CardType(vnp_CardType);
        response.setVnp_OrderInfo(vnp_OrderInfo);
        response.setVnp_PayDate(vnp_PayDate);
        response.setVnp_ResponseCode(vnp_ResponseCode);
        response.setVnp_TmnCode(vnp_TmnCode);
        response.setVnp_TransactionNo(vnp_TransactionNo);
        // TODO: 8/2/2024  vnp_TransactionStatus handle Status
        response.setVnp_TransactionStatus(vnp_TransactionStatus);
        response.setVnp_TxnRef(vnp_TxnRef);
        response.setVnp_SecureHash(vnp_SecureHash);
        /* By pass cho giao dịch thanh toán */
        String orderID = vnp_OrderInfo;
        TransactionVNPay transactionVNPay = transactionVNPayRepository.findByOrderID(orderID).get();
        long now =System.currentTimeMillis();
        transactionVNPay.setEndTransactionTime(now);
        transactionVNPay.setResultCode(vnp_ResponseCode);
        transactionVNPay.setResultDesc("Thành Công");
        transactionVNPay.setCustomerAccountLink("Giả lập");
        transactionVNPay.setShopAccountLink("Giả lập");
        transactionVNPayRepository.save(transactionVNPay);
        OrderDetail orderDetail= orderDetailRepository.findById(Long.valueOf(orderID)).get();
        ProductOfStore productOfStore= orderDetail.getProductOfStore();
        orderDetail.setIsPayment("1");
        orderDetailRepository.save(orderDetail);
        productOfStore.setQuantity(productOfStore.getQuantity()-orderDetail.getQuantity());
        productOfStoreRepository.save(productOfStore);
        BillPayment billPayment= billPaymentRepository.findByOrderID(orderID).get();
        billPayment.setTransID(transactionVNPay.getTransID());
        billPayment.setOrderStatus(orderStatusRepository.findById(1).get());
        billPayment.setPayment(true);
        PaymentMethod paymentMethod = paymentMethodRepository.findById(1).get();
        billPayment.setPaymentMethod(paymentMethod);
        billPaymentRepository.save(billPayment);
        // Ở đây bạn có thể thêm logic để xử lý thanh toán như kiểm tra chữ ký, cập nhật trạng thái đơn hàng, v.v.

        return ResponseEntity.ok(response);
    }

}
