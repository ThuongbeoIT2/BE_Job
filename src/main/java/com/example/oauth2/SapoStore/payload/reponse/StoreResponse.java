package com.example.oauth2.SapoStore.payload.reponse;

import com.example.oauth2.SapoStore.model.Store;
import lombok.Data;


import java.util.Date;
import java.util.UUID;

@Data

public class StoreResponse {
    private Long storeId;

    private String storeCode;

    private String storeName;

    private String address;

    private String email_manager;

    private String phoneNumber;
    private String thumbnail;
    private String ekyc_01;
    private String ekyc_02;
    private String description;
    private String VNPauAccountLink;
    private boolean status;
    private double evaluate;
    private Date createdAt;
    private Date updatedAt;
    private long view;
    private String StoreType;
    private int urlIntroduce;

    public static StoreResponse cloneFromStore(Store store) {
        StoreResponse storeResponse = new StoreResponse();
        storeResponse.setStoreId(store.getStoreId());
        storeResponse.setStoreCode(store.getStoreCode());
        storeResponse.setStoreName(store.getStoreName());
        storeResponse.setAddress(store.getAddress());
        storeResponse.setEmail_manager(store.getEmail_manager());
        storeResponse.setPhoneNumber(store.getPhoneNumber());
        storeResponse.setThumbnail(store.getThumbnail());
        storeResponse.setVNPauAccountLink(store.getVNPayAccountLink());
        storeResponse.setDescription(store.getDescription());
        storeResponse.setStatus(store.isStatus());
        storeResponse.setEvaluate(store.getEvaluate());
        storeResponse.setEkyc_01(store.getEKyc_01());
        storeResponse.setEkyc_02(store.getEKyc_02());
        storeResponse.setView(store.getView());
        storeResponse.setStoreType(store.getStoretype().getTypeName());
        storeResponse.setCreatedAt(store.getCreatedAt());
        storeResponse.setUpdatedAt(store.getUpdatedAt());
        return storeResponse;
    }
}
