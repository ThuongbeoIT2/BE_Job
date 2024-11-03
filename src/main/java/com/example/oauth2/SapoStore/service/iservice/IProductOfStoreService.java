package com.example.oauth2.SapoStore.service.iservice;

import com.example.oauth2.SapoStore.model.ProductOfStore;
import com.example.oauth2.SapoStore.model.ProductOfStoreImage;
import com.example.oauth2.SapoStore.page.SapoPageRequest;
import com.example.oauth2.SapoStore.payload.reponse.ProductOSImageResponse;
import com.example.oauth2.SapoStore.payload.reponse.ProductOfStoreResponse;
import com.example.oauth2.SapoStore.payload.request.ProductOSImageRequest;
import com.example.oauth2.SapoStore.payload.request.ProductOfStoreRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IProductOfStoreService {
    List<ProductOSImageResponse> getImageByProductOS(long id);
    List<ProductOSImageResponse> getAllImageByProductOS(long id);
    void insert(ProductOSImageRequest productOSImageRequest);
    void enable(ProductOfStoreImage productOfStoreImage);
    void softDelete(ProductOfStoreImage productOfStoreImage);
    Page<ProductOfStoreResponse> findAll(Pageable pageable);
    Optional<ProductOfStoreResponse> getProductOfStoreById(Long id);
    Optional<ProductOfStore> ProductOfStoreById(Long id);
    Page<ProductOfStoreResponse> findProductOfStoreByStore(String storeCode, Pageable pageable);
    Optional<ProductOfStore> isExistProductOfStore(String slug, String storeCode);
    Page<ProductOfStoreResponse> getProductByCategoryandStore(String slug,String storeCode, Pageable pageable);
    Page<ProductOfStoreResponse> searchProductOfStoreByKey(String key,String storeCode, Pageable pageable);
    void Save(ProductOfStore productOfStore);
    void insert(ProductOfStoreRequest productOfStoreRequest);
    void update(ProductOfStoreRequest productOfStoreRequest, ProductOfStore productOfStore);
    void enable(ProductOfStore productOfStore);
    void softDelete(ProductOfStore productOfStore);
    Page<ProductOfStoreResponse> getListProductOfStoreBySlug(String slug, SapoPageRequest sapoPageRequest);
}
