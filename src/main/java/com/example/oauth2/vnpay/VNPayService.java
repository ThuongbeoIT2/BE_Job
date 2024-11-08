package com.example.oauth2.vnpay;

import com.example.oauth2.SapoStore.model.*;
import com.example.oauth2.SapoStore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayService {
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
    public String createOrder(int total, String orderInfor, String urlReturn){
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String orderType = "order-type";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(total*100));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfor);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);

        urlReturn += VNPayConfig.vnp_Returnurl;
        vnp_Params.put("vnp_ReturnUrl", urlReturn);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        return paymentUrl;
    }

    public int orderReturn(HttpServletRequest request){
        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = null;
            String fieldValue = null;
            fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII);
            fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");
        String signValue = VNPayConfig.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }

    public void handleTransaction (long orderID,String vnp_ResponseCode){
        TransactionVNPay transactionVNPay = transactionVNPayRepository.findByOrderID(orderID).get(0);
        long now =System.currentTimeMillis();
        transactionVNPay.setEndTransactionTime(now);
        transactionVNPay.setResultCode(vnp_ResponseCode);
        transactionVNPay.setResultDesc("Thành Công");
        transactionVNPay.setCustomerAccountLink("Giả lập");
        transactionVNPay.setShopAccountLink("Giả lập");
        transactionVNPay.setIsPaymentByShipper("0");
        OrderDetail orderDetail= orderDetailRepository.findById(orderID).get();
        ProductOfStore productOfStore= orderDetail.getProductOfStore();
        orderDetail.setIsPayment("1");
        productOfStore.setQuantity(productOfStore.getQuantity()-orderDetail.getQuantity());
        BillPayment billPayment= billPaymentRepository.findByOrderID(orderID).get();
        billPayment.setTransID(transactionVNPay.getTransID());
        billPayment.setOrderStatus(orderStatusRepository.findById(1).get());
        billPayment.setPayment(true);
        PaymentMethod paymentMethod = paymentMethodRepository.findById(1).get();
        billPayment.setPaymentMethod(paymentMethod);

        billPaymentRepository.save(billPayment);
        orderDetailRepository.save(orderDetail);
        productOfStoreRepository.save(productOfStore);
        transactionVNPayRepository.save(transactionVNPay);
    }

}