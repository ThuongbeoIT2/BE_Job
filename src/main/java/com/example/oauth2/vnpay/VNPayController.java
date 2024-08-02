package com.example.oauth2.vnpay;

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

        // Ở đây bạn có thể thêm logic để xử lý thanh toán như kiểm tra chữ ký, cập nhật trạng thái đơn hàng, v.v.

        return ResponseEntity.ok(response);
    }

}
