package com.example.oauth2.SapoStore.controller;

import com.example.oauth2.SapoStore.exception.NotFoundObjectException;
import com.example.oauth2.SapoStore.model.*;
import com.example.oauth2.SapoStore.payload.reponse.OrderDetailResponse;
import com.example.oauth2.SapoStore.repository.BillPaymentRepository;
import com.example.oauth2.SapoStore.repository.OrderDetailRepository;
import com.example.oauth2.SapoStore.repository.OrderStatusRepository;
import com.example.oauth2.SapoStore.repository.PaymentMethodRepository;
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
    @PostMapping(value = "/buy-now")

    ResponseEntity<ApiResponse> buyNow(@RequestParam long productOSID,
                                       @RequestParam int quantity,
                                       @RequestParam int paymentMethodID) throws InterruptedException {
        Optional<ProductOfStore> productOfStore = isProductOSExist(productOSID);
        if (quantity<0 ){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ApiResponse("FAILED",GlobalConstant.ResultResponse.FAILURE,""));
        }
        PaymentMethod paymentMethod = getPaymentMethodById(paymentMethodID);
        OrderDetail orderDetail=orderService.orderToCart(productOfStore.get(),quantity,getEmailCustomer());
        orderService.addOrderToQueue(orderDetail.getId());
        if ("VN-PAY".equalsIgnoreCase(paymentMethod.getSlug())){
            orderDetail.setVNPAY(true);
            orderDetailRepository.save(orderDetail);
            orderService.intTransactionPaymentVNPay(orderDetail);
            String redirectUrl = vnPayService.createOrder(
                    (int) orderDetail.getPrice_total(),
                    String.valueOf(orderDetail.getId()),
                    "http://localhost:8080"
            );
            Map<String, String> response = new HashMap<>();
            response.put("redirectUrl", redirectUrl);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK", "Redirecting to VNPay", response));
        }
        orderDetail.setVNPAY(false);
        orderDetailRepository.save(orderDetail);
        BillPayment billPayment = new BillPayment();
        OrderStatus orderStatus = orderStatusRepository.findById(1).get();
        billPayment.setOrderStatus(orderStatus);
        billPayment.setPayment(false);
        billPaymentRepository.save(billPayment);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK","Đặt  hàng thành công",orderDetail));
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
