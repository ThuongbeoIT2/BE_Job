package com.example.oauth2.SapoStore.service.iservice;

import com.example.oauth2.SapoStore.model.Store;
import com.example.oauth2.SapoStore.payload.reponse.StoreResponse;
import com.example.oauth2.SapoStore.payload.request.StoreRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IStoreService {
    Page<StoreResponse> findAll(Pageable pageable);
    Optional<Store> findStoreBystoreCode(UUID storeCode);

    Optional<StoreResponse> findStoreByCode(UUID storeCode);

    Page<StoreResponse> getStoreByType(String slug, Pageable pageable);

    Page<StoreResponse> getStoreByEmailManager(String email, Pageable pageable);

    Page<StoreResponse> searchStoreByKey(String key, Pageable pageable);
    void Save(Store store);
    void insert(StoreRequest storeRequest);
    void update(StoreRequest storeRequest, Store store);
    void softDelete(Store store);
    void enableStatus(Store store);
}
