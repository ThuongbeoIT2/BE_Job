package com.example.oauth2.SapoStore.service;

import com.example.oauth2.SapoStore.model.ProductOfStore;
import com.example.oauth2.SapoStore.payload.reponse.ProductOfStoreResponse;
import com.example.oauth2.SapoStore.payload.request.ProductOfStoreRequest;
import com.example.oauth2.SapoStore.repository.ProductOfStoreRepository;
import com.example.oauth2.SapoStore.service.iservice.IProductOfStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductOfStoreService implements IProductOfStoreService {
    @Autowired
    private ProductOfStoreRepository productOfStoreRepository;


    @Override
    public Page<ProductOfStoreResponse> findAll(Pageable pageable) {
        return productOfStoreRepository.findAll(pageable).map(ProductOfStoreResponse::cloneFromProductOfStore);
    }

    @Override
    public Optional<ProductOfStoreResponse> getProductOfStoreById(Long id) {
        Optional<ProductOfStore> productOfStore= productOfStoreRepository.findById(id);
       return productOfStore.map(ProductOfStoreResponse::cloneFromProductOfStore);
    }

    @Override
    public Optional<ProductOfStore> ProductOfStoreById(Long id) {
        return productOfStoreRepository.findById(id);
    }

    @Override
    public Page<ProductOfStoreResponse> findProductOfStoreByStore(UUID storeCode, Pageable pageable) {
        return productOfStoreRepository.findProductByStore(storeCode, pageable).map(ProductOfStoreResponse::cloneFromProductOfStore);
    }

    @Override
    public Optional<ProductOfStore> isExistProductOfStore(String slug, UUID storeCode) {
        return productOfStoreRepository.isExistProductOfStore(slug,storeCode);
    }


    @Override
    public Page<ProductOfStoreResponse> getProductByCategoryandStore(String slug, UUID storeCode, Pageable pageable) {
        return productOfStoreRepository.getProductByCategoryandStore(slug, storeCode, pageable).map(ProductOfStoreResponse::cloneFromProductOfStore);
    }

    @Override
    public Page<ProductOfStoreResponse> searchProductOfStoreByKey(String key, UUID storeCode, Pageable pageable) {
        return productOfStoreRepository.searchProductOfStoreByKey(key, storeCode, pageable).map(ProductOfStoreResponse::cloneFromProductOfStore);
    }

    @Override
    public void Save(ProductOfStore productOfStore) {
        productOfStoreRepository.save(productOfStore);
    }

    @Override
    public void insert(ProductOfStoreRequest productOfStoreRequest) {
        ProductOfStore productOfStore = new ProductOfStore();
        productOfStore.setProduct(productOfStoreRequest.getProduct());
        productOfStore.setStore(productOfStoreRequest.getStore());
        productOfStore.setView(0);
        productOfStore.setCU("VND");
        productOfStore.setProductOfStoreImages(new HashSet<>());
        productOfStore.setStatus(true);
        productOfStore.setComments(new HashSet<>());
        productOfStore.setPriceI(productOfStoreRequest.getPriceI());
        productOfStore.setPriceO(productOfStoreRequest.getPriceO());
        productOfStore.setDiscount(productOfStoreRequest.getDiscount());
        productOfStoreRepository.save(productOfStore);
    }

    @Override
    public void update(ProductOfStoreRequest productOfStoreRequest, ProductOfStore productOfStore) {
        productOfStore.setDiscount(productOfStoreRequest.getDiscount());
        productOfStore.setPriceO(productOfStoreRequest.getPriceO());
        productOfStoreRepository.save(productOfStore);
    }

    @Override
    public void enable(ProductOfStore productOfStore) {
        productOfStore.setStatus(true);
        productOfStoreRepository.save(productOfStore);
    }

    @Override
    public void softDelete(ProductOfStore productOfStore) {
        productOfStore.setStatus(false);
        productOfStoreRepository.save(productOfStore);
    }
}
