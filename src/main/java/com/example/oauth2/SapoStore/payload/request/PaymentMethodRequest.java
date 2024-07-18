package com.example.oauth2.SapoStore.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodRequest {
    private String slug;
    private String paymentmethod;
    private String description;
}
