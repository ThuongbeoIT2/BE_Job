package com.example.oauth2.vnpay;

import com.example.oauth2.SapoStore.model.*;
import com.example.oauth2.SapoStore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
    /*
    * http://localhost:8080/vnpay-payment?
    * vnp_Amount=1980000
    * &vnp_BankCode=NCB
    * &vnp_BankTranNo=VNP14650640
    * &vnp_CardType=ATM
    * &vnp_OrderInfo=412
    * &vnp_PayDate=20241104222301
    * &vnp_ResponseCode=00
    * &vnp_TmnCode=V5J6TT7T
    * &vnp_TransactionNo=14650640
    * &vnp_TransactionStatus=00
    * &vnp_TxnRef=20047956
    * &vnp_SecureHash=22a4244466ec6e907d59e81466b8670470d8a9a73238532688f27ce7731fa49ce8a3f9b1c4a44c01299504894c88a4310cc6c467ff3d14d9e6b94404de765fe6
    * */

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
        response.setVnp_TransactionStatus(vnp_TransactionStatus);
        response.setVnp_TxnRef(vnp_TxnRef);
        response.setVnp_SecureHash(vnp_SecureHash);
        /* By pass thành công cho giao dịch thanh toán . Sau còn đảm bảo giao dịch được gửi vào đúng shopAccountLink */
        String orderID = vnp_OrderInfo;

        // Ở đây bạn có thể thêm logic để xử lý thanh toán như kiểm tra chữ ký, cập nhật trạng thái đơn hàng, v.v.
        // Trong trường hợp giao dịch thất bại (Khách hàng chưa thanh toán. VNPAYsession hết hạn thì hoàn hàng lại.)
        // Trong trường hợp chuyển tiền thành công rồi thì coi như full luồng. Nếu có lỗi bên BE mình thì sẽ validateTransacionError();

        if ("00".equals(vnp_ResponseCode)) {
            vnPayService.handleTransaction(Long.parseLong(orderID),vnp_ResponseCode);
            // Payment was successful, redirect to order-detail page
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "http://localhost:4200/my-cart");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } else {
            vnPayService.handleTransactionError(Long.parseLong(orderID),vnp_ResponseCode);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "http://localhost:4200/payment-failure");
            return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 redirect
        }
    }

}
