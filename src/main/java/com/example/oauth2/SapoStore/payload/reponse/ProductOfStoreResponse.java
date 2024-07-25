package com.example.oauth2.SapoStore.payload.reponse;

import com.example.oauth2.SapoStore.model.ProductOfStore;
import lombok.Getter;
import lombok.Setter;


import java.util.UUID;

@Getter
@Setter
public class ProductOfStoreResponse {
    private Long id;
    private Long priceO;
    private double discount;
    private String CU = "VND";
    private long view;
    private boolean status;
    private String proName;
    private String slug;
    private String category;
    private String storeName;
    private UUID storeCode;

    public static ProductOfStoreResponse cloneFromProductOfStore(ProductOfStore productOfStore) {
        ProductOfStoreResponse productOfStoreResponse = new ProductOfStoreResponse();
        productOfStoreResponse.setId(productOfStore.getId());
        productOfStoreResponse.setPriceO(productOfStore.getPriceO());
        productOfStoreResponse.setDiscount(productOfStore.getDiscount());
        productOfStoreResponse.setCU("VND");
        productOfStoreResponse.setView(productOfStore.getView());
        productOfStoreResponse.setStatus(productOfStore.isStatus());
        productOfStoreResponse.setProName(productOfStore.getProduct().getProName());
        productOfStoreResponse.setSlug(productOfStore.getProduct().getSlug());
        productOfStoreResponse.setCategory(productOfStore.getProduct().getCategory().getCateName());
        productOfStoreResponse.setStoreName(productOfStore.getStore().getStoreName());
        productOfStoreResponse.setStoreCode(productOfStore.getStore().getStoreCode());

        return productOfStoreResponse;
    }
}
