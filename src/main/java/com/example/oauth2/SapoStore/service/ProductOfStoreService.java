package com.example.oauth2.SapoStore.service;

import com.example.oauth2.SapoStore.model.ProductOfStore;
import com.example.oauth2.SapoStore.model.ProductOfStoreImage;
import com.example.oauth2.SapoStore.page.SapoPageRequest;
import com.example.oauth2.SapoStore.payload.reponse.ProductOSImageResponse;
import com.example.oauth2.SapoStore.payload.reponse.ProductOfStoreResponse;
import com.example.oauth2.SapoStore.payload.request.ProductOSImageRequest;
import com.example.oauth2.SapoStore.payload.request.ProductOfStoreRequest;
import com.example.oauth2.SapoStore.repository.ProductOfStoreImageRepository;
import com.example.oauth2.SapoStore.repository.ProductOfStoreRepository;
import com.example.oauth2.SapoStore.service.iservice.IProductOfStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductOfStoreService implements IProductOfStoreService {
    @Autowired
    private ProductOfStoreRepository productOfStoreRepository;
    @Autowired
    private ProductOfStoreImageRepository productOfStoreImageRepository;

    @Override
    public List<ProductOfStoreResponse> getAllData() {
        return productOfStoreRepository.findAll().stream().map(ProductOfStoreResponse::cloneFromProductOfStore).collect(Collectors.toList());
    }

    @Override
    public List<ProductOSImageResponse> getImageByProductOS(long id) {
        return productOfStoreImageRepository
                .getProductOfStoreImageByProduct(id)
                .stream().
                map(ProductOSImageResponse::cloneFromProductOSImage)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductOSImageResponse> getAllImageByProductOS(long id) {
        return productOfStoreImageRepository
                .getAllProductOfStoreImageByProduct(id)
                .stream().
                map(ProductOSImageResponse::cloneFromProductOSImage)
                .collect(Collectors.toList());
    }

    @Override
    public void insert(ProductOSImageRequest productOSImageRequest) {
        ProductOfStoreImage product = new ProductOfStoreImage();
        product.setTitle(productOSImageRequest.getTitle());
        product.setDescription(productOSImageRequest.getDescription());
        product.setStatus(productOSImageRequest.isStatus());
        product.setUrlImage(productOSImageRequest.getUrlImage());
        product.setProductOfStore(productOSImageRequest.getProductOfStore());
        productOfStoreImageRepository.save(product);
    }

    @Override
    public void enable(ProductOfStoreImage productOfStoreImage) {
        productOfStoreImage.setStatus(true);
        productOfStoreImageRepository.save(productOfStoreImage);
    }

    @Override
    public void softDelete(ProductOfStoreImage productOfStoreImage) {
        productOfStoreImage.setStatus(false);
        productOfStoreImageRepository.save(productOfStoreImage);
    }

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
    public Page<ProductOfStoreResponse> findProductOfStoreByStore(String storeCode, Pageable pageable) {
        return productOfStoreRepository.findProductByStore(storeCode, pageable).map(ProductOfStoreResponse::cloneFromProductOfStore);
    }

    @Override
    public Optional<ProductOfStore> isExistProductOfStore(String slug, String storeCode) {
        return productOfStoreRepository.isExistProductOfStore(slug,storeCode);
    }


    @Override
    public Page<ProductOfStoreResponse> getProductByCategoryandStore(String slug, String storeCode, Pageable pageable) {
        return productOfStoreRepository.getProductByCategoryandStore(slug, storeCode, pageable).map(ProductOfStoreResponse::cloneFromProductOfStore);
    }

    @Override
    public Page<ProductOfStoreResponse> searchProductOfStoreByKey(String key, String storeCode, Pageable pageable) {
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
        productOfStore.setProductOfStoreImages(new ArrayList<>());
        productOfStore.setStatus(true);
        productOfStore.setQuantity(productOfStoreRequest.getQuantity());
        productOfStore.setComments(new ArrayList<>());
        productOfStore.setDescription(productOfStoreRequest.getDescription());
        productOfStore.setPriceI(productOfStoreRequest.getPriceI());
        productOfStore.setPriceO(productOfStoreRequest.getPriceO());
        productOfStore.setDiscount(productOfStoreRequest.getDiscount());
        productOfStoreRepository.save(productOfStore);
    }

    @Override
    public void update(ProductOfStoreRequest productOfStoreRequest, ProductOfStore productOfStore) {
        productOfStore.setDiscount(productOfStoreRequest.getDiscount());
        productOfStore.setPriceO(productOfStoreRequest.getPriceO());
        productOfStore.setQuantity(productOfStoreRequest.getQuantity());
        productOfStore.setDescription(productOfStoreRequest.getDescription());
        productOfStore.setDiscount(productOfStoreRequest.getDiscount());
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

    @Override
    public Page<ProductOfStoreResponse> getListProductOfStoreBySlug(String slug, SapoPageRequest sapoPageRequest) {
        return productOfStoreRepository.getProductOfStoreBySlug(slug,sapoPageRequest).map(ProductOfStoreResponse::cloneFromProductOfStore);
    }

    @Override
    public Page<ProductOfStoreResponse> getProductOSSuggest(Pageable pageable) {
        return productOfStoreRepository.findAllSorted(pageable).map(ProductOfStoreResponse::cloneFromProductOfStore);
    }
}
