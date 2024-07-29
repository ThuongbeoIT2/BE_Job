package com.example.oauth2.SapoStore.service.iservice;

import com.example.oauth2.SapoStore.model.ProductOfStore;
import com.example.oauth2.SapoStore.payload.reponse.ProductOfStoreResponse;
import com.example.oauth2.SapoStore.payload.request.ProductOfStoreRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IProductOfStoreService {
    Page<ProductOfStoreResponse> findAll(Pageable pageable);
    Optional<ProductOfStoreResponse> getProductOfStoreById(Long id);
    Optional<ProductOfStore> ProductOfStoreById(Long id);
    Page<ProductOfStoreResponse> findProductOfStoreByStore(UUID storeCode, Pageable pageable);
    Optional<ProductOfStore> isExistProductOfStore(String slug, UUID storeCode);
    Page<ProductOfStoreResponse> getProductByCategoryandStore(String slug,UUID storeCode, Pageable pageable);
    Page<ProductOfStoreResponse> searchProductOfStoreByKey(String key,UUID storeCode, Pageable pageable);
    void Save(ProductOfStore productOfStore);
    void insert(ProductOfStoreRequest productOfStoreRequest);
    void update(ProductOfStoreRequest productOfStoreRequest, ProductOfStore productOfStore);
    void enable(ProductOfStore productOfStore);
    void softDelete(ProductOfStore productOfStore);

}
