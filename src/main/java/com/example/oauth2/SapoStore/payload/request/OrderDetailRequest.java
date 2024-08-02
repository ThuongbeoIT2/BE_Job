package com.example.oauth2.SapoStore.payload.request;

import com.example.oauth2.SapoStore.model.ProductOfStore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailRequest {
    private ProductOfStore productOfStore;
    private int quantity;

}
