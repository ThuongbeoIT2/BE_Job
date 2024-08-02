package com.example.oauth2.SapoStore.payload.reponse;


import com.example.oauth2.SapoStore.model.ProductOfStoreImage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductOSImageResponse {
    private long id;
    private String title;
    private String description;
    private String urlImage;
    private boolean status;
    private String productName;
    public static ProductOSImageResponse cloneFromProductOSImage(ProductOfStoreImage productOfStoreImage){
        ProductOSImageResponse productOSImageResponse = new ProductOSImageResponse();
        productOSImageResponse.setId(productOfStoreImage.getId());
        productOSImageResponse.setTitle(productOfStoreImage.getTitle());
        productOSImageResponse.setDescription(productOfStoreImage.getDescription());
        productOSImageResponse.setUrlImage(productOfStoreImage.getUrlImage());
        productOSImageResponse.setStatus(productOfStoreImage.isStatus());
        productOSImageResponse.setProductName(productOfStoreImage.getProductOfStore().getProduct().getProName());
        return productOSImageResponse;
    }
}
