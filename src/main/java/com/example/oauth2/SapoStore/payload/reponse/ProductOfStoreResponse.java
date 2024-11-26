package com.example.oauth2.SapoStore.payload.reponse;

import com.example.oauth2.SapoStore.model.ProductOfStore;
import com.example.oauth2.SapoStore.model.ProductOfStoreImage;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;


import java.util.UUID;

@Getter
@Setter
public class ProductOfStoreResponse {
    private Long id;
    private Long priceO;
    private Long priceI;
    private double discount;
    private String CU = "VND";
    private long view;
    private boolean status;
    private String proName;
    private String description;
    private int quantity;
    private double evaluate;
    private String slug;
    private int sold;
    private String category;
    private String storeName;
    private String storeCode;
    private String thumbnail="https://res.cloudinary.com/dqvr7kat6/image/upload/v1721289530/agbhiqut7wyrgpjcgxm9.jpg";

    @Override
    public String toString() {
        return new Gson().toJson(this); // Sử dụng Gson để chuyển đổi đối tượng sang JSON
    }

    public static ProductOfStoreResponse cloneFromProductOfStore(ProductOfStore productOfStore) {
        ProductOfStoreResponse productOfStoreResponse = new ProductOfStoreResponse();
        productOfStoreResponse.setId(productOfStore.getId());
        productOfStoreResponse.setPriceO(productOfStore.getPriceO());
        productOfStoreResponse.setDiscount(productOfStore.getDiscount());
        productOfStoreResponse.setCU("VND");
        productOfStoreResponse.setSold(productOfStore.getSold());
//        ProductOfStoreImage productOfStoreImage = productOfStore.getProductOfStoreImages().get(0);
//        productOfStoreResponse.setThumbnail(productOfStoreImage.getUrlImage());
        productOfStoreResponse.setPriceI(productOfStore.getPriceI());
        productOfStoreResponse.setDescription(productOfStore.getDescription());
        productOfStoreResponse.setView(productOfStore.getView());
        productOfStoreResponse.setStatus(productOfStore.isStatus());
        productOfStoreResponse.setProName(productOfStore.getProduct().getProName());
        productOfStoreResponse.setSlug(productOfStore.getProduct().getSlug());
        productOfStoreResponse.setCategory(productOfStore.getProduct().getCategory().getCateName());
        productOfStoreResponse.setStoreName(productOfStore.getStore().getStoreName());
        productOfStoreResponse.setStoreCode(productOfStore.getStore().getStoreCode());
        productOfStoreResponse.setEvaluate(productOfStore.getEvaluate());
        productOfStoreResponse.setQuantity(productOfStore.getQuantity());
        return productOfStoreResponse;
    }

}
