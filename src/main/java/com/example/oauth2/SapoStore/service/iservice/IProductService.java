package com.example.oauth2.SapoStore.service.iservice;

import com.example.oauth2.SapoStore.model.Product;
import com.example.oauth2.SapoStore.payload.reponse.ProductResponse;
import com.example.oauth2.SapoStore.payload.request.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IProductService {
    Page<ProductResponse> findAll(Pageable pageable);
    List<ProductResponse> getAllData();
    Optional<ProductResponse> findProductBySlug(String slug);
    Page<ProductResponse> getProductByCategory(String category, Pageable pageable);
    Page<ProductResponse> searchProductByKey(String key, Pageable pageable);
    Optional<Product> findById(int id);
    Optional<Product> findBySlug(String slug);
    void Save(Product product);
    void Delete(Product product);
    void insert(ProductRequest productRequest);
    void update(ProductRequest productRequest, Product product);
}
