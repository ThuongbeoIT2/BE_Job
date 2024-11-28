package com.example.oauth2.SapoStore.payload.reponse;

import com.example.oauth2.SapoStore.model.BillPayment;
import com.example.oauth2.SapoStore.model.OrderStatus;
import com.example.oauth2.SapoStore.model.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillPaymentResponse {
    private Long billID;
    private long orderID;
    private boolean isPayment;
    private String shipperAccount;
    private String fullName;
    private String phone;
    private String address;
    private long transID;
    private String paymentMethod;
    private int orderStatus;

    // Clone constructor that copies fields from BillPayment
    public BillPaymentResponse cloneFromBillPayment(BillPayment billPayment) {
        BillPaymentResponse clonedResponse = new BillPaymentResponse();
        clonedResponse.setBillID(billPayment.getBillID());
        clonedResponse.setOrderID(billPayment.getOrderID());
        clonedResponse.setShipperAccount(billPayment.getShipperAccount());
        clonedResponse.setFullName(billPayment.getFullName());
        clonedResponse.setPhone(billPayment.getPhone());
        clonedResponse.setPayment(billPayment.isPayment());
        clonedResponse.setAddress(billPayment.getAddress());
        clonedResponse.setTransID(billPayment.getTransID());
        clonedResponse.setPaymentMethod(billPayment.getPaymentMethod().getMethod()); // Assuming PaymentMethod is an enum
        clonedResponse.setOrderStatus(billPayment.getOrderStatus().getStatusId()); // Assuming OrderStatus is an object
        return clonedResponse;
    }
}
