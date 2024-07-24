package com.example.oauth2.SapoStore.controller;

import com.cloudinary.Cloudinary;
import com.example.oauth2.SapoStore.exception.NotFoundObjectException;
import com.example.oauth2.SapoStore.exception.SlugConflictException;
import com.example.oauth2.SapoStore.model.Category;
import com.example.oauth2.SapoStore.payload.reponse.CategoryResponse;
import com.example.oauth2.SapoStore.repository.CategoryRepository;
import com.example.oauth2.globalContanst.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
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
    ResponseEntity<String> InsertStoreType(@RequestParam String cateName,
                                           @RequestParam String slug,
                                           @RequestParam String description,
                                           @RequestParam MultipartFile thumbnailimg){
        if (findStoreTypeBySlug(slug)==null){
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
            return ResponseEntity.ok("Insert Category success");
        }
        return ResponseEntity.badRequest().body("Slug Already exist");
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
    @PostMapping(value = "/update/{id}")
    ResponseEntity<String> updateStoretype(@PathVariable int id,
                                           @RequestParam String cateName,
                                           @RequestParam String slug,
                                           @RequestParam String description,
                                           @RequestParam MultipartFile thumnnailimg
    ){
        Optional<Category> category = findStoreTypeById(id);
        if (category.isEmpty()){
            throw  new NotFoundObjectException(GlobalConstant.ObjectClass.CATEGORY,GlobalConstant.ErrorCode.MER404);
        }
        if (findStoreTypeBySlug(slug)!=null && category.get().getId()!=id){
            throw new SlugConflictException(GlobalConstant.ObjectClass.CATEGORY,GlobalConstant.ErrorCode.MER420);
        }
        if (!thumnnailimg.isEmpty()){
            Map<String, Object> uploadResult = upload(thumnnailimg);
            category.get().setThumbnail(uploadResult.get("secure_url").toString());
        }
        category.get().setSlug(slug);
        category.get().setDescription(description);
        category.get().setCateName(cateName);
        categoryRepository.save(category.get());
        return ResponseEntity.ok("update success");
    }
    @GetMapping(value = "/delete/{id}")
    ResponseEntity<String> deleteStoreType(@PathVariable int id){
        Optional<Category> category = findStoreTypeById(id);
        if (category.isEmpty()){
            throw  new NotFoundObjectException(GlobalConstant.ObjectClass.CATEGORY,GlobalConstant.ErrorCode.MER404);
        }
        categoryRepository.delete(category.get());
        return ResponseEntity.ok("delete success");
    }
    public Category findStoreTypeBySlug(String slug){
        Category category = categoryRepository.findCategoriesBySlug(slug);
        return category;
    }
    public Optional<Category> findStoreTypeById(int id){
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
