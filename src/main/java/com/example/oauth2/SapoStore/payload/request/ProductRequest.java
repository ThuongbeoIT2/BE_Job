package com.example.oauth2.SapoStore.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    private String proName;
    private String slug;
    private String thumbnail;
    private int quantity;
    private String description;
    private String category;


}
