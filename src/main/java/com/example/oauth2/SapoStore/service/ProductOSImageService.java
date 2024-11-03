package com.example.oauth2.SapoStore.service;

import com.example.oauth2.SapoStore.model.ProductOfStoreImage;
import com.example.oauth2.SapoStore.payload.reponse.ProductOSImageResponse;
import com.example.oauth2.SapoStore.payload.request.ProductOSImageRequest;
import com.example.oauth2.SapoStore.repository.ProductOfStoreImageRepository;
import com.example.oauth2.SapoStore.service.iservice.IProductOSImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductOSImageService implements IProductOSImageService {
    @Autowired
    private ProductOfStoreImageRepository productOfStoreImageRepository;
    @Override
    public List<ProductOSImageResponse> getAllProductOSImage(String storeCode, long productOSID) {
        return productOfStoreImageRepository.getProductOfStoreImageByProduct(productOSID).stream()
                .map(ProductOSImageResponse::cloneFromProductOSImage).collect(Collectors.toList());
    }

    @Override
    public Optional<ProductOfStoreImage> getProductOSImageById(long ID) {
        return productOfStoreImageRepository.findById(ID);
    }

    @Override
    public void activeImage(ProductOfStoreImage productOfStoreImage) {
        productOfStoreImage.setStatus(true);
        productOfStoreImageRepository.save(productOfStoreImage);
    }

    @Override
    public void inActive(ProductOfStoreImage productOfStoreImage) {
        productOfStoreImage.setStatus(false);
        productOfStoreImageRepository.save(productOfStoreImage);
    }

    @Override
    public void Insert(ProductOSImageRequest productOSImageRequest) {
        ProductOfStoreImage productOfStoreImage = new ProductOfStoreImage();
        productOfStoreImage.setUrlImage(productOSImageRequest.getUrlImage());
        productOfStoreImage.setTitle(productOSImageRequest.getTitle());
        productOfStoreImage.setStatus(false);
        productOfStoreImage.setProductOfStore(productOSImageRequest.getProductOfStore());
        productOfStoreImage.setDescription(productOSImageRequest.getDescription());
        productOfStoreImageRepository.save(productOfStoreImage);
    }
}
