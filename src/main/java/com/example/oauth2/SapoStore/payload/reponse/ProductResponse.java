package com.example.oauth2.SapoStore.payload.reponse;


import com.example.oauth2.SapoStore.model.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponse {
    private int proId;

    private String proName;

    private String slug;

    private String thumbnail;
    private String description;
    private String category;
    public static ProductResponse cloneFromProduct(Product product){
        ProductResponse productResponse= new ProductResponse();
        productResponse.setProName(product.getProName());
        productResponse.setProId(product.getProId());
        productResponse.setSlug(product.getSlug());
        productResponse.setDescription(product.getDescription());
        productResponse.setThumbnail(product.getThumbnail());
        productResponse.setCategory(product.getCategory().getCateName());
        return productResponse;
    }

}
