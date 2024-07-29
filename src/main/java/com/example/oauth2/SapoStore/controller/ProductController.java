package com.example.oauth2.SapoStore.controller;

import com.cloudinary.Cloudinary;
import com.example.oauth2.SapoStore.exception.NotFoundObjectException;
import com.example.oauth2.SapoStore.model.Category;
import com.example.oauth2.SapoStore.page.SapoPageRequest;
import com.example.oauth2.SapoStore.payload.reponse.ProductResponse;
import com.example.oauth2.SapoStore.payload.request.ProductRequest;
import com.example.oauth2.SapoStore.repository.CategoryRepository;
import com.example.oauth2.SapoStore.service.iservice.IProductService;
import com.example.oauth2.globalContanst.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private CategoryRepository categoryRepository;
    @GetMapping(value = "/getall")
    ResponseEntity<Page<ProductResponse>> getAllStore(@RequestParam(defaultValue = "0") int page) {
        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Page<ProductResponse> productResponses = iProductService.findAll(sapoPageRequest);
        return ResponseEntity.ok(productResponses);
    }
    @GetMapping(value = "/view/{slug}")
    ResponseEntity<ProductResponse> viewProduct(@PathVariable String slug) {
        Optional<ProductResponse> productResponse = iProductService.findProductBySlug(slug);
        if (productResponse.isEmpty()) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.PRODUCT, GlobalConstant.ErrorCode.MER404);
        }

        return ResponseEntity.ok(productResponse.get());
    }
    @GetMapping(value = "/list-product/{category}")
    ResponseEntity<Page<ProductResponse>> getProductByCategory(@PathVariable String category, @RequestParam(defaultValue = "0") int page) {
        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Page<ProductResponse> productResponses = iProductService.getProductByCategory(category, sapoPageRequest);
        return ResponseEntity.ok(productResponses);
    }
    @GetMapping(value = "/search")
    ResponseEntity<Page<ProductResponse>> getProductByKey(@RequestParam String key, @RequestParam(defaultValue = "0") int page) {
        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Page<ProductResponse> productResponses = iProductService.searchProductByKey(key, sapoPageRequest);
        return ResponseEntity.ok(productResponses);
    }
    @PostMapping(value = "/insert")
    ResponseEntity<String> registerProduct(@RequestParam String proName,
                                         @RequestParam String slug,
                                         @RequestParam String description,
                                         @RequestParam MultipartFile thumbnail,
                                         @RequestParam String category) {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProName(proName);
        productRequest.setSlug(slug);
        productRequest.setDescription(description);
       if (findCategoryBySlug(category)== null){
           throw new NotFoundObjectException(GlobalConstant.ObjectClass.CATEGORY,GlobalConstant.ErrorCode.MER404);
       }
       productRequest.setCategory(category);
        if (thumbnail.isEmpty()) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORE, GlobalConstant.ErrorCode.MER430);
        }
        Map<String, Object> uploadthumbnail = upload(thumbnail);
        productRequest.setThumbnail(uploadthumbnail.get("secure_url").toString());
        iProductService.insert(productRequest);
        return ResponseEntity.ok(GlobalConstant.ResultResponse.SUCCESS);
    }

    public Map upload(MultipartFile file) {
        try {
            Map data = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return data;
        } catch (IOException io) {
            throw new RuntimeException("Image upload fail");
        }
    }
    public Category findCategoryBySlug(String slug) {
        Category category = categoryRepository.findCategoriesBySlug(slug);
        if (category == null) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.CATEGORY, GlobalConstant.ErrorCode.MER404);
        }
        return category;
    }
}
