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

//    public int orderReturn(HttpServletRequest request){
//        Map fields = new HashMap();
//        for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
//            String fieldName = null;
//            String fieldValue = null;
//            fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII);
//            fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII);
//            if ((fieldValue != null) && (fieldValue.length() > 0)) {
//                fields.put(fieldName, fieldValue);
//            }
//        }
//
//        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
//        fields.remove("vnp_SecureHashType");
//        fields.remove("vnp_SecureHash");
//        String signValue = VNPayConfig.hashAllFields(fields);
//        if (signValue.equals(vnp_SecureHash)) {
//            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
//                return 1;
//            } else {
//                return 0;
//            }
//        } else {
//            return -1;
//        }
//    }

    public int orderReturn(HttpServletRequest request, long orderId) {
        Map<String, String> fields = new HashMap<>();

        // Trích xuất tất cả các tham số từ request và lưu vào fields
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }

        // Lấy `vnp_SecureHash` từ request
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");

        // Xóa các trường không cần thiết trước khi hash
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        // Tính toán lại secure hash
        String calculatedHash = VNPayConfig.hashAllFields(fields);

        // Xác thực `secureHash`
        if (!calculatedHash.equals(vnp_SecureHash)) {
            System.out.println("Invalid secure hash! Calculated: " + calculatedHash + ", Provided: " + vnp_SecureHash);
            return -1; // Sai secure hash
        }

        // Lấy `orderId` từ `vnp_OrderInfo`
        String orderIdFromRequest = request.getParameter("vnp_OrderInfo");
        if (orderIdFromRequest == null || orderIdFromRequest.isEmpty()) {
            System.out.println("Missing orderId in vnp_OrderInfo.");
            return -2; // Thiếu hoặc sai orderId
        }
        fields.remove("vnp_OrderInfo");
        // Lấy `orderId` từ cơ sở dữ liệu
        if (orderIdFromRequest.equalsIgnoreCase(String.valueOf(orderId))) {
            System.out.println("Order ID not compare: " + orderIdFromRequest);
            return -3; // Không tìm thấy orderId trong cơ sở dữ liệu
        }

        // Kiểm tra trạng thái giao dịch
        String transactionStatus = request.getParameter("vnp_TransactionStatus");
        if (VNPayTransactionStatus.SUCCESS.getCode().equals(transactionStatus)) {
            return 1; // Giao dịch thành công
        } else {
            System.out.println("Transaction failed for Order ID: " + orderIdFromRequest);
            return 0; // Giao dịch thất bại
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

    public void handleTransactionError(long orderID, String vnpResponseCode) {
        switch (vnpResponseCode) {
            case "00":
                // Giao dịch thành công
                System.out.println("Order ID: " + orderID + " - Transaction successful.");
                // Thêm logic cập nhật trạng thái đơn hàng thành công vào database.
                break;
            case "07":
                // Giao dịch nghi ngờ gian lận
                System.out.println("Order ID: " + orderID + " - Suspected fraud.");
                // Gửi cảnh báo tới quản trị viên hoặc khách hàng.
                break;
            case "09":
                // Chưa đăng ký Internet Banking
                System.out.println("Order ID: " + orderID + " - Internet banking not registered.");
                // Thông báo khách hàng cần đăng ký Internet Banking.
                break;
            case "10":
                // Thông tin xác thực không đúng
                System.out.println("Order ID: " + orderID + " - Authentication failed.");
                // Thông báo lỗi xác thực.
                break;
            case "11":
                // Hết hạn chờ thanh toán
                System.out.println("Order ID: " + orderID + " - Payment timeout.");
                // Hủy đơn hàng hoặc gửi thông báo gia hạn thanh toán.
                break;
            case "12":
                // Tài khoản bị khóa
                System.out.println("Order ID: " + orderID + " - Account locked.");
                // Hủy giao dịch và thông báo cho khách hàng.
                break;
            case "51":
                // Không đủ số dư
                System.out.println("Order ID: " + orderID + " - Insufficient balance.");
                // Thông báo khách hàng nạp thêm tiền.
                break;
            case "75":
                // Ngân hàng bảo trì
                System.out.println("Order ID: " + orderID + " - Bank under maintenance.");
                // Gửi thông báo yêu cầu thử lại sau.
                break;
            default:
                // Các lỗi khác
                System.out.println("Order ID: " + orderID + " - Other error, code: " + vnpResponseCode);
                // Xử lý lỗi chung.
                break;
        }
    }
}