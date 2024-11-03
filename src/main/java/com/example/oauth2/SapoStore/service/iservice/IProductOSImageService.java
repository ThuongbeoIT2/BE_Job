package com.example.oauth2.SapoStore.service.iservice;

import com.example.oauth2.SapoStore.model.ProductOfStoreImage;
import com.example.oauth2.SapoStore.payload.reponse.ProductOSImageResponse;
import com.example.oauth2.SapoStore.payload.request.ProductOSImageRequest;

import java.util.List;
import java.util.Optional;

public interface IProductOSImageService {
    List<ProductOSImageResponse> getAllProductOSImage(String storeCode, long productOSID);
    Optional<ProductOfStoreImage> getProductOSImageById(long ID);
    void activeImage(ProductOfStoreImage productOfStoreImage);
    void inActive(ProductOfStoreImage productOfStoreImage);
    void Insert(ProductOSImageRequest productOSImageRequest);

}
