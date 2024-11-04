package com.example.oauth2.SapoStore.controller;

import com.example.oauth2.SapoStore.exception.NotFoundObjectException;
import com.example.oauth2.SapoStore.model.*;
import com.example.oauth2.SapoStore.payload.reponse.OrderDetailResponse;
import com.example.oauth2.SapoStore.repository.*;
import com.example.oauth2.SapoStore.service.OrderService;
import com.example.oauth2.SapoStore.service.iservice.IOrderDetailService;
import com.example.oauth2.SapoStore.service.iservice.IProductOfStoreService;
import com.example.oauth2.globalContanst.GlobalConstant;
import com.example.oauth2.payload.ApiResponse;
import com.example.oauth2.vnpay.VNPayService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
public class PaymentController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private IOrderDetailService iOrderDetailService;
    @Autowired
    private IProductOfStoreService iProductOfStoreService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private OrderStatusRepository orderStatusRepository;
    @Autowired
    private BillPaymentRepository billPaymentRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
@Autowired
private TransactionVNPayRepository transactionVNPayRepository;
    @Autowired
    private VNPayService vnPayService;
    @GetMapping(value = "/cart/order")
    ResponseEntity<ApiResponse> getOrderDetailInMyCart(){
        List<OrderDetailResponse> orderDetailResponses = iOrderDetailService.getOrderDetailByUser();
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK", GlobalConstant.ResultResponse.SUCCESS,orderDetailResponses));

    }
    @PostMapping(value = "/add-to-cart")
    ResponseEntity<ApiResponse> addToCart(@RequestParam long productOSID,
                                          @RequestParam int quantity){
        Optional<ProductOfStore> productOfStore = isProductOSExist(productOSID);
        Optional<OrderDetail> orderDetail= iOrderDetailService.getProductOSByUser(productOSID,getEmailCustomer());
        if (orderDetail.isPresent()){
            orderDetail.get().setQuantity(orderDetail.get().getQuantity()+quantity);
            orderDetail.get().setPrice_total((long) (orderDetail.get().getProductOfStore().getPriceO()*orderDetail.get().getQuantity()*(100-orderDetail.get().getProductOfStore().getDiscount())/100));
            orderDetailRepository.save(orderDetail.get());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
        }
        if (productOfStore.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("FAIL",GlobalConstant.ResultResponse.FAILURE,""));
        }
        orderService.orderToCart(productOfStore.get(),quantity,getEmailCustomer());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
    }

    @PostMapping(value = "/order/update")
    ResponseEntity<ApiResponse> Update(@RequestParam long orderDetailID,
                                          @RequestParam int quantity){
        Optional<OrderDetail> orderDetail = orderDetailRepository.findById(orderDetailID);
        if (orderDetail.get().getEmailCustomer().equalsIgnoreCase(getEmailCustomer())){
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("FAIL",GlobalConstant.ResultResponse.FAILURE,"Không phải đơn hàng của bạn"));
        }
        if (orderDetail.isPresent()){
            orderDetail.get().setQuantity(quantity);
            orderDetail.get().setPrice_total((long) (orderDetail.get().getProductOfStore().getPriceO()*orderDetail.get().getQuantity()*(100-orderDetail.get().getProductOfStore().getDiscount())/100));
            orderDetailRepository.save(orderDetail.get());
            ProductOfStore productOfStore = orderDetail.get().getProductOfStore();
            productOfStore.setQuantity(productOfStore.getQuantity()+orderDetail.get().getQuantity()-quantity);
            iProductOfStoreService.Save(productOfStore);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
        }else {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("FAIL",GlobalConstant.ResultResponse.FAILURE,""));

        }

    }
