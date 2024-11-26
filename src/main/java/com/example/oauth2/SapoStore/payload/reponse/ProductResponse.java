package com.example.oauth2.SapoStore.payload.reponse;


import com.example.oauth2.SapoStore.model.Product;
import com.google.gson.Gson;
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
    private boolean isHotSale ;

    @Override
    public String toString() {
        return new Gson().toJson(this); // Sử dụng Gson để chuyển đổi đối tượng sang JSON
    }

    public static ProductResponse cloneFromProduct(Product product){
        ProductResponse productResponse= new ProductResponse();
        productResponse.setProName(product.getProName());
        productResponse.setProId(product.getProId());
        productResponse.setSlug(product.getSlug());
        productResponse.setHotSale(product.isHotSale());
        productResponse.setDescription(product.getDescription());
        productResponse.setThumbnail(product.getThumbnail());
        productResponse.setCategory(product.getCategory().getCateName());
        return productResponse;
    }

}
