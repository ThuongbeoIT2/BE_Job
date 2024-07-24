package com.example.oauth2.SapoStore.service;

import com.example.oauth2.SapoStore.model.Category;
import com.example.oauth2.SapoStore.model.Product;
import com.example.oauth2.SapoStore.payload.reponse.ProductResponse;
import com.example.oauth2.SapoStore.payload.request.ProductRequest;
import com.example.oauth2.SapoStore.repository.CategoryRepository;
import com.example.oauth2.SapoStore.repository.ProductRepository;
import com.example.oauth2.SapoStore.service.iservice.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class ProductService implements IProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Override
    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductResponse::cloneFromProduct);
    }

    @Override
    public Optional<Product> findProductBySlug(String slug) {
        return productRepository.findProductBySlug(slug);
    }

    @Override
    public Page<ProductResponse> getProductByCategory(String category, Pageable pageable) {
        return productRepository.getProductByCategory(category, pageable).map(ProductResponse::cloneFromProduct);
    }

    @Override
    public Page<ProductResponse> searchProductByKey(String key, Pageable pageable) {
        return productRepository.searchStoreByKey(key, pageable).map(ProductResponse::cloneFromProduct);
    }

    @Override
    public void Save(Product product) {
        productRepository.save(product);
    }
    @Override
    public void insert(ProductRequest productRequest) {
        Product product= new Product();
        product.setProName(productRequest.getProName());
        product.setSlug(productRequest.getSlug());
        product.setDescription(productRequest.getDescription());
        product.setThumbnail(productRequest.getThumbnail());
        product.setCategory(findCategoryBySlug(productRequest.getCategory()));
        productRepository.save(product);
    }
    @Override
    public void update(ProductRequest productRequest, Product product) {
        product.setProName(productRequest.getProName());
        product.setSlug(productRequest.getSlug());
        product.setDescription(productRequest.getDescription());
        product.setThumbnail(productRequest.getThumbnail());
        product.setCategory(findCategoryBySlug(productRequest.getCategory()));
        productRepository.save(product);
    }
    private Category findCategoryBySlug(String category) {
        return   categoryRepository.findCategoriesBySlug(category);
    }
}
