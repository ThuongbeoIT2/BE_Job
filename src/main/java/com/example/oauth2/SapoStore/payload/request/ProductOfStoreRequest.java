package com.example.oauth2.SapoStore.payload.request;

import com.example.oauth2.SapoStore.model.Product;
import com.example.oauth2.SapoStore.model.Store;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductOfStoreRequest {
    private Long priceI;
    private Long priceO;
    private double discount;
    private Product product;
    private Store store;

}
