package com.example.oauth2.SapoStore.payload.reponse;

import com.example.oauth2.SapoStore.model.OrderStatus;
import com.example.oauth2.SapoStore.model.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class OrderStatusRespone {
    private int statusId;

    private String Status;
    public static OrderStatusRespone cloneFromOrderStatus(OrderStatus orderStatus){
        OrderStatusRespone orderStatusRespone = new OrderStatusRespone();
        orderStatusRespone.setStatusId(orderStatus.getStatusId());
        orderStatusRespone.setStatus(orderStatus.getStatus());
        return orderStatusRespone;
    }
}
