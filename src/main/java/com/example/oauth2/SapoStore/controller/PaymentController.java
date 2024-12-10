package com.example.oauth2.SapoStore.controller;

import com.example.oauth2.SapoStore.exception.NotFoundObjectException;
import com.example.oauth2.SapoStore.model.*;
import com.example.oauth2.SapoStore.page.SapoPageRequest;
import com.example.oauth2.SapoStore.payload.reponse.BillPaymentResponse;
import com.example.oauth2.SapoStore.payload.reponse.OrderDetailResponse;
import com.example.oauth2.SapoStore.repository.*;
import com.example.oauth2.SapoStore.service.OrderService;
import com.example.oauth2.SapoStore.service.iservice.IOrderDetailService;
import com.example.oauth2.SapoStore.service.iservice.IProductOfStoreService;
import com.example.oauth2.globalContanst.GlobalConstant;
import com.example.oauth2.model.User;
import com.example.oauth2.notify.Notify;
import com.example.oauth2.notify.NotifyRepository;
import com.example.oauth2.payload.ApiResponse;
import com.example.oauth2.repository.UserRepository;
import com.example.oauth2.vnpay.VNPayService;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
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
    private UserRepository userRepository;
    @Autowired
    private NotifyRepository notifyRepository;

@Autowired
private TransactionVNPayRepository transactionVNPayRepository;
    @Autowired
    private VNPayService vnPayService;
    @GetMapping(value = "/cart/order")
    ResponseEntity<ApiResponse> getOrderDetailInMyCart(){
        List<OrderDetailResponse> orderDetailResponses = iOrderDetailService.getOrderDetailByUser();
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK", GlobalConstant.ResultResponse.SUCCESS,orderDetailResponses));

    }
    @PostMapping(value = "/store/order")
    ResponseEntity<Page<OrderDetailResponse>> getOrderDetailInStore(@RequestParam String storeCode,
                                                      @RequestParam(defaultValue = "0") int page){
        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);

        Page<OrderDetailResponse> orderDetailResponses = iOrderDetailService.getOrderDetailByStore(storeCode,sapoPageRequest);
        return ResponseEntity.status(HttpStatus.OK).body(orderDetailResponses);

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
    @PostMapping(value = "/delete-order")
    ResponseEntity<ApiResponse> deletOrder(@RequestParam long orderId){

        Optional<OrderDetail> orderDetail= orderDetailRepository.findById(orderId);
        if (orderDetail.isPresent()){
            orderDetailRepository.delete(orderDetail.get());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("FAIL",GlobalConstant.ResultResponse.FAILURE,""));
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


    @PostMapping(value = "/order/getById")
    public ResponseEntity<ApiResponse> getOrderById(@RequestParam long orderId) {
        Optional<OrderDetail> orderDetail = orderDetailRepository.findById(orderId);
        String emailCustomer = getEmailCustomer();
        if (orderDetail.isPresent() && orderDetail.get().getEmailCustomer().equals(emailCustomer) || orderDetail.get().getProductOfStore().getStore().getEmail_manager().equals(emailCustomer) ) {
            // Convert OrderDetail to OrderDetailResponse if needed
            OrderDetailResponse orderDetailResponse = OrderDetailResponse.cloneFromOrderDetail(orderDetail.get());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("OK", GlobalConstant.ResultResponse.SUCCESS, orderDetailResponse));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("FAIL", GlobalConstant.ResultResponse.FAILURE, "Order not found"));
        }
    }

    @PostMapping(value = "order-in-cart")
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
            BillPayment billPayment = new BillPayment();
            billPayment.setOrderID(orderDetailQueue.getId());
            billPayment.setOrderStatus(orderStatusRepository.findById(1).get());
            billPayment.setPaymentMethod(paymentMethodRepository.findById(2).get());
            billPaymentRepository.save(billPayment);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("OK", GlobalConstant.ResultResponse.SUCCESS, orderDetailID));
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Lỗi xử lý đơn hàng"));
        }).join();
    }


    @PostMapping(value = "paymentVNPAY")
    ResponseEntity<ApiResponse> paymentWithVNPAY(@RequestParam long orderDetailID,
                                                 @RequestParam String fullName,
                                                 @RequestParam String phoneNumber,
                                                 @RequestParam String address){
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
            Optional<BillPayment> billPaymentO = billPaymentRepository.findByOrderID(orderDetailID);
            if (billPaymentO.isPresent() && billPaymentO.get().isPayment()){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(new ApiResponse("NOT_IMPLEMENTED", GlobalConstant.ResultResponse.FAILURE, "Đơn hàng đã được thanh toán"));
            }
            if (billPaymentO.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(new ApiResponse("NOT_IMPLEMENTED", GlobalConstant.ResultResponse.FAILURE, "Đơn hàng không tồn tại"));
            }

        BillPayment billPayment = billPaymentO.get();
        billPayment.setPayment(false);
        billPayment.setOrderID(orderDetailID);
        billPayment.setFullName(fullName);
        billPayment.setPhone(phoneNumber);
        billPayment.setAddress(address);
        billPaymentRepository.save(billPayment);
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

        /* Hàm Shipper nhận đơn */

    @PostMapping(value = "/shipper/received")
    ResponseEntity<ApiResponse> orderReceived(@RequestParam String shipperAccount,
                                              @RequestParam long billID){
        Optional<BillPayment> billPayment = billPaymentRepository.findById(billID);
        if (billPayment.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ApiResponse("FAILD","NOT_IMPLEMENTED",""));
        }
        OrderDetail orderDetail = orderDetailRepository.findById(billPayment.get().getOrderID()).get();
        if (!billPaymentOfStore(orderDetail)){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ApiResponse("FAILD","NOT_IMPLEMENTED",""));
        }
        billPayment.get().setShipperAccount(shipperAccount);
        billPayment.get().setPayment(true);

        orderDetail.setIsPayment("1");
        OrderStatus orderStatus = orderStatusRepository.findById(2).get();
        billPayment.get().setOrderStatus(orderStatus);
        orderDetailRepository.save(orderDetail);
        billPaymentRepository.save(billPayment.get());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK","Đơn hàng đã được vận chuyển",""));
    }

    @PostMapping(value = "/shipper/complete")
    ResponseEntity<ApiResponse> orderComplete(@RequestParam long billID){
        Optional<BillPayment> billPayment = billPaymentRepository.findById(billID);
        if (billPayment.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ApiResponse("FAILD","NOT_IMPLEMENTED",""));
        }
        OrderDetail orderDetail = orderDetailRepository.findById(billPayment.get().getOrderID()).get();
        if (!billPaymentOfStore(orderDetail)){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ApiResponse("FAILD","NOT_IMPLEMENTED",""));
        }
        User user = userRepository.findByEmail(orderDetail.getEmailCustomer()).get();
        OrderStatus orderStatus = orderStatusRepository.findById(3).get();
        billPayment.get().setOrderStatus(orderStatus);
        billPaymentRepository.save(billPayment.get());
        Notify notify = new Notify();
        notify.setDescription("Đơn hàng của bạn đã giao thành công. Vui lòng để lại bình luận sau khi trải nghiệm sản phẩm.");
        notify.setUser(user);
        notifyRepository.save(notify);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK","Đơn hàng đã chuyển thành công",""));
    }

    @PostMapping(value = "paymentManual")
    ResponseEntity<ApiResponse> paymentManual(@RequestParam long orderDetailID,
                                              @RequestParam String fullName,
                                              @RequestParam String phoneNumber,
                                              @RequestParam String address){
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
        BillPayment billPayment = billPaymentRepository.findByOrderID(orderDetailID).get();
        ProductOfStore productOfStore = orderDetail.get().getProductOfStore();
        productOfStore.setSold(productOfStore.getSold() + orderDetail.get().getQuantity());
        iProductOfStoreService.Save(productOfStore);
        OrderStatus orderStatus = orderStatusRepository.findById(2).get();
        billPayment.setOrderStatus(orderStatus);
        billPayment.setOrderID(orderDetailID);
        billPayment.setFullName(fullName);
        billPayment.setPhone(phoneNumber);
        billPayment.setAddress(address);
        billPaymentRepository.save(billPayment);
        orderService.initTransactionPaymentVNPay(orderDetail.get());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK","Đơn hàng đã được thanh toán VNPay thành công",""));
    }
    @PostMapping(value = "/buy-now")
    public ResponseEntity<ApiResponse> buyNow(@RequestParam long productOSID,
                                              @RequestParam int quantity
                                             ) throws InterruptedException {
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
            BillPayment billPayment = new BillPayment();
            billPayment.setOrderStatus(orderStatusRepository.findById(1).get());
            billPayment.setOrderID(orderDetailQueue.getId());
            billPayment.setPaymentMethod(paymentMethodRepository.findById(2).get());
            billPaymentRepository.save(billPayment);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("OK", GlobalConstant.ResultResponse.SUCCESS, orderDetailQueue.getId()));
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Lỗi xử lý đơn hàng"));
        }).join();
    }

    @PostMapping(value = "/payment-bill/detail")
    ResponseEntity<ApiResponse> getPaymentBillDetail(@RequestParam long orderId){
        Optional<BillPayment> billPayment = billPaymentRepository.findByOrderID(orderId);
        if (billPayment.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ApiResponse("FAILD","FAILD","Không thể thực hiện thao tác"));
        }
        BillPaymentResponse billPaymentResponse = new BillPaymentResponse() ;
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK","Truy vấn thành công",billPaymentResponse.cloneFromBillPayment(billPayment.get())));

    }

    @PostMapping(value = "/order/cancel")
    public ResponseEntity<ApiResponse> cancelOrder(@RequestParam long orderId) {
        // Tìm OrderDetail theo orderId
        Optional<OrderDetail> orderDetailOpt = orderDetailRepository.findById(orderId);

        // Kiểm tra nếu OrderDetail không tồn tại
        if (orderDetailOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Đơn hàng không tồn tại"));
        }

        OrderDetail orderDetail = orderDetailOpt.get();

        // Kiểm tra OrderDetail có thuộc về email người dùng hiện tại hay không
        String currentEmail = getEmailCustomer();
        if (!orderDetail.getEmailCustomer().equalsIgnoreCase(currentEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Không thể hủy đơn hàng không thuộc về bạn"));
        }

        // Lấy ProductOfStore liên quan
        ProductOfStore productOfStore = orderDetail.getProductOfStore();
        if (productOfStore == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("FAILED", GlobalConstant.ResultResponse.FAILURE, "Lỗi xử lý sản phẩm liên quan đến đơn hàng"));
        }

        // Cập nhật lại số lượng sản phẩm trong kho
        productOfStore.setQuantity(productOfStore.getQuantity() + orderDetail.getQuantity());
        iProductOfStoreService.Save(productOfStore);

        // Xóa OrderDetail
        orderDetail.setDelete(true);
        orderDetailRepository.save(orderDetail);

        // Trả về phản hồi thành công
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("OK", GlobalConstant.ResultResponse.SUCCESS, "Hủy đơn hàng thành công"));
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
    boolean billPaymentOfStore(OrderDetail orderDetail){
        String emailManager = getEmailCustomer();
        return orderDetail.getProductOfStore().getStore().getEmail_manager().equals(emailManager);
    }
}
