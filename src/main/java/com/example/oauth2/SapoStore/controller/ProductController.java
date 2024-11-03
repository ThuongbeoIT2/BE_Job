package com.example.oauth2.SapoStore.controller;

import com.cloudinary.Cloudinary;
import com.example.oauth2.SapoStore.exception.NotFoundObjectException;
import com.example.oauth2.SapoStore.exception.SlugConflictException;
import com.example.oauth2.SapoStore.model.Category;
import com.example.oauth2.SapoStore.model.Product;
import com.example.oauth2.SapoStore.page.SapoPageRequest;
import com.example.oauth2.SapoStore.payload.reponse.ProductResponse;
import com.example.oauth2.SapoStore.payload.request.ProductRequest;
import com.example.oauth2.SapoStore.repository.CategoryRepository;
import com.example.oauth2.SapoStore.service.iservice.IProductService;
import com.example.oauth2.globalContanst.GlobalConstant;
import com.example.oauth2.payload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
    @GetMapping(value = "/list-product/{cateId}")
    ResponseEntity<Page<ProductResponse>> getProductByCategory(@PathVariable int cateId, @RequestParam(defaultValue = "0") int page) {
        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Optional<Category> category = categoryRepository.findById(cateId);
        Page<ProductResponse> productResponses = iProductService.getProductByCategory(category.get().getSlug(), sapoPageRequest);
        return ResponseEntity.ok(productResponses);
    }
    @GetMapping(value = "/search")
    ResponseEntity<Page<ProductResponse>> getProductByKey(@RequestParam String key, @RequestParam(defaultValue = "0") int page) {
        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Page<ProductResponse> productResponses = iProductService.searchProductByKey(key, sapoPageRequest);
        return ResponseEntity.ok(productResponses);
    }
    @PostMapping(value = "/insert")
    ResponseEntity<ApiResponse> registerProduct(@RequestParam String proName,
                                                @RequestParam String slug,
                                                @RequestParam String description,
                                                @RequestParam MultipartFile thumbnail,
                                                @RequestParam String category) {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProName(proName);
        productRequest.setSlug(slug);
        productRequest.setDescription(description);
        if (iProductService.findProductBySlug(slug).isPresent()){
            throw new SlugConflictException(GlobalConstant.ObjectClass.PRODUCT,GlobalConstant.ErrorCode.MER420);
        }
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
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
    }
    @PostMapping(value = "/update/{slug}")
    ResponseEntity<ApiResponse> updateProduct(@PathVariable String slug,
                                              @RequestParam int id,
                                              @RequestParam String proName,
                                              @RequestParam(required = false) MultipartFile thumbnail,
                                              @RequestParam String description,
                                              @RequestParam String category) {

        // Find the existing product by slug
        Optional<Product> product = iProductService.findBySlug(slug);
        if (product.isEmpty()){
            product = iProductService.findById(id);
        }else if (product.get().getProId() != id) {
            throw new SlugConflictException(GlobalConstant.ObjectClass.PRODUCT, GlobalConstant.ErrorCode.MER420);
        }
        Product existingProduct = product.get();

        // Check if the category exists
        if (findCategoryBySlug(category) == null) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.CATEGORY, GlobalConstant.ErrorCode.MER404);
        }
        existingProduct.setProName(proName);
        existingProduct.setSlug(slug);
        existingProduct.setDescription(description);
        existingProduct.setCategory(findCategoryBySlug(category));
        // If a new thumbnail is provided, upload it
        if (thumbnail != null && !thumbnail.isEmpty()) {
            Map<String, Object> uploadThumbnail = upload(thumbnail);
            existingProduct.setThumbnail(uploadThumbnail.get("secure_url").toString());
        }

        // Save the updated product
        iProductService.Save(existingProduct);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("OK", GlobalConstant.ResultResponse.SUCCESS, "Product updated successfully"));
    }

    @GetMapping(value = "/delete/{id}")
    ResponseEntity<ApiResponse> deleteStoreType(@PathVariable int id){
        Optional<Product> product = iProductService.findById(id);
        if (product.isEmpty()){
            throw  new NotFoundObjectException(GlobalConstant.ObjectClass.CATEGORY,GlobalConstant.ErrorCode.MER404);
        }
        iProductService.Delete(product.get());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
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