//    @PostMapping(value = "/buy-now")
//
//    ResponseEntity<ApiResponse> buyNow(@RequestParam long productOSID,
//                                       @RequestParam int quantity,
//                                       @RequestParam int paymentMethodID) throws InterruptedException {
//        Optional<ProductOfStore> productOfStore = isProductOSExist(productOSID);
//        if (quantity<0 ){
//            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ApiResponse("FAILED",GlobalConstant.ResultResponse.FAILURE,""));
//        }
//        PaymentMethod paymentMethod = getPaymentMethodById(paymentMethodID);
//        OrderDetail orderDetail=orderService.orderToCart(productOfStore.get(),quantity,getEmailCustomer());
//        orderService.addOrderToQueue(orderDetail.getId());
//        if ("VN-PAY".equalsIgnoreCase(paymentMethod.getSlug())){
//            orderDetail.setVNPAY(true);
//            orderDetailRepository.save(orderDetail);
//            orderService.intTransactionPaymentVNPay(orderDetail);
//            String redirectUrl = vnPayService.createOrder(
//                    (int) orderDetail.getPrice_total(),
//                    String.valueOf(orderDetail.getId()),
//                    "http://localhost:8080"
//            );
//            Map<String, String> response = new HashMap<>();
//            response.put("redirectUrl", redirectUrl);
//            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK", "Redirecting to VNPay", response));
//        }
//        orderDetail.setVNPAY(false);
//        orderDetailRepository.save(orderDetail);
//        BillPayment billPayment = new BillPayment();
//        OrderStatus orderStatus = orderStatusRepository.findById(1).get();
//        billPayment.setOrderStatus(orderStatus);
//        billPayment.setPayment(false);
//        billPaymentRepository.save(billPayment);
//        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK","Đặt  hàng thành công",orderDetail));
//    }
//    @PostMapping(value = "/buy-now")
//    ResponseEntity<ApiResponse> buyNow(@RequestParam long productOSID,
//                                       @RequestParam int quantity) throws InterruptedException {
//        Optional<ProductOfStore> productOfStore = isProductOSExist(productOSID);
//        if (quantity < 0 ) {
//            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, ""));
//        }
//        OrderDetail orderDetail = orderService.orderToCart(productOfStore.get(), quantity, getEmailCustomer());
//        // Đẩy vào queue
//        orderService.addOrderToQueue(orderDetail.getId());
//        Thread.sleep(1000);
//        OrderDetail orderDetailQueue = orderDetailRepository.findById(orderDetail.getId()).get();
//        System.out.println(orderDetailQueue.getInitOrderStatus());
//        if ("FAILD".equalsIgnoreCase(orderDetailQueue.getInitOrderStatus())){
//            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Hết hàng"));
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK","Đặt  hàng thành công",""));
//
//    }

    @PostMapping(value = "payment-in-cart")
    public ResponseEntity<ApiResponse> PaymentInCart(@RequestParam long orderDetailID
                                              ) throws InterruptedException {

       OrderDetail orderDetail = orderDetailRepository.findById(orderDetailID).get();
        // Đẩy vào hàng đợi và xử lý bất đồng bộ
        orderService.addOrderToQueue(orderDetail.getId());

        // Sử dụng CompletableFuture để kiểm tra trạng thái của đơn hàng mà không chặn luồng
        CompletableFuture<OrderDetail> orderDetailFuture = CompletableFuture.supplyAsync(() -> {
            OrderDetail orderDetailQueue;
            do {
                orderDetailQueue = orderDetailRepository.findById(orderDetail.getId()).orElse(null);
            } while (orderDetailQueue != null && orderDetailQueue.getInitOrderStatus().equalsIgnoreCase("INIT"));

            return orderDetailQueue;
        });

        // Trả về kết quả khi CompletableFuture hoàn thành
        return orderDetailFuture.thenApply(orderDetailQueue -> {
            if (orderDetailQueue == null || "FAILED".equalsIgnoreCase(orderDetailQueue.getInitOrderStatus())) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Hết hàng"));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("OK", GlobalConstant.ResultResponse.SUCCESS, "Đặt hàng thành công"));
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Lỗi xử lý đơn hàng"));
        }).join();
    }

    @PostMapping(value = "paymentVNPAY")
    ResponseEntity<ApiResponse> paymentWithVNPAY(@RequestParam long orderDetailID){
        Optional<OrderDetail> orderDetail = orderDetailRepository.findById(orderDetailID);
        if (orderDetail.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Lỗi xử lý đơn hàng"));
        }
        if (orderDetail.get().getInitOrderStatus().equalsIgnoreCase("FAIL")
        || orderDetail.get().getInitOrderStatus().equalsIgnoreCase("INIT")){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Lỗi xử lý đơn hàng"));
        }
        orderDetail.get().setVNPAY(true);
            orderDetailRepository.save(orderDetail.get());
            orderService.initTransactionPaymentVNPay(orderDetail.get());
            String redirectUrl = vnPayService.createOrder(
                    (int) orderDetail.get().getPrice_total(),
                    String.valueOf(orderDetail.get().getId()),
                    "http://localhost:8080"
            );
            Map<String, String> response = new HashMap<>();
            response.put("redirectUrl", redirectUrl);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK", "Redirecting to VNPay", response));
        }
    @PostMapping(value = "paymentManual")
    ResponseEntity<ApiResponse> paymentManual(@RequestParam long orderDetailID){
        Optional<OrderDetail> orderDetail = orderDetailRepository.findById(orderDetailID);
        if (orderDetail.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Lỗi xử lý đơn hàng"));
        }
        if (orderDetail.get().getInitOrderStatus().equalsIgnoreCase("FAIL")
                || orderDetail.get().getInitOrderStatus().equalsIgnoreCase("INIT")){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Lỗi xử lý đơn hàng"));
        }
        TransactionVNPay transactionVNPay=orderService.initTransactionPaymentVNPay(orderDetail.get());
        transactionVNPay.setIsPaymentByShipper("ShipperCode");
        transactionVNPayRepository.save(transactionVNPay);
        BillPayment billPayment = new BillPayment();
        OrderStatus orderStatus = orderStatusRepository.findById(2).get();
        billPayment.setOrderStatus(orderStatus);
        billPayment.setPayment(false);
        billPaymentRepository.save(billPayment);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK","Đơn hàng đã được vận chuyển",""));
    }
    @PostMapping(value = "/buy-now")
    public ResponseEntity<ApiResponse> buyNow(@RequestParam long productOSID,
                                              @RequestParam int quantity) throws InterruptedException {
        // Kiểm tra điều kiện ban đầu
        Optional<ProductOfStore> productOfStoreOpt = isProductOSExist(productOSID);
        if (!productOfStoreOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Sản phẩm không tồn tại"));
        }

        if (quantity <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Số lượng không hợp lệ"));
        }

        ProductOfStore productOfStore = productOfStoreOpt.get();

        // Tạo OrderDetail
        OrderDetail orderDetail = orderService.orderToCart(productOfStore, quantity, getEmailCustomer());

        // Đẩy vào hàng đợi và xử lý bất đồng bộ
        orderService.addOrderToQueue(orderDetail.getId());

        // Sử dụng CompletableFuture để kiểm tra trạng thái của đơn hàng mà không chặn luồng
        CompletableFuture<OrderDetail> orderDetailFuture = CompletableFuture.supplyAsync(() -> {
            OrderDetail orderDetailQueue;
            do {
                orderDetailQueue = orderDetailRepository.findById(orderDetail.getId()).orElse(null);
            } while (orderDetailQueue != null && orderDetailQueue.getInitOrderStatus().equalsIgnoreCase("INIT"));

            return orderDetailQueue;
        });

        // Trả về kết quả khi CompletableFuture hoàn thành
        return orderDetailFuture.thenApply(orderDetailQueue -> {
            if (orderDetailQueue == null || "FAILED".equalsIgnoreCase(orderDetailQueue.getInitOrderStatus())) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Hết hàng"));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("OK", GlobalConstant.ResultResponse.SUCCESS, "Đặt hàng thành công"));
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Lỗi xử lý đơn hàng"));
        }).join();
    }

    public Optional<ProductOfStore> isProductOSExist(long productOSID){
        Optional<ProductOfStore> productOfStore = iProductOfStoreService.ProductOfStoreById(productOSID);
        if (productOfStore.isEmpty()){
            throw new NotFoundObjectException(GlobalConstant.ErrorCode.MER404,GlobalConstant.ResultResponse.FAILURE);
        }
        return productOfStore;
    }
    String getEmailCustomer(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    public PaymentMethod getPaymentMethodById(int paymentMethodID){
        Optional<PaymentMethod> paymentMethod= paymentMethodRepository.findById(paymentMethodID);
        if (paymentMethod.isEmpty()){
            throw new RuntimeException("Không tìm thấy phương thức thanh toán!");
        }
        return paymentMethod.get();
    }
}
