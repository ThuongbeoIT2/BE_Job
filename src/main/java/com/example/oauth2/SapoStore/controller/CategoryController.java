package com.example.oauth2.SapoStore.controller;

import com.cloudinary.Cloudinary;
import com.example.oauth2.SapoStore.exception.NotFoundObjectException;
import com.example.oauth2.SapoStore.exception.SlugConflictException;
import com.example.oauth2.SapoStore.model.Category;
import com.example.oauth2.SapoStore.payload.reponse.CategoryResponse;
import com.example.oauth2.SapoStore.repository.CategoryRepository;
import com.example.oauth2.globalContanst.GlobalConstant;
import com.example.oauth2.payload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
@RestController
@RequestMapping(value = "/category")
public class CategoryController {
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private CategoryRepository categoryRepository;
    @PostMapping("/insert")
    ResponseEntity<ApiResponse> InsertStoreType(@RequestParam String cateName,
                                           @RequestParam String slug,
                                           @RequestParam String description,
                                           @RequestParam MultipartFile thumbnailimg){
        if (findCategoryBySlug(slug)==null){
            if (thumbnailimg.isEmpty()){
                throw  new NotFoundObjectException(GlobalConstant.ObjectClass.CATEGORY,GlobalConstant.ErrorCode.MER430);
            }
            Category category= new Category();
            category.setSlug(slug);
            category.setCateName(cateName);
            category.setDescription(description);

            Map<String, Object> uploadResult = upload(thumbnailimg);
            category.setThumbnail(uploadResult.get("secure_url").toString());
            categoryRepository.save(category);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
        }
        return ResponseEntity.badRequest().body(new ApiResponse("FAILD",GlobalConstant.ResultResponse.FAILURE,""));
    }
    @GetMapping(value = "getAll")
    ResponseEntity<List<CategoryResponse>> getAllStoreType(){
        return ResponseEntity.ok(categoryRepository.findAll()
                .stream()
                .map(category -> {
                    CategoryResponse categoryResponse = CategoryResponse.cloneFromCategory(category);
                    return categoryResponse;
                }).collect(Collectors.toList())
        );
    }
    @PostMapping(value = "search")
    ResponseEntity<List<CategoryResponse>> searchCategory(@RequestParam String key){
        return ResponseEntity.ok(categoryRepository.searchCategoriesByKey(key)
                .stream()
                .map(category -> {
                    CategoryResponse categoryResponse = CategoryResponse.cloneFromCategory(category);
                    return categoryResponse;
                }).collect(Collectors.toList())
        );
    }
    @GetMapping(value = "getDetail/{id}")
    public ResponseEntity<CategoryResponse> getDetailCategory(@PathVariable int id) {
        Optional<Category> optionalCategory = findCategoryById(id);
        return optionalCategory.map(category -> ResponseEntity.ok(CategoryResponse.cloneFromCategory(category)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/update/{id}")
    public ResponseEntity<ApiResponse> updateCategory(
            @PathVariable int id,
            @RequestParam String cateName,
            @RequestParam String slug,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile thumbnailimg) {

        // Retrieve the category by ID
        Optional<Category> optionalCategory = findCategoryById(id);
        if (optionalCategory.isEmpty()) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.CATEGORY, GlobalConstant.ErrorCode.MER404);
        }

        Category existingCategory = optionalCategory.get();

        // Check for slug conflict
        if (findCategoryBySlug(slug) != null && existingCategory.getId() != id) {
            throw new SlugConflictException(GlobalConstant.ObjectClass.CATEGORY, GlobalConstant.ErrorCode.MER420);
        }

        // Handle thumbnail image upload if provided
        if (thumbnailimg != null && !thumbnailimg.isEmpty()) {
            Map<String, Object> uploadResult = upload(thumbnailimg);
            existingCategory.setThumbnail(uploadResult.get("secure_url").toString());
        }

        // Update the category details
        existingCategory.setSlug(slug);
        existingCategory.setDescription(description);
        existingCategory.setCateName(cateName);

        // Save the updated category
        categoryRepository.save(existingCategory);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK", GlobalConstant.ResultResponse.SUCCESS, ""));
    }

    @GetMapping(value = "/delete/{id}")
    ResponseEntity<ApiResponse> deleteStoreType(@PathVariable int id){
        Optional<Category> category = findCategoryById(id);
        if (category.isEmpty()){
            throw  new NotFoundObjectException(GlobalConstant.ObjectClass.CATEGORY,GlobalConstant.ErrorCode.MER404);
        }
        categoryRepository.delete(category.get());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
    }
    public Category findCategoryBySlug(String slug){
        Category category = categoryRepository.findCategoriesBySlug(slug);
        return category;
    }
    public Optional<Category> findCategoryById(int id){
        return categoryRepository.findById(id);
    }

    public  Map upload(MultipartFile file)  {
        try{
            Map data = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return data;
        }catch (IOException io){
            throw new RuntimeException("Image upload fail");
        }
    }
}
