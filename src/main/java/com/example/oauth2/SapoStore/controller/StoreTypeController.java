package com.example.oauth2.SapoStore.controller;

import com.cloudinary.Cloudinary;
import com.example.oauth2.SapoStore.exception.NotFoundObjectException;
import com.example.oauth2.SapoStore.exception.SlugConflictException;
import com.example.oauth2.SapoStore.model.StoreType;
import com.example.oauth2.SapoStore.payload.reponse.StoreTypeResponse;
import com.example.oauth2.SapoStore.repository.StoreTypeRepository;
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
@RequestMapping(value = "/storetype")
public class StoreTypeController {
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private StoreTypeRepository storeTypeRepository;
    @PostMapping("/insert")
    ResponseEntity<ApiResponse> InsertStoreType(@RequestParam String typeName,
                                                @RequestParam String slug,
                                                @RequestParam String description,
                                                @RequestParam MultipartFile thumbnailimg){
        if (findStoreTypeBySlug(slug)==null){
            if (thumbnailimg.isEmpty()){
                throw  new NotFoundObjectException(GlobalConstant.ObjectClass.STORETYPE,GlobalConstant.ErrorCode.MER430);
            }
            StoreType storeType= new StoreType();
            storeType.setSlug(slug);
            storeType.setTypeName(typeName);
            storeType.setDescription(description);

            Map<String, Object> uploadResult = upload(thumbnailimg);
            storeType.setThumbnail(uploadResult.get("secure_url").toString());
            storeTypeRepository.save(storeType);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK","Store type added successfully",""));
        }
        return ResponseEntity.badRequest().body(new ApiResponse("FAILED","Slug already exist",""));
    }
    @GetMapping(value = "getAll")
    ResponseEntity<List<StoreTypeResponse>> getAllStoreType(){
        return ResponseEntity.ok(storeTypeRepository.findAll()
                .stream()
                .map(storeType -> {
                    StoreTypeResponse storeTypeResponse = StoreTypeResponse.cloneFromStoreType(storeType);
                    return storeTypeResponse;
                }).collect(Collectors.toList())
        );
    }
    @PostMapping(value = "/update/{id}")
    public ResponseEntity<ApiResponse> updateStoretype(
            @PathVariable int id,
            @RequestParam String typeName,
            @RequestParam String slug,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile thumbnailimg) {

        // Retrieve the store type by ID
        Optional<StoreType> optionalStoreType = findStoreTypeById(id);
        if (optionalStoreType.isEmpty()) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORETYPE, GlobalConstant.ErrorCode.MER404);
        }

        StoreType existingStoreType = optionalStoreType.get();

        // Check for slug conflict
        if (findStoreTypeBySlug(slug) != null && existingStoreType.getId() != id) {
            throw new SlugConflictException(GlobalConstant.ObjectClass.STORETYPE, GlobalConstant.ErrorCode.MER420);
        }

        // Update the store type details
        existingStoreType.setSlug(slug);
        existingStoreType.setDescription(description);
        existingStoreType.setTypeName(typeName);

        // Handle thumbnail image upload if provided
        if (thumbnailimg != null && !thumbnailimg.isEmpty()) {
            Map<String, Object> uploadResult = upload(thumbnailimg);
            existingStoreType.setThumbnail(uploadResult.get("secure_url").toString());
        }

        // Save the updated store type
        storeTypeRepository.save(existingStoreType);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK","Update success",""));
    }

    @GetMapping(value = "/delete/{id}")
    ResponseEntity<String> deleteStoreType(@PathVariable int id){
        Optional<StoreType> storeType = findStoreTypeById(id);
        if (storeType.isEmpty()){
            throw  new NotFoundObjectException(GlobalConstant.ObjectClass.STORETYPE,GlobalConstant.ErrorCode.MER404);
        }
        storeTypeRepository.delete(storeType.get());
        return ResponseEntity.ok("delete success");
    }
    public StoreType findStoreTypeBySlug(String slug){
        StoreType storeType = storeTypeRepository.findBySlug(slug);
        return storeType;
    }
    public Optional<StoreType> findStoreTypeById(int id){
        return storeTypeRepository.findById(id);
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
