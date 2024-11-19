package com.example.oauth2.SapoStore.payload.reponse;



import com.example.oauth2.SapoStore.model.OrderDetail;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailResponse {

    private long id;
    private int quantity;
    private long price_total;
    private String productName;
    private String storeName;
    private String initStatus;
    private String isPayment;
    private Long priceO;
    private double discount;
    public static OrderDetailResponse cloneFromOrderDetail(OrderDetail orderDetail){
        OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
        orderDetailResponse.setId(orderDetail.getId());
        orderDetailResponse.setQuantity(orderDetail.getQuantity());
        orderDetailResponse.setPrice_total(orderDetail.getPrice_total());
        orderDetailResponse.setDiscount(orderDetail.getProductOfStore().getDiscount());
        orderDetailResponse.setProductName(orderDetail.getProductOfStore().getProduct().getProName());
        orderDetailResponse.setPriceO(orderDetail.getProductOfStore().getPriceO());
        orderDetailResponse.setInitStatus(orderDetail.getInitOrderStatus());
        orderDetailResponse.setIsPayment(orderDetail.getIsPayment());
        orderDetailResponse.setStoreName(orderDetail.getProductOfStore().getStore().getStoreName());
        return orderDetailResponse;
    }
}
