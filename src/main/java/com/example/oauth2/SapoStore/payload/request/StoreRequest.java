package com.example.oauth2.SapoStore.payload.request;

import com.example.oauth2.SapoStore.model.StoreType;
import lombok.Data;

@Data
public class StoreRequest {
    private String storeName;
    private String address;
    private String phoneNumber;
    private String thumbnail;
    private String description;
    private String eKyc;
    private StoreType storeType;
}
