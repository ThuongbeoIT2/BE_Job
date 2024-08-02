package com.example.oauth2.SapoStore.payload.request;

import com.example.oauth2.SapoStore.model.ProductOfStore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductOSImageRequest {
    private long id;
    private String title;
    private String description;
    private String urlImage;
    private boolean status;
    private ProductOfStore productOfStore;
}
