package com.example.oauth2.SapoStore.payload.reponse;

import com.example.oauth2.SapoStore.model.Store;
import lombok.Data;


import java.util.Date;
import java.util.UUID;
@Data

public class StoreResponse {
    private Long storeId;

    private UUID storeCode;

    private String storeName;

    private String address;

    private String email_manager;

    private String phoneNumber;
    private String thumbnail;
    private String description;
    private boolean status;
    private double evaluate;
    private Date createdAt;
    private Date updatedAt;
    private long view;
    private String StoreType;
    private int urlIntroduce;
    public static StoreResponse cloneFromStore(Store store){
        StoreResponse storeResponse = new StoreResponse();
        storeResponse.setStoreId(store.getStoreId());
        storeResponse.setStoreCode(store.getStoreCode());
        storeResponse.setStoreName(store.getStoreName());
        storeResponse.setAddress(storeResponse.address);
        storeResponse.setEmail_manager(store.getEmail_manager());
        storeResponse.setPhoneNumber(store.getPhoneNumber());
        storeResponse.setThumbnail(store.getThumbnail());
        storeResponse.setDescription(storeResponse.description);
        storeResponse.setStatus(store.isStatus());
        storeResponse.setEvaluate(store.getEvaluate());
        storeResponse.setView(store.getView());
        storeResponse.setStoreType(store.getStoretype().getTypeName());
        storeResponse.setCreatedAt(store.getCreatedAt());
        storeResponse.setUpdatedAt(store.getUpdatedAt());
        return storeResponse;
    }
}
